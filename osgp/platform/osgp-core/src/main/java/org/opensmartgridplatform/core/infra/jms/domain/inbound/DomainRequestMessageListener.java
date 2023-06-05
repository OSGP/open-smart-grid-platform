// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.jms.domain.inbound;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.core.application.services.DeviceRequestMessageService;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.ScheduledTask;
import org.opensmartgridplatform.domain.core.repositories.ScheduledTaskRepository;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ProtocolRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainRequestMessageListener implements MessageListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(DomainRequestMessageListener.class);

  private final DomainInfo domainInfo;
  private final DeviceRequestMessageService deviceRequestMessageService;
  private final ScheduledTaskRepository scheduledTaskRepository;

  public DomainRequestMessageListener(
      final DomainInfo domainInfo,
      final DeviceRequestMessageService osgpRequestMessageService,
      final ScheduledTaskRepository scheduledTaskRepository) {
    this.domainInfo = domainInfo;
    this.deviceRequestMessageService = osgpRequestMessageService;
    this.scheduledTaskRepository = scheduledTaskRepository;
  }

  @Override
  public void onMessage(final Message message) {
    try {
      LOGGER.info(
          "Received domain request message of type: {} for domain: {} and domainVersion: {}",
          message.getJMSType(),
          this.domainInfo.getDomain(),
          this.domainInfo.getDomainVersion());

      if (message.propertyExists(Constants.SCHEDULE_TIME)) {
        final ScheduledTask scheduledTask = this.createScheduledTask(message);
        this.scheduledTaskRepository.save(scheduledTask);
        LOGGER.info(
            "Scheduled task for device [{}] at [{}] created.",
            scheduledTask.getDeviceIdentification(),
            scheduledTask.getScheduledTime());
      } else {
        final ProtocolRequestMessage protocolRequestMessage =
            this.createProtocolRequestMessage(message);
        this.deviceRequestMessageService.processMessage(protocolRequestMessage);
        LOGGER.info(
            "Domain request for device [{}] processed.",
            protocolRequestMessage.getDeviceIdentification());
      }
    } catch (final JMSException | FunctionalException e) {
      LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
    }
  }

  public ScheduledTask createScheduledTask(final Message message) throws JMSException {

    final Serializable messageData = ((ObjectMessage) message).getObject();
    final MessageMetadata messageMetadata = MessageMetadata.fromMessage(message);
    final Timestamp scheduleTimeStamp = new Timestamp(messageMetadata.getScheduleTime());

    return new ScheduledTask(
        messageMetadata,
        this.domainInfo.getDomain(),
        this.domainInfo.getDomainVersion(),
        messageData,
        scheduleTimeStamp);
  }

  public ProtocolRequestMessage createProtocolRequestMessage(final Message message)
      throws JMSException {

    final MessageMetadata messageMetadata =
        MessageMetadata.fromMessage(message)
            .builder()
            .withDomain(this.domainInfo.getDomain())
            .withDomainVersion(this.domainInfo.getDomainVersion())
            .build();
    final Serializable messageData = ((ObjectMessage) message).getObject();

    return ProtocolRequestMessage.newBuilder()
        .messageMetadata(messageMetadata)
        .request(messageData)
        .build();
  }
}
