/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.publiclighting.application.services;

import javax.validation.Valid;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.publiclighting.infra.jms.PublicLightingRequestMessage;
import com.alliander.osgp.adapter.ws.publiclighting.infra.jms.PublicLightingRequestMessageSender;
import com.alliander.osgp.adapter.ws.publiclighting.infra.jms.PublicLightingRequestMessageType;
import com.alliander.osgp.adapter.ws.publiclighting.infra.jms.PublicLightingResponseMessageFinder;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.Schedule;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Service(value = "wsPublicLightingScheduleManagementService")
@Transactional(value = "transactionManager")
@Validated
public class ScheduleManagementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleManagementService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private PublicLightingRequestMessageSender publicLightingRequestMessageSender;

    @Autowired
    private PublicLightingResponseMessageFinder publicLightingResponseMessageFinder;

    /**
     * Constructor
     */
    public ScheduleManagementService() {
        // Parameterless constructor required for transactions...
    }

    public String enqueueSetLightSchedule(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, @Valid final Schedule schedule,
            final DateTime scheduledTime, final int messagePriority) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_LIGHT_SCHEDULE);
        this.domainHelperService.isInMaintenance(device);

        LOGGER.debug("enqueueSetLightSchedule called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, PublicLightingRequestMessageType.SET_LIGHT_SCHEDULE.name(),
                messagePriority, scheduledTime == null ? null : scheduledTime.getMillis());

        final PublicLightingRequestMessage message = new PublicLightingRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(schedule).build();

        this.publicLightingRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueSetLightScheduleResponse(final String correlationUid) throws OsgpException {

        return this.publicLightingResponseMessageFinder.findMessage(correlationUid);
    }

}
