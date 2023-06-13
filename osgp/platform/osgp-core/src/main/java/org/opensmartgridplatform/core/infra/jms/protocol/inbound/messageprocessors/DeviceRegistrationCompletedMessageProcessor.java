// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.jms.protocol.inbound.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.core.application.services.DeviceRegistrationMessageService;
import org.opensmartgridplatform.core.infra.jms.protocol.inbound.AbstractProtocolRequestMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeviceRegistrationCompletedMessageProcessor
    extends AbstractProtocolRequestMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DeviceRegistrationCompletedMessageProcessor.class);

  @Autowired private DeviceRegistrationMessageService deviceRegistrationMessageService;

  protected DeviceRegistrationCompletedMessageProcessor() {
    super(MessageType.DEVICE_REGISTRATION_COMPLETED);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {

    final MessageMetadata metadata = MessageMetadata.fromMessage(message);

    LOGGER.info(
        "Received message of messageType: {} organisationIdentification: {} deviceIdentification: {}",
        this.messageType,
        metadata.getOrganisationIdentification(),
        metadata.getDeviceIdentification());

    this.deviceRegistrationMessageService.sendRequestMessageToDomainCore(
        metadata.getDeviceIdentification(),
        metadata.getOrganisationIdentification(),
        metadata.getCorrelationUid(),
        MessageType.DEVICE_REGISTRATION_COMPLETED);
  }
}
