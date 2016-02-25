/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.adhocmanagement;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.jboss.netty.channel.Channel;
import org.junit.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import com.alliander.osgp.acceptancetests.OslpTestUtils;
import com.alliander.osgp.acceptancetests.ProtocolInfoTestUtils;
import com.alliander.osgp.adapter.domain.publiclighting.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.application.services.DeviceRegistrationService;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDeviceBuilder;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerClient;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpDeviceService;
import com.alliander.osgp.adapter.ws.publiclighting.application.mapping.AdHocManagementMapper;
import com.alliander.osgp.adapter.ws.publiclighting.application.services.AdHocManagementService;
import com.alliander.osgp.adapter.ws.publiclighting.endpoints.PublicLightingAdHocManagementEndpoint;
import com.alliander.osgp.adapter.ws.publiclighting.infra.jms.PublicLightingResponseMessageFinder;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.AsyncRequest;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceAuthorizationBuilder;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.Oslp.Status;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Configurable
@DomainSteps
public class ResumeScheduleSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResumeScheduleSteps.class);

    private static final String ORGANISATION_ID = "ORGANISATION-01";
    private static final String ORGANISATION_PREFIX = "ORG";
    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";

    // TODO - Add as parameters to tests
    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // WS Adapter fields
    private PublicLightingAdHocManagementEndpoint adHocManagementEndpoint;

    @Autowired
    @Qualifier("wsPublicLightingAdHocManagementService")
    private AdHocManagementService adHocManagementService;

    private ResumeScheduleRequest request;
    private ResumeScheduleAsyncResponse resumeScheduleAsyncResponse;
    private ResumeScheduleAsyncRequest resumeScheduleAsyncRequest;
    private ResumeScheduleResponse response;

    @Autowired
    @Qualifier("wsPublicLightingIncomingResponsesMessageFinder")
    private PublicLightingResponseMessageFinder publicLightingResponseMessageFinder;

    @Autowired
    @Qualifier("wsPublicLightingIncomingResponsesJmsTemplate")
    private JmsTemplate publicLightingResponsesJmsTemplate;

    // Domain Adapter fields
    @Autowired
    @Qualifier("domainPublicLightingOutgoingWebServiceResponseMessageSender")
    private WebServiceResponseMessageSender webServiceResponseMessageSenderMock;

    private Ssld device;
    private Organisation organisation;

    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private SsldRepository ssldRepositoryMock;
    @Autowired
    private OrganisationRepository organisationRepositoryMock;
    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepositoryMock;
    @Autowired
    private DeviceLogItemRepository deviceLogItemRepositoryMock;

    // Protocol Adapter fields
    @Autowired
    private DeviceRegistrationService deviceRegistrationService;
    @Autowired
    private OslpDeviceService oslpDeviceService;
    private OslpDevice oslpDevice;
    @Autowired
    private OslpDeviceRepository oslpDeviceRepositoryMock;

    private OslpEnvelope oslpRequest;
    private OslpEnvelope oslpResponse;
    private OslpChannelHandlerClient oslpChannelHandler;
    @Autowired
    private Channel channelMock;

    // Test fields
    private Throwable throwable;

    // === GIVEN ===

    @DomainStep("a resume schedule request for device (.*) with index (.*), and isimmediate (.*)")
    public void givenAResumeScheduleRequest(final String device, final String index, final String isImmediate) {
        LOGGER.info("GIVEN: \"a resume schedule request for device {} with index {}, and isimmediate {}\".",
                new Object[] { device, index, isImmediate });

        this.setUp();

        this.request = new ResumeScheduleRequest();
        this.request.setDeviceIdentification(device);
        if (!StringUtils.isBlank(index)) {
            this.request.setIndex(Integer.parseInt(index));
        } else {
            this.request.setIndex(0); // default value 0
        }
        if (!StringUtils.isBlank(isImmediate)) {
            this.request.setIsImmediate(Boolean.parseBoolean(isImmediate));
        } else {
            this.request.setIsImmediate(false);
        }
    }

    @DomainStep("the resume schedule request refers to a device (.*) with status (.*), and hasschedule (.*)")
    public void givenADevice(final String deviceIdentification, final String status, final Boolean hasSchedule)
            throws Exception {
        LOGGER.info("GIVEN: \"the resume schedule reqeust refers to a device {} with status {}, and hasschedule {}\".",
                new Object[] { deviceIdentification, status, hasSchedule });

        switch (status.toUpperCase()) {
        case "ACTIVE":
            this.createDevice(deviceIdentification, true);

            this.device.setHasSchedule(hasSchedule != null ? hasSchedule : false);
            when(this.deviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);
            when(this.ssldRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);
            when(this.ssldRepositoryMock.findOne(1L)).thenReturn(this.device);
            when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(
                    this.oslpDevice);
            when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);

            final com.alliander.osgp.oslp.Oslp.ResumeScheduleResponse resumeScheduleResponse = com.alliander.osgp.oslp.Oslp.ResumeScheduleResponse
                    .newBuilder().setStatus(Status.OK).build();

            this.oslpResponse = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                    .withPayloadMessage(Message.newBuilder().setResumeScheduleResponse(resumeScheduleResponse).build())
                    .build();

            this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpResponse,
                    this.channelMock, this.device.getNetworkAddress());
            this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
            this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);

            break;
        case "UNKNOWN":
            when(this.deviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(null);
            when(this.ssldRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(null);
            when(this.ssldRepositoryMock.findOne(1L)).thenReturn(null);
            break;
        case "UNREGISTERED":
            this.createDevice(deviceIdentification, false);
            when(this.deviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);
            when(this.ssldRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);
            when(this.ssldRepositoryMock.findOne(1L)).thenReturn(this.device);
            break;
        default:
            throw new Exception("Unknown device status");
        }
    }

    @DomainStep("the resume schedule request refers to an organisation")
    public void givenAnAuthorisedOrganisation() {
        LOGGER.info("GIVEN: \"the resume schedule request refers to an organisation {}\".", ORGANISATION_ID);

        this.organisation = new Organisation(ORGANISATION_ID, ORGANISATION_ID, ORGANISATION_PREFIX,
                PlatformFunctionGroup.USER);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID)).thenReturn(
                this.organisation);

        final List<DeviceAuthorization> authorizations = new ArrayList<>();
        authorizations.add(new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.organisation)
                .withFunctionGroup(DeviceFunctionGroup.AD_HOC).build());
        when(this.deviceAuthorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device))
                .thenReturn(authorizations);
    }

    @DomainStep("a resume schedule response request with correlationId (.*) and deviceId (.*)")
    public void givenAResumeScheduleResponseRequest(final String correlationId, final String deviceId) {
        LOGGER.info("GIVEN: \"a resume schedule response request with correlationId {} and deviceId {}\".",
                correlationId, deviceId);

        this.setUp();

        this.resumeScheduleAsyncRequest = new ResumeScheduleAsyncRequest();

        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationId);
        asyncRequest.setDeviceId(deviceId);

        this.resumeScheduleAsyncRequest.setAsyncRequest(asyncRequest);
    }

    @DomainStep("a resume schedule response message with correlationId (.*), deviceId (.*), qresult (.*) and qdescription (.*) is found in the queue (.*)")
    public void givenAResumeScheduleResponseMessageIsFoundInTheQueue(final String correlationId, final String deviceId,
            final String qresult, final String qdescription, final Boolean isFound) {
        LOGGER.info(
                "GIVEN: \"a resume schedule response message with correlationId {}, deviceId {}, qresult {} and qdescription {} is found in the queue {}\".",
                correlationId, deviceId, qresult, qdescription, isFound);
        if (isFound) {
            final ObjectMessage messageMock = mock(ObjectMessage.class);

            try {
                when(messageMock.getJMSCorrelationID()).thenReturn(correlationId);
                when(messageMock.getStringProperty("OrganisationIdentification")).thenReturn(
                        this.organisation.getOrganisationIdentification());
                when(messageMock.getStringProperty("DeviceIdentification")).thenReturn(deviceId);

                final ResponseMessageResultType result = ResponseMessageResultType.valueOf(qresult);
                Serializable dataObject = null;
                OsgpException exception = null;
                if (result.equals(ResponseMessageResultType.NOT_OK)) {
                    dataObject = new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                            ComponentType.UNKNOWN, new ValidationException());
                    exception = (OsgpException) dataObject;
                }
                final ResponseMessage message = new ResponseMessage(correlationId,
                        this.organisation.getOrganisationIdentification(), deviceId, result, exception, dataObject);
                when(messageMock.getObject()).thenReturn(message);
            } catch (final JMSException e) {
                e.printStackTrace();
            }

            when(this.publicLightingResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(messageMock);
        } else {
            when(this.publicLightingResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(null);
        }
    }

    // === WHEN ===

    @DomainStep("the resume schedule request is received")
    public void whenTheRequestIsReceived() {
        LOGGER.info("WHEN: \"the request is received\".");

        try {
            this.resumeScheduleAsyncResponse = this.adHocManagementEndpoint.resumeSchedule(
                    this.organisation.getOrganisationIdentification(), this.request);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("the resume schedule response request is received")
    public void whenTheResumeScheduleResponseRequestIsReceived() {
        LOGGER.info("WHEN: \"the resume schedule response request is received\".");

        try {
            this.response = this.adHocManagementEndpoint.getResumeScheduleResponse(
                    this.organisation.getOrganisationIdentification(), this.resumeScheduleAsyncRequest);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("the resume schedule request should return an async response with a correlationId and deviceId (.*)")
    public boolean thenResumeScheduleRequestShouldReturnAsyncResponse(final String deviceId) {
        LOGGER.info(
                "THEN: \"the resume schedule request should return an async response with a correlationId and deviceId {}\".",
                deviceId);

        try {
            Assert.assertNotNull("asyncResponse should not be null", this.resumeScheduleAsyncResponse);
            Assert.assertNotNull("CorrelationId should not be null", this.resumeScheduleAsyncResponse
                    .getAsyncResponse().getCorrelationUid());
            Assert.assertNotNull("DeviceId should not be null", this.resumeScheduleAsyncResponse.getAsyncResponse()
                    .getDeviceId());
            Assert.assertNull("Throwable should be null", this.throwable);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the resume schedule request should return a validation error")
    public boolean thenTheResumeScheduleRequestShouldReturnAValidationError() {
        try {
            Assert.assertNotNull("Throwable should not be null", this.throwable);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("a resume schedule oslp message is sent to device (.*) should be (.*)")
    public boolean thenAnOslpMessageShouldBeSent(final String device, final Boolean isMessageSent) {
        LOGGER.info("THEN: \"a resume schedule oslp message is sent to device {} should be {}.\"", device,
                isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, timeout(10000).times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpRequest = argument.getValue();

                Assert.assertTrue("Message should contain resume schedule request.", this.oslpRequest
                        .getPayloadMessage().hasResumeScheduleRequest());
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the resume schedule response request should return a resume schedule response with result (.*) and description (.*)")
    public boolean thenTheResumeScheduleResponseRequestShouldReturn(final String result, final String description) {
        LOGGER.info(
                "THEN: \"the resume schedule response request should return a stop device test response with result {} and description {}\".",
                result, description);

        try {
            if ("NOT_OK".equals(result)) {
                Assert.assertNull("Set Schedule Response should be null", this.response);
                Assert.assertNotNull("Throwable should not be null", this.throwable);
                Assert.assertTrue("Throwable should contain a validation exception",
                        this.throwable.getCause() instanceof ValidationException);
            } else {

                Assert.assertNotNull("Response should not be null", this.response);

                final String expected = result.equals("NULL") ? null : result;
                final String actual = this.response.getResult().toString();

                Assert.assertTrue("Invalid result, found: " + actual + " , expected: " + expected,
                        (actual == null && expected == null) || actual.equals(expected));
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("an ovl resume schedule message with result (.*) and description (.*) should be sent to the ovl out queue")
    public boolean thenAnOvlResumeScheduleMessageShouldBeSent(final String result, final String description) {
        LOGGER.info(
                "THEN: \"an ovl resume schedule message with result {} and description {} should be sent to the ovl out queue.\"",
                result, description);

        try {
            final ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
            verify(this.webServiceResponseMessageSenderMock, timeout(10000).times(1)).send(argument.capture());

            final String expected = result.equals("NULL") ? null : result;
            final String actual = argument.getValue().getResult().getValue();

            Assert.assertTrue("Invalid result, found: " + actual + " , expected: " + expected, actual.equals(expected));

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    private void createDevice(final String deviceIdentification, final boolean activated) {
        LOGGER.info("Creating device [{}] with active [{}]", deviceIdentification, activated);

        this.device = (Ssld) new DeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withNetworkAddress(activated ? InetAddress.getLoopbackAddress() : null)
                .withPublicKeyPresent(PUBLIC_KEY_PRESENT)
                .withProtocolInfo(ProtocolInfoTestUtils.getProtocolInfo(PROTOCOL, PROTOCOL_VERSION))
                .ofDeviceType("SSLD").isActivated(activated).build();

        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withDeviceUid(DEVICE_UID).build();
    }

    // === Private methods ===

    private void setUp() {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.ssldRepositoryMock,
                this.organisationRepositoryMock, this.deviceAuthorizationRepositoryMock,
                this.deviceLogItemRepositoryMock, this.channelMock, this.oslpDeviceRepositoryMock,
                this.webServiceResponseMessageSenderMock });

        this.adHocManagementEndpoint = new PublicLightingAdHocManagementEndpoint(this.adHocManagementService,
                new AdHocManagementMapper());
        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        this.request = null;
        this.response = null;
        this.resumeScheduleAsyncRequest = null;
        this.resumeScheduleAsyncResponse = null;
        this.throwable = null;
    }

}
