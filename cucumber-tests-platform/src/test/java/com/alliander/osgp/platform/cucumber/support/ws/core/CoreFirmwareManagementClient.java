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

import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionResponse;
import com.alliander.osgp.platform.cucumber.support.ws.BaseClient;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceSecurityException;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceTemplateFactory;

@Component
public class CoreFirmwareManagementClient extends BaseClient {
	
    @Autowired
    private WebServiceTemplateFactory coreFirmwareManagementWstf;

	public GetFirmwareVersionAsyncResponse getFirmwareVersion(GetFirmwareVersionRequest request) throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreFirmwareManagementWstf.getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (GetFirmwareVersionAsyncResponse) wst.marshalSendAndReceive(request);
	}

	public GetFirmwareVersionResponse getGetFirmwareVersion(GetFirmwareVersionAsyncRequest request) throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate wst = this.coreFirmwareManagementWstf.getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (GetFirmwareVersionResponse) wst.marshalSendAndReceive(request);
	} 
}
