//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.da.application.services;

import java.util.UUID;
import org.opensmartgridplatform.adapter.domain.da.application.mapping.DomainDistributionAutomationMapper;
import org.opensmartgridplatform.adapter.domain.da.application.routing.ResponseMessageRouter;
import org.opensmartgridplatform.adapter.domain.da.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
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

  @Autowired
  @Qualifier(value = "domainDistributionAutomationOutboundOsgpCoreRequestsMessageSender")
  protected OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

  @Autowired protected DomainDistributionAutomationMapper domainCoreMapper;

  @Autowired protected ResponseMessageRouter responseMessageRouter;

  protected Device findActiveDevice(final String deviceIdentification) throws FunctionalException {
    return this.deviceDomainService.searchActiveDevice(
        deviceIdentification, ComponentType.DOMAIN_DISTRIBUTION_AUTOMATION);
  }

  protected Organisation findOrganisation(final String organisationIdentification)
      throws FunctionalException {
    final Organisation organisation;
    try {
      organisation = this.organisationDomainService.searchOrganisation(organisationIdentification);
    } catch (final UnknownEntityException e) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_ORGANISATION,
          ComponentType.DOMAIN_DISTRIBUTION_AUTOMATION,
          e);
    }
    return organisation;
  }

  protected OsgpException ensureOsgpException(final Throwable t, final String defaultMessage) {
    if (t instanceof OsgpException) {
      return (OsgpException) t;
    }

    return new TechnicalException(ComponentType.DOMAIN_DISTRIBUTION_AUTOMATION, defaultMessage, t);
  }

  protected static String getCorrelationId(
      final String organisationIdentification, final String deviceIdentification) {

    return organisationIdentification
        + "|||"
        + deviceIdentification
        + "|||"
        + UUID.randomUUID().toString();
  }
}
