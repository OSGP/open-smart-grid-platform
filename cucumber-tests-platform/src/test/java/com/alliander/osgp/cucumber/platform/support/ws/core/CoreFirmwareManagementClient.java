/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.support.ws.core;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.AddDeviceModelRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.AddFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.AddFirmwareResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeDeviceModelRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeDeviceModelResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeFirmwareResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FindAllDeviceModelsRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FindAllDeviceModelsResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FindFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.RemoveDeviceModelRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.RemoveDeviceModelResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.RemoveFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.RemoveFirmwareResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareResponse;
import com.alliander.osgp.cucumber.platform.support.ws.BaseClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class CoreFirmwareManagementClient extends BaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory coreFirmwareManagementWstf;

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

    public RemoveFirmwareResponse removeFirmware(final RemoveFirmwareRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.coreFirmwareManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (RemoveFirmwareResponse) wst.marshalSendAndReceive(request);
    }

    public AddFirmwareResponse addFirmware(final AddFirmwareRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.coreFirmwareManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (AddFirmwareResponse) wst.marshalSendAndReceive(request);
    }

    public ChangeDeviceModelResponse changeDeviceModel(final ChangeDeviceModelRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.coreFirmwareManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (ChangeDeviceModelResponse) wst.marshalSendAndReceive(request);
    }

    public RemoveDeviceModelResponse removeDeviceModel(final RemoveDeviceModelRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.coreFirmwareManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (RemoveDeviceModelResponse) wst.marshalSendAndReceive(request);
    }

    public FindAllDeviceModelsResponse findAllDeviceModels(final FindAllDeviceModelsRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.coreFirmwareManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (FindAllDeviceModelsResponse) wst.marshalSendAndReceive(request);
    }

    public AddDeviceModelRequest addDeviceModel(final AddDeviceModelRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.coreFirmwareManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (AddDeviceModelRequest) wst.marshalSendAndReceive(request);
    }

    public FindFirmwareRequest findFirmware(final FindFirmwareRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.coreFirmwareManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (FindFirmwareRequest) wst.marshalSendAndReceive(request);
    }
}
