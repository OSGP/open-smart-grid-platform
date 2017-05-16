/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueResponse;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.SmartMeteringBaseClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class SmartMeteringAdHocClient extends SmartMeteringBaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory smartMeteringAdHocWstf;

    public GetAllAttributeValuesAsyncResponse sendGetAllAttributeValuesRequest(
            final GetAllAttributeValuesRequest request) throws WebServiceSecurityException {
        return (GetAllAttributeValuesAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    public GetAllAttributeValuesResponse retrieveGetAllAttributeValuesResponse(
            final GetAllAttributeValuesAsyncRequest asyncRequest) throws WebServiceSecurityException {
        this.waitForDlmsResponseData(asyncRequest.getCorrelationUid());
        return (GetAllAttributeValuesResponse) this.getTemplate().marshalSendAndReceive(asyncRequest);
    }

    public GetSpecificAttributeValueAsyncResponse sendGetSpecificAttributeValueRequest(
            final GetSpecificAttributeValueRequest request) throws WebServiceSecurityException {
        return (GetSpecificAttributeValueAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    public GetSpecificAttributeValueResponse retrieveGetSpecificAttributeValueResponse(
            final GetSpecificAttributeValueAsyncRequest asyncRequest) throws WebServiceSecurityException {
        this.waitForDlmsResponseData(asyncRequest.getCorrelationUid());
        return (GetSpecificAttributeValueResponse) this.getTemplate().marshalSendAndReceive(asyncRequest);
    }

    private WebServiceTemplate getTemplate() throws WebServiceSecurityException {
        return this.smartMeteringAdHocWstf.getTemplate(this.getOrganizationIdentification(), this.getUserName());
    }

}
