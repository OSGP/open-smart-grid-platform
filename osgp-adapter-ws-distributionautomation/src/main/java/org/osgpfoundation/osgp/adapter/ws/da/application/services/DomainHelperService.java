/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgpfoundation.osgp.adapter.ws.da.application.services;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.NotAuthorizedException;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.services.SecurityService;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import org.osgpfoundation.osgp.domain.da.entities.RtuDevice;
import org.osgpfoundation.osgp.domain.da.repositories.RtuDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainHelperService {

    private static final ComponentType COMPONENT_TYPE = ComponentType.WS_DISTRIBUTION_AUTOMATION;

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
        final Organisation organisation = this.organisationRepository.findByOrganisationIdentification(organisationIdentification);
        if (organisation == null) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_ORGANISATION, COMPONENT_TYPE,
                    new UnknownEntityException(Organisation.class, organisationIdentification));
        }
        return organisation;
    }

    public void isAllowed(final Organisation organisation, final Device device, final DeviceFunction deviceFunction) throws FunctionalException {
        try {
            this.securityService.checkAuthorization(organisation, device, deviceFunction);
        } catch (final NotAuthorizedException e) {
            throw new FunctionalException(FunctionalExceptionType.UNAUTHORIZED, COMPONENT_TYPE, e);
        }
    }
}
