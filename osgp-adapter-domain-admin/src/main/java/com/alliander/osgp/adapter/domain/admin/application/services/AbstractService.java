package com.alliander.osgp.adapter.domain.admin.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alliander.osgp.adapter.domain.admin.infra.jms.core.OsgpCoreRequestMessageSender;
import com.alliander.osgp.adapter.domain.admin.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.services.DeviceDomainService;
import com.alliander.osgp.domain.core.services.OrganisationDomainService;

public class AbstractService {

    @Autowired
    protected DeviceDomainService deviceDomainService;

    @Autowired
    protected OrganisationDomainService organisationDomainService;

    @Autowired
    @Qualifier("domainAdminOutgoingOsgpCoreRequestMessageSender")
    protected OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Autowired
    @Qualifier("domainAdminOutgoingWebServiceResponseMessageSender")
    protected WebServiceResponseMessageSender webServiceResponseMessageSender;
}
