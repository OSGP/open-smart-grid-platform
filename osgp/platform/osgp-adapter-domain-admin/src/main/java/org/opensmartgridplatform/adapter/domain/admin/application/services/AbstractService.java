/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.admin.application.services;

import org.opensmartgridplatform.adapter.domain.admin.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AbstractService {

  @Autowired protected DeviceDomainService deviceDomainService;

  @Autowired protected OrganisationDomainService organisationDomainService;

  @Autowired
  @Qualifier("domainAdminOutboundOsgpCoreRequestsMessageSender")
  protected OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

  @Autowired
  @Qualifier("domainAdminOutboundWebServiceResponsesMessageSender")
  protected ResponseMessageSender webServiceResponseMessageSender;
}
