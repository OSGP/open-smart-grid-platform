/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.deviceinstallation;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.commons.codec.binary.Base64;
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
import com.alliander.osgp.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.application.services.DeviceRegistrationService;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDeviceBuilder;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerClient;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpDeviceService;
import com.alliander.osgp.adapter.ws.core.application.mapping.DeviceInstallationMapper;
import com.alliander.osgp.adapter.ws.core.application.services.DeviceInstallationService;
import com.alliander.osgp.adapter.ws.core.endpoints.DeviceInstallationEndpoint;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonResponseMessageFinder;
import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestResponse;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceAuthorizationBuilder;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
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
public class StartDeviceTestSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartDeviceTestSteps.class);

    // private static final String DEVICE_ID = "DEVICE-01";
    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";

    private static final String ORGANISATION_PREFIX = "ORG";
    // private static final String DEVICE_ID_EMPTY = "";
    // private static final String DEVICE_ID_SPACES = "   ";
    // private static final String DEVICE_ID_UNKNOWN = "DEVICE-02";

    private static final String ORGANISATION_ID_UNKNOWN = "UNKNOWN";
    private static final String ORGANISATION_ID_EMPTY = "";
    private static final String ORGANISATION_ID_SPACES = "   ";

    // TODO - Add as parameters to tests
    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // WS Adapter fields
    private DeviceInstallationEndpoint deviceInstallationEndpoint;

    private StartDeviceTestRequest request;
    private StartDeviceTestAsyncResponse startDeviceTestAsyncResponse;
    private StartDeviceTestAsyncRequest startDeviceTestAsyncRequest;
    private StartDeviceTestResponse response;

    @Autowired
    @Qualifier(value = "wsCoreDeviceInstallationService")
    private DeviceInstallationService deviceInstallationService;

    @Autowired
    @Qualifier(value = "wsCoreIncomingResponsesMessageFinder")
    private CommonResponseMessageFinder commonResponseMessageFinder;

    @Autowired
    @Qualifier("wsCoreIncomingResponsesJmsTemplate")
    private JmsTemplate commonResponsesJmsTemplate;

    // Domain Adapter fields
    @Autowired
    @Qualifier(value = "domainCoreOutgoingWebServiceResponsesMessageSender")
    private WebServiceResponseMessageSender webServiceResponseMessageSenderMock;

    private Device device;
    private Organisation organisation;

    @Autowired
    private DeviceRepository deviceRepositoryMock;
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

    @DomainStep("a start device test request for device (.*)")
    public void givenARequest(final String device) {
        LOGGER.info("GIVEN: a start device test request for device {}.", device);

        this.setUp();

        this.request = new StartDeviceTestRequest();
        this.request.setDeviceIdentification(device);
    }

    @DomainStep("the start device test request refers to a device (.*) with status (.*)")
    public void givenADeviceWithStatus(final String device, final String status) throws Exception {
        LOGGER.info("GIVEN: the start device test request refers to a device {} with status {}.", device, status);

        switch (status.toUpperCase()) {
        case "ACTIVE":
            this.createDevice(device, true);
            when(this.deviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.device);
            when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.oslpDevice);
            when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);
            break;
        case "UNKNOWN":
            when(this.deviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(null);
            break;
        case "UNREGISTERED":
            this.createDevice(device, false);
            when(this.deviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.device);
            break;
        default:
            throw new Exception("Unknown device status");
        }
    }

    @DomainStep("the start device test request refers to an organisation (.*)")
    public void givenAnAuthorisedOrganisation(final String organisation) {
        LOGGER.info("GIVEN: the start device test request refers to a organisation {}.", organisation);

        this.organisation = new Organisation(organisation, organisation, ORGANISATION_PREFIX,
                PlatformFunctionGroup.USER);

        if (organisation.equals(ORGANISATION_ID_UNKNOWN) || organisation.equals(ORGANISATION_ID_EMPTY)
                || organisation.equals(ORGANISATION_ID_SPACES)) {
            when(this.organisationRepositoryMock.findByOrganisationIdentification(organisation)).thenReturn(null);
        } else {
            when(this.organisationRepositoryMock.findByOrganisationIdentification(organisation)).thenReturn(
                    this.organisation);
        }

        final List<DeviceAuthorization> authorizations = new ArrayList<>();
        authorizations.add(new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.organisation)
                .withFunctionGroup(DeviceFunctionGroup.INSTALLATION).build());
        when(this.deviceAuthorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device))
                .thenReturn(authorizations);
    }

    @DomainStep("the start device test oslp message from the device")
    public void givenTheOslpResponse() {
        LOGGER.info("GIVEN: the start device test oslp message from the device.");

        final com.alliander.osgp.oslp.Oslp.StartSelfTestResponse startDeviceTestResponse = com.alliander.osgp.oslp.Oslp.StartSelfTestResponse
                .newBuilder().setStatus(Status.OK).build();

        this.oslpResponse = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setStartSelfTestResponse(startDeviceTestResponse).build())
                .build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpResponse,
                this.channelMock, this.device.getNetworkAddress());
        this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
        this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);
    }

    @DomainStep("the start device test oslp message returns failure (.*)")
    public void givenTheOslpFailure(final String oslpResponse) {
        LOGGER.info("the start device test oslp message returns failure {}.", oslpResponse);

    }

    @DomainStep("a start device test response request with correlationId (.*) and deviceId (.*)")
    public void givenAStartDeviceTestResponseRequest(final String correlationId, final String deviceId) {
        LOGGER.info("a start device test response request with correlationId {} and deviceId {}.", correlationId,
                deviceId);

        this.setUp();

        this.startDeviceTestAsyncRequest = new StartDeviceTestAsyncRequest();

        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationId);
        asyncRequest.setDeviceId(deviceId);

        this.startDeviceTestAsyncRequest.setAsyncRequest(asyncRequest);
    }

    @DomainStep("a start device test response message with correlationId (.*), deviceId (.*), qresult (.*) and qdescription (.*) is found in the queue (.*)")
    public void givenAStartDeviceTestResponseMessageIsFoundInTheQueue(final String correlationId,
            final String deviceId, final String qresult, final String qdescription, final Boolean isFound) {
        LOGGER.info(
                "a start device test response message with correlationId {}, deviceId {}, qresult {} and qdescription {} is found in the queue {}",
                correlationId, deviceId, qresult, qdescription, isFound);
        if (isFound) {
            final ObjectMessage messageMock = mock(ObjectMessage.class);

            try {
                when(messageMock.getJMSCorrelationID()).thenReturn(correlationId);
                when(messageMock.getStringProperty("OrganisationIdentification")).thenReturn(
                        this.organisation.getOrganisationIdentification());
                when(messageMock.getStringProperty("DeviceIdentification")).thenReturn(deviceId);
                final ResponseMessageResultType result = ResponseMessageResultType.valueOf(qresult);
                Object dataObject = null;
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

            when(this.commonResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(messageMock);
        } else {
            when(this.commonResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(null);
        }
    }

    // === WHEN ===

    @DomainStep("the start device test request is received")
    public void whenTheRequestIsReceived() {
        LOGGER.info("WHEN: the start device test request is received.");

        try {
            this.startDeviceTestAsyncResponse = this.deviceInstallationEndpoint.startDeviceTest(
                    this.organisation.getOrganisationIdentification(), this.request);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("the start device test response request is received")
    public void whenTheStartDeviceTestResponseRequestIsReceived() {
        LOGGER.info("WHEN: \"the start device test response request is received\".");

        try {
            this.response = this.deviceInstallationEndpoint.getStartDeviceTestResponse(
                    this.organisation.getOrganisationIdentification(), this.startDeviceTestAsyncRequest);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("the start device test request should return an async response with a correlationId and deviceId (.*)")
    public boolean thenStartDeviceTestShouldReturnAsyncResponse(final String deviceId) {
        LOGGER.info(
                "THEN: \"the start device test request should return an async response with a correlationId and deviceId {}\".",
                deviceId);

        try {
            Assert.assertNotNull("asyncResponse should not be null", this.startDeviceTestAsyncResponse);
            Assert.assertNotNull("CorrelationId should not be null", this.startDeviceTestAsyncResponse
                    .getAsyncResponse().getCorrelationUid());
            Assert.assertNotNull("DeviceId should not be null", this.startDeviceTestAsyncResponse.getAsyncResponse()
                    .getDeviceId());
            Assert.assertNull("Throwable should be null", this.throwable);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("a start device test oslp message is sent to device (.*) should be (.*)")
    public boolean thenAStartDeviceTestOslpMessageShouldBeSent(final String device, final Boolean isMessageSent) {
        LOGGER.info("THEN: a start device test oslp message is sent to device should be {}.", isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, timeout(1000).times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpRequest = argument.getValue();

                Assert.assertTrue("Message should contain start device test request.", this.oslpRequest
                        .getPayloadMessage().hasStartSelfTestRequest());
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the start device test response")
    public boolean thenTheResponseShouldContain() {
        LOGGER.info("THEN: the start device test response.");

        try {
            Assert.assertNotNull("Response should not be null", this.response);
            Assert.assertNull("Throwable should be null", this.throwable);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }

        return true;
    }

    @DomainStep("the start device test response should return result (.*)")
    public boolean thenTheResponseShouldReturn(final String result) {
        LOGGER.info("THEN: the start device test response should return {}.", result);

        if (result.toUpperCase().equals("OK")) {
            try {
                Assert.assertNotNull("Response should not be null", this.response);
                Assert.assertNull("Throwable should be null", this.throwable);
            } catch (final AssertionError e) {
                LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
                return false;
            }
        } else {
            try {
                Assert.assertNotNull("Throwable should not be null", this.throwable);
                Assert.assertEquals(result.toUpperCase(), this.throwable.getClass().getSimpleName().toUpperCase());
            } catch (final AssertionError e) {
                LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
                return false;
            }
        }

        return true;
    }

    @DomainStep("an ovl start device test message with result (.*) should be sent to the ovl out queue")
    public boolean thenAnOvlStartDeviceTestMessage(final String result) {
        LOGGER.info("THEN: an ovl start device test message with result {} should be sent to the ovl out queue.",
                result);

        try {
            final ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
            verify(this.webServiceResponseMessageSenderMock, timeout(1000).times(1)).send(argument.capture());

            final String expected = result.equals("NULL") ? null : result;
            final String actual = argument.getValue().getResult().getValue();

            Assert.assertTrue("Invalid result, found: " + actual + " , expected: " + expected, actual.equals(expected));

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the start device test response request should return a start device test response with result (.*) and description (.*)")
    public boolean thenTheStartDeviceTestResponseRequestShouldReturnAStartDeviceTestResponse(final String result,
            final String description) {
        LOGGER.info(
                "THEN: \"the start device test response request should return a start device test response with result {} and description {}",
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

    // === Private methods ===

    private void setUp() {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.organisationRepositoryMock,
                this.deviceAuthorizationRepositoryMock, this.deviceLogItemRepositoryMock, this.channelMock,
                this.webServiceResponseMessageSenderMock });

        this.deviceInstallationEndpoint = new DeviceInstallationEndpoint(this.deviceInstallationService,
                new DeviceInstallationMapper());
        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        this.startDeviceTestAsyncRequest = null;
        this.startDeviceTestAsyncResponse = null;

        this.request = null;
        this.response = null;

        this.throwable = null;
    }

    private void createDevice(final String deviceIdentification, final boolean activated) {
        LOGGER.info("Creating device [{}] with active [{}]", deviceIdentification, activated);

        this.device = new DeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withNetworkAddress(activated ? InetAddress.getLoopbackAddress() : null)
                .withPublicKeyPresent(PUBLIC_KEY_PRESENT)
                .withProtocolInfo(ProtocolInfoTestUtils.getProtocolInfo(PROTOCOL, PROTOCOL_VERSION))
                .isActivated(activated).build();

        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withDeviceUid(DEVICE_UID).build();
    }

}
