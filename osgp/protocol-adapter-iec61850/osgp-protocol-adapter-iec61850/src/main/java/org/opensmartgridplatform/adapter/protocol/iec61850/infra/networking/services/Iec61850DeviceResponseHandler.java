// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import com.beanit.openiec61850.ServiceError;
import jakarta.jms.JMSException;
import java.io.Serializable;
import java.util.Objects;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DomainInformation;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.BaseMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.springframework.util.StringUtils;

public class Iec61850DeviceResponseHandler implements DeviceResponseHandler {

  private static final String NO_EXCEPTION_SPECIFIED = "no exception specified";

  private final BaseMessageProcessor messageProcessor;
  private final Integer jmsxDeliveryCount;
  private final MessageMetadata messageMetadata;
  private final DomainInformation domainInformation;
  private final Integer retryCount;
  private final Boolean isScheduled;
  private final Serializable messageData;
  private final ResponseMessageSender responseMessageSender;

  public Iec61850DeviceResponseHandler(
      final BaseMessageProcessor messageProcessor,
      final Integer jmsxDeliveryCount,
      final RequestMessageData requestMessageData,
      final ResponseMessageSender responseMessageSender) {
    this.messageProcessor = messageProcessor;
    this.jmsxDeliveryCount = jmsxDeliveryCount;
    this.messageMetadata =
        new MessageMetadata.Builder()
            .withCorrelationUid(requestMessageData.getCorrelationUid())
            .withOrganisationIdentification(requestMessageData.getOrganisationIdentification())
            .withDeviceIdentification(requestMessageData.getDeviceIdentification())
            .withMessageType(requestMessageData.getMessageType())
            .withMessagePriority(requestMessageData.getMessagePriority())
            .build();
    this.domainInformation =
        new DomainInformation(
            requestMessageData.getDomain(), requestMessageData.getDomainVersion());
    this.retryCount = requestMessageData.getRetryCount();
    this.isScheduled = requestMessageData.isScheduled();
    this.messageData = requestMessageData.getMessageData();
    this.responseMessageSender = responseMessageSender;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.opensmartgridplatform.adapter.protocol.iec61850.device.
   * DeviceResponseHandler
   * #handleResponse(org.opensmartgridplatform.adapter.protocol.iec61850.
   * device. DeviceResponse)
   */
  @Override
  public void handleResponse(final DeviceResponse deviceResponse) {
    this.messageProcessor.handleDeviceResponse(
        deviceResponse,
        this.responseMessageSender,
        this.domainInformation,
        this.messageMetadata.getMessageType(),
        this.retryCount,
        this.isScheduled);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.opensmartgridplatform.adapter.protocol.iec61850.device.
   * DeviceResponseHandler #handleConnectionFailure(java.lang.Throwable,
   * org.opensmartgridplatform.adapter.protocol.iec61850.device.
   * DeviceResponse)
   */
  @Override
  public void handleConnectionFailure(final Throwable t, final DeviceResponse deviceResponse)
      throws JMSException {
    Objects.requireNonNull(t, "handleConnectionFailure() Throwable t may not be null");
    final ConnectionFailureException connectionFailureException =
        new ConnectionFailureException(ComponentType.PROTOCOL_IEC61850, t.getMessage());
    this.messageProcessor.checkForRedelivery(
        this.messageMetadata,
        connectionFailureException,
        this.domainInformation,
        this.jmsxDeliveryCount);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.opensmartgridplatform.adapter.protocol.iec61850.device.
   * DeviceResponseHandler #handleException(java.lang.Throwable,
   * org.opensmartgridplatform.adapter.protocol.iec61850.device.
   * DeviceResponse)
   */
  @Override
  public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
    Objects.requireNonNull(t, "handleException() Throwable t may not be null");

    final OsgpException ex = this.ensureOsgpException(t);

    final ProtocolResponseMessage protocolResponseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(
                this.messageMetadata
                    .builder()
                    .withDomain(this.domainInformation.getDomain())
                    .withDomainVersion(this.domainInformation.getDomainVersion())
                    .withScheduled(this.isScheduled)
                    .withRetryCount(this.retryCount)
                    .build())
            .result(ResponseMessageResultType.NOT_OK)
            .osgpException(ex)
            .dataObject(this.messageData)
            .build();
    this.responseMessageSender.send(protocolResponseMessage);
  }

  private OsgpException ensureOsgpException(final Throwable t) {
    if (t instanceof OsgpException && !(t instanceof ProtocolAdapterException)) {
      return (OsgpException) t;
    }

    if (t instanceof ServiceError) {
      final String message;
      if (StringUtils.isEmpty(t.getMessage())) {
        message = "no specific service error code";
      } else if ("Error code=22".equals(t.getMessage())) {
        message = "Device communication failure";
      } else {
        message = t.getMessage();
      }
      return new TechnicalException(ComponentType.PROTOCOL_IEC61850, message);
    }

    return new TechnicalException(
        ComponentType.PROTOCOL_IEC61850, t == null ? NO_EXCEPTION_SPECIFIED : t.getMessage());
  }
}
