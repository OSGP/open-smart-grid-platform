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

import com.alliander.osgp.adapter.ws.schema.core.adhocmanagement.SetRebootAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.adhocmanagement.SetRebootAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.adhocmanagement.SetRebootRequest;
import com.alliander.osgp.adapter.ws.schema.core.adhocmanagement.SetRebootResponse;
import com.alliander.osgp.cucumber.platform.support.ws.BaseClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class CoreAdHocManagementClient extends BaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory coreAdHocManagementWstf;

    public SetRebootAsyncResponse setReboot(final SetRebootRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate webServiceTemplate = this.coreAdHocManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (SetRebootAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public SetRebootResponse getSetRebootResponse(final SetRebootAsyncRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate webServiceTemplate = this.coreAdHocManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (SetRebootResponse) webServiceTemplate.marshalSendAndReceive(request);
    }
}
