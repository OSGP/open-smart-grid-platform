/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.support.ws.smartmetering;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataResponse;
import com.alliander.osgp.platform.cucumber.support.ws.BaseClient;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceSecurityException;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceTemplateFactory;

@Component
public class SmartMeteringMonitoringManagementClient extends BaseClient {

    @Autowired
    private WebServiceTemplateFactory smartMeteringMonitoringManagementWstf;

    public ProfileGenericDataResponse requestProfileGenericData(final ProfileGenericDataRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (ProfileGenericDataResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    private WebServiceTemplate getTemplate() throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return this.smartMeteringMonitoringManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
    }
}
