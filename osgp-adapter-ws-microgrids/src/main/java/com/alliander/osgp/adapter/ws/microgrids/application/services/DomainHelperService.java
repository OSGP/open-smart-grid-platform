/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.microgrids.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.NotAuthorizedException;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.services.SecurityService;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunction;
import com.alliander.osgp.domain.microgrids.entities.RtuDevice;
import com.alliander.osgp.domain.microgrids.repositories.RtuDeviceRepository;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

@Service
public class DomainHelperService {

    private static final ComponentType COMPONENT_TYPE = ComponentType.WS_MICROGRIDS;

    @Autowired
    private RtuDeviceRepository rtuDeviceRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private SecurityService securityService;

    public RtuDevice findDevice(final String deviceIdentification) throws FunctionalException {
        final RtuDevice device = this.rtuDeviceRepository.findByDeviceIdentification(deviceIdentification);
        if (device == null) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, COMPONENT_TYPE,
                    new UnknownEntityException(RtuDevice.class, deviceIdentification));
        }
        return device;
    }

    public Organisation findOrganisation(final String organisationIdentification) throws FunctionalException {
        final Organisation organisation = this.organisationRepository
                .findByOrganisationIdentification(organisationIdentification);
        if (organisation == null) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_ORGANISATION, COMPONENT_TYPE,
                    new UnknownEntityException(Organisation.class, organisationIdentification));
        }
        return organisation;
    }

    public void isAllowed(final Organisation organisation, final PlatformFunction platformFunction)
            throws FunctionalException {
        try {
            this.securityService.checkAuthorization(organisation, platformFunction);
        } catch (final NotAuthorizedException e) {
            throw new FunctionalException(FunctionalExceptionType.UNAUTHORIZED, COMPONENT_TYPE, e);
        }
    }

    public void isAllowed(final Organisation organisation, final Device device, final DeviceFunction deviceFunction)
            throws FunctionalException {
        try {
            this.securityService.checkAuthorization(organisation, device, deviceFunction);
        } catch (final NotAuthorizedException e) {
            throw new FunctionalException(FunctionalExceptionType.UNAUTHORIZED, COMPONENT_TYPE, e);
        }
    }
}
