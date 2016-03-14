/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.devicemanagement;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.alliander.osgp.adapter.protocol.oslp.application.mapping.OslpMapper;
import com.alliander.osgp.adapter.protocol.oslp.application.services.DeviceRegistrationService;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDeviceBuilder;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerClient;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpDeviceService;
import com.alliander.osgp.adapter.ws.core.application.mapping.DeviceManagementMapper;
import com.alliander.osgp.adapter.ws.core.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.ws.core.endpoints.DeviceManagementEndpoint;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonResponseMessageFinder;
import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.EventNotificationType;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsResponse;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceAuthorizationBuilder;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.OrganisationBuilder;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceFunctionMappingRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
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

@DomainSteps
@Configurable
public class SetEventNotificationsSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetEventNotificationsSteps.class);

    private static final String ORGANISATION_ID_OWNER = "ORGANISATION-01";
    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";

    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // WS Adapterfields
    private DeviceManagementEndpoint deviceManagementEndpoint;

    private SetEventNotificationsRequest request;
    private SetEventNotificationsAsyncResponse setEventNotificationsAsyncResponse;
    private SetEventNotificationsAsyncRequest setEventNotificationsAsyncRequest;
    private SetEventNotificationsResponse response;

    @Autowired
    @Qualifier("wsCoreDeviceManagementService")
    private DeviceManagementService deviceManagementService;
    @Autowired
    @Qualifier("coreDeviceManagementMapper")
    private DeviceManagementMapper deviceManagementMapper;

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

    private Ssld device;
    private Organisation organisation;
    private DeviceAuthorization authorization;

    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private SsldRepository ssldRepositoryMock;
    @Autowired
    private OrganisationRepository organisationRepositoryMock;
    @Autowired
    private DeviceAuthorizationRepository authorizationRepositoryMock;
    @Autowired
    private DeviceFunctionMappingRepository deviceFunctionMappingRepositoryMock;
    @Autowired
    private DeviceLogItemRepository logItemRepositoryMock;

    // Protocol Adapter fields
    @Autowired
    private DeviceRegistrationService deviceRegistrationService;
    @Autowired
    private OslpDeviceService oslpDeviceService;
    private OslpDevice oslpDevice;
    @Autowired
    private OslpDeviceRepository oslpDeviceRepositoryMock;

    private OslpEnvelope oslpEnvelope;
    private OslpEnvelope oslpResponse;
    private OslpChannelHandlerClient oslpChannelHandler;
    @Autowired
    private Channel channelMock;

    // Test fields
    private Throwable throwable;

    // === GIVEN ===

    @DomainStep("a valid set event notifications request with (.*) and (.*)")
    public void givenAValidSetEventNotificationsRequest(final String device, final String notifications)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        LOGGER.info(
                "GIVEN: \"a valid set event notifications request with (.*) and (.*)\" with device [{}] and notifications [{}].",
                device, notifications);

        this.setUp();

        this.request = new SetEventNotificationsRequest();
        this.request.setDeviceIdentification(device);
        this.request.getEventNotifications().addAll(this.convertEventNotifications(notifications));
    }

    @DomainStep("the set event notifications request refers to an existing device (.*) that will always respond OK")
    public void givenTheSetEventNotificationsRequestRefersToAnExistingDevice(final String device)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        LOGGER.info(
                "GIVEN: \"the set event notifications request refers to an existing device (.*) that will always respond OK\" with device [{}].",
                device);

        // existing device
        this.device = this.createDevice(device);
        this.oslpDevice = this.createOslpDevice(device);

        when(this.deviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.device);
        when(this.ssldRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.device);
        when(this.ssldRepositoryMock.findOne(1L)).thenReturn(this.device);
        when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);

        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.SetEventNotificationsResponse oslpResponse = com.alliander.osgp.oslp.Oslp.SetEventNotificationsResponse
                .newBuilder().setStatus(Status.OK).build();

        this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setSetEventNotificationsResponse(oslpResponse).build())
                .build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope,
                this.channelMock, this.device.getNetworkAddress());
        this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
        this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);
    }

    @DomainStep("the set event notifications request refers to an existing organisation that is authorized")
    public void givenTheSetEventNotificationsRequestRefersToAnExistingOrganisation() {
        LOGGER.info("GIVEN: \"the set event notifications request refers to an existing organisation that is authorized\".");

        this.organisation = this.createOrganisation();
        this.authorization = this.createAuthorization();

        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID_OWNER)).thenReturn(
                this.organisation);
        when(this.authorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device)).thenReturn(
                Arrays.asList(this.authorization));

        final List<DeviceFunction> deviceFunctions = new ArrayList<>();
        deviceFunctions.add(DeviceFunction.SET_EVENT_NOTIFICATIONS);

        when(this.deviceFunctionMappingRepositoryMock.findByDeviceFunctionGroups(any(ArrayList.class))).thenReturn(
                deviceFunctions);
    }

    // === WHEN ===

    @DomainStep("the set event notifications request is received on OSGP")
    public void whenTheSetEventNotificationsRequestIsReceived() {
        LOGGER.info("WHEN: \"the set event notifications request is received on OSGP\".");

        try {
            this.setEventNotificationsAsyncResponse = this.deviceManagementEndpoint.setEventNotifications(
                    ORGANISATION_ID_OWNER, this.request);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("the set event notifications request should return an async response with a correlationId and deviceId (.*)")
    public boolean thenTheSetEventNotificationsRequestShouldReturnASetEventNotificationsResponseWithACorrelationID(
            final String deviceId) {
        LOGGER.info(
                "THEN: \"the set event notifications request should return a set event notifications response with a correlationId and deviceId {}\".",
                deviceId);

        try {
            Assert.assertNotNull("Set Event Notifications Async Response should not be null",
                    this.setEventNotificationsAsyncResponse);
            Assert.assertNotNull("Async Response should not be null",
                    this.setEventNotificationsAsyncResponse.getAsyncResponse());
            Assert.assertNotNull("CorrelationId should not be null", this.setEventNotificationsAsyncResponse
                    .getAsyncResponse().getCorrelationUid());
            Assert.assertNull("Throwable should be null", this.throwable);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("a set event notifications oslp message is sent to device (.*) should be (.*)")
    public boolean thenASetEventNotificationsOslpMessageShouldBeSent(final String device, final Boolean isMessageSent) {
        LOGGER.info("THEN: \"a set event notifications oslp message is sent to device [{}] should be [{}]\".", device,
                isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, timeout(10000).times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpResponse = argument.getValue();

                Assert.assertTrue("Message should contain set event notifications request.", this.oslpResponse
                        .getPayloadMessage().hasSetEventNotificationsRequest());
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("an ovl set event notifications result message with result (.*) and description (.*) should be sent to the ovl out queue")
    public boolean thenAnOvlSetEventNotificationsResultMessageShouldBeSentToTheOvlOutQueue(final String result,
            final String description) {
        LOGGER.info(
                "THEN: \"an ovl set event notifications result message with result [{}] and description [{}] should be sent to the ovl out queue\".",
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

    @DomainStep("a set event notifications oslp message should be sent to device (.*)")
    public boolean thenASetEventNotificationOslpMessageShouldBeSent(final String device) {
        LOGGER.info("THEN: \"a set event notifications oslp message should be sent to device (.*)\" with device [{}].",
                device);

        try {
            verify(this.channelMock, timeout(1000).times(1)).write(any(OslpEnvelope.class));
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("a set event notifications oslp message should not be sent to device (.*)")
    public boolean thenASetEventNotificationOslpMessageShouldNotBeSent(final String device) throws Throwable {
        LOGGER.info(
                "THEN: \"a set event notifications oslp message should not be sent to device (.*)\" with device [{}].",
                device);

        try {
            verify(this.channelMock, timeout(1000).times(0)).write(any(OslpEnvelope.class));
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the set event notifications request should return a set event notifications response")
    public boolean thenOsgpShouldReturnASetEventNotificationsResponse() throws Throwable {
        LOGGER.info("THEN: \"the set event notifications request should return a set event notifications response\".");

        return this.response != null && this.throwable == null;
    }

    @DomainStep("the set event notifications request should return an error (.*)")
    public boolean thenTheSetEventNotificationsRequestShouldReturnAnError(final String errorMessage) {
        LOGGER.info("THEN: \"the set event notifications request should return an error (.*)\" with error [{}].",
                errorMessage);

        try {
            Assert.assertEquals(errorMessage.toUpperCase(), this.throwable.getClass().getSimpleName().toUpperCase());
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("a get set event notifications response request with correlationId (.*) and deviceId (.*)")
    public void givenAGetSetEventNotificationsResultRequestWithCorrelationId(final String correlationId,
            final String deviceId) {
        LOGGER.info("GIVEN: \"a get set event notifications response with correlationId {} and deviceId {}\".",
                correlationId, deviceId);

        this.setUp();

        this.setEventNotificationsAsyncRequest = new SetEventNotificationsAsyncRequest();

        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationId);
        asyncRequest.setDeviceId(deviceId);

        this.setEventNotificationsAsyncRequest.setAsyncRequest(asyncRequest);
    }

    @DomainStep("a set event notifications response message with correlationId (.*), deviceId (.*), qresult (.*) and qdescription (.*) is found in the queue (.*)")
    public void givenASetEventNotificationsResponseMessageIsFoundInTheQueue(final String correlationId,
            final String deviceId, final String qresult, final String qdescription, final Boolean isFound) {
        LOGGER.info(
                "GIVEN: \"a set event notifications response message with correlationId {}, deviceId {}, qresult {} and qdescription {} is found {}\".",
                correlationId, deviceId, qresult, qdescription, isFound);

        if (isFound) {
            final ObjectMessage messageMock = mock(ObjectMessage.class);

            try {
                when(messageMock.getJMSCorrelationID()).thenReturn(correlationId);
                when(messageMock.getStringProperty("OrganisationIdentification")).thenReturn(ORGANISATION_ID_OWNER);
                when(messageMock.getStringProperty("DeviceIdentification")).thenReturn(deviceId);
                final ResponseMessageResultType result = ResponseMessageResultType.valueOf(qresult);
                Serializable dataObject = null;
                OsgpException exception = null;
                if (result.equals(ResponseMessageResultType.NOT_OK)) {
                    dataObject = new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                            ComponentType.UNKNOWN, new ValidationException());
                    exception = (OsgpException) dataObject;
                }
                final ResponseMessage message = new ResponseMessage(correlationId, ORGANISATION_ID_OWNER, deviceId,
                        result, exception, dataObject);
                when(messageMock.getObject()).thenReturn(message);
            } catch (final JMSException e) {
                LOGGER.error("given a set event notifications response", e);
            }

            when(this.commonResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(messageMock);

        } else {
            when(this.commonResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(null);
        }
    }

    @DomainStep("the get set event notifications response request is received")
    public void whenTheGetSetEventNotificationsResultRequestIsReceived() {
        LOGGER.info("WHEN: \"the set event notifications request is received\".");

        try {

            this.response = this.deviceManagementEndpoint.getSetEventNotificationsResponse(ORGANISATION_ID_OWNER,
                    this.setEventNotificationsAsyncRequest);

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("the get set event notifications response request should return a set event notifications response with result (.*) and description (.*)")
    public boolean thenTheGetSetEventNotificationsResultRequestShouldReturnAGetSetEventNotificationsResultResponseWithResult(
            final String result, final String description) {
        LOGGER.info(
                "THEN: \"the get set event notifications result request should return a get set event notifications response with result {} and description {}",
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

    // === private methods ===

    private void setUp() {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.ssldRepositoryMock,
                this.organisationRepositoryMock, this.logItemRepositoryMock, this.authorizationRepositoryMock,
                this.channelMock, this.webServiceResponseMessageSenderMock, this.oslpDeviceRepositoryMock });

        this.oslpDeviceService.setMapper(new OslpMapper());
        OslpTestUtils.configureDeviceServiceForOslp(this.oslpDeviceService);

        this.deviceManagementEndpoint = new DeviceManagementEndpoint(this.deviceManagementService,
                this.deviceManagementMapper);
        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        this.setEventNotificationsAsyncRequest = null;
        this.setEventNotificationsAsyncResponse = null;

        this.request = null;
        this.response = null;
        this.throwable = null;
    }

    private Ssld createDevice(final String deviceIdentification) {
        return (Ssld) new DeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withNetworkAddress(InetAddress.getLoopbackAddress()).withPublicKeyPresent(PUBLIC_KEY_PRESENT)
                .withProtocolInfo(ProtocolInfoTestUtils.getProtocolInfo(PROTOCOL, PROTOCOL_VERSION)).isActivated(true)
                .build();
    }

    private OslpDevice createOslpDevice(final String deviceIdentification) {
        return new OslpDeviceBuilder().withDeviceIdentification(deviceIdentification).withDeviceUid(DEVICE_UID).build();
    }

    private Organisation createOrganisation() {
        return new OrganisationBuilder().withOrganisationIdentification(ORGANISATION_ID_OWNER).build();
    }

    private DeviceAuthorization createAuthorization() {
        return new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.organisation)
                .withFunctionGroup(DeviceFunctionGroup.OWNER).build();
    }

    private List<EventNotificationType> convertEventNotifications(final String eventNotifications) {
        final List<EventNotificationType> eventNotificationTypes = new ArrayList<>();
        for (final String eventNotification : eventNotifications.split(",")) {
            if (eventNotification.equals(EventNotificationType.DIAG_EVENTS.name())) {
                eventNotificationTypes.add(EventNotificationType.FIRMWARE_EVENTS);
            } else if (eventNotification.equals(EventNotificationType.FIRMWARE_EVENTS.name())) {
                eventNotificationTypes.add(EventNotificationType.FIRMWARE_EVENTS);
            } else if (eventNotification.equals(EventNotificationType.HARDWARE_FAILURE.name())) {
                eventNotificationTypes.add(EventNotificationType.HARDWARE_FAILURE);
            } else if (eventNotification.equals(EventNotificationType.LIGHT_EVENTS.name())) {
                eventNotificationTypes.add(EventNotificationType.LIGHT_EVENTS);
            } else if (eventNotification.equals(EventNotificationType.TARIFF_EVENTS.name())) {
                eventNotificationTypes.add(EventNotificationType.TARIFF_EVENTS);
            } else if (eventNotification.equals(EventNotificationType.MONITOR_EVENTS.name())) {
                eventNotificationTypes.add(EventNotificationType.MONITOR_EVENTS);
            } else if (eventNotification.equals(EventNotificationType.COMM_EVENTS.name())) {
                eventNotificationTypes.add(EventNotificationType.COMM_EVENTS);
            }
        }

        return eventNotificationTypes;
    }
}
