/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.publiclighting.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.valueobjects.Schedule;
import com.alliander.osgp.dto.valueobjects.ScheduleDto;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

@Service(value = "domainPublicLightingScheduleManagementService")
@Transactional(value = "transactionManager")
public class ScheduleManagementService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleManagementService.class);

    /**
     * Constructor
     */
    public ScheduleManagementService() {
        // Parameterless constructor required for transactions...
    }

    // === SET LIGHT SCHEDULE ===

    /**
     * Set a light schedule.
     *
     * @throws FunctionalException
     * @throws ValidationException
     */
    public void setLightSchedule(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final Schedule schedule, final Long scheduleTime, final String messageType)
            throws FunctionalException {

        LOGGER.info("setSchedule called with organisation {} and device {}.", organisationIdentification,
                deviceIdentification);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        final ScheduleDto scheduleDto = this.domainCoreMapper.map(schedule, ScheduleDto.class);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, scheduleDto),
                messageType, device.getIpAddress(), scheduleTime);
    }

    // === SET HAS SCHEDULE ===

    /**
     * Method for setting the 'hasSchedule' boolean for a device.
     *
     * @throws FunctionalException
     */
    public void setHasSchedule(final String deviceIdentification, final Boolean hasSchedule)
            throws FunctionalException {

        LOGGER.info("setHasSchedule called for device {} with hasSchedule: {}.", deviceIdentification, hasSchedule);

        final Device device = this.findActiveDevice(deviceIdentification);
        final Ssld ssld = this.findSsldForDevice(device);
        ssld.setHasSchedule(hasSchedule);
        this.ssldRepository.save(ssld);
    }
}
