/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.microgrids.application.services;

import java.util.Optional;
import org.opensmartgridplatform.adapter.domain.microgrids.application.mapping.DomainMicrogridsMapper;
import org.opensmartgridplatform.adapter.domain.microgrids.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.microgrids.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BaseService {

  @Autowired protected DeviceDomainService deviceDomainService;

  @Autowired protected OrganisationDomainService organisationDomainService;

  @Autowired protected RtuDeviceRepository rtuDeviceRepository;

  @Autowired
  @Qualifier(value = "domainMicrogridsOutboundOsgpCoreRequestsMessageSender")
  protected OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

  @Autowired protected DomainMicrogridsMapper domainCoreMapper;

  @Autowired
  @Qualifier(value = "domainMicrogridsOutboundWebServiceResponsesMessageSender")
  protected WebServiceResponseMessageSender webServiceResponseMessageSender;

  protected Device findActiveDevice(final String deviceIdentification) throws FunctionalException {
    return this.deviceDomainService.searchActiveDevice(
        deviceIdentification, ComponentType.DOMAIN_MICROGRIDS);
  }

  protected Organisation findOrganisation(final String organisationIdentification)
      throws FunctionalException {
    final Organisation organisation;
    try {
      organisation = this.organisationDomainService.searchOrganisation(organisationIdentification);
    } catch (final UnknownEntityException e) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_ORGANISATION, ComponentType.DOMAIN_MICROGRIDS, e);
    }
    return organisation;
  }

  protected Optional<RtuDevice> findRtuDeviceForDevice(final Device device) {
    return this.rtuDeviceRepository.findById(device.getId());
  }

  protected OsgpException ensureOsgpException(final Throwable t, final String defaultMessage) {
    if (t instanceof OsgpException) {
      return (OsgpException) t;
    }

    return new TechnicalException(ComponentType.DOMAIN_MICROGRIDS, defaultMessage, t);
  }
}
