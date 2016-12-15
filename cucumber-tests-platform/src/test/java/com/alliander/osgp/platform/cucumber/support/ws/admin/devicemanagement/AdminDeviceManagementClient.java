/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support.ws.admin.devicemanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.CreateOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.CreateOrganisationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeactivateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeactivateDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationResponse;
import com.alliander.osgp.platform.cucumber.support.ws.BaseClient;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceSecurityException;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceTemplateFactory;

@Component
public class AdminDeviceManagementClient extends BaseClient {

    @Autowired
    private WebServiceTemplateFactory adminDeviceManagementWstf;
    
    private WebServiceTemplate wst;
    
    public AdminDeviceManagementClient() throws WebServiceSecurityException {
    	wst = this.adminDeviceManagementWstf.getTemplate(this.getOrganizationIdentification(), this.getUserName());
    }

    public ActivateDeviceResponse activateDevice(final ActivateDeviceRequest request) {
        return (ActivateDeviceResponse) wst.marshalSendAndReceive(request);
    }

    public DeactivateDeviceResponse deactivateDevice(final DeactivateDeviceRequest request) {
        return (DeactivateDeviceResponse) wst.marshalSendAndReceive(request);
    }

    public CreateOrganisationResponse createOrganization(final CreateOrganisationRequest request)  {
        return (CreateOrganisationResponse) wst.marshalSendAndReceive(request);
    }

    public ChangeOrganisationResponse changeOrganization(final ChangeOrganisationRequest request) {
        return (ChangeOrganisationResponse) wst.marshalSendAndReceive(request);
    }

    public RemoveDeviceResponse removeDevice(final RemoveDeviceRequest request) {
        return (RemoveDeviceResponse) wst.marshalSendAndReceive(request);
    }

    public RemoveOrganisationResponse removeOrganization(final RemoveOrganisationRequest request) {
        return (RemoveOrganisationResponse) wst.marshalSendAndReceive(request);
    }
}
