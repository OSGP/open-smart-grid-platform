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
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.RetrievePeriodicMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.RetrievePeriodicMeterReadsResponse;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.MonitoringService;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.PeriodicMeterReads;
import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.PeriodicMeterReadsRepository;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

// MethodConstraintViolationException is deprecated.
// Will by replaced by equivalent functionality defined
// by the Bean Validation 1.1 API as of Hibernate Validator 5.
@SuppressWarnings("deprecation")
@Endpoint
public class SmartMeteringMonitoringEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringMonitoringEndpoint.class);
    private static final String SMARTMETER_MONITORING_NAMESPACE = "http://www.alliander.com/schemas/osgp/smartmetering/sm-monitoring/2014/10";

    @Autowired
    private MonitoringService monitoringService;

    @Autowired
    private MonitoringMapper monitoringMapper;

    @Autowired
    private PeriodicMeterReadsRepository periodicMeterReadsRepository;

    public SmartMeteringMonitoringEndpoint() {
    }

    @PayloadRoot(localPart = "PeriodicMeterReadsRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public PeriodicMeterReadsResponse requestPeriodicData(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final PeriodicMeterReadsRequest request) throws OsgpException {

        LOGGER.info("Incoming PeriodicMeterReadsRequest for meter: {}.", request.getDeviceIdentification());

        final PeriodicMeterReadsResponse response = new PeriodicMeterReadsResponse();

        // try {
        //
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequest dataRequest = this.monitoringMapper
                .map(request, com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequest.class);
        //
        final String correlationUid = this.monitoringService.requestPeriodicMeterReads(organisationIdentification,
                dataRequest);

        final AsyncResponse asyncResponse = new AsyncResponse();
        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceId("567812346584849");
        response.setAsyncResponse(asyncResponse);
        //
        // } catch (final MethodConstraintViolationException e) {
        //
        //
        // LOGGER.error("Exception: {} while adding device: {} for organisation {}.",
        // // new Object[] { e.getMessage(),
        // // request.getDevice().getDeviceIdentification(),
        // // organisationIdentification }, e);
        // //
        // // throw new
        // // FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
        // // ComponentType.WS_CORE,
        // // new ValidationException(e.getConstraintViolations()));
        // //
        // // } catch (final Exception e) {
        // //
        // //
        // LOGGER.error("Exception: {} while adding device: {} for organisation {}.",
        // // new Object[] { e.getMessage(),
        // // request.getDevice().getDeviceIdentification(),
        // // organisationIdentification }, e);
        // //
        // // this.handleException(e);
        // // }
        //
        return response;
    }

    @PayloadRoot(localPart = "RetrievePeriodicMeterReadsRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
    @ResponsePayload
    public RetrievePeriodicMeterReadsResponse requestPeriodicData(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final RetrievePeriodicMeterReadsRequest request) throws OsgpException {

        LOGGER.info("Incoming RetrievePeriodicMeterReadsRequest for meter: {}.", request.getDeviceIdentification());

        final RetrievePeriodicMeterReadsResponse response = new RetrievePeriodicMeterReadsResponse();

        final PeriodicMeterReads meterReads = this.periodicMeterReadsRepository
                .findByCorrelationUidAndDeviceIdentification(request.getCorrelationUid(),
                        request.getDeviceIdentification());

        // removing
        this.periodicMeterReadsRepository.delete(meterReads.getId());

        response.setPeriodicMeterReads(this.monitoringMapper.map(meterReads,
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReads.class));

        // TODO not OK when not found

        // final PeriodicMeterReadsResponse response = new
        // PeriodicMeterReadsResponse();
        //
        // // try {
        // //
        // final
        // com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequest
        // dataRequest = this.monitoringMapper
        // .map(request,
        // com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequest.class);
        // //
        // final String correlationUid =
        // this.monitoringService.requestPeriodicMeterReads(organisationIdentification,
        // dataRequest);
        //
        // final AsyncResponse asyncResponse = new AsyncResponse();
        // asyncResponse.setCorrelationUid(correlationUid);
        // asyncResponse.setDeviceId("567812346584849");
        // response.setAsyncResponse(asyncResponse);
        //
        // } catch (final MethodConstraintViolationException e) {
        //
        //
        // LOGGER.error("Exception: {} while adding device: {} for organisation {}.",
        // // new Object[] { e.getMessage(),
        // // request.getDevice().getDeviceIdentification(),
        // // organisationIdentification }, e);
        // //
        // // throw new
        // // FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
        // // ComponentType.WS_CORE,
        // // new ValidationException(e.getConstraintViolations()));
        // //
        // // } catch (final Exception e) {
        // //
        // //
        // LOGGER.error("Exception: {} while adding device: {} for organisation {}.",
        // // new Object[] { e.getMessage(),
        // // request.getDevice().getDeviceIdentification(),
        // // organisationIdentification }, e);
        // //
        // // this.handleException(e);
        // // }
        //
        return response;
    }

    private void handleException(final Exception e) throws OsgpException {
        // Rethrow exception if it already is a functional or technical
        // exception,
        // otherwise throw new technical exception.
        if (e instanceof OsgpException) {
            LOGGER.error("Exception occurred: ", e);
            throw (OsgpException) e;
        } else {
            LOGGER.error("Exception occurred: ", e);
            throw new TechnicalException(ComponentType.WS_SMART_METERING, e);
        }
    }
}
