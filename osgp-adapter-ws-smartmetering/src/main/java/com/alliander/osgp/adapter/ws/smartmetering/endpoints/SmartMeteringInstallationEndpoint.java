/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.endpoints;

import org.hibernate.validator.method.MethodConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.endpointinterceptors.MessagePriority;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.endpointinterceptors.ScheduleTime;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceResponse;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.InstallationMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.InstallationService;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.wsheaderattribute.priority.MessagePriorityEnum;

// MethodConstraintViolationException is deprecated.
// Will by replaced by equivalent functionality defined
// by the Bean Validation 1.1 API as of Hibernate Validator 5.
@SuppressWarnings("deprecation")
@Endpoint
public class SmartMeteringInstallationEndpoint extends SmartMeteringEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringInstallationEndpoint.class);
    private static final String SMARTMETER_INSTALLATION_NAMESPACE = "http://www.alliander.com/schemas/osgp/smartmetering/sm-installation/2014/10";

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
            @ScheduleTime final String scheduleTime) throws OsgpException {

        LOGGER.info("Incoming AddDeviceRequest for meter: {}.", request.getDevice().getDeviceIdentification());

        AddDeviceAsyncResponse response = null;
        try {
            response = new AddDeviceAsyncResponse();
            final SmartMeteringDevice device = this.installationMapper.map(request.getDevice(),
                    SmartMeteringDevice.class);

            final String correlationUid = this.installationService.enqueueAddSmartMeterRequest(
                    organisationIdentification, device.getDeviceIdentification(), device,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.installationMapper.map(scheduleTime, Long.class));

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDevice().getDeviceIdentification());

        } catch (final MethodConstraintViolationException e) {

            LOGGER.error("Exception: {} while adding device: {} for organisation {}.", new Object[] { e.getMessage(),
                    request.getDevice().getDeviceIdentification(), organisationIdentification }, e);

            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));

        } catch (final Exception e) {

            LOGGER.error("Exception: {} while adding device: {} for organisation {}.", new Object[] { e.getMessage(),
                    request.getDevice().getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "AddDeviceAsyncRequest", namespace = SMARTMETER_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public AddDeviceResponse getSetConfigurationObjectResponse(@RequestPayload final AddDeviceAsyncRequest request)
            throws OsgpException {

        AddDeviceResponse response = null;
        try {
            response = new AddDeviceResponse();
            final MeterResponseData meterResponseData = this.installationService.dequeueResponse(request
                    .getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
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
            @ScheduleTime final String scheduleTime) throws OsgpException {

        final String deviceIdentification = request.getDeviceIdentification();
        final String mbusDeviceIdentification = request.getMbusDeviceIdentification();
        final short channel = request.getChannel();
        LOGGER.info("Incoming CoupleMbusDeviceRequest for meter: {} and mbus device {} on channel {}.",
                deviceIdentification, mbusDeviceIdentification, channel);

        CoupleMbusDeviceAsyncResponse response = null;
        try {
            response = new CoupleMbusDeviceAsyncResponse();

            final String correlationUid = this.installationService.enqueueCoupleMbusDeviceRequest(
                    organisationIdentification, deviceIdentification, mbusDeviceIdentification, channel,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.installationMapper.map(scheduleTime, Long.class));

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(deviceIdentification);

        } catch (final Exception e) {

            LOGGER.error("Exception: {} while coupling devices: {} and {} on channel {} for organisation {}.",
                    new Object[] { e.getMessage(), deviceIdentification, mbusDeviceIdentification, channel,
                            organisationIdentification }, e);

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
            final MeterResponseData meterResponseData = this.installationService.dequeueResponse(request
                    .getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
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
            @ScheduleTime final String scheduleTime) throws OsgpException {

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

        } catch (final Exception e) {

            LOGGER.error("Exception: {} while decoupling devices: {} and {} for organisation {}.",
                    new Object[] { e.getMessage(), deviceIdentification, mbusDeviceIdentification,
                    organisationIdentification }, e);

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
            final MeterResponseData meterResponseData = this.installationService.dequeueResponse(request
                    .getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
            }

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

}
