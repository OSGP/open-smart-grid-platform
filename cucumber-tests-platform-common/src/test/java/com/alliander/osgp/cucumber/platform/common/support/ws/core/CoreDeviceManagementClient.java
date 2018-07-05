/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.common.support.ws.core;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindDevicesRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindDevicesResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindEventsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindEventsResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindOrganisationResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindScheduledTasksRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindScheduledTasksResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsResponse;
import com.alliander.osgp.cucumber.platform.support.ws.BaseClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class CoreDeviceManagementClient extends BaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory coreDeviceManagementWstf;

    public FindDevicesResponse findDevices(final FindDevicesRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (FindDevicesResponse) wst.marshalSendAndReceive(request);
    }

    public FindEventsResponse findEventsResponse(final FindEventsRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (FindEventsResponse) wst.marshalSendAndReceive(request);
    }

    public SetEventNotificationsResponse getSetEventNotificationsResponse(
            final SetEventNotificationsAsyncRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (SetEventNotificationsResponse) wst.marshalSendAndReceive(request);
    }

    public SetEventNotificationsAsyncResponse setEventNotifications(final SetEventNotificationsRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (SetEventNotificationsAsyncResponse) wst.marshalSendAndReceive(request);
    }

    public SetOwnerResponse setOwner(final SetOwnerRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (SetOwnerResponse) wst.marshalSendAndReceive(request);
    }

    public FindOrganisationResponse findOrganization(final FindOrganisationRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.coreDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (FindOrganisationResponse) wst.marshalSendAndReceive(request);
    }

    public FindOrganisationResponse findOrganization(final String organizationIdentification,
            final FindOrganisationRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.coreDeviceManagementWstf.getTemplate(organizationIdentification,
                this.getUserName());
        return (FindOrganisationResponse) wst.marshalSendAndReceive(request);
    }

    public FindAllOrganisationsResponse findAllOrganizations(final FindAllOrganisationsRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (FindAllOrganisationsResponse) wst.marshalSendAndReceive(request);
    }

    public FindAllOrganisationsResponse findAllOrganizations(final String organizationIdentification,
            final FindAllOrganisationsRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceManagementWstf.getTemplate(organizationIdentification,
                this.getUserName());
        return (FindAllOrganisationsResponse) wst.marshalSendAndReceive(request);
    }

    public FindScheduledTasksResponse findScheduledTasks(final FindScheduledTasksRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.coreDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (FindScheduledTasksResponse) wst.marshalSendAndReceive(request);
    }

    public SetDeviceLifecycleStatusAsyncResponse setDeviceLifecycleStatus(final SetDeviceLifecycleStatusRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.coreDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (SetDeviceLifecycleStatusAsyncResponse) wst.marshalSendAndReceive(request);
    }

    public SetDeviceLifecycleStatusResponse getSetDeviceLifecycleStatusResponse(
            final SetDeviceLifecycleStatusAsyncRequest asyncRequest)
            throws WebServiceSecurityException, InterruptedException {
        final WebServiceTemplate wst = this.coreDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (SetDeviceLifecycleStatusResponse) wst.marshalSendAndReceive(asyncRequest);
    }
}
