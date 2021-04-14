/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.services;

import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.exceptions.NotAuthorizedException;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.opensmartgridplatform.domain.core.services.SecurityService;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainHelperService {

  private static final ComponentType COMPONENT_TYPE = ComponentType.KAFKA_DISTRIBUTION_AUTOMATION;

  private final RtuDeviceRepository rtuDeviceRepository;
  private final OrganisationRepository organisationRepository;
  private final SecurityService securityService;

  @Autowired
  public DomainHelperService(
      final RtuDeviceRepository rtuDeviceRepository,
      final OrganisationRepository organisationRepository,
      final SecurityService securityService) {
    this.rtuDeviceRepository = rtuDeviceRepository;
    this.organisationRepository = organisationRepository;
    this.securityService = securityService;
  }

  public RtuDevice findDevice(final String deviceIdentification) throws FunctionalException {
    return this.rtuDeviceRepository
        .findByDeviceIdentification(deviceIdentification)
        .orElseThrow(
            () ->
                new FunctionalException(
                    FunctionalExceptionType.UNKNOWN_DEVICE,
                    COMPONENT_TYPE,
                    new UnknownEntityException(RtuDevice.class, deviceIdentification)));
  }

  public Organisation findOrganisation(final String organisationIdentification)
      throws FunctionalException {
    final Organisation organisation =
        this.organisationRepository.findByOrganisationIdentification(organisationIdentification);
    if (organisation == null) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_ORGANISATION,
          COMPONENT_TYPE,
          new UnknownEntityException(Organisation.class, organisationIdentification));
    }
    return organisation;
  }

  public void isAllowed(
      final Organisation organisation, final Device device, final DeviceFunction deviceFunction)
      throws FunctionalException {
    try {
      this.securityService.checkAuthorization(organisation, device, deviceFunction);
    } catch (final NotAuthorizedException e) {
      throw new FunctionalException(FunctionalExceptionType.UNAUTHORIZED, COMPONENT_TYPE, e);
    }
  }
}
