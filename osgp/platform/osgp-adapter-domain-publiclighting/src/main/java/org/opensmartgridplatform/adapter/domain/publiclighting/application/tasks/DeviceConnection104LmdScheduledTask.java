/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.tasks;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.opensmartgridplatform.adapter.domain.publiclighting.application.config.SchedulingConfigForDeviceConnection104LmdScheduledTask;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.specifications.RtuDeviceSpecifications;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.OsgpSystemCorrelationUid;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.dto.valueobjects.DomainTypeDto;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Periodic task to ensure active connection to IEC 60870-5-104 light
 * measurement devices.
 *
 * See
 * {@link SchedulingConfigForDeviceConnection104LmdScheduledTask#deviceConnection104LmdScheduledTaskCronTrigger()}
 * and
 * {@link SchedulingConfigForDeviceConnection104LmdScheduledTask#deviceConnection104LmdTaskScheduler()}.
 */
@Component
public class DeviceConnection104LmdScheduledTask extends BaseTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceConnection104LmdScheduledTask.class);

    @Autowired
    private int deviceConnection104LmdScheduledTaskMaximumAllowedAge;

    @Override
    public void run() {
        LOGGER.info("Ensuring active connections with IEC 60870-5-104 light measurement devices");
        try {
            final List<RtuDevice> gatewaysToConnect = this
                    .findLightMeasurementGateways(this.deviceConnection104LmdScheduledTaskMaximumAllowedAge);
            this.connectLightMeasurementGateways(gatewaysToConnect);

        } catch (final Exception e) {
            LOGGER.error("Exception caught ensuring active connection to IEC 60870-5-104 light measurement devices", e);
        }
    }

    protected List<RtuDevice> findLightMeasurementGateways(final int maxAllowedAge) {
        LOGGER.info("Trying to find light measurement gateways");

        final RtuDeviceSpecifications specifications = new RtuDeviceSpecifications();

        final Specification<RtuDevice> specification = specifications
                .hasDeviceLifeCycleStatus(DeviceLifecycleStatus.IN_USE)
                .and(specifications.hasDeviceType("LMG"))
                .and(specifications
                        .hasLastCommunicationTimeBefore(Instant.now().minus(maxAllowedAge, ChronoUnit.HOURS)));

        final List<RtuDevice> gateways = this.rtuDeviceRepository.findAll(specification);
        LOGGER.info("{} light measurement gateways found for which connection needs to be restored.", gateways.size());
        return gateways;
    }

    protected void connectLightMeasurementGateways(final List<RtuDevice> devicesToConnect) {
        if (CollectionUtils.isEmpty(devicesToConnect)) {
            LOGGER.info("No light measurement gateways to connect with");
            return;
        }

        for (final RtuDevice device : devicesToConnect) {
            LOGGER.info("Send connect request to light measurement gateway {}", device.getDeviceIdentification());
            this.sendConnectRequestToGateway(device);
        }
    }

    protected void sendConnectRequestToGateway(final RtuDevice gateway) {
        final String deviceIdentification = gateway.getDeviceIdentification();
        final String organisation = gateway.getOwner() == null ? ""
                : gateway.getOwner().getOrganisationIdentification();

        // Creating message with OSGP System CorrelationUID. This way the
        // responses for scheduled tasks can be filtered out.
        final String correlationUid = OsgpSystemCorrelationUid.CORRELATION_UID;
        final String messageType = DeviceFunction.GET_LIGHT_SENSOR_STATUS.name();
        final DomainTypeDto domain = DomainTypeDto.PUBLIC_LIGHTING;
        final String ipAddress = gateway.getIpAddress();

        final RequestMessage requestMessage = new RequestMessage(correlationUid, organisation, deviceIdentification,
                domain);
        this.osgpCoreRequestMessageSender.send(requestMessage, messageType, MessagePriorityEnum.LOW.getPriority(),
                ipAddress);
    }
}
