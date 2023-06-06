// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.EmptyDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DomainInformation;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.iec61850.services.DeviceResponseService;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.support.JmsUtils;

public abstract class BaseMessageProcessor implements MessageProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseMessageProcessor.class);

  @Autowired protected int maxRedeliveriesForIec61850Requests;

  @Autowired protected DeviceResponseMessageSender responseMessageSender;

  @Autowired protected DeviceResponseService deviceResponseService;

  @Autowired
  @Qualifier("iec61850DeviceRequestMessageProcessorMap")
  protected MessageProcessorMap iec61850RequestMessageProcessorMap;

  protected MessageType messageType;

  protected void printDomainInfo(final RequestMessageData requestMessageData) {
    LOGGER.info(
        "Calling DeviceService function: {} for domain: {} {}",
        requestMessageData.getMessageType(),
        requestMessageData.getDomain(),
        requestMessageData.getDomainVersion());
  }

  /** Get the delivery count for a {@link Message} using 'JMSXDeliveryCount' property. */
  public Integer getJmsXdeliveryCount(final Message message) {
    try {
      final int jmsXdeliveryCount = message.getIntProperty("JMSXDeliveryCount");
      LOGGER.info("jmsXdeliveryCount: {}", jmsXdeliveryCount);
      return jmsXdeliveryCount;
    } catch (final JMSException e) {
      LOGGER.error("JMSException while reading JMSXDeliveryCount", e);
      return null;
    }
  }

  /**
   * Use 'jmsxDeliveryCount' to determine if a request should be retried using the re-delivery
   * options. In case a JMSException is thrown, the request will be rolled-back to the
   * message-broker and will be re-delivered according to the re-delivery policy set. If the maximum
   * number of re-deliveries have been executed, a protocol response message will be sent to
   * osgp-core.
   */
  public void checkForRedelivery(
      final MessageMetadata deviceMessageMetadata,
      final OsgpException e,
      final DomainInformation domainInformation,
      final int jmsxDeliveryCount) {
    final int jmsxRedeliveryCount = jmsxDeliveryCount - 1;
    LOGGER.info(
        "jmsxDeliveryCount: {}, jmsxRedeliveryCount: {}, maxRedeliveriesForIec61850Requests: {}",
        jmsxDeliveryCount,
        jmsxRedeliveryCount,
        this.maxRedeliveriesForIec61850Requests);
    if (jmsxRedeliveryCount < this.maxRedeliveriesForIec61850Requests) {
      LOGGER.info(
          "Redelivering message with messageType: {}, correlationUid: {}, for device: {} - jmsxRedeliveryCount: {} is less than maxRedeliveriesForIec61850Requests: {}",
          deviceMessageMetadata.getMessageType(),
          deviceMessageMetadata.getCorrelationUid(),
          deviceMessageMetadata.getDeviceIdentification(),
          jmsxRedeliveryCount,
          this.maxRedeliveriesForIec61850Requests);
      final JMSException jmsException =
          new JMSException(
              e == null
                  ? "checkForRedelivery() unknown error: OsgpException e is null"
                  : e.getMessage());
      throw JmsUtils.convertJmsAccessException(jmsException);
    } else {
      LOGGER.warn(
          "All redelivery attempts failed for message with messageType: {}, correlationUid: {}, for device: {}",
          deviceMessageMetadata.getMessageType(),
          deviceMessageMetadata.getCorrelationUid(),
          deviceMessageMetadata.getDeviceIdentification());
      final DeviceResponse deviceResponse =
          new DeviceResponse(
              deviceMessageMetadata.getOrganisationIdentification(),
              deviceMessageMetadata.getDeviceIdentification(),
              deviceMessageMetadata.getCorrelationUid(),
              deviceMessageMetadata.getMessagePriority());
      this.handleExpectedError(
          deviceResponse,
          e,
          domainInformation,
          deviceMessageMetadata.getMessageType(),
          deviceMessageMetadata.isScheduled());
    }
  }

  /**
   * Handles {@link EmptyDeviceResponse} by default. MessageProcessor implementations can override
   * this function to handle responses containing data.
   */
  public void handleDeviceResponse(
      final DeviceResponse deviceResponse,
      final ResponseMessageSender responseMessageSender,
      final DomainInformation domainInformation,
      final String messageType,
      final int retryCount,
      final boolean isScheduled) {

    ResponseMessageResultType result = ResponseMessageResultType.OK;
    OsgpException ex = null;

    try {
      final EmptyDeviceResponse response = (EmptyDeviceResponse) deviceResponse;
      this.deviceResponseService.handleDeviceMessageStatus(response.getStatus());
    } catch (final OsgpException e) {
      LOGGER.error("Device Response Exception", e);
      result = ResponseMessageResultType.NOT_OK;
      ex = e;
    }

    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder()
            .withDeviceIdentification(deviceResponse.getDeviceIdentification())
            .withOrganisationIdentification(deviceResponse.getOrganisationIdentification())
            .withCorrelationUid(deviceResponse.getCorrelationUid())
            .withMessageType(messageType)
            .withDomain(domainInformation.getDomain())
            .withDomainVersion(domainInformation.getDomainVersion())
            .withMessagePriority(deviceResponse.getMessagePriority())
            .withScheduled(isScheduled)
            .withRetryCount(retryCount)
            .build();
    final ProtocolResponseMessage protocolResponseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(messageMetadata)
            .result(result)
            .osgpException(ex)
            .build();
    responseMessageSender.send(protocolResponseMessage);
  }

  protected void handleExpectedError(
      final DeviceResponse deviceResponse,
      final OsgpException e,
      final DomainInformation domainInformation,
      final String messageType,
      final boolean isScheduled) {
    LOGGER.error("Expected error while processing message", e);

    final int retryCount = Integer.MAX_VALUE;

    final MessageMetadata deviceMessageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceResponse.getDeviceIdentification())
            .withOrganisationIdentification(deviceResponse.getOrganisationIdentification())
            .withCorrelationUid(deviceResponse.getCorrelationUid())
            .withMessageType(messageType)
            .withDomain(domainInformation.getDomain())
            .withDomainVersion(domainInformation.getDomainVersion())
            .withMessagePriority(deviceResponse.getMessagePriority())
            .withScheduled(isScheduled)
            .withRetryCount(retryCount)
            .build();
    final ProtocolResponseMessage protocolResponseMessage =
        new ProtocolResponseMessage.Builder()
            .messageMetadata(deviceMessageMetadata)
            .result(ResponseMessageResultType.NOT_OK)
            .osgpException(e)
            .build();
    this.responseMessageSender.send(protocolResponseMessage);
  }

  protected Iec61850DeviceResponseHandler createIec61850DeviceResponseHandler(
      final RequestMessageData requestMessageData, final Message message) {
    final Integer jsmxDeliveryCount = this.getJmsXdeliveryCount(message);
    return new Iec61850DeviceResponseHandler(
        this, jsmxDeliveryCount, requestMessageData, this.responseMessageSender);
  }
}
