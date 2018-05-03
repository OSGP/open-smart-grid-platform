/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.tariffswitching.application.services;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.tariffswitching.infra.jms.TariffSwitchingRequestMessage;
import com.alliander.osgp.adapter.ws.tariffswitching.infra.jms.TariffSwitchingRequestMessageSender;
import com.alliander.osgp.adapter.ws.tariffswitching.infra.jms.TariffSwitchingRequestMessageType;
import com.alliander.osgp.adapter.ws.tariffswitching.infra.jms.TariffSwitchingResponseMessageFinder;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.ScheduleEntry;
import com.alliander.osgp.domain.core.valueobjects.Schedule;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Service(value = "wsTariffSwitchingScheduleManagementService")
@Transactional(value = "transactionManager")
@Validated
public class ScheduleManagementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleManagementService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private TariffSwitchingRequestMessageSender tariffSwitchingRequestMessageSender;

    @Autowired
    private TariffSwitchingResponseMessageFinder tariffSwitchingResponseMessageFinder;

    /**
     * Constructor
     */
    public ScheduleManagementService() {
        // Parameterless constructor required for transactions...
    }

    public String enqueueSetTariffSchedule(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            @NotNull @Size(min = 1, max = 50) @Valid final List<ScheduleEntry> mapAsList, final DateTime scheduledTime)
                    throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_TARIFF_SCHEDULE);
        this.domainHelperService.isInMaintenance(device);

        LOGGER.debug("enqueueSetTariffSchedule called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final Schedule schedule = new Schedule(mapAsList);

        final TariffSwitchingRequestMessage message = new TariffSwitchingRequestMessage(
                TariffSwitchingRequestMessageType.SET_TARIFF_SCHEDULE, correlationUid, organisationIdentification,
                deviceIdentification, schedule, scheduledTime);

        this.tariffSwitchingRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueSetTariffScheduleResponse(final String correlationUid) throws OsgpException {

        return this.tariffSwitchingResponseMessageFinder.findMessage(correlationUid);
    }

}
