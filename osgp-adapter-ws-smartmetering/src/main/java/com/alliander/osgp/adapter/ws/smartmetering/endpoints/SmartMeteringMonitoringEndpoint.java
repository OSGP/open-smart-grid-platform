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
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsContainer;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequest;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.MonitoringService;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.MeterResponseDataRepository;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReads;
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
    public PeriodicMeterReadsAsyncResponse getPeriodicMeterReads(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final PeriodicMeterReadsRequest request) throws OsgpException {

        LOGGER.debug("Incoming PeriodicMeterReadsRequest for meter: {}.", request.getDeviceIdentification());

        try {
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequestData dataRequest = this.monitoringMapper
                    .map(request,
                            com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequestData.class);

            final String correlationUid = this.monitoringService.requestPeriodicMeterReads(organisationIdentification,
                    dataRequest);

            final PeriodicMeterReadsAsyncResponse response = new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ObjectFactory()
                    .createPeriodicMeterReadsAsyncResponse();
            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDeviceIdentification());
            return response;
        } catch (final Exception e) {
            LOGGER.error("Exception: {} while requesting meter reads for device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }

        return new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ObjectFactory()
                .createPeriodicMeterReadsAsyncResponse();
    }

    @PayloadRoot(localPart = "PeriodicMeterReadsAsyncRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public PeriodicMeterReadsContainer getPeriodicMeterReadsResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final PeriodicMeterReadsAsyncRequest request) throws OsgpException {

        LOGGER.debug("Incoming PeriodicMeterReadsAsyncRequest for meter: {}.", request.getDeviceIdentification());

        try {

            final MeterResponseData meterResponseData = this.meterResponseDataRepository
                    .findSingleResultByCorrelationUid(request.getCorrelationUid());

            if (meterResponseData == null) {

                throw new FunctionalException(FunctionalExceptionType.UNKNOWN_CORRELATION_UID,
                        ComponentType.WS_SMART_METERING);
            }

            if (meterResponseData.getMessageData() instanceof PeriodicMeterReadContainer) {

                final PeriodicMeterReadsContainer response = this.monitoringMapper
                        .map(meterResponseData.getMessageData(),
                                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsContainer.class);

                // removing
                LOGGER.debug("deleting MeterResponseData for CorrelationUid {}", request.getCorrelationUid());
                this.meterResponseDataRepository.delete(meterResponseData);
                return response;
            } else {
                LOGGER.warn(
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

        return new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ObjectFactory()
                .createPeriodicMeterReadsContainer();
    }

    @PayloadRoot(localPart = "ActualMeterReadsRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public ActualMeterReadsAsyncResponse getActualMeterReads(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ActualMeterReadsRequest request) throws OsgpException {

        final String deviceIdentification = request.getValue();

        LOGGER.debug("Incoming ActualMeterReadsRequest for meter: {}", deviceIdentification);

        try {
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsRequest requestValueObject = new com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsRequest(
                    deviceIdentification);

            final String correlationUid = this.monitoringService.requestActualMeterReads(organisationIdentification,
                    requestValueObject);

            final ActualMeterReadsAsyncResponse asyncResponse = new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ObjectFactory()
                    .createActualMeterReadsAsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(deviceIdentification);
            return asyncResponse;

        } catch (final Exception e) {
            LOGGER.error("Exception: {} while requesting actual meter reads for device: {} for organisation {}.",
                    new Object[] { e.getMessage(), deviceIdentification, organisationIdentification }, e);

            this.handleException(e);
        }

        return new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ObjectFactory()
                .createActualMeterReadsAsyncResponse();
    }

    @PayloadRoot(localPart = "ActualMeterReadsAsyncRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReads getActualMeterReadsResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ActualMeterReadsAsyncRequest request) throws OsgpException {

        LOGGER.debug("Incoming ActualMeterReadsAsyncRequest for meter: {}", request.getDeviceIdentification());

        try {
            final MeterResponseData meterResponseData = this.meterResponseDataRepository
                    .findSingleResultByCorrelationUid(request.getCorrelationUid());

            if (meterResponseData == null) {
                throw new FunctionalException(FunctionalExceptionType.UNKNOWN_CORRELATION_UID,
                        ComponentType.WS_SMART_METERING);
            }

            if (meterResponseData.getMessageData() instanceof ActualMeterReads) {
                final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReads response = this.monitoringMapper
                        .map(meterResponseData.getMessageData(),
                                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReads.class);

                this.meterResponseDataRepository.delete(meterResponseData);
                return response;
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

        return new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ObjectFactory()
                .createActualMeterReads();
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
