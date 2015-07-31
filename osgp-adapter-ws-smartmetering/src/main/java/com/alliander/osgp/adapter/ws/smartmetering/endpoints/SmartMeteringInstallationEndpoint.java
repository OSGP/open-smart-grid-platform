/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.endpoints;

import org.hibernate.validator.method.MethodConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddMeterRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddMeterResponse;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.InstallationService;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

// MethodConstraintViolationException is deprecated.
// Will by replaced by equivalent functionality defined
// by the Bean Validation 1.1 API as of Hibernate Validator 5.
@SuppressWarnings("deprecation")
@Endpoint
public class SmartMeteringInstallationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringInstallationEndpoint.class);
    private static final String SMARTMETER_INSTALLATION_NAMESPACE = "http://www.alliander.com/schemas/osgp/smartmetering/sm-installation/2014/10";

    @Autowired
    private InstallationService installationService;

    // private InstallationMapper installationMapper;

    public SmartMeteringInstallationEndpoint() {
    }

    // @Autowired
    // public SmartMeteringInstallationEndpoint(@Qualifier(value =
    // "wsCoreDeviceInstallationService") final DeviceInstallationService
    // deviceInstallationService,
    // @Qualifier(value = "coreDeviceInstallationMapper") final
    // DeviceInstallationMapper deviceInstallationMapper) {
    // this.deviceInstallationService = deviceInstallationService;
    // this.deviceInstallationMapper = deviceInstallationMapper;
    // }

    @PayloadRoot(localPart = "AddMeterRequest", namespace = SMARTMETER_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public AddMeterResponse addDevice(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final AddMeterRequest request) throws OsgpException {

        LOGGER.info("Incoming AddMeterRequest for meter: {}.", request.getMeter().getIdentificationNumber());

        try {
            // final Device device =
            // this.installationMapper.map(request.getDevice(),
            // Device.class);

            // Add a mapper as soon as the structure is set
            // we'll use identificationNumber as deviceIdentification for now

            final Device device = new Device(request.getMeter().getIdentificationNumber());

            this.installationService.addDevice(organisationIdentification, device);
        } catch (final MethodConstraintViolationException e) {

            LOGGER.error("Exception: {} while adding device: {} for organisation {}.", new Object[] { e.getMessage(),
                    request.getMeter().getIdentificationNumber(), organisationIdentification }, e);

            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));

        } catch (final Exception e) {

            LOGGER.error("Exception: {} while adding device: {} for organisation {}.", new Object[] { e.getMessage(),
                    request.getMeter().getIdentificationNumber(), organisationIdentification }, e);

            this.handleException(e);
        }

        return new AddMeterResponse();
    }

    private void handleException(final Exception e) throws OsgpException {
        // Rethrow exception if it already is a functional or technical
        // exception,
        // otherwise throw new technical exception.
        if (e instanceof OsgpException) {
            LOGGER.error("Exception occurred: ", e);
            throw (OsgpException) e;
        } else {
            LOGGER.error("Exception occurred: ", e);
            throw new TechnicalException(ComponentType.WS_SMART_METERING, e);
        }
    }
}
