//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.core.infra.jms.core.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.core.application.services.FirmwareManagementService;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.DomainCoreDeviceRequestMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeviceRegistrationCompletedMessageProcessor
    extends DomainCoreDeviceRequestMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DeviceRegistrationCompletedMessageProcessor.class);

  @Autowired private FirmwareManagementService firmwareManagementService;

  public DeviceRegistrationCompletedMessageProcessor() {
    super(MessageType.DEVICE_REGISTRATION_COMPLETED);
  }

  @Override
  public void processMessage(final ObjectMessage message) {

    final MessageMetadata messageMetadata;
    try {
      messageMetadata = MessageMetadata.fromMessage(message);
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      return;
    }

    this.firmwareManagementService.handleSsldPendingFirmwareUpdate(
        messageMetadata.getDeviceIdentification());
  }
}
