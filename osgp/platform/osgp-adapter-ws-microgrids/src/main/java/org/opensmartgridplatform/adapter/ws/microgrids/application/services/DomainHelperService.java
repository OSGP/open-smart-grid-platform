//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.microgrids.application.services;

import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.exceptions.NotAuthorizedException;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.opensmartgridplatform.domain.core.services.SecurityService;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunction;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainHelperService {

  private static final ComponentType COMPONENT_TYPE = ComponentType.WS_MICROGRIDS;

  @Autowired private RtuDeviceRepository rtuDeviceRepository;

  @Autowired private OrganisationRepository organisationRepository;

  @Autowired private SecurityService securityService;

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

  public void isAllowed(final Organisation organisation, final PlatformFunction platformFunction)
      throws FunctionalException {
    try {
      this.securityService.checkAuthorization(organisation, platformFunction);
    } catch (final NotAuthorizedException e) {
      throw new FunctionalException(FunctionalExceptionType.UNAUTHORIZED, COMPONENT_TYPE, e);
    }
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
