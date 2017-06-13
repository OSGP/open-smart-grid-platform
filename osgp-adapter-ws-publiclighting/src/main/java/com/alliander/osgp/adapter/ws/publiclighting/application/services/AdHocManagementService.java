/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.publiclighting.application.services;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.publiclighting.infra.jms.PublicLightingRequestMessage;
import com.alliander.osgp.adapter.ws.publiclighting.infra.jms.PublicLightingRequestMessageSender;
import com.alliander.osgp.adapter.ws.publiclighting.infra.jms.PublicLightingRequestMessageType;
import com.alliander.osgp.adapter.ws.publiclighting.infra.jms.PublicLightingResponseMessageFinder;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.LightValue;
import com.alliander.osgp.domain.core.valueobjects.LightValueMessageDataContainer;
import com.alliander.osgp.domain.core.valueobjects.ResumeScheduleData;
import com.alliander.osgp.domain.core.valueobjects.TransitionMessageDataContainer;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Service(value = "wsPublicLightingAdHocManagementService")
@Transactional(value = "transactionManager")
@Validated
public class AdHocManagementService {

    private static final int PAGE_SIZE = 30;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    @Qualifier("wsPublicLightingOutgoingRequestsMessageSender")
    private PublicLightingRequestMessageSender publicLightingRequestMessageSender;

    @Autowired
    @Qualifier("wsPublicLightingIncomingResponsesMessageFinder")
    private PublicLightingResponseMessageFinder publicLightingResponseMessageFinder;

    public AdHocManagementService() {
        // Parameterless constructor required for transactions
    }

    public Page<Device> findAllDevices(@Identification final String organisationIdentification, final int pageNumber)
            throws FunctionalException {
        LOGGER.debug("findAllDevices called with organisation {} and pageNumber {}", organisationIdentification,
                pageNumber);

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

        final PageRequest request = new PageRequest(pageNumber, PAGE_SIZE, Sort.Direction.DESC, "deviceIdentification");
        return this.deviceRepository.findAllAuthorized(organisation, request);
    }

    public String enqueueSetLightRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            @Size(min = 1, max = 6) @Valid final List<LightValue> lightValues) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_LIGHT);
        this.domainHelperService.isInMaintenance(device);

        LOGGER.debug("enqueueSetLightRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final LightValueMessageDataContainer lightValueMessageDataContainer = new LightValueMessageDataContainer(
                lightValues);

        final PublicLightingRequestMessage message = new PublicLightingRequestMessage(
                PublicLightingRequestMessageType.SET_LIGHT, correlationUid, organisationIdentification,
                deviceIdentification, lightValueMessageDataContainer, null);

        this.publicLightingRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueSetLightResponse(final String correlationUid) throws OsgpException {

        return this.publicLightingResponseMessageFinder.findMessage(correlationUid);
    }

    public String enqueueGetStatusRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_STATUS);

        LOGGER.debug("enqueueGetStatusRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final PublicLightingRequestMessage message = new PublicLightingRequestMessage(
                PublicLightingRequestMessageType.GET_LIGHT_STATUS, correlationUid, organisationIdentification,
                deviceIdentification, null, null);

        this.publicLightingRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueGetStatusResponse(final String correlationUid) throws OsgpException {

        return this.publicLightingResponseMessageFinder.findMessage(correlationUid);
    }

    public String enqueueResumeScheduleRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, @Valid final ResumeScheduleData resumeScheduleData)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.RESUME_SCHEDULE);
        this.domainHelperService.isInMaintenance(device);

        LOGGER.debug("enqueueResumeScheduleRequest called with organisation {}, device {} and resumeScheduleData {} ",
                organisationIdentification, deviceIdentification, resumeScheduleData);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final PublicLightingRequestMessage message = new PublicLightingRequestMessage(
                PublicLightingRequestMessageType.RESUME_SCHEDULE, correlationUid, organisationIdentification,
                deviceIdentification, resumeScheduleData, null);

        this.publicLightingRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueResumeScheduleResponse(final String correlationUid) throws OsgpException {
        return this.publicLightingResponseMessageFinder.findMessage(correlationUid);
    }

    public String enqueueTransitionRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final TransitionMessageDataContainer transitionMessageDataContainer) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_TRANSITION);
        this.domainHelperService.isInMaintenance(device);

        LOGGER.debug("enqueueTransitionRequest called with organisation {}, device {} ", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final PublicLightingRequestMessage message = new PublicLightingRequestMessage(
                PublicLightingRequestMessageType.SET_TRANSITION, correlationUid, organisationIdentification,
                deviceIdentification, transitionMessageDataContainer, null);

        this.publicLightingRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueSetTransitionResponse(final String correlationUid) throws OsgpException {
        return this.publicLightingResponseMessageFinder.findMessage(correlationUid);
    }

}