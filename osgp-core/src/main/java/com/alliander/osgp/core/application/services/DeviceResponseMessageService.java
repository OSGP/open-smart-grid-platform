package com.alliander.osgp.core.application.services;

import java.io.Serializable;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.core.domain.model.domain.DomainResponseService;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.ScheduledTask;
import com.alliander.osgp.domain.core.exceptions.OsgpCoreException;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.ScheduledTaskRepository;
import com.alliander.osgp.domain.core.valueobjects.ScheduledTaskStatusType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.infra.jms.ProtocolRequestMessage;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service
@Transactional
public class DeviceResponseMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceResponseMessageService.class);

    @Autowired
    private DomainResponseService domainResponseMessageSender;

    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    @Autowired
    private DeviceRequestMessageService deviceRequestMessageService;

    @Autowired
    private DeviceRepository deviceRepository;

    public void processMessage(final ProtocolResponseMessage message) {
        LOGGER.info("Processing protocol response message with correlation uid [{}]", message.getCorrelationUid());

        try {

            if (message.getResult() == ResponseMessageResultType.NOT_OK && message.getRetryCount() < 3) {
                LOGGER.info("Retrying: {} for {} time", message.getMessageType(), message.getRetryCount() + 1);
                final ProtocolRequestMessage protocolRequestMessage = this.createProtocolRequestMessage(message);
                this.deviceRequestMessageService.processMessage(protocolRequestMessage);
            } else {
                if (message.isScheduled()) {
                    LOGGER.info("Handling scheduled protocol response message.");
                    this.handleScheduleTask(message);
                } else {
                    LOGGER.info("Sending domain response message.");
                    this.domainResponseMessageSender.send(message);
                }
            }
        } catch (JMSException | FunctionalException | OsgpCoreException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
        }
    }

    private void handleScheduleTask(final ProtocolResponseMessage message) {
        final ScheduledTask scheduledTask = this.scheduledTaskRepository.findByCorrelationUid(message
                .getCorrelationUid());

        if (scheduledTask == null) {
            LOGGER.error("Scheduled task for device [{}] with correlation uid [{}] not found",
                    message.getDeviceIdentification(), message.getCorrelationUid());
            return;
        }

        if (message.getResult() == ResponseMessageResultType.OK
                && scheduledTask.getStatus() == ScheduledTaskStatusType.PENDING) {
            scheduledTask.setComplete();
            // TODO:delete the completed schedule from the database
            // this.scheduledTaskRepository.delete(scheduledTask)
        } else {
            scheduledTask.setFailed(message.getOsgpException().getCause().getMessage());
        }
        this.scheduledTaskRepository.save(scheduledTask);
    }

    private ProtocolRequestMessage createProtocolRequestMessage(final ProtocolResponseMessage message)
            throws JMSException {
        final Device device = this.deviceRepository.findByDeviceIdentification(message.getDeviceIdentification());

        final Serializable messageData = (Serializable) message.getDataObject();

        return new ProtocolRequestMessage(message.getDomain(), message.getDomainVersion(), message.getMessageType(),
                message.getCorrelationUid(), message.getOrganisationIdentification(),
                message.getDeviceIdentification(), device.getNetworkAddress().toString(), messageData,
                message.isScheduled(), message.getRetryCount() + 1);
    }
}
