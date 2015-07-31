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
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageType;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

/**
 * @author OSGP
 *
 */
@Service(value = "wsSmartMeteringInstallationService")
// @Transactional(value = "transactionManager")
@Validated
public class InstallationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstallationService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

    // public InstallationService() {
    // // Parameterless constructor required for transactions
    // }

    public String enqueueAddSmartMeterRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification) throws FunctionalException {

        // TODO: bypassing authorization logic for now, needs to be fixed.

        // final Organisation organisation =
        // this.domainHelperService.findOrganisation(organisationIdentification);
        // final Device device =
        // this.domainHelperService.findActiveDevice(deviceIdentification);
        //
        // this.domainHelperService.isAllowed(organisation, device,
        // DeviceFunction.GET_STATUS);

        LOGGER.debug("enqueueAddSmartMeterRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.ADD_METER, correlationUid, organisationIdentification,
                deviceIdentification, null, null);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    /**
     * @param organisationIdentification
     * @param device
     * @throws FunctionalException
     */
    public String addDevice(final String organisationIdentification, final Device device) throws FunctionalException {
        return this.enqueueAddSmartMeterRequest(organisationIdentification, device.getDeviceIdentification());
    }

}
