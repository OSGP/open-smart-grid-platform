/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support.ws.core.deviceinstallation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesResponse;
import com.alliander.osgp.platform.cucumber.support.ws.BaseClient;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceSecurityException;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceTemplateFactory;

@Component
public class CoreDeviceInstallationClient extends BaseClient {

    @Autowired
    private WebServiceTemplateFactory coreDeviceInstallationWstf;

    public FindRecentDevicesResponse findRecentDevices(final FindRecentDevicesRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate wst = this.coreDeviceInstallationWstf.getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (FindRecentDevicesResponse) wst.marshalSendAndReceive(request);
    }
    
}
