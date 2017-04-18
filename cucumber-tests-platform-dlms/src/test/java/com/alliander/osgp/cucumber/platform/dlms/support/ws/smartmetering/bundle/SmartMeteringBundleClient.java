/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.bundle;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.SmartMeteringBaseClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class SmartMeteringBundleClient extends SmartMeteringBaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory smartMeteringBundleWstf;

    public BundleAsyncResponse sendBundleRequest(final BundleRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (BundleAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    public BundleResponse retrieveBundleResponse(final BundleAsyncRequest asyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForDlmsResponseData(correlationUid);

        return (BundleResponse) this.getTemplate().marshalSendAndReceive(asyncRequest);
    }

    private WebServiceTemplate getTemplate() throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return this.smartMeteringBundleWstf.getTemplate(this.getOrganizationIdentification(), this.getUserName());
    }
}
