/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.publiclighting;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryResponse;
import org.opensmartgridplatform.cucumber.platform.support.ws.BaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class PublicLightingDeviceMonitoringClient extends BaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory publicLightingDeviceMonitoringWstf;

    public GetPowerUsageHistoryResponse getGetPowerUsageHistoryResponse(final GetPowerUsageHistoryAsyncRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate webServiceTemplate = this.publicLightingDeviceMonitoringWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (GetPowerUsageHistoryResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public GetPowerUsageHistoryAsyncResponse getPowerUsageHistory(final GetPowerUsageHistoryRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final WebServiceTemplate webServiceTemplate = this.publicLightingDeviceMonitoringWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (GetPowerUsageHistoryAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

}
