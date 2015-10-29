/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.firmwaremanagement;

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
import com.alliander.osgp.adapter.ws.core.application.services.FirmwareManagementService;
import com.alliander.osgp.adapter.ws.core.endpoints.FirmwareManagementEndpoint;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonResponseMessageFinder;
import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionResponse;
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
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Configurable
@DomainSteps
public class GetFirmwareVersionSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFirmwareVersionSteps.class);

    private static final String ORGANISATION_ID = "ORGANISATION-01";
    private static final String ORGANISATION_PREFIX = "ORG";
    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";

    // TODO - Add as parameters to tests
    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // WS Adapter fields
    private FirmwareManagementEndpoint firmwareManagementEndpoint;

    private GetFirmwareVersionRequest request;
    private GetFirmwareVersionResponse response;
    private GetFirmwareVersionAsyncRequest asyncRequest;
    private GetFirmwareVersionAsyncResponse asyncResponse;

    @Autowired
    @Qualifier("wsCoreFirmwareManagementService")
    private FirmwareManagementService firmwareManagementService;

    @Autowired
    @Qualifier("wsCoreIncomingResponsesMessageFinder")
    private CommonResponseMessageFinder commonResponseMessageFinder;

    @Autowired
    @Qualifier("wsCoreIncomingResponsesJmsTemplate")
    private JmsTemplate commonResponsesJmsTemplate;

    // Domain Adapter fields
    @Autowired
    @Qualifier("domainCoreOutgoingWebServiceResponsesMessageSender")
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

    // Protocol adapter fields
    @Autowired
    private DeviceRegistrationService deviceRegistrationService;
    @Autowired
    private OslpDeviceService oslpDeviceService;
    private OslpDevice oslpDevice;
    @Autowired
    private OslpDeviceRepository oslpDeviceRepositoryMock;
    private OslpChannelHandlerClient oslpChannelHandler;
    @Autowired
    private Channel channelMock;
    private OslpEnvelope oslpRequest;
    private OslpEnvelope oslpResponse;

    // Test fields
    private Throwable throwable;

    // === GIVEN ===

    @DomainStep("a get firmware version request for device (.*)")
    public void givenARequest(final String device) {
        LOGGER.info("GIVEN: a get firmware version request for device {}.", device);

        this.setUp();

        this.request = new GetFirmwareVersionRequest();
        this.request.setDeviceIdentification(device);
    }

    @DomainStep("the get firmware version request refers to a device (.*) with status (.*)")
    public void givenADeviceWithStatus(final String device, final String status) throws Exception {
        LOGGER.info("GIVEN: the get firmware version request refers to a device {} with status {}.", device, status);

        switch (status.toUpperCase()) {
        case "ACTIVE":
            this.createDevice(device, true);
            this.createOslpDevice(device);
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

    @DomainStep("the get firmware version request refers to an organisation that is authorised")
    public void givenAnAuthorisedOrganisation() {
        LOGGER.info("GIVEN: the get firmware version request refers to an organisation that is authorised.");

        this.organisation = new Organisation(ORGANISATION_ID, ORGANISATION_ID, ORGANISATION_PREFIX,
                PlatformFunctionGroup.USER);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID)).thenReturn(
                this.organisation);

        final List<DeviceAuthorization> authorizations = new ArrayList<>();
        authorizations.add(new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.organisation)
                .withFunctionGroup(DeviceFunctionGroup.FIRMWARE).build());
        when(this.deviceAuthorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device))
                .thenReturn(authorizations);
    }

    @DomainStep("the get firmware version oslp message from the device contains (.*)")
    public void givenTheOslpResponse(final String firmwareVersion) {
        LOGGER.info("GIVEN: the get firmware version oslp message from the device contains {}.", firmwareVersion);

        final com.alliander.osgp.oslp.Oslp.GetFirmwareVersionResponse getFirmwareVersionResponse = com.alliander.osgp.oslp.Oslp.GetFirmwareVersionResponse
                .newBuilder().setFirmwareVersion(firmwareVersion).build();

        this.oslpResponse = OslpTestUtils
                .createOslpEnvelopeBuilder()
                .withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(
                        Message.newBuilder().setGetFirmwareVersionResponse(getFirmwareVersionResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpResponse,
                this.channelMock, this.device.getNetworkAddress());
        this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
        this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);
    }

    @DomainStep("the get firmware version oslp message returns failure (.*)")
    public void givenTheOslpFailure(final String oslpResponse) {
        LOGGER.info("the get firmware version oslp message returns failure {}.", oslpResponse);

    }

    @DomainStep("a get firmware version response request with correlationId (.*) and deviceId (.*)")
    public void givenAGetFirmwareVersionResponseRequest(final String correlationId, final String deviceId) {
        LOGGER.info("a get firmware response request with correlationId {} and deviceId {}.", correlationId, deviceId);

        this.setUp();

        this.asyncRequest = new GetFirmwareVersionAsyncRequest();

        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationId);
        asyncRequest.setDeviceId(deviceId);

        this.asyncRequest.setAsyncRequest(asyncRequest);
    }

    @DomainStep("a get firmware version response message with correlationId (.*), deviceId (.*), qresult (.*), qdescription (.*) and firmwareversion (.*) is found in the queue (.*)")
    public void givenAGetFirmwareVersionResponseMessageIsFoundInTheQueue(final String correlationId,
            final String deviceId, final String qresult, final String qdescription, final String firmwareversion,
            final Boolean isFound) {
        LOGGER.info(
                "a firmware response message with correlationId {}, deviceId {}, qresult {}, qdescription {} and firmwareversion {} is found in the queue {}",
                correlationId, deviceId, qresult, qdescription, firmwareversion, isFound);
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
                } else {
                    dataObject = firmwareversion;
                }
                final ResponseMessage message = new ResponseMessage(correlationId, ORGANISATION_ID, deviceId, result,
                        exception, dataObject);
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

    @DomainStep("the get firmware version request is received")
    public void whenTheRequestIsReceived() {
        LOGGER.info("WHEN: the get firmware version request is received.");

        try {
            this.asyncResponse = this.firmwareManagementEndpoint.getFirmwareVersion(ORGANISATION_ID, this.request);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("the get firmware version response request is received")
    public void whenTheGetFirmwareVersionResponseRequestIsReceived() {
        LOGGER.info("WHEN: \"the get firmware response request is received\".");

        try {
            this.response = this.firmwareManagementEndpoint.getGetFirmwareVersionResponse(ORGANISATION_ID,
                    this.asyncRequest);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("the get firmware version request should return an async response with a correlationId and deviceId (.*)")
    public boolean thenGetFirmwareVersionShouldReturnAsyncResponse(final String deviceId) {
        LOGGER.info(
                "THEN: \"the get firmware version request should return an async response with a correlationId and deviceId {}\".",
                deviceId);

        // TODO Add check on device id
        try {
            Assert.assertNotNull("asyncResponse should not be null", this.asyncResponse);
            Assert.assertNotNull("CorrelationId should not be null", this.asyncResponse.getAsyncResponse()
                    .getCorrelationUid());
            Assert.assertNull("Throwable should be null", this.throwable);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("a get firmware version oslp message is sent to device (.*) should be (.*)")
    public boolean thenAGetFirmwareOslpMessageShouldBeSent(final String device, final Boolean isMessageSent) {
        LOGGER.info("THEN: a get firmware version oslp message is sent to device should be {}.", isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, timeout(10000).times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpRequest = argument.getValue();

                Assert.assertTrue("Message should contain get firmware version request.", this.oslpRequest
                        .getPayloadMessage().hasGetFirmwareVersionRequest());
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the get firmware version response should contain firmware version (.*)")
    public boolean thenTheResponseShouldContain(final String firmwareVersion) {
        LOGGER.info("THEN: the get firmware version response should return {}.", firmwareVersion);

        try {
            Assert.assertNotNull("Response should not be null", this.response);
            Assert.assertNull("Throwable should be null", this.throwable);
            Assert.assertEquals("Firmware version should equal expected value", firmwareVersion,
                    this.response.getFirmwareVersion());
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }

        return true;
    }

    @DomainStep("the get firmware version response should return result (.*)")
    public boolean thenTheResponseShouldReturn(final String result) {
        LOGGER.info("THEN: the get firmware version response should return {}.", result);

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

    @DomainStep("an ovl get firmware version message with result (.*) and firmwareversion (.*) should be sent to the ovl out queue")
    public boolean thenAnOvlGetFirmwareVersionMessage(final String result, final String firmwareversion) {
        LOGGER.info(
                "THEN: an ovl get firmware version message with result {} and firmwareversion {} should be sent to the ovl out queue.",
                result, firmwareversion);

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

    @DomainStep("the get firmware version response request should return a firmware response with result (.*), description (.*) and firmwareversion (.*)")
    public boolean thenTheGetFirmwareVersionResponseRequestShouldReturnAGetFirmwareVersionResponse(final String result,
            final String description, final String firmwareversion) {
        LOGGER.info(
                "THEN: \"the get firmware response request should return a firmware response with result {}, description {} and firmwareversion {}",
                result, description, firmwareversion);

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
                this.webServiceResponseMessageSenderMock, this.oslpDeviceRepositoryMock });

        this.firmwareManagementEndpoint = new FirmwareManagementEndpoint(this.firmwareManagementService);
        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        this.request = null;
        this.response = null;
        this.asyncRequest = null;
        this.asyncResponse = null;
        this.throwable = null;
    }

    private void createDevice(final String deviceIdentification, final boolean activated) {
        LOGGER.info("Creating device [{}] with active [{}]", deviceIdentification, activated);

        this.device = new DeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withNetworkAddress(activated ? InetAddress.getLoopbackAddress() : null)
                .withPublicKeyPresent(PUBLIC_KEY_PRESENT)
                .withProtocolInfo(ProtocolInfoTestUtils.getProtocolInfo(PROTOCOL, PROTOCOL_VERSION))
                .isActivated(activated).build();

    }

    private void createOslpDevice(final String deviceIdentification) {
        LOGGER.info("Creating oslp device [{}]", deviceIdentification);

        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withDeviceUid(DEVICE_UID).build();
    }

}
