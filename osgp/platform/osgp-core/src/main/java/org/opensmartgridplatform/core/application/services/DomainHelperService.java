// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.services;

import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.NotAuthorizedException;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.domain.core.services.SecurityService;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunction;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "osgpCoreDomainHelperService")
public class DomainHelperService {

  private static ComponentType COMPONENT_TYPE = ComponentType.OSGP_CORE;

  @Autowired private DeviceDomainService deviceDomainService;

  @Autowired private OrganisationDomainService organisationDomainService;

  @Autowired private SecurityService securityService;

  public Device findDevice(final String deviceIdentification) throws FunctionalException {
    return this.deviceDomainService.searchDevice(deviceIdentification);
  }

  public Device findActiveDevice(final String deviceIdentification) throws FunctionalException {
    return this.deviceDomainService.searchActiveDevice(deviceIdentification, COMPONENT_TYPE);
  }

  public Organisation findOrganisation(final String organisationIdentification)
      throws FunctionalException {
    Organisation organisation;
    try {
      organisation = this.organisationDomainService.searchOrganisation(organisationIdentification);
    } catch (final UnknownEntityException e) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_ORGANISATION, COMPONENT_TYPE, e);
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
