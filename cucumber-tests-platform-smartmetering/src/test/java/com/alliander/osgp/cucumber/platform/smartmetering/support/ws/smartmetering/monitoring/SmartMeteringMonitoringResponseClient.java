/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.SmartMeteringBaseClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class SmartMeteringMonitoringResponseClient<T, V> extends SmartMeteringBaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory smartMeteringMonitoringWstf;

    private WebServiceTemplate getTemplate() throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return this.smartMeteringMonitoringWstf.getTemplate(this.getOrganizationIdentification(), this.getUserName());
    }

    /**
     * This method could be a little better, if V could extend AsyncRequest. But
     * ReadAlarmRegisterAsyncRequest is not an AsyncRequest, so this extension
     * is impossible. Therefore, the correlationUid is a needed argument in this
     * method.
     */
    public T getResponse(final V request, final String correlationUid)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        this.waitForDlmsResponseData(correlationUid);
        return (T) this.getTemplate().marshalSendAndReceive(request);
    }

}
