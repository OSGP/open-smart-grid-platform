/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
/*
 User story: Set Light

 Scenario 1: Receive A Set Light Request With A Single Light Value
 given 	a set light request for device @device with index @index, on @on, and dimvalue @dimvalue
 and 	the set light request refers to a device @device with status @status
 and 	the set light request refers to an organisation that is authorised
 when 	the set light request is received
 then 	the set light request should return a set light response with a correlationID
 and 	an ovl set light request message should be sent to the ovl in queue
 and 	an oslp set light request message is sent to the oslp out queue should be @ismessagesent
 and 	a set light oslp message is sent to device @device should be @ismessagesent
 and 	an oslp set light response message is sent to the oslp in queue should be @ismessagesent
 and 	an ovl set light result message is sent to the ovl out queue should be true
 and 	the ovl set light result message should contain a set light result @result

 Scenario 2: Receive A Set Light Request With Multiple Light Values
 given 	a set light request for device @device with @validnr valid light values and @invalidnr invalid light values
 and 	the set light request refers to a device @device with status @status
 and 	the set light request refers to an organisation that is authorised
 when 	the set light request is received
 then 	a correlationID @isgenerated
 and 	a message with @lightvaluenr lightvalues @isqueued
 and 	the set light request should return result @result

 Scenario 3: Receive A Get Set light Result Request
 given 	a get set light result request with correlationId @correlationId
 and 	a set light response message with correlationId @correlationId and content @content is found in the queue @isFound
 when 	the get set light result request is received
 then 	the get set light result request should return a get set light result response with result @result

 */

package com.alliander.osgp.acceptancetests.adhocmanagement;

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
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.LightValue;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetLightAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetLightAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetLightRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetLightResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.AsyncRequest;
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
public class SetLightSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetLightSteps.class);

    private static final String ORGANISATION_ID = "ORGANISATION-01";
    private static final String ORGANISATION_PREFIX = "ORG";
    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";

    // TODO - Add as parameters to tests
    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // WS Adapter fields
    private PublicLightingAdHocManagementEndpoint adHocManagementEndpoint;

    private SetLightRequest request;
    private SetLightAsyncResponse setLightAsyncResponse;
    private SetLightAsyncRequest setLightAsyncRequest;
    private SetLightResponse response;

    @Autowired
    @Qualifier("wsPublicLightingAdHocManagementService")
    private AdHocManagementService adHocManagementService;

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

    private OslpEnvelope oslpEnvelope;
    private OslpEnvelope oslpMessage;
    private OslpChannelHandlerClient oslpChannelHandler;
    @Autowired
    private Channel channelMock;

    // Test fields
    private Throwable throwable;

    // === GIVEN ===

    @DomainStep("a set light request for device (.*) with index (.*), on (.*), and dimvalue (.*)")
    public void givenASetLightRequest(final String device, final String index, final Boolean on, final String dimValue) {
        LOGGER.info("GIVEN: \"a set light request for device {} with index {}, on {}, and dimvalue {}\".",
                new Object[] { device, index, on, dimValue });

        this.setUp();

        final LightValue lv = new LightValue();
        if (index != null && !index.isEmpty()) {
            lv.setIndex(Integer.parseInt(index));
        }
        lv.setOn(on);
        if (dimValue != null && !dimValue.isEmpty()) {
            lv.setDimValue(Integer.parseInt(dimValue));
        }

        this.request = new SetLightRequest();
        this.request.setDeviceIdentification(device);
        this.request.getLightValue().add(lv);
    }

    @DomainStep("a set light request for device (.*) with (.*) valid light values and (.*) invalid light values")
    public void givenASetLightRequest(final String device, final Integer validNr, final Integer invalidNr) {
        LOGGER.info(
                "GIVEN: \"a set light request for device {} with {} valid light values and {} invalid light values\".",
                new Object[] { device, validNr, invalidNr });

        this.setUp();

        this.request = new SetLightRequest();
        this.request.setDeviceIdentification(device);

        for (int i = 1; i <= validNr; i++) {
            final LightValue lv = new LightValue();
            lv.setIndex(i);
            lv.setOn(true);
            lv.setDimValue(50);

            this.request.getLightValue().add(lv);
        }

        for (int i = 1; i <= invalidNr; i++) {
            final LightValue lv = new LightValue();
            lv.setIndex(i);
            lv.setOn(true);
            lv.setDimValue(150);

            this.request.getLightValue().add(lv);
        }
    }

    @DomainStep("the set light request refers to a device (.*) with status (.*)")
    public void givenTheSetLightRequestRefersToADeviceWithStatus(final String deviceIdentification, final String status)
            throws Exception {
        LOGGER.info("GIVEN: \"the set light request refers to a device {} with status {}\".", deviceIdentification,
                status);

        switch (status.toUpperCase()) {
        case "ACTIVE":
            this.createDevice(deviceIdentification, true);
            when(this.deviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);
            when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(
                    this.oslpDevice);
            when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);

            // device always responds ok
            final com.alliander.osgp.oslp.Oslp.SetLightResponse oslpResponse = com.alliander.osgp.oslp.Oslp.SetLightResponse
                    .newBuilder().setStatus(Status.OK).build();

            this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                    .withPayloadMessage(Message.newBuilder().setSetLightResponse(oslpResponse).build()).build();

            this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope,
                    this.channelMock, this.device.getNetworkAddress());
            this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
            this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);

            break;
        case "UNKNOWN":
            when(this.deviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(null);
            break;
        case "UNREGISTERED":
            this.createDevice(deviceIdentification, false);
            when(this.deviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);
            break;
        default:
            throw new Exception("Unknown device status");
        }
    }

    @DomainStep("the set light request refers to an organisation that is authorised")
    public void givenTheSetLightRequestRefersToAnOrganisationThatIsAuthorised() {
        LOGGER.info("GIVEN: \"the set light request refers to an organisation that is authorised\".");

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

    @DomainStep("a get set light response request with correlationId (.*) and deviceId (.*)")
    public void givenAGetSetLightResultRequestWithCorrelationId(final String correlationId, final String deviceId) {
        LOGGER.info("GIVEN: \"a get set light response with correlationId {} and deviceId {}\".", correlationId,
                deviceId);

        this.setUp();

        this.setLightAsyncRequest = new SetLightAsyncRequest();

        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationId);
        asyncRequest.setDeviceId(deviceId);

        this.setLightAsyncRequest.setAsyncRequest(asyncRequest);
    }

    @DomainStep("a set light response message with correlationId (.*), deviceId (.*), qresult (.*) and qdescription (.*) is found in the queue (.*)")
    public void givenASetLightResponseMessageIsFoundInTheQueue(final String correlationId, final String deviceId,
            final String qresult, final String qdescription, final Boolean isFound) {
        LOGGER.info(
                "GIVEN: \"a set light response message with correlationId {}, deviceId {}, qresult {} and qdescription {} is found {}\".",
                correlationId, deviceId, qresult, qdescription, isFound);

        if (isFound) {
            final ObjectMessage messageMock = mock(ObjectMessage.class);

            try {
                when(messageMock.getJMSCorrelationID()).thenReturn(correlationId);
                when(messageMock.getStringProperty("OrganisationIdentification")).thenReturn(ORGANISATION_ID);
                when(messageMock.getStringProperty("DeviceIdentification")).thenReturn(deviceId);
                final ResponseMessageResultType result = ResponseMessageResultType.valueOf(qresult);
                Object dataObject = null;
                OsgpException exception = null;
                if (result.equals(ResponseMessageResultType.NOT_OK)) {
                    dataObject = new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                            ComponentType.UNKNOWN, new ValidationException());
                    exception = (OsgpException) dataObject;
                }
                final ResponseMessage message = new ResponseMessage(correlationId, ORGANISATION_ID, deviceId, result,
                        exception, dataObject);
                when(messageMock.getObject()).thenReturn(message);
            } catch (final JMSException e) {
                LOGGER.error("JMSException", e);
            }

            when(this.publicLightingResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(messageMock);
        } else {
            when(this.publicLightingResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(null);
        }
    }

    // === WHEN ===

    @DomainStep("the set light request is received")
    public void whenTheSetLightRequestIsReceived() {
        LOGGER.info("WHEN: \"the set light request is received\".");

        try {
            this.setLightAsyncResponse = this.adHocManagementEndpoint.setLight(ORGANISATION_ID, this.request);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("the get set light response request is received")
    public void whenTheGetSetLightResultReqeustIsReceived() {
        LOGGER.info("WHEN: \"the set light request is received\".");

        try {
            this.response = this.adHocManagementEndpoint
                    .getSetLightResponse(ORGANISATION_ID, this.setLightAsyncRequest);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("the set light request should return an async response with a correlationId and deviceId (.*)")
    public boolean thenTheSetLightRequestShouldReturnASetLightResponseWithACorrelationID(final String deviceId) {
        LOGGER.info(
                "THEN: \"the set light request should return a set light response with a correlationId and deviceId {}\".",
                deviceId);

        try {
            Assert.assertNotNull("Set Light Async Response should not be null", this.setLightAsyncResponse);
            Assert.assertNotNull("Async Response should not be null", this.setLightAsyncResponse.getAsyncResponse());
            Assert.assertNotNull("CorrelationId should not be null", this.setLightAsyncResponse.getAsyncResponse()
                    .getCorrelationUid());
            Assert.assertNull("Throwable should be null", this.throwable);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the set light request should return a validation error")
    public boolean thenTheSetLightRequestShouldReturnAValidationError() {
        LOGGER.info("THEN: \"the set light request should return a validation error\".");

        try {
            Assert.assertNull("Set Light Async Response should be null", this.setLightAsyncResponse);
            Assert.assertNotNull("Throwable should not be null", this.throwable);
            Assert.assertTrue(this.throwable.getCause() instanceof ValidationException);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("a set light oslp message is sent to device (.*) should be (.*)")
    public boolean thenASetLightOslpMessageShouldBeSent(final String device, final Boolean isMessageSent) {
        LOGGER.info("THEN: \"a set light oslp message is sent to device [{}] should be [{}]\".", device, isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, timeout(10000).times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpMessage = argument.getValue();

                Assert.assertTrue("Message should contain set light request.", this.oslpMessage.getPayloadMessage()
                        .hasSetLightRequest());
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("an ovl set light result message with result (.*) and description (.*) should be sent to the ovl out queue")
    public boolean thenAnOvlSetLightResultMessageShouldBeSentToTheOvlOutQueue(final String result,
            final String description) {
        LOGGER.info(
                "THEN: \"an ovl set light result message with result [{}] and description [{}] should be sent to the ovl out queue\".",
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

    @DomainStep("a set light oslp message with (.*) light values is sent to device (.*) should be (.*)")
    public boolean thenASetLightOslpMessageShouldBeSent(final Integer lightvalueNr, final String device,
            final Boolean isMessageSent) {
        LOGGER.info("THEN: \"a set light oslp message is sent to device [{}] should be [{}]\".", device, isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, timeout(1000).times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpMessage = argument.getValue();

                Assert.assertTrue("Message should contain set light request.", this.oslpMessage.getPayloadMessage()
                        .hasSetLightRequest());

                Assert.assertTrue("Message should contain " + lightvalueNr + " light values.", this.oslpMessage
                        .getPayloadMessage().getSetLightRequest().getValuesCount() == lightvalueNr);
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the set light request should return result (.*)")
    public boolean thenTheSetLightRequestShouldReturn(final String result) {
        LOGGER.info("THEN: \"the set light request should return result {}", result);

        if (result.toUpperCase().equals("OK")) {
            try {
                Assert.assertNotNull("Response should not be null", this.response);
                Assert.assertNull("Throwable should be null", this.throwable);
            } catch (final Exception e) {
                LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
                return false;
            }
        } else {
            try {
                Assert.assertNotNull("Throwable should not be null", this.throwable);
                Assert.assertEquals(result.toUpperCase(), this.throwable.getCause().getClass().getSimpleName()
                        .toUpperCase());
            } catch (final Exception e) {
                LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
                return false;
            }
        }
        return true;
    }

    @DomainStep("the get set light response request should return a set light response with result (.*) and description (.*)")
    public boolean thenTheGetSetLightResultRequestShouldReturnAGetSetLightResultResponseWithResult(final String result,
            final String description) {
        LOGGER.info(
                "THEN: \"the get set light result request should return a get set light response with result {} and description {}",
                result, description);

        try {
            if ("NOT_OK".equals(result)) {
                Assert.assertNull("Set Schedule Response should be null", this.response);
                Assert.assertNotNull("Throwable should not be null", this.throwable);
                Assert.assertTrue("Throwable should contain a validation exception",
                        this.throwable.getCause() instanceof ValidationException);
            } else {

                Assert.assertNotNull("Response should not be null", this.response);

                final String expectedResult = result.equals("NULL") ? null : result;
                final String actualResult = this.response.getResult().toString();

                Assert.assertTrue("Invalid result, found: " + actualResult + " , expected: " + expectedResult,
                        (actualResult == null && expectedResult == null) || actualResult.equals(expectedResult));
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    // === Private Methods ===

    private void setUp() {
        LOGGER.info("Resetting mocks");
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.organisationRepositoryMock,
                this.deviceAuthorizationRepositoryMock, this.deviceLogItemRepositoryMock, this.channelMock,
                this.oslpDeviceRepositoryMock, this.webServiceResponseMessageSenderMock });

        LOGGER.info("Setting endpoint");
        this.adHocManagementEndpoint = new PublicLightingAdHocManagementEndpoint(this.adHocManagementService,
                new AdHocManagementMapper());

        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        this.setLightAsyncRequest = null;
        this.setLightAsyncResponse = null;

        this.throwable = null;
        this.request = null;
        this.response = null;
    }

    private void createDevice(final String deviceIdentification, final Boolean activated) {
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
