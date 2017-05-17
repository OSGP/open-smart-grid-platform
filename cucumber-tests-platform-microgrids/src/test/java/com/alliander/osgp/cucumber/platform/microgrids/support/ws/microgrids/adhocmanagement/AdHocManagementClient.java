/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.support.ws.microgrids.adhocmanagement;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.microgrids.domain.repositories.RtuResponseDataRepository;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataResponse;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetDataAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetDataRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetDataResponse;
import com.alliander.osgp.cucumber.platform.support.ws.BaseClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class AdHocManagementClient extends BaseClient {

    @Autowired
    @Qualifier("webServiceTemplateFactoryMicrogridsAdHocManagement")
    private DefaultWebServiceTemplateFactory webServiceTemplateFactoryMicrogridsAdHocManagement;

    @Autowired
    private RtuResponseDataRepository rtuResponseDataRepository;

    @Value("${iec61850.rtu.response.wait.check.interval:1000}")
    private int waitCheckIntervalMillis;
    @Value("${iec61850.rtu.response.wait.fail.duration:15000}")
    private int waitFailMillis;

    public GetDataAsyncResponse getDataAsync(final GetDataRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate webServiceTemplate = this.webServiceTemplateFactoryMicrogridsAdHocManagement
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (GetDataAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public SetDataAsyncResponse setDataAsync(final SetDataRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate webServiceTemplate = this.webServiceTemplateFactoryMicrogridsAdHocManagement
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (SetDataAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public GetDataResponse getData(final GetDataAsyncRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = request.getAsyncRequest().getCorrelationUid();
        this.waitForRtuResponseData(correlationUid);

        final WebServiceTemplate webServiceTemplate = this.webServiceTemplateFactoryMicrogridsAdHocManagement
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (GetDataResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public SetDataResponse setData(final SetDataAsyncRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = request.getAsyncRequest().getCorrelationUid();
        this.waitForRtuResponseData(correlationUid);

        final WebServiceTemplate webServiceTemplate = this.webServiceTemplateFactoryMicrogridsAdHocManagement
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (SetDataResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    private void waitForRtuResponseData(final String correlationUid) {
        try {
            for (int timeSpentWaiting = 0; timeSpentWaiting < this.waitFailMillis; timeSpentWaiting += this.waitCheckIntervalMillis) {
                Thread.sleep(this.waitCheckIntervalMillis);
                if (this.rtuResponseDataRepository.findSingleResultByCorrelationUid(correlationUid) != null) {
                    return;
                }
            }
            throw new AssertionError("RtuResponseData not available within " + this.waitFailMillis + " milliseconds.");
        } catch (final InterruptedException e) {
            throw new AssertionError("Waiting for RtuResponseData was interrupted.", e);
        }
    }
}
