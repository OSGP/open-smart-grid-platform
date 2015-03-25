package com.alliander.osgp.adapter.domain.tariffswitching.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alliander.osgp.adapter.domain.tariffswitching.application.mapping.DomainTariffSwitchingMapper;
import com.alliander.osgp.adapter.domain.tariffswitching.infra.jms.core.OsgpCoreRequestMessageSender;
import com.alliander.osgp.adapter.domain.tariffswitching.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.exceptions.UnregisteredDeviceException;
import com.alliander.osgp.domain.core.services.DeviceDomainService;
import com.alliander.osgp.domain.core.services.OrganisationDomainService;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

public class AbstractService {

    @Autowired
    protected DeviceDomainService deviceDomainService;

    @Autowired
    protected OrganisationDomainService organisationDomainService;

    @Autowired
    @Qualifier("domainTariffSwitchingOutgoingOsgpCoreRequestMessageSender")
    protected OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Autowired
    protected DomainTariffSwitchingMapper domainCoreMapper;

    @Autowired
    @Qualifier("domainTariffSwitchingOutgoingWebServiceResponseMessageSender")
    protected WebServiceResponseMessageSender webServiceResponseMessageSender;

    protected Device findActiveDevice(final String deviceIdentification) throws FunctionalException {
        Device device;
        try {
            device = this.deviceDomainService.searchActiveDevice(deviceIdentification);
        } catch (final UnregisteredDeviceException e) {
            throw new FunctionalException(FunctionalExceptionType.UNREGISTERED_DEVICE, ComponentType.DOMAIN_TARIFF_SWITCHING, e);
        } catch (final UnknownEntityException e) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, ComponentType.DOMAIN_TARIFF_SWITCHING, e);
        }
        return device;
    }

    protected Organisation findOrganisation(final String organisationIdentification) throws FunctionalException {
        Organisation organisation;
        try {
            organisation = this.organisationDomainService.searchOrganisation(organisationIdentification);
        } catch (final UnknownEntityException e) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_ORGANISATION, ComponentType.DOMAIN_TARIFF_SWITCHING, e);
        }
        return organisation;
    }
}
