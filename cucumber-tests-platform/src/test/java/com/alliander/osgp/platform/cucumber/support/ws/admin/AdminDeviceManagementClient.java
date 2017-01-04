/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support.ws.admin;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ChangeOrganizationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ChangeOrganizationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.CreateOrganizationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.CreateOrganizationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeactivateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeactivateDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ReceiveEventNotificationsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ReceiveEventNotificationsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ReceiveEventNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ReceiveEventNotificationsResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveOrganizationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveOrganizationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RevokeKeyRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RevokeKeyResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateKeyRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateKeyResponse;
import com.alliander.osgp.platform.cucumber.support.ws.BaseClient;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceSecurityException;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceTemplateFactory;

@Component
public class AdminDeviceManagementClient extends BaseClient {

    @Autowired
    private WebServiceTemplateFactory adminDeviceManagementWstf;

    public ActivateDeviceResponse activateDevice(final ActivateDeviceRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (ActivateDeviceResponse) wst.marshalSendAndReceive(request);
    }

    public DeactivateDeviceResponse deactivateDevice(final DeactivateDeviceRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (DeactivateDeviceResponse) wst.marshalSendAndReceive(request);
    }

    public CreateOrganizationResponse createOrganization(final CreateOrganizationRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (CreateOrganizationResponse) wst.marshalSendAndReceive(request);
    }

    public ChangeOrganizationResponse changeOrganization(final ChangeOrganizationRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (ChangeOrganizationResponse) wst.marshalSendAndReceive(request);
    }

    public RemoveDeviceResponse removeDevice(final RemoveDeviceRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (RemoveDeviceResponse) wst.marshalSendAndReceive(request);
    }

    public RemoveOrganizationResponse removeOrganization(final RemoveOrganizationRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (RemoveOrganizationResponse) wst.marshalSendAndReceive(request);
    }

    public ReceiveEventNotificationsAsyncResponse receiveEventNotifications(
            final ReceiveEventNotificationsRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        final Object obj = wst.marshalSendAndReceive(request);
        System.out.println(obj);
        return (ReceiveEventNotificationsAsyncResponse) wst.marshalSendAndReceive(request);
    }

    public ReceiveEventNotificationsResponse getReceiveEventNotificationsResponse(
            final ReceiveEventNotificationsAsyncRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (ReceiveEventNotificationsResponse) wst.marshalSendAndReceive(request);
    }

    public RevokeKeyResponse getRevokeKeyResponse(final RevokeKeyRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        final Object obj = wst.marshalSendAndReceive(request);
        return (RevokeKeyResponse) obj;
        // return (RevokeKeyResponse) wst.marshalSendAndReceive(request);
    }

    public SetOwnerResponse setOwner(final SetOwnerRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (SetOwnerResponse) wst.marshalSendAndReceive(request);
    }

    public UpdateKeyResponse getUpdateKeyResponse(final UpdateKeyRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (UpdateKeyResponse) wst.marshalSendAndReceive(request);
    }

    public FindDevicesWhichHaveNoOwnerResponse findDevicesWithoutOwner(final FindDevicesWhichHaveNoOwnerRequest request)
            throws WebServiceSecurityException {

        final WebServiceTemplate wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
        return (FindDevicesWhichHaveNoOwnerResponse) wst.marshalSendAndReceive(request);
    }
}
