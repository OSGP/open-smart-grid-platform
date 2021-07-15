/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.endpoints;

import javax.validation.ConstraintViolationException;

import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.ResponseUrl;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.ScheduleTime;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceByChannelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceResponse;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping.InstallationMapper;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.InstallationService;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceModel;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AddSmartMeterRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class SmartMeteringInstallationEndpoint extends SmartMeteringEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringInstallationEndpoint.class);
    private static final String SMARTMETER_INSTALLATION_NAMESPACE = "http://www.opensmartgridplatform.org/schemas/smartmetering/sm-installation/2014/10";

    @Autowired
    private InstallationService installationService;

    @Autowired
    private InstallationMapper installationMapper;

    public SmartMeteringInstallationEndpoint() {
        // Empty constructor
    }

    @PayloadRoot(localPart = "AddDeviceRequest", namespace = SMARTMETER_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public AddDeviceAsyncResponse addDevice(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final AddDeviceRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

        LOGGER.info("Incoming AddDeviceRequest for meter: {}.", request.getDevice().getDeviceIdentification());

        AddDeviceAsyncResponse response = null;
        try {
            response = new AddDeviceAsyncResponse();
            final SmartMeteringDevice device = this.installationMapper.map(request.getDevice(),
                    SmartMeteringDevice.class);
            final DeviceModel deviceModel = new DeviceModel(request.getDeviceModel().getManufacturer(),
                    request.getDeviceModel().getModelCode(), "");
            final AddSmartMeterRequest addSmartMeterRequest = new AddSmartMeterRequest(device, deviceModel);
            final String correlationUid = this.installationService.enqueueAddSmartMeterRequest(
                    organisationIdentification, device.getDeviceIdentification(), addSmartMeterRequest,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.installationMapper.map(scheduleTime, Long.class));

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDevice().getDeviceIdentification());
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final ConstraintViolationException e) {

            LOGGER.error("Exception: {} while adding device: {} for organisation {}.", e.getMessage(),
                    request.getDevice().getDeviceIdentification(), organisationIdentification, e);

            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));

        } catch (final Exception e) {

            LOGGER.error("Exception: {} while adding device: {} for organisation {}.", e.getMessage(),
                    request.getDevice().getDeviceIdentification(), organisationIdentification, e);

            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "AddDeviceAsyncRequest", namespace = SMARTMETER_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public AddDeviceResponse getAddDeviceResponse(@RequestPayload final AddDeviceAsyncRequest request)
            throws OsgpException {

        AddDeviceResponse response = null;
        try {
            response = new AddDeviceResponse();
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            this.throwExceptionIfResultNotOk(responseData, "Add Device");

            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
            }

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    /**
     * @param organisationIdentification
     *            the organisation requesting the coupling of devices
     * @param request
     *            the CoupleMbusDeviceRequest containing the
     *            deviceIdentification, mbusDeviceIdentification and channel
     * @param messagePriority
     *            the priority of the message
     * @param scheduleTime
     *            the time the request is scheduled for
     * @return a response containing a correlationUid and the
     *         deviceIdentification
     * @throws OsgpException
     */
    @PayloadRoot(localPart = "CoupleMbusDeviceRequest", namespace = SMARTMETER_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public CoupleMbusDeviceAsyncResponse coupleMbusDevice(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final CoupleMbusDeviceRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

        final String deviceIdentification = request.getDeviceIdentification();
        final String mbusDeviceIdentification = request.getMbusDeviceIdentification();
        LOGGER.info("Incoming CoupleMbusDeviceRequest for meter: {} and mbus device {}.", deviceIdentification,
                mbusDeviceIdentification);

        CoupleMbusDeviceAsyncResponse response = null;
        try {
            response = new CoupleMbusDeviceAsyncResponse();

            final String correlationUid = this.installationService.enqueueCoupleMbusDeviceRequest(
                    organisationIdentification, deviceIdentification, mbusDeviceIdentification,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.installationMapper.map(scheduleTime, Long.class));

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(deviceIdentification);
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {
            LOGGER.error("Exception while coupling devices: {} and {} for organisation {}.", deviceIdentification,
                    mbusDeviceIdentification, organisationIdentification, e);
            this.handleException(e);
        }
        return response;
    }

    /**
     * @param request
     *            the request message containing the correlationUid
     * @return the response message containing the OsgpResultType and optional a
     *         message
     * @throws OsgpException
     */
    @PayloadRoot(localPart = "CoupleMbusDeviceAsyncRequest", namespace = SMARTMETER_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public CoupleMbusDeviceResponse getCoupleMbusDeviceResponse(
            @RequestPayload final CoupleMbusDeviceAsyncRequest request) throws OsgpException {

        CoupleMbusDeviceResponse response = null;
        try {
            response = new CoupleMbusDeviceResponse();
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            this.throwExceptionIfResultNotOk(responseData, "Couple Mbus Device");

            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
            }

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    /**
     * @param organisationIdentification
     *            the organisation requesting the coupling of devices
     * @param request
     *            the DeCoupleMbusDeviceRequest containing the
     *            deviceIdentification, mbusDeviceIdentification and channel
     * @param messagePriority
     *            the priority of the message
     * @param scheduleTime
     *            the time the request is scheduled for
     * @return a response containing a correlationUid and the
     *         deviceIdentification
     * @throws OsgpException
     */
    @PayloadRoot(localPart = "DeCoupleMbusDeviceRequest", namespace = SMARTMETER_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public DeCoupleMbusDeviceAsyncResponse deCoupleMbusDevice(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final DeCoupleMbusDeviceRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

        final String deviceIdentification = request.getDeviceIdentification();
        final String mbusDeviceIdentification = request.getMbusDeviceIdentification();
        LOGGER.info("Incoming DeCoupleMbusDeviceRequest for meter: {} and mbus device {}.", deviceIdentification,
                mbusDeviceIdentification);

        DeCoupleMbusDeviceAsyncResponse response = null;
        try {
            response = new DeCoupleMbusDeviceAsyncResponse();

            final String correlationUid = this.installationService.enqueueDeCoupleMbusDeviceRequest(
                    organisationIdentification, deviceIdentification, mbusDeviceIdentification,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.installationMapper.map(scheduleTime, Long.class));

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(deviceIdentification);
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {

            LOGGER.error("Exception: {} while decoupling devices: {} and {} for organisation {}.", e.getMessage(),
                    deviceIdentification, mbusDeviceIdentification, organisationIdentification, e);

            this.handleException(e);
        }
        return response;
    }

    /**
     * @param request
     *            the request message containing the correlationUid
     * @return the response message containing the OsgpResultType and optional a
     *         message
     * @throws OsgpException
     */
    @PayloadRoot(localPart = "DeCoupleMbusDeviceAsyncRequest", namespace = SMARTMETER_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public DeCoupleMbusDeviceResponse getDeCoupleMbusDeviceResponse(
            @RequestPayload final DeCoupleMbusDeviceAsyncRequest request) throws OsgpException {

        DeCoupleMbusDeviceResponse response = null;
        try {
            response = new DeCoupleMbusDeviceResponse();
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            this.throwExceptionIfResultNotOk(responseData, "DeCouple Mbus Device");

            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
            }

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    /**
     * @param organisationIdentification
     *            the organization requesting the coupling of devices
     * @param request
     *            the CoupleMbusDeviceByChannelRequest containing the
     *            gatewayDeviceIdentification and channel
     * @param messagePriority
     *            the priority of the message
     * @param scheduleTime
     *            the time the request is scheduled for
     * @return a response containing a correlationUid and the
     *         deviceIdentification
     * @throws OsgpException
     */
    @PayloadRoot(localPart = "CoupleMbusDeviceByChannelRequest", namespace = SMARTMETER_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public CoupleMbusDeviceByChannelAsyncResponse coupleMbusDeviceByChannel(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final CoupleMbusDeviceByChannelRequest request,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime,
            @ResponseUrl final String responseUrl) throws OsgpException {

        final String deviceIdentification = request.getDeviceIdentification();
        final short channel = request.getCoupleMbusDeviceByChannelRequestData().getChannel();
        LOGGER.info("Incoming CoupleMbusDeviceByChannelRequest for device: {} and channel {}.", deviceIdentification,
                channel);

        CoupleMbusDeviceByChannelAsyncResponse response = null;
        try {
            response = new CoupleMbusDeviceByChannelAsyncResponse();

            final String correlationUid = this.installationService.enqueueCoupleMbusDeviceByChannelRequest(
                    organisationIdentification, deviceIdentification,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.installationMapper.map(scheduleTime, Long.class), channel);

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(deviceIdentification);
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {
            LOGGER.error("Exception while coupling on channel: {} for device: {} for organisation {}.", channel,
                    deviceIdentification, organisationIdentification, e);
            this.handleException(e);
        }
        return response;
    }

    /**
     * @param request
     *            the request message containing the correlationUid
     * @return the response message containing the OsgpResultType and optional a
     *         message
     * @throws OsgpException
     */
    @PayloadRoot(localPart = "CoupleMbusDeviceByChannelAsyncRequest", namespace = SMARTMETER_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public CoupleMbusDeviceByChannelResponse getCoupleMbusDeviceByChannelResponse(
            @RequestPayload final CoupleMbusDeviceByChannelAsyncRequest request) throws OsgpException {

        CoupleMbusDeviceByChannelResponse response = null;
        try {
            response = new CoupleMbusDeviceByChannelResponse();
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            this.throwExceptionIfResultNotOk(responseData, "Couple Mbus Device By Channel");

            if (responseData.getMessageData() instanceof String) {
                response.setResultString((String) responseData.getMessageData());
            }
            response = this.installationMapper.map(responseData.getMessageData(),
                    CoupleMbusDeviceByChannelResponse.class);

            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

}
