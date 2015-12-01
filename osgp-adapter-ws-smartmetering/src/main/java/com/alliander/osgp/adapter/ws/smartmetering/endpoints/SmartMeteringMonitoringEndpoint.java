/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.AsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.RetrieveActualMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.RetrieveActualMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.RetrievePeriodicMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.RetrievePeriodicMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.RetrieveReadAlarmRegisterRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.RetrieveReadAlarmRegisterResponse;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.MonitoringService;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.MeterResponseDataRepository;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReads;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmRegister;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadContainer;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

@Endpoint
public class SmartMeteringMonitoringEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringMonitoringEndpoint.class);
    private static final String SMARTMETER_MONITORING_NAMESPACE = "http://www.alliander.com/schemas/osgp/smartmetering/sm-monitoring/2014/10";

    @Autowired
    private MonitoringService monitoringService;

    @Autowired
    private MonitoringMapper monitoringMapper;

    @Autowired
    private MeterResponseDataRepository meterResponseDataRepository;

    public SmartMeteringMonitoringEndpoint() {
    }

    @PayloadRoot(localPart = "PeriodicMeterReadsRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public PeriodicMeterReadsResponse requestPeriodicData(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final PeriodicMeterReadsRequest request) throws OsgpException {

        LOGGER.info("Incoming PeriodicMeterReadsRequest for meter: {}.", request.getDeviceIdentification());

        final PeriodicMeterReadsResponse response = new PeriodicMeterReadsResponse();

        try {
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequest dataRequest = this.monitoringMapper
                    .map(request,
                            com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequest.class);

            final String correlationUid = this.monitoringService.requestPeriodicMeterReads(organisationIdentification,
                    dataRequest);

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);
        } catch (final Exception e) {
            LOGGER.error("Exception: {} while requesting meter reads for device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "RetrievePeriodicMeterReadsRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public RetrievePeriodicMeterReadsResponse requestPeriodicData(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final RetrievePeriodicMeterReadsRequest request) throws OsgpException {

        LOGGER.info("Incoming RetrievePeriodicMeterReadsRequest for meter: {}.", request.getDeviceIdentification());

        final RetrievePeriodicMeterReadsResponse response = new RetrievePeriodicMeterReadsResponse();

        try {

            final MeterResponseData meterResponseData = this.meterResponseDataRepository
                    .findSingleResultByCorrelationUid(request.getCorrelationUid());

            if (meterResponseData == null) {

                throw new FunctionalException(FunctionalExceptionType.UNKNOWN_CORRELATION_UID,
                        ComponentType.WS_SMART_METERING);
            }

            if (meterResponseData.getMessageData() instanceof PeriodicMeterReadContainer) {

                response.setPeriodicMeterReadsContainer(this.monitoringMapper.map(meterResponseData.getMessageData(),
                        com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsContainer.class));

                // removing
                LOGGER.info("deleting MeterResponseData for CorrelationUid {}", request.getCorrelationUid());
                this.meterResponseDataRepository.delete(meterResponseData);
            } else {
                LOGGER.info(
                        "findEventsByCorrelationUid also found other type of meter response data: {} for correlation UID: {}",
                        meterResponseData.getClass().getName(), request.getCorrelationUid());
            }

        } catch (final Exception e) {

            if ((e instanceof FunctionalException)
                    && ((FunctionalException) e).getExceptionType() == FunctionalExceptionType.UNKNOWN_CORRELATION_UID) {

                LOGGER.warn("No response data for correlation UID {} in RetrievePeriodicMeterReadsRequest",
                        request.getCorrelationUid());

                throw e;

            } else {

                LOGGER.error("Exception: {} while sending PeriodicMeterReads of device: {} for organisation {}.",
                        new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification });

                this.handleException(e);
            }
        }

        return response;
    }

    @PayloadRoot(localPart = "ActualMeterReadsRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public ActualMeterReadsResponse requestActualMeterReads(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ActualMeterReadsRequest request) throws OsgpException {

        LOGGER.info("Incoming ActualMeterReadsRequest for meter: {}", request.getDeviceIdentification());

        final ActualMeterReadsResponse response = new ActualMeterReadsResponse();

        try {
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsRequest requestValueObject = this.monitoringMapper
                    .map(request,
                            com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsRequest.class);

            final String correlationUid = this.monitoringService.requestActualMeterReads(organisationIdentification,
                    requestValueObject);

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);

        } catch (final Exception e) {
            LOGGER.error("Exception: {} while requesting actual meter reads for device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "RetrieveActualMeterReadsRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public RetrieveActualMeterReadsResponse retrieveActualMeterReads(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final RetrieveActualMeterReadsRequest request) throws OsgpException {

        LOGGER.info("Incoming RetreiveActualMeterReadsRequest for meter: {}", request.getDeviceIdentification());

        final RetrieveActualMeterReadsResponse response = new RetrieveActualMeterReadsResponse();

        try {
            final MeterResponseData meterResponseData = this.meterResponseDataRepository
                    .findSingleResultByCorrelationUid(request.getCorrelationUid());

            if (meterResponseData == null) {
                throw new FunctionalException(FunctionalExceptionType.UNKNOWN_CORRELATION_UID,
                        ComponentType.WS_SMART_METERING);
            }

            if (meterResponseData.getMessageData() instanceof ActualMeterReads) {
                response.setActualMeterReads(this.monitoringMapper.map(meterResponseData.getMessageData(),
                        com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReads.class));

                this.meterResponseDataRepository.delete(meterResponseData);
            } else {
                LOGGER.warn("Incorrect type of response data: {} for correlation UID: {}", meterResponseData.getClass()
                        .getName(), request.getCorrelationUid());
            }

        } catch (final Exception e) {
            if ((e instanceof FunctionalException)
                    && ((FunctionalException) e).getExceptionType() == FunctionalExceptionType.UNKNOWN_CORRELATION_UID) {

                LOGGER.warn("No response data for correlation UID {} in RetrieveActualMeterReadsRequest",
                        request.getCorrelationUid());

                throw e;

            } else {
                LOGGER.error("Exception: {} while sending ActualMeterReads of device: {} for organisation {}.",
                        new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification });

                this.handleException(e);
            }
        }

        return response;
    }

    @PayloadRoot(localPart = "ReadAlarmRegisterRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public ReadAlarmRegisterResponse requestReadAlarmRegister(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ReadAlarmRegisterRequest request) throws OsgpException {

        LOGGER.info("Incoming ReadAlarmRegisterRequest for meter: {}", request.getDeviceIdentification());

        final ReadAlarmRegisterResponse response = new ReadAlarmRegisterResponse();

        try {
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest requestValueObject = this.monitoringMapper
                    .map(request,
                            com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest.class);

            final String correlationUid = this.monitoringService.requestReadAlarmRegister(organisationIdentification,
                    requestValueObject);

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);

        } catch (final Exception e) {
            LOGGER.error("Exception: {} while requesting read alarm register for device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "RetrieveReadAlarmRegisterRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public RetrieveReadAlarmRegisterResponse retrieveReadAlarmRegister(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final RetrieveReadAlarmRegisterRequest request) throws OsgpException {

        LOGGER.info("Incoming RetrieveReadAlarmRegisterRequest for meter: {}", request.getDeviceIdentification());

        final RetrieveReadAlarmRegisterResponse response = new RetrieveReadAlarmRegisterResponse();

        try {
            final MeterResponseData meterResponseData = this.meterResponseDataRepository
                    .findSingleResultByCorrelationUid(request.getCorrelationUid());

            if (meterResponseData == null) {
                throw new FunctionalException(FunctionalExceptionType.UNKNOWN_CORRELATION_UID,
                        ComponentType.WS_SMART_METERING);
            }

            if (meterResponseData.getMessageData() instanceof AlarmRegister) {
                response.setAlarmRegister(this.monitoringMapper.map(meterResponseData.getMessageData(),
                        com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AlarmRegister.class));

                this.meterResponseDataRepository.delete(meterResponseData);
            } else {
                LOGGER.warn("Incorrect type of response data: {} for correlation UID: {}", meterResponseData
                        .getMessageData().getClass().getName(), request.getCorrelationUid());
            }

        } catch (final Exception e) {
            if ((e instanceof FunctionalException)
                    && ((FunctionalException) e).getExceptionType() == FunctionalExceptionType.UNKNOWN_CORRELATION_UID) {

                LOGGER.warn("No response data for correlation UID {} in RetrieveReadAlarmRegisterRequest",
                        request.getCorrelationUid());

                throw e;

            } else {
                LOGGER.error(
                        "Exception: {} while sending RetrieveReadAlarmRegisterRequest of device: {} for organisation {}.",
                        new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification });

                this.handleException(e);
            }
        }

        return response;
    }

    private void handleException(final Exception e) throws OsgpException {
        // Rethrow exception if it already is a functional or technical
        // exception, otherwise throw new technical exception.
        LOGGER.error("Exception occurred: ", e);
        if (e instanceof OsgpException) {
            throw (OsgpException) e;
        } else {
            throw new TechnicalException(ComponentType.WS_SMART_METERING, e);
        }
    }
}
