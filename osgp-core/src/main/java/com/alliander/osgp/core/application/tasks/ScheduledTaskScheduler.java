package com.alliander.osgp.core.application.tasks;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.core.application.services.DeviceRequestMessageService;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.ScheduledTask;
import com.alliander.osgp.domain.core.exceptions.OsgpCoreException;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.ScheduledTaskRepository;
import com.alliander.osgp.domain.core.valueobjects.ScheduledTaskStatusType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.infra.jms.ProtocolRequestMessage;

@Component
public class ScheduledTaskScheduler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTaskScheduler.class);

    @Autowired
    private DeviceRequestMessageService deviceRequestMessageService;

    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public void run() {
        LOGGER.info("Processing scheduled tasks");

        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        final List<ScheduledTask> scheduledTasks = this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(
                ScheduledTaskStatusType.NEW, timestamp);

        for (ScheduledTask scheduledTask : scheduledTasks) {
            LOGGER.info("Processing scheduled task for device [{}] to perform [{}]  ",
                    scheduledTask.getDeviceIdentification(), scheduledTask.getMessageType());
            try {
                scheduledTask.setPending();
                scheduledTask = this.scheduledTaskRepository.save(scheduledTask);
                final ProtocolRequestMessage protocolRequestMessage = this.createProtocolRequestMessage(scheduledTask);
                this.deviceRequestMessageService.processMessage(protocolRequestMessage);
            } catch (final OsgpCoreException | FunctionalException e) {
                LOGGER.error("Processing scheduled task failed.", e);
                scheduledTask.setFailed(e.getMessage());
                scheduledTask = this.scheduledTaskRepository.save(scheduledTask);
            }
        }
    }

    private ProtocolRequestMessage createProtocolRequestMessage(final ScheduledTask scheduledTask) {
        final Device device = this.deviceRepository.findByDeviceIdentification(scheduledTask.getDeviceIdentification());

        return new ProtocolRequestMessage(scheduledTask.getDomain(), scheduledTask.getDomainVersion(),
                scheduledTask.getMessageType(), scheduledTask.getCorrelationId(),
                scheduledTask.getOrganisationIdentification(), scheduledTask.getDeviceIdentification(), device
                        .getNetworkAddress().toString(), scheduledTask.getMessageData(), true, 0);
    }
}
