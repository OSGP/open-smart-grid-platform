// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.infra.jms.ws.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.da.application.services.DeviceManagementService;
import org.opensmartgridplatform.domain.core.valueobjects.AddRtuDeviceRequest;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.BaseNotificationMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.NotificationResponseMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("domainDistributionAutomationAddDeviceRequestMessageProcessor")
public class AddDeviceRequestMessageProcessor extends BaseNotificationMessageProcessor {

  @Autowired
  @Qualifier("domainDistributionAutomationDeviceManagementService")
  private DeviceManagementService deviceManagementService;

  @Autowired
  public AddDeviceRequestMessageProcessor(
      @Qualifier("domainDistributionAutomationOutboundResponseMessageRouter")
          final NotificationResponseMessageSender responseMessageSender,
      @Qualifier("domainDistributionAutomationInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(responseMessageSender, messageProcessorMap, MessageType.ADD_DEVICE);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {

    final MessageMetadata deviceMessageMetadata = MessageMetadata.fromMessage(message);

    final AddRtuDeviceRequest addRtuDeviceRequest = (AddRtuDeviceRequest) message.getObject();

    try {
      this.deviceManagementService.addDevice(deviceMessageMetadata, addRtuDeviceRequest);
    } catch (final FunctionalException e) {
      this.handleError(
          e,
          deviceMessageMetadata.getCorrelationUid(),
          deviceMessageMetadata.getOrganisationIdentification(),
          deviceMessageMetadata.getDeviceIdentification(),
          deviceMessageMetadata.getMessageType());
    }
  }
}
