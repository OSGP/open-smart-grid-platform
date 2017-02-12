/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.support.ws.tariffswitching;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.schema.tariffswitching.adhocmanagement.GetStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.adhocmanagement.GetStatusRequest;
import com.alliander.osgp.cucumber.platform.support.ws.BaseClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class TariffSwitchingAdHocManagementClient extends BaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory tariffSwitchingAdHocManagementWstf;

    public GetStatusAsyncResponse getStatus(final GetStatusRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate webServiceTemplate = this.tariffSwitchingAdHocManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (GetStatusAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

}
