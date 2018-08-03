/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.publiclighting;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionResponse;
import org.opensmartgridplatform.cucumber.platform.support.ws.BaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class PublicLightingAdHocManagementClient extends BaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory publicLightingAdHocManagementWstf;

    public GetStatusResponse getGetStatusResponse(final GetStatusAsyncRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate webServiceTemplate = this.publicLightingAdHocManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (GetStatusResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public ResumeScheduleResponse getResumeScheduleResponse(final ResumeScheduleAsyncRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate webServiceTemplate = this.publicLightingAdHocManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (ResumeScheduleResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public SetLightResponse getSetLightResponse(final SetLightAsyncRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate webServiceTemplate = this.publicLightingAdHocManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (SetLightResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public SetTransitionResponse getSetTransitionResponse(final SetTransitionAsyncRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate webServiceTemplate = this.publicLightingAdHocManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (SetTransitionResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public GetStatusAsyncResponse getStatus(final GetStatusRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate webServiceTemplate = this.publicLightingAdHocManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (GetStatusAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public ResumeScheduleAsyncResponse resumeSchedule(final ResumeScheduleRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate webServiceTemplate = this.publicLightingAdHocManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (ResumeScheduleAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public SetLightAsyncResponse setLight(final SetLightRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate webServiceTemplate = this.publicLightingAdHocManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (SetLightAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public SetTransitionAsyncResponse setTransition(final SetTransitionRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate webServiceTemplate = this.publicLightingAdHocManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (SetTransitionAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

}
