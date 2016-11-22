/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support.ws.microgrids.adhocmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.microgrids.domain.repositories.RtuResponseDataRepository;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataRequest;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.GetDataResponse;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceSecurityException;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceTemplateFactory;

@Component
public class AdHocManagementServiceAdapter {

    @Autowired
    private WebServiceTemplateFactory webServiceTemplateFactoryMicrogridsAdHocManagement;

    @Autowired
    private RtuResponseDataRepository rtuResponseDataRepository;

    @Value("${iec61850.rtu.response.wait.check.interval:1000}")
    private int waitCheckIntervalMillis;
    @Value("${iec61850.rtu.response.wait.fail.duration:15000}")
    private int waitFailMillis;

    public GetDataAsyncResponse getDataAsync(final GetDataRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate webServiceTemplate = this.webServiceTemplateFactoryMicrogridsAdHocManagement
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (GetDataAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public GetDataResponse getData(final GetDataAsyncRequest request) throws WebServiceSecurityException {

        final String correlationUid = request.getAsyncRequest().getCorrelationUid();
        this.waitForRtuResponseData(correlationUid);

        final WebServiceTemplate webServiceTemplate = this.webServiceTemplateFactoryMicrogridsAdHocManagement
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (GetDataResponse) webServiceTemplate.marshalSendAndReceive(request);
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

    private String getOrganizationIdentification() {
        final String organizationIdentification = (String) ScenarioContext.Current()
                .get(Keys.KEY_ORGANIZATION_IDENTIFICATION);
        if (organizationIdentification == null) {
            throw new AssertionError("ScenarioContext must contain the organization identification for key \""
                    + Keys.KEY_ORGANIZATION_IDENTIFICATION + "\" before calling a web service.");
        }
        return organizationIdentification;
    }

    private String getUserName() {
        final String userName = (String) ScenarioContext.Current().get(Keys.KEY_USER_NAME);
        if (userName == null) {
            throw new AssertionError("ScenarioContext must contain the user name for key \"" + Keys.KEY_USER_NAME
                    + "\" before calling a web service.");
        }
        return userName;
    }
}
