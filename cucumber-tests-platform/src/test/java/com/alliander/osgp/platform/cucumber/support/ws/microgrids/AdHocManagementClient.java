/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support.ws.microgrids;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.microgrids.domain.repositories.RtuResponseDataRepository;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataResponse;
import com.alliander.osgp.platform.cucumber.support.ws.BaseClient;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceSecurityException;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceTemplateFactory;

@Component
public class AdHocManagementClient extends BaseClient {

    @Autowired
    private WebServiceTemplateFactory webServiceTemplateFactoryMicrogridsAdHocManagement;

    public GetDataAsyncResponse getDataAsync(final GetDataRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate webServiceTemplate = this.webServiceTemplateFactoryMicrogridsAdHocManagement
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (GetDataAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public GetDataResponse getData(final GetDataAsyncRequest request) throws WebServiceSecurityException {

        final WebServiceTemplate webServiceTemplate = this.webServiceTemplateFactoryMicrogridsAdHocManagement
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (GetDataResponse) webServiceTemplate.marshalSendAndReceive(request);
    }
}
