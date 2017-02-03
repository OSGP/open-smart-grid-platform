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

import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeFirmwareResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetDeviceFirmwareHistoryRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetDeviceFirmwareHistoryResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareResponse;
import com.alliander.osgp.platform.cucumber.support.ws.BaseClient;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceSecurityException;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceTemplateFactory;

@Component
public class CoreFirmwareManagementClient extends BaseClient {

    @Autowired
    private WebServiceTemplateFactory coreFirmwareManagementWstf;

    public GetFirmwareVersionAsyncResponse getFirmwareVersion(final GetFirmwareVersionRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreFirmwareManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (GetFirmwareVersionAsyncResponse) wst.marshalSendAndReceive(request);
    }

    public GetFirmwareVersionResponse getGetFirmwareVersion(final GetFirmwareVersionAsyncRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreFirmwareManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (GetFirmwareVersionResponse) wst.marshalSendAndReceive(request);
    }

    public UpdateFirmwareAsyncResponse updateFirmware(final UpdateFirmwareRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreFirmwareManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (UpdateFirmwareAsyncResponse) wst.marshalSendAndReceive(request);
    }

    public UpdateFirmwareResponse getUpdateFirmware(final UpdateFirmwareAsyncRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreFirmwareManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (UpdateFirmwareResponse) wst.marshalSendAndReceive(request);
    }

    public ChangeFirmwareResponse changeFirmware(final ChangeFirmwareRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreFirmwareManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (ChangeFirmwareResponse) wst.marshalSendAndReceive(request);
    }

    public GetDeviceFirmwareHistoryResponse getDeviceFirmwareHistory(final GetDeviceFirmwareHistoryRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreFirmwareManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (GetDeviceFirmwareHistoryResponse) wst.marshalSendAndReceive(request);
    }
}
