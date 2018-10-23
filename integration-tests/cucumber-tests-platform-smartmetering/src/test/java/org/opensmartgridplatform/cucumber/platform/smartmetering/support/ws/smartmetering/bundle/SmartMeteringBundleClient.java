/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.SmartMeteringBaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class SmartMeteringBundleClient extends SmartMeteringBaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory smartMeteringBundleWebServiceTemplateFactory;

    public BundleAsyncResponse sendBundleRequest(final BundleRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (BundleAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    public BundleResponse retrieveBundleResponse(final BundleAsyncRequest asyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (BundleResponse) this.getTemplate().marshalSendAndReceive(asyncRequest);
    }

    private WebServiceTemplate getTemplate() throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return this.smartMeteringBundleWebServiceTemplateFactory.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
    }
}
