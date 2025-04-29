// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.NotAuthorizedException;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.domain.core.services.SecurityService;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.springframework.stereotype.Service;

@Service(value = "wsSmartMeteringDomainHelperService")
public class DomainHelperService {

  private static final ComponentType COMPONENT_TYPE = ComponentType.WS_SMART_METERING;

  private final DeviceDomainService deviceDomainService;
  private final OrganisationDomainService organisationDomainService;
  private final SecurityService securityService;

  public DomainHelperService(
      final DeviceDomainService deviceDomainService,
      final OrganisationDomainService organisationDomainService,
      final SecurityService securityService) {
    this.deviceDomainService = deviceDomainService;
    this.organisationDomainService = organisationDomainService;
    this.securityService = securityService;
  }

  Device findDevice(final String deviceIdentification) throws FunctionalException {
    return this.deviceDomainService.searchDevice(deviceIdentification);
  }

  Device findActiveDevice(final String deviceIdentification) throws FunctionalException {
    return this.deviceDomainService.searchActiveDevice(deviceIdentification, COMPONENT_TYPE);
  }

  Organisation findOrganisation(final String organisationIdentification)
      throws FunctionalException {
    final Organisation organisation;
    try {
      organisation = this.organisationDomainService.searchOrganisation(organisationIdentification);
    } catch (final UnknownEntityException e) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_ORGANISATION, COMPONENT_TYPE, e);
    }
    return organisation;
  }

  void checkAllowed(
      final Organisation organisation, final Device device, final DeviceFunction deviceFunction)
      throws FunctionalException {
    try {
      this.securityService.checkAuthorization(organisation, device, deviceFunction);
    } catch (final NotAuthorizedException e) {
      throw new FunctionalException(FunctionalExceptionType.UNAUTHORIZED, COMPONENT_TYPE, e);
    }
  }
}
