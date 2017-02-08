/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.support.ws.publiclighting;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetActualPowerUsageAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetActualPowerUsageAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetActualPowerUsageRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetActualPowerUsageResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryResponse;
import com.alliander.osgp.cucumber.platform.support.ws.BaseClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class PublicLightingDeviceMonitoringClient extends BaseClient {

	@Autowired
	private DefaultWebServiceTemplateFactory publicLightingDeviceMonitoringWstf;

	public GetActualPowerUsageAsyncResponse getActualPowerUsage(final GetActualPowerUsageRequest request)
			throws WebServiceSecurityException, GeneralSecurityException, IOException {
		final WebServiceTemplate webServiceTemplate = this.publicLightingDeviceMonitoringWstf
				.getTemplate(this.getOrganizationIdentification(), this.getUserName());
		return (GetActualPowerUsageAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
	}

	public GetActualPowerUsageResponse getGetActualPowerUsageResponse(final GetActualPowerUsageAsyncRequest request)
			throws WebServiceSecurityException, GeneralSecurityException, IOException {
		final WebServiceTemplate webServiceTemplate = this.publicLightingDeviceMonitoringWstf
				.getTemplate(this.getOrganizationIdentification(), this.getUserName());
		return (GetActualPowerUsageResponse) webServiceTemplate.marshalSendAndReceive(request);
	}

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
