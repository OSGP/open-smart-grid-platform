//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.core.infra.jms.protocol.inbound.messageprocessors;

import java.net.UnknownHostException;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.core.application.services.DeviceRegistrationMessageService;
import org.opensmartgridplatform.core.infra.jms.protocol.inbound.AbstractProtocolRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.DeviceRegistrationDataDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("oslpRegisterDeviceMessageProcessor")
public class RegisterDeviceMessageProcessor extends AbstractProtocolRequestMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(RegisterDeviceMessageProcessor.class);

  @Autowired private DeviceRegistrationMessageService deviceRegistrationMessageService;

  protected RegisterDeviceMessageProcessor() {
    super(MessageType.REGISTER_DEVICE);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {

    final MessageMetadata metadata = MessageMetadata.fromMessage(message);

    LOGGER.info(
        "Received message of messageType: {} organisationIdentification: {} deviceIdentification: {}",
        this.messageType,
        metadata.getOrganisationIdentification(),
        metadata.getDeviceIdentification());

    final RequestMessage requestMessage = (RequestMessage) message.getObject();
    final Object dataObject = requestMessage.getRequest();

    try {
      final DeviceRegistrationDataDto deviceRegistrationData =
          (DeviceRegistrationDataDto) dataObject;

      this.deviceRegistrationMessageService.updateRegistrationData(
          metadata.getDeviceIdentification(),
          deviceRegistrationData.getIpAddress(),
          deviceRegistrationData.getDeviceType(),
          deviceRegistrationData.isHasSchedule());

    } catch (final UnknownHostException e) {
      final String errorMessage =
          String.format("%s occurred, reason: %s", e.getClass().getName(), e.getMessage());
      LOGGER.error(errorMessage, e);

      throw new JMSException(errorMessage);
    }
  }
}
