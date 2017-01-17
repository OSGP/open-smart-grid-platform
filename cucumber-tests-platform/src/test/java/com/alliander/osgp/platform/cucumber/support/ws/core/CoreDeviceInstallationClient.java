/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support.ws.core;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.AddDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.AddDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StopDeviceTestAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StopDeviceTestAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StopDeviceTestRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StopDeviceTestResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.UpdateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.UpdateDeviceResponse;
import com.alliander.osgp.platform.cucumber.support.ws.BaseClient;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceSecurityException;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceTemplateFactory;

@Component
public class CoreDeviceInstallationClient extends BaseClient {

    @Autowired
    private WebServiceTemplateFactory coreDeviceInstallationWstf;

    public AddDeviceResponse addDevice(final AddDeviceRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceInstallationWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (AddDeviceResponse) wst.marshalSendAndReceive(request);
    }

    public AddDeviceResponse addDevice(final AddDeviceRequest request, final String organizationIdentification)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceInstallationWstf.getTemplate(organizationIdentification,
                this.getUserName());
        return (AddDeviceResponse) wst.marshalSendAndReceive(request);
    }

    public UpdateDeviceResponse updateDevice(final UpdateDeviceRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceInstallationWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (UpdateDeviceResponse) wst.marshalSendAndReceive(request);
    }

    public StartDeviceTestAsyncResponse startDeviceTest(final StartDeviceTestRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceInstallationWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (StartDeviceTestAsyncResponse) wst.marshalSendAndReceive(request);
    }

    public FindRecentDevicesResponse findRecentDevices(final FindRecentDevicesRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceInstallationWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (FindRecentDevicesResponse) wst.marshalSendAndReceive(request);
    }

    public StartDeviceTestResponse getStartDeviceTestResponse(final StartDeviceTestAsyncRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceInstallationWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());

        return (StartDeviceTestResponse) wst.marshalSendAndReceive(request);
    }

    public StopDeviceTestAsyncResponse stopDeviceTest(final StopDeviceTestRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceInstallationWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (StopDeviceTestAsyncResponse) wst.marshalSendAndReceive(request);
    }

    public StopDeviceTestResponse getStopDeviceTestResponse(final StopDeviceTestAsyncRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceInstallationWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (StopDeviceTestResponse) wst.marshalSendAndReceive(request);
    }

    public GetStatusAsyncResponse getStatus(final GetStatusRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceInstallationWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (GetStatusAsyncResponse) wst.marshalSendAndReceive(request);
    }

    public GetStatusResponse getStatusResponse(final GetStatusAsyncRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceInstallationWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (GetStatusResponse) wst.marshalSendAndReceive(request);
    }
}
