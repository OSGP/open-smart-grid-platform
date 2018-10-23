/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdemoapp.application.services;

import java.util.List;

import org.opensmartgridplatform.webdemoapp.infra.platform.SoapRequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;

import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.FindAllDevicesRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.FindAllDevicesResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightValue;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.AsyncRequest;

import ma.glasnost.orika.MapperFacade;

/**
 * Handles communication with the platform using SOAP.
 *
 */
public class OsgpPublicLightingClientSoapService {

    @Autowired
    private SoapRequestHelper soapRequestHelper;

    private final MapperFacade publicLightingAdHocMapperFacade;

    public OsgpPublicLightingClientSoapService(final MapperFacade mapper) {
        this.publicLightingAdHocMapperFacade = mapper;
    }

    /**
     * Creates a WebServiceTemplate with a FindAllDevices Request using the
     * SoapRequestHelper class. Sends the request to the Platform, and returns
     * the List with Devices from the Response (after it is converted by Orika).
     *
     * @return a list of devices
     */
    public List<org.opensmartgridplatform.webdemoapp.domain.Device> findAllDevicesRequest() {
        final FindAllDevicesRequest findAllDevicesRequest = new FindAllDevicesRequest();

        final WebServiceTemplate template = this.soapRequestHelper.createPublicLightingRequest();

        final FindAllDevicesResponse response = (FindAllDevicesResponse) template
                .marshalSendAndReceive(findAllDevicesRequest);

        return this.publicLightingAdHocMapperFacade
                .mapAsList(response.getDevicePage().getDevices(),
                        org.opensmartgridplatform.webdemoapp.domain.Device.class);
    }

    /**
     * Creates a WebServiceTemplate and a SetLightRequest with the parameters
     * deviceId, dimValue and LightOn. Sends the SetLightRequest to the platform
     * using the WebServiceTemplate. Returns the response (CorrelationId) from
     * the Platform.
     *
     * @return correlation id
     */
    public String setLightRequest(final String deviceId, final int dimValue, final boolean lightOn) {
        final SetLightRequest request = new SetLightRequest();
        final LightValue lightValue = new LightValue();
        lightValue.setDimValue(dimValue);
        lightValue.setOn(lightOn);

        final WebServiceTemplate template = this.soapRequestHelper.createPublicLightingRequest();

        request.setDeviceIdentification(deviceId);
        request.getLightValue().add(lightValue);

        final SetLightAsyncResponse response = (SetLightAsyncResponse) template.marshalSendAndReceive(request);

        return response.getAsyncResponse().getCorrelationUid();
    }

    /**
     * Creates a SetLight request using the parameters deviceId and lightOn.
     * Sends the request to the platform using the WebServiceTemplate.
     *
     * Returns the CorrelationId response from the Platform.
     */
    public String switchLightRequest(final String deviceId, final boolean lightOn) {
        final SetLightRequest request = new SetLightRequest();
        final LightValue lightValue = new LightValue();
        lightValue.setOn(lightOn);

        final WebServiceTemplate template = this.soapRequestHelper.createPublicLightingRequest();

        request.setDeviceIdentification(deviceId);
        request.getLightValue().add(lightValue);

        final SetLightAsyncResponse response = (SetLightAsyncResponse) template.marshalSendAndReceive(request);

        return response.getAsyncResponse().getCorrelationUid();
    }

    /**
     * Creates a getDeviceStatus request with the parameter deviceId. Sends it
     * to the Platform using a WebServiceTemplate.
     *
     * Returns the CorrelationId response from the platform.
     */
    public String getDeviceStatus(final String deviceId) {
        final WebServiceTemplate requestTemplate = this.soapRequestHelper.createPublicLightingRequest();

        final GetStatusRequest request = new GetStatusRequest();
        request.setDeviceIdentification(deviceId);
        GetStatusAsyncResponse asyncStatusResponse = null;

        asyncStatusResponse = (GetStatusAsyncResponse) requestTemplate.marshalSendAndReceive(request);

        return asyncStatusResponse.getAsyncResponse().getCorrelationUid();
    }

    /**
     * Async request. Sends a GetStatusAsyncRequest to the platform using the
     * deviceId and correlationId parameters.
     *
     * Returns the response from the Platform.
     */
    public GetStatusResponse getGetStatusResponse(final String deviceId, final String correlationId) {
        final AsyncRequest asyncRequest = new AsyncRequest();
        final GetStatusAsyncRequest asyncStatusRequest = new GetStatusAsyncRequest();

        asyncRequest.setCorrelationUid(correlationId);
        asyncRequest.setDeviceId(deviceId);

        asyncStatusRequest.setAsyncRequest(asyncRequest);

        final WebServiceTemplate template = this.soapRequestHelper.createPublicLightingRequest();

        return (GetStatusResponse) template.marshalSendAndReceive(asyncStatusRequest);
    }

}
