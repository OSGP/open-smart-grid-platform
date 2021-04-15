/**
 * Copyright 2016 Smart Society Services B.V.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.*;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.SmartMeteringBaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class SmartMeteringInstallationClient extends SmartMeteringBaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory smartMeteringInstallationWebServiceTemplateFactory;

    public AddDeviceAsyncResponse addDevice(final AddDeviceRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate webServiceTemplate =
                this.smartMeteringInstallationWebServiceTemplateFactory.getTemplate(
                        this.getOrganizationIdentification(), this.getUserName());
        return (AddDeviceAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public AddDeviceResponse getAddDeviceResponse(final AddDeviceAsyncRequest asyncRequest)
            throws WebServiceSecurityException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        final WebServiceTemplate webServiceTemplate =
                this.smartMeteringInstallationWebServiceTemplateFactory.getTemplate(
                        this.getOrganizationIdentification(), this.getUserName());
        return (AddDeviceResponse) webServiceTemplate.marshalSendAndReceive(asyncRequest);
    }

    public CoupleMbusDeviceAsyncResponse coupleMbusDevice(final CoupleMbusDeviceRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate webServiceTemplate =
                this.smartMeteringInstallationWebServiceTemplateFactory.getTemplate(
                        this.getOrganizationIdentification(), this.getUserName());
        return (CoupleMbusDeviceAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public CoupleMbusDeviceResponse getCoupleMbusDeviceResponse(final CoupleMbusDeviceAsyncRequest asyncRequest)
            throws WebServiceSecurityException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        final WebServiceTemplate webServiceTemplate =
                this.smartMeteringInstallationWebServiceTemplateFactory.getTemplate(
                        this.getOrganizationIdentification(), this.getUserName());
        return (CoupleMbusDeviceResponse) webServiceTemplate.marshalSendAndReceive(asyncRequest);
    }

    public DecoupleMbusDeviceAsyncResponse decoupleMbusDevice(final DecoupleMbusDeviceRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate webServiceTemplate =
                this.smartMeteringInstallationWebServiceTemplateFactory.getTemplate(
                        this.getOrganizationIdentification(), this.getUserName());
        return (DecoupleMbusDeviceAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public DecoupleMbusDeviceResponse getDecoupleMbusDeviceResponse(final DecoupleMbusDeviceAsyncRequest asyncRequest)
            throws WebServiceSecurityException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        final WebServiceTemplate webServiceTemplate =
                this.smartMeteringInstallationWebServiceTemplateFactory.getTemplate(
                        this.getOrganizationIdentification(), this.getUserName());
        return (DecoupleMbusDeviceResponse) webServiceTemplate.marshalSendAndReceive(asyncRequest);
    }

    public CoupleMbusDeviceByChannelAsyncResponse coupleMbusDeviceByChannel(
            final CoupleMbusDeviceByChannelRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate webServiceTemplate =
                this.smartMeteringInstallationWebServiceTemplateFactory.getTemplate(
                        this.getOrganizationIdentification(), this.getUserName());
        return (CoupleMbusDeviceByChannelAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public CoupleMbusDeviceByChannelResponse getCoupleMbusDeviceByChannelResponse(
            final CoupleMbusDeviceByChannelAsyncRequest asyncRequest) throws WebServiceSecurityException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        final WebServiceTemplate webServiceTemplate =
                this.smartMeteringInstallationWebServiceTemplateFactory.getTemplate(
                        this.getOrganizationIdentification(), this.getUserName());
        return (CoupleMbusDeviceByChannelResponse) webServiceTemplate.marshalSendAndReceive(asyncRequest);
    }

    public DecoupleMbusDeviceByChannelAsyncResponse decoupleMbusDeviceByChannel(
            final DecoupleMbusDeviceByChannelRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate webServiceTemplate =
                this.smartMeteringInstallationWebServiceTemplateFactory.getTemplate(
                        this.getOrganizationIdentification(), this.getUserName());
        return (DecoupleMbusDeviceByChannelAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public DecoupleMbusDeviceByChannelResponse getDecoupleMbusDeviceByChannelResponse(
            final DecoupleMbusDeviceByChannelAsyncRequest asyncRequest) throws WebServiceSecurityException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        final WebServiceTemplate webServiceTemplate =
                this.smartMeteringInstallationWebServiceTemplateFactory.getTemplate(
                        this.getOrganizationIdentification(), this.getUserName());
        return (DecoupleMbusDeviceByChannelResponse) webServiceTemplate.marshalSendAndReceive(asyncRequest);
    }
}
