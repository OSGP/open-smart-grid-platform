/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageType;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

@Service(value = "wsSmartMeteringManagementService")
@Transactional(value = "transactionManager")
@Validated
public class ManagementService {

    // TODO refactor

    private static final int PAGE_SIZE = 30;

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

    public ManagementService() {
        // Parameterless constructor required for transactions
    }

    public Page<Device> findAllDevices(@Identification final String organisationIdentification, final int pageNumber)
            throws FunctionalException {

        LOGGER.debug("findAllDevices called with organisation {} and pageNumber {}", organisationIdentification,
                pageNumber);

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

        final PageRequest request = new PageRequest(pageNumber, PAGE_SIZE, Sort.Direction.DESC, "deviceIdentification");
        return this.deviceRepository.findAllAuthorized(organisation, request);
    }

    public String enqueueGetSmartMeterStatusRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_STATUS);

        LOGGER.debug("enqueueGetSmartMeterStatusRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.SM_REQUEST1, correlationUid, organisationIdentification,
                deviceIdentification, null);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

}