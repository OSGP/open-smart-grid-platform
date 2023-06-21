// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.tariffswitching.application.services;

import org.opensmartgridplatform.adapter.domain.tariffswitching.application.mapping.DomainTariffSwitchingMapper;
import org.opensmartgridplatform.adapter.domain.tariffswitching.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.tariffswitching.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AbstractService {

  @Autowired protected DeviceDomainService deviceDomainService;

  @Autowired protected OrganisationDomainService organisationDomainService;

  @Autowired protected SsldRepository ssldRepository;

  @Autowired
  @Qualifier("domainTariffSwitchingOutboundOsgpCoreRequestsMessageSender")
  protected OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

  @Autowired protected DomainTariffSwitchingMapper domainCoreMapper;

  @Autowired
  @Qualifier("domainTariffSwitchingOutboundWebServiceResponsesMessageSender")
  protected WebServiceResponseMessageSender webServiceResponseMessageSender;

  protected Device findActiveDevice(final String deviceIdentification) throws FunctionalException {
    return this.deviceDomainService.searchActiveDevice(
        deviceIdentification, ComponentType.DOMAIN_TARIFF_SWITCHING);
  }

  protected Organisation findOrganisation(final String organisationIdentification)
      throws FunctionalException {
    Organisation organisation;
    try {
      organisation = this.organisationDomainService.searchOrganisation(organisationIdentification);
    } catch (final UnknownEntityException e) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_ORGANISATION, ComponentType.DOMAIN_TARIFF_SWITCHING, e);
    }
    return organisation;
  }

  protected Ssld getSsldForDevice(final Device device) {
    return this.ssldRepository.findById(device.getId()).orElse(null);
  }
}
