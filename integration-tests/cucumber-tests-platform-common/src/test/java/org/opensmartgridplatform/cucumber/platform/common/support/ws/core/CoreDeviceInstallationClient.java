/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.support.ws.core;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.AddDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.AddDeviceResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.GetStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.GetStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.GetStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.GetStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StartDeviceTestRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StartDeviceTestResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StopDeviceTestAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StopDeviceTestAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StopDeviceTestRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StopDeviceTestResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.UpdateDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.UpdateDeviceResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.support.ws.BaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class CoreDeviceInstallationClient extends BaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory coreDeviceInstallationWstf;

    public AddDeviceResponse addDevice(final AddDeviceRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        String organizationIdentification = (String) ScenarioContext.current()
                .get(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION);
        if (organizationIdentification == null) {
            organizationIdentification = PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION;
        }
        return this.addDevice(request, organizationIdentification);
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

    public FindRecentDevicesResponse findRecentDevices(final FindRecentDevicesRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceInstallationWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (FindRecentDevicesResponse) wst.marshalSendAndReceive(request);
    }

    public StartDeviceTestAsyncResponse startDeviceTest(final StartDeviceTestRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return this.startDeviceTest(request, "test-org");
    }

    public StartDeviceTestAsyncResponse startDeviceTest(final StartDeviceTestRequest request,
            final String organizationIdentification)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreDeviceInstallationWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (StartDeviceTestAsyncResponse) wst.marshalSendAndReceive(request);
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
