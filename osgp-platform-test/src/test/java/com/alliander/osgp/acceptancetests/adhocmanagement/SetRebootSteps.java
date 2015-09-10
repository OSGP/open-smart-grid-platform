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
import com.alliander.osgp.adapter.ws.core.application.services.AdHocManagementService;
import com.alliander.osgp.adapter.ws.core.endpoints.AdHocManagementEndpoint;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonResponseMessageFinder;
import com.alliander.osgp.adapter.ws.schema.core.adhocmanagement.SetRebootAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.adhocmanagement.SetRebootAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.adhocmanagement.SetRebootRequest;
import com.alliander.osgp.adapter.ws.schema.core.adhocmanagement.SetRebootResponse;
import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.core.application.services.DeviceResponseMessageService;
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
public class SetRebootSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetRebootSteps.class);

    private static final String ORGANISATION_ID = "ORGANISATION-01";
    private static final String ORGANISATION_PREFIX = "ORG";
    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";

    // TODO - Add as parameters to tests
    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // WS Adapter fields
    private AdHocManagementEndpoint adHocManagementEndpoint;

    private SetRebootRequest request;
    private SetRebootAsyncResponse setRebootAsyncResponse;
    private SetRebootAsyncRequest setRebootAsyncRequest;
    private SetRebootResponse response;

    @Autowired
    @Qualifier("wsCoreAdHocManagementService")
    private AdHocManagementService adHocManagementService;

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

    @Autowired
    private DeviceResponseMessageService deviceResponseMessageService;

    private Device device;
    private Organisation organisation;

    private OslpDevice oslpDevice;

    private OslpChannelHandlerClient oslpChannelHandler;
    private OslpEnvelope oslpRequest;
    private OslpEnvelope oslpResponse;

    @Autowired
    private DeviceRegistrationService deviceRegistrationService;

    // Repository mocks
    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private OrganisationRepository organisationRepositoryMock;
    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepositoryMock;
    @Autowired
    private DeviceLogItemRepository deviceLogItemRepositoryMock;
    @Autowired
    private OslpDeviceRepository oslpDeviceRepositoryMock;

    // Channel mock
    @Autowired
    private Channel channelMock;

    // Oslp Service
    @Autowired
    private OslpDeviceService oslpDeviceService;

    // Test fields
    private Throwable throwable;

    // === GIVEN ===

    @DomainStep("a set reboot request for device (.*)")
    public void givenASetRebootRequest(final String device) {
        LOGGER.info("GIVEN: a set reboot request for device {}.", device);

        this.setUp();

        this.request = new SetRebootRequest();
        this.request.setDeviceIdentification(device);
    }

    @DomainStep("the set reboot request refers to a device (.*) with status (.*)")
    public void givenADevice(final String device, final String status) throws Exception {
        LOGGER.info("GIVEN: the set reboot request refers to a device {} with status {}.", device, status);

        switch (status.toUpperCase()) {
        case "ACTIVE":
            this.createDevice(device, true);
            when(this.deviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.device);
            when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.oslpDevice);
            when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);
            this.initializeOslp();
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

    @DomainStep("the set reboot request refers to an organisation that is authorised")
    public void givenAnAuthorisedOrganisation() {
        LOGGER.info("GIVEN: the set reboot request refers to an organisation that is authorised.");

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

    @DomainStep("a get set reboot response request with correlationId (.*) and deviceId (.*)")
    public void givenAGetSetRebootResultRequestWithCorrelationId(final String correlationId, final String deviceId) {
        LOGGER.info("GIVEN: \"a get set reboot response with correlationId {} and deviceId {}\".", correlationId,
                deviceId);

        this.setUp();

        this.setRebootAsyncRequest = new SetRebootAsyncRequest();

        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationId);
        asyncRequest.setDeviceId(deviceId);

        this.setRebootAsyncRequest.setAsyncRequest(asyncRequest);
    }

    @DomainStep("a set reboot response message with correlationId (.*), deviceId (.*), qresult (.*) and qdescription (.*) is found in the queue (.*)")
    public void givenASetRebootResponseMessageIsFoundInTheQueue(final String correlationId, final String deviceId,
            final String qresult, final String qdescription, final Boolean isFound) {
        LOGGER.info(
                "GIVEN: \"a set reboot response message with correlationId {}, deviceId {}, qresult {} and qdescription {} is found {}\".",
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            when(this.commonResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(messageMock);
        } else {
            when(this.commonResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(null);
        }
    }

    // === WHEN ===

    @DomainStep("the set reboot request is received")
    public void whenTheSetRebootRequestIsReceived() {
        LOGGER.info("WHEN: \"the set reboot request is received\".");

        try {

            this.setRebootAsyncResponse = this.adHocManagementEndpoint.setReboot(ORGANISATION_ID, this.request);

            // Add sleep to enable queue processing
            for (int i = 0; i < 1000; i++) {
                Thread.sleep(1);
            }

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("the get set reboot response request is received")
    public void whenTheGetSetRebootResultRequestIsReceived() {
        LOGGER.info("WHEN: \"the set reboot request is received\".");

        try {
            this.response = this.adHocManagementEndpoint.getSetRebootResponse(ORGANISATION_ID,
                    this.setRebootAsyncRequest);

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("the set reboot request should return an async response with a correlationId and deviceId (.*)")
    public boolean thenTheSetRebootRequestShouldReturnASetRebootResponseWithACorrelationID(final String deviceId) {
        LOGGER.info(
                "THEN: \"the set reboot request should return a set reboot response with a correlationId and deviceId {}\".",
                deviceId);

        // TODO Add check on device id
        try {
            Assert.assertNotNull("Set Reboot Async Response should not be null", this.setRebootAsyncResponse);
            Assert.assertNotNull("Async Response should not be null", this.setRebootAsyncResponse.getAsyncResponse());
            Assert.assertNotNull("CorrelationId should not be null", this.setRebootAsyncResponse.getAsyncResponse()
                    .getCorrelationUid());
            Assert.assertNull("Throwable should be null", this.throwable);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("a set reboot oslp message is sent to the device should be (.*)")
    public boolean thenAnOslpMessageShouldBeSent(final Boolean isMessageSent) {
        LOGGER.info("THEN: a set reboot oslp message is sent to the device should be {}.", isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, timeout(1000).times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpRequest = argument.getValue();

                Assert.assertTrue("Message should contain set reboot request.", this.oslpRequest.getPayloadMessage()
                        .hasSetRebootRequest());
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("an ovl set reboot result message with result (.*) and description (.*) should be sent to the ovl out queue")
    public boolean thenAnOvlSetRebootResultMessageShouldBeSentToTheOvlOutQueue(final String result,
            final String description) {
        LOGGER.info(
                "THEN: \"an ovl set reboot result message with result [{}] and description [{}] should be sent to the ovl out queue\".",
                result, description);

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

    @DomainStep("a set reboot oslp message is sent to device (.*) should be (.*)")
    public boolean thenASetRebootOslpMessageShouldBeSent(final String device, final Boolean isMessageSent) {
        LOGGER.info("THEN: \"a set reboot oslp message is sent to device [{}] should be [{}]\".", device, isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, timeout(1000).times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpResponse = argument.getValue();

                Assert.assertTrue("Message should contain set reboot request.", this.oslpResponse.getPayloadMessage()
                        .hasSetRebootRequest());

            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the set reboot request should return result (.*)")
    public boolean thenTheSetRebootRequestShouldReturn(final String result) {
        LOGGER.info("THEN: \"the set reboot request should return result {}", result);

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
                Assert.assertEquals(result.toUpperCase(), this.throwable.getClass().getSimpleName().toUpperCase());
            } catch (final Exception e) {
                LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
                return false;
            }
        }
        return true;
    }

    @DomainStep("the get set reboot response request should return a set reboot response with result (.*) and description (.*)")
    public boolean thenTheGetSetRebootResultRequestShouldReturnAGetSetRebootResultResponseWithResult(
            final String result, final String description) {
        LOGGER.info(
                "THEN: \"the get set reboot result request should return a get set reboot response with result {} and description {}",
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

                // TODO: check description
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    // === private methods ===

    private void setUp() {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.organisationRepositoryMock,
                this.deviceAuthorizationRepositoryMock, this.deviceLogItemRepositoryMock,
                this.oslpDeviceRepositoryMock, this.webServiceResponseMessageSenderMock, this.channelMock });

        this.adHocManagementEndpoint = new AdHocManagementEndpoint(this.adHocManagementService);
        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        this.setRebootAsyncRequest = null;
        this.setRebootAsyncResponse = null;

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

    private void initializeOslp() {
        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.SetRebootResponse oslpResponse = com.alliander.osgp.oslp.Oslp.SetRebootResponse
                .newBuilder().setStatus(Status.OK).build();

        this.oslpResponse = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setSetRebootResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpResponse,
                this.channelMock, this.device.getNetworkAddress());
        this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
        this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);
    }
}
