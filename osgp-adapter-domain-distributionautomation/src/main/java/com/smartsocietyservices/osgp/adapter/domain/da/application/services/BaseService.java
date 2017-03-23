/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.smartsocietyservices.osgp.adapter.domain.da.application.services;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.InactiveDeviceException;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.exceptions.UnregisteredDeviceException;
import com.alliander.osgp.domain.core.services.DeviceDomainService;
import com.alliander.osgp.domain.core.services.OrganisationDomainService;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.smartsocietyservices.osgp.adapter.domain.da.infra.jms.core.OsgpCoreRequestMessageSender;
import com.smartsocietyservices.osgp.adapter.domain.da.infra.jms.ws.WebServiceResponseMessageSender;
import com.smartsocietyservices.osgp.domain.da.entities.RtuDevice;
import com.smartsocietyservices.osgp.domain.da.repositories.RtuDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BaseService {

    @Autowired
    protected DeviceDomainService deviceDomainService;

    @Autowired
    protected OrganisationDomainService organisationDomainService;

    @Autowired
    protected RtuDeviceRepository rtuDeviceRepository;

    @Autowired
    @Qualifier(value = "domainDistributionAutomationOutgoingOsgpCoreRequestMessageSender")
    protected OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Autowired
    protected com.smartsocietyservices.osgp.adapter.domain.da.application.mapping.DomainDistributionAutomationMapper domainCoreMapper;

    @Autowired
    @Qualifier(value = "domainDistributionAutomationOutgoingWebServiceResponseMessageSender")
    protected WebServiceResponseMessageSender webServiceResponseMessageSender;

    protected Device findActiveDevice(final String deviceIdentification) throws FunctionalException {
        Device device;
        try {
            device = this.deviceDomainService.searchActiveDevice(deviceIdentification);
        } catch (final UnregisteredDeviceException e) {
            throw new FunctionalException(FunctionalExceptionType.UNREGISTERED_DEVICE, ComponentType.DOMAIN_MICROGRIDS,
                    e);
        } catch (final InactiveDeviceException e) {
            throw new FunctionalException(FunctionalExceptionType.INACTIVE_DEVICE, ComponentType.DOMAIN_MICROGRIDS, e);
        } catch (final UnknownEntityException e) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, ComponentType.DOMAIN_MICROGRIDS, e);
        }
        return device;
    }

    protected Organisation findOrganisation(final String organisationIdentification) throws FunctionalException {
        Organisation organisation;
        try {
            organisation = this.organisationDomainService.searchOrganisation(organisationIdentification);
        } catch (final UnknownEntityException e) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_ORGANISATION, ComponentType.DOMAIN_MICROGRIDS,
                    e);
        }
        return organisation;
    }

    protected RtuDevice findRtuDeviceForDevice(final Device device) {
        return this.rtuDeviceRepository.findById(device.getId());
    }

    protected OsgpException ensureOsgpException(final Throwable t, final String defaultMessage) {
        if (t instanceof OsgpException) {
            return (OsgpException) t;
        }

        return new TechnicalException(ComponentType.DOMAIN_MICROGRIDS, defaultMessage, t);
    }
}
