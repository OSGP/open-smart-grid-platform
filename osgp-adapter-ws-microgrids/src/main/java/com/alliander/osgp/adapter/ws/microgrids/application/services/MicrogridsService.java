/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.microgrids.application.services;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.microgrids.application.exceptionhandling.ResponseNotFoundException;
import com.alliander.osgp.adapter.ws.microgrids.infra.jms.MicrogridsRequestMessage;
import com.alliander.osgp.adapter.ws.microgrids.infra.jms.MicrogridsRequestMessageSender;
import com.alliander.osgp.adapter.ws.microgrids.infra.jms.MicrogridsRequestMessageType;
import com.alliander.osgp.adapter.ws.shared.services.ResponseDataService;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.microgrids.entities.RtuDevice;
import com.alliander.osgp.domain.microgrids.valueobjects.EmptyResponse;
import com.alliander.osgp.domain.microgrids.valueobjects.GetDataRequest;
import com.alliander.osgp.domain.microgrids.valueobjects.GetDataResponse;
import com.alliander.osgp.domain.microgrids.valueobjects.SetDataRequest;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Service
@Transactional(value = "transactionManager")
@Validated
public class MicrogridsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicrogridsService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private MicrogridsRequestMessageSender requestMessageSender;

    @Autowired
    private ResponseDataService responseDataService;

    public MicrogridsService() {
        // Parameterless constructor required for transactions
    }

    public String enqueueGetDataRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, @NotNull final GetDataRequest dataRequest)
            throws OsgpException {

        LOGGER.debug("enqueueGetDataRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final RtuDevice device = this.domainHelperService.findDevice(deviceIdentification);
        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_DATA);

        final MicrogridsRequestMessage message = new MicrogridsRequestMessage(MicrogridsRequestMessageType.GET_DATA,
                correlationUid, organisationIdentification, deviceIdentification, dataRequest, null);

        try {
            this.requestMessageSender.send(message);
        } catch (final ArgumentNullOrEmptyException e) {
            throw new TechnicalException(ComponentType.WS_MICROGRIDS, e);
        }

        return correlationUid;
    }

    public GetDataResponse dequeueGetDataResponse(final String correlationUid) throws OsgpException {

        LOGGER.debug("dequeueGetDataRequest called with correlation uid {}", correlationUid);

        final ResponseData responseData = this.responseDataService.dequeue(correlationUid, ResponseMessage.class,
                ComponentType.WS_MICROGRIDS);
        final ResponseMessage response = (ResponseMessage) responseData.getMessageData();

        switch (response.getResult()) {
        case NOT_FOUND:
            throw new ResponseNotFoundException(ComponentType.WS_MICROGRIDS, "Response message not found.");
        case NOT_OK:
            if (response.getOsgpException() != null) {
                throw response.getOsgpException();
            }
            throw new TechnicalException(ComponentType.WS_MICROGRIDS, "Response message not ok.");
        case OK:
            if (response.getDataObject() != null) {
                return (GetDataResponse) response.getDataObject();
            }
            // Should not get here
            throw new TechnicalException(ComponentType.WS_MICROGRIDS, "Response message contains no data.");
        default:
            // Should not get here
            throw new TechnicalException(ComponentType.WS_MICROGRIDS, "Response message contains invalid result.");
        }

    }

    public String enqueueSetDataRequest(final String organisationIdentification, final String deviceIdentification,
            final SetDataRequest setDataRequest) throws OsgpException {

        LOGGER.debug("enqueueSetDataRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final RtuDevice device = this.domainHelperService.findDevice(deviceIdentification);
        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_DATA);

        final MicrogridsRequestMessage message = new MicrogridsRequestMessage(MicrogridsRequestMessageType.SET_DATA,
                correlationUid, organisationIdentification, deviceIdentification, setDataRequest, null);

        try {
            this.requestMessageSender.send(message);
        } catch (final ArgumentNullOrEmptyException e) {
            throw new TechnicalException(ComponentType.WS_MICROGRIDS, e);
        }

        return correlationUid;
    }

    public EmptyResponse dequeueSetDataResponse(final String correlationUid) throws OsgpException {

        LOGGER.debug("dequeueSetDataRequest called with correlation uid {}", correlationUid);

        final ResponseData responseData = this.responseDataService.dequeue(correlationUid, ResponseMessage.class,
                ComponentType.WS_MICROGRIDS);
        final ResponseMessage response = (ResponseMessage) responseData.getMessageData();

        switch (response.getResult()) {
        case NOT_FOUND:
            throw new ResponseNotFoundException(ComponentType.WS_MICROGRIDS, "Response message not found.");
        case NOT_OK:
            if (response.getOsgpException() != null) {
                throw response.getOsgpException();
            }
            throw new TechnicalException(ComponentType.WS_MICROGRIDS, "Response message not ok.");
        case OK:
            return new EmptyResponse();
        default:
            // Should not get here
            throw new TechnicalException(ComponentType.WS_MICROGRIDS, "Response message contains invalid result.");
        }

    }
}
