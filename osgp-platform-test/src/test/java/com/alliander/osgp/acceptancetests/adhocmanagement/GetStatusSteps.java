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
import java.util.HashSet;
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
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.LightValue;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.AsyncRequest;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceAuthorizationBuilder;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceFunctionMappingRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.DeviceStatus;
import com.alliander.osgp.domain.core.valueobjects.DeviceStatusMapped;
import com.alliander.osgp.domain.core.valueobjects.DomainType;
import com.alliander.osgp.domain.core.valueobjects.EventNotificationType;
import com.alliander.osgp.domain.core.valueobjects.LightType;
import com.alliander.osgp.domain.core.valueobjects.LinkType;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.RelayType;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;
import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.Oslp.Status;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.oslp.OslpUtils;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Configurable
@DomainSteps
public class GetStatusSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetStatusSteps.class);

    private static final String ORGANISATION_ID = "ORGANISATION-01";
    private static final String ORGANISATION_PREFIX = "ORG";
    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";
    private static final String EMPTY = "";
    private static final String NULL = "NULL";
    private static final String LINK_NOT_SET = "LINK_NOT_SET";
    private static final String LT_NOT_SET = "LT_NOT_SET";

    // TODO - Add as parameters to tests
    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // WS Adapter fields
    private PublicLightingAdHocManagementEndpoint adHocManagementEndpoint;

    private GetStatusRequest request;
    private GetStatusAsyncResponse getStatusAsyncResponse;
    private GetStatusAsyncRequest getStatusAsyncRequest;
    private GetStatusResponse response;

    @Autowired
    @Qualifier(value = "wsPublicLightingAdHocManagementService")
    private AdHocManagementService adHocManagementService;

    @Autowired
    @Qualifier("wsPublicLightingIncomingResponsesMessageFinder")
    private PublicLightingResponseMessageFinder ovlResponseMessageFinder;

    @Autowired
    @Qualifier("wsPublicLightingIncomingResponsesJmsTemplate")
    private JmsTemplate publicLightingResponsesJmsTemplate;

    // Domain Adapter fields
    @Autowired
    @Qualifier("domainPublicLightingOutgoingWebServiceResponseMessageSender")
    private WebServiceResponseMessageSender webServiceResponseMessageSenderMock;

    private Ssld device;
    private RelayType deviceRelayType;
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
    private DeviceFunctionMappingRepository deviceFunctionMappingRepositoryMock;
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
    @Autowired
    private OslpChannelHandlerClient oslpChannelHandler;
    @Autowired
    private Channel channelMock;

    // Test fields
    private Throwable throwable;

    // === GIVEN ===

    @DomainStep("a get status request for device (.*)")
    public void givenAGetStatusRequest(final String device) {
        LOGGER.info("GIVEN: a get status request for device {}.", device);

        this.setUp();

        this.request = new GetStatusRequest();
        this.request.setDeviceIdentification(device);
    }

    @DomainStep("a get get status response request with correlationId (.*) and deviceId (.*)")
    public void givenAGetGetStatusResponseRequest(final String correlationId, final String deviceId) {
        LOGGER.info("GIVEN: a get get status response request with correlationId {} and deviceId {}", correlationId,
                deviceId);

        this.setUp();

        this.getStatusAsyncRequest = new GetStatusAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationId);
        asyncRequest.setDeviceId(deviceId);
        this.getStatusAsyncRequest.setAsyncRequest(asyncRequest);
    }

    @DomainStep("the get status request refers to a device (.*) with status (.*) which is configured with relayType (.*)")
    public void givenADeviceWithStausAndRelayType(final String device, final String status, final String relayType)
            throws Exception {
        LOGGER.info(
                "GIVEN: the get status request refers to a device {} with status {} which is configured with relayType {}.",
                device, status, relayType);

        if (!relayType.equals(NULL)) {
            this.deviceRelayType = RelayType.valueOf(relayType);
        }

        switch (status.toUpperCase()) {
        case "ACTIVE":
            this.createDevice(device, true);
            when(this.deviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.device);
            when(this.ssldRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.device);
            when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.oslpDevice);
            when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);
            break;
        case "UNKNOWN":
            when(this.deviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(null);
            when(this.ssldRepositoryMock.findByDeviceIdentification(device)).thenReturn(null);
            break;
        case "UNREGISTERED":
            this.createDevice(device, false);
            when(this.deviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.device);
            when(this.ssldRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.device);
            break;
        default:
            throw new Exception("Unknown device status");
        }
    }

    @DomainStep("the get status request refers to an organisation that is authorised")
    public void givenAnAuthorisedOrganisation() {
        LOGGER.info("GIVEN: the get status request refers to an organisation that is authorised.");

        this.organisation = new Organisation(ORGANISATION_ID, ORGANISATION_ID, ORGANISATION_PREFIX,
                PlatformFunctionGroup.USER);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID)).thenReturn(
                this.organisation);

        final List<DeviceAuthorization> authorizations = new ArrayList<>();
        authorizations.add(new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.organisation)
                .withFunctionGroup(DeviceFunctionGroup.AD_HOC).build());
        when(this.deviceAuthorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device))
                .thenReturn(authorizations);

        final List<DeviceFunction> deviceFunctions = new ArrayList<>();
        deviceFunctions.add(DeviceFunction.GET_STATUS);
        deviceFunctions.add(DeviceFunction.GET_LIGHT_STATUS);

        when(this.deviceFunctionMappingRepositoryMock.findByDeviceFunctionGroups(any(ArrayList.class))).thenReturn(
                deviceFunctions);

    }

    @DomainStep("the get status oslp message from the device contains (.*), (.*), (.*), (.*), (.*), (.*), and (.*)")
    public void givenAnOslpResponse(final String preferredLinkType, final String actualLinkType,
            final String lightType, final String eventNotifications, final String index, final Boolean on,
            final String dimValue) {
        LOGGER.info("GIVEN: the get status oslp message from the device contains {}, {}, {}, {}, {}, {}, and {}.",
                new Object[] { preferredLinkType, actualLinkType, lightType, eventNotifications, index, on, dimValue });

        // LightValue
        final Oslp.LightValue.Builder lightValueBuilder = Oslp.LightValue.newBuilder();
        if (StringUtils.isNotBlank(index)) {
            lightValueBuilder.setIndex(OslpUtils.integerToByteString(Integer.parseInt(index)));
        }
        lightValueBuilder.setOn(on);
        if (StringUtils.isNotBlank(dimValue)) {
            lightValueBuilder.setDimValue(OslpUtils.integerToByteString(Integer.parseInt(dimValue)));
        }

        // EventNotificationMask
        int mask = 0;
        if (StringUtils.isNotBlank(eventNotifications)) {
            for (final String event : eventNotifications.split(",")) {
                mask += (Enum.valueOf(EventNotificationType.class, event)).getValue();
            }
        }

        final com.alliander.osgp.oslp.Oslp.GetStatusResponse getStatusResponse = com.alliander.osgp.oslp.Oslp.GetStatusResponse
                .newBuilder()
                .setStatus(Status.OK)
                .setPreferredLinktype(
                        StringUtils.isBlank(preferredLinkType) ? Oslp.LinkType.LINK_NOT_SET : Enum.valueOf(
                                Oslp.LinkType.class, preferredLinkType))
                .setActualLinktype(
                        StringUtils.isBlank(actualLinkType) ? Oslp.LinkType.LINK_NOT_SET : Enum.valueOf(
                                Oslp.LinkType.class, actualLinkType))
                .setLightType(
                        StringUtils.isBlank(lightType) ? Oslp.LightType.LT_NOT_SET : Enum.valueOf(Oslp.LightType.class,
                                lightType)).setEventNotificationMask(mask).addValue(lightValueBuilder).build();

        this.oslpResponse = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setGetStatusResponse(getStatusResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpResponse,
                this.channelMock, this.device.getNetworkAddress());
        this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
        this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);
    }

    @DomainStep("the get status oslp message from the device contains (.*) light sources")
    public void givenAnOslpResponseWith(final Integer numberOfLightValues) {

        // LightValues
        final List<Oslp.LightValue> lightValues = new ArrayList<>();
        for (int i = 1; i <= numberOfLightValues; i++) {
            final Oslp.LightValue.Builder lightValueBuilder = Oslp.LightValue.newBuilder();
            lightValueBuilder.setIndex(OslpUtils.integerToByteString(i));
            lightValueBuilder.setOn(true);
            lightValues.add(lightValueBuilder.build());
        }

        final com.alliander.osgp.oslp.Oslp.GetStatusResponse getStatusResponse = com.alliander.osgp.oslp.Oslp.GetStatusResponse
                .newBuilder().setStatus(Status.OK).setPreferredLinktype(Oslp.LinkType.LINK_NOT_SET)
                .setActualLinktype(Oslp.LinkType.LINK_NOT_SET).setLightType(Oslp.LightType.LT_NOT_SET)
                .setEventNotificationMask(0).addAllValue(lightValues).build();

        this.oslpResponse = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setGetStatusResponse(getStatusResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpResponse,
                this.channelMock, this.device.getNetworkAddress());
        this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
        this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);
    }

    @DomainStep("a get status response message for domainType (.*) with correlationId (.*), deviceId (.*), qresult (.*), qdescription (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*) is found in the queue (.*)")
    public void givenAGetStatusResponseMessageIsFoundInQueue(final String domainType, final String correlationId,
            final String deviceId, final String qresult, final String qdescription, final String preferredLinkType,
            final String actualLinkType, final String lightType, final String eventNotifications, final String index,
            final String on, final String dimValue, final Boolean isFound) {
        LOGGER.info(
                "GIVEN: \"a get status response message for domainType {} with correlationId {}, deviceId {}, qresult {} and qdescription {} is found {}\".",
                domainType, correlationId, deviceId, qresult, qdescription, isFound);

        if (isFound) {
            final ObjectMessage messageMock = mock(ObjectMessage.class);

            try {
                when(messageMock.getJMSCorrelationID()).thenReturn(correlationId);
                when(messageMock.getStringProperty("OrganisationIdentification")).thenReturn(ORGANISATION_ID);
                when(messageMock.getStringProperty("DeviceIdentification")).thenReturn(deviceId);

                final LinkType prefLinkType = StringUtils.isBlank(preferredLinkType) ? null : Enum.valueOf(
                        LinkType.class, preferredLinkType);
                final LinkType actLinkType = StringUtils.isBlank(actualLinkType) ? null : Enum.valueOf(LinkType.class,
                        actualLinkType);
                final LightType lt = StringUtils.isBlank(lightType) ? null : Enum.valueOf(LightType.class, lightType);

                // EventNotificationTypes
                int mask = 0;
                if (StringUtils.isNotBlank(eventNotifications)) {
                    for (final String event : eventNotifications.split(",")) {
                        mask += (Enum.valueOf(EventNotificationType.class, event)).getValue();
                    }
                }

                final ResponseMessage message;

                final ResponseMessageResultType result = ResponseMessageResultType.valueOf(qresult);
                Serializable dataObject = null;

                if (result.equals(ResponseMessageResultType.NOT_OK)) {
                    dataObject = new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                            ComponentType.UNKNOWN, new ValidationException());
                    message = new ResponseMessage(correlationId, ORGANISATION_ID, deviceId, result,
                            (OsgpException) dataObject, dataObject);
                } else {
                    if (domainType.equals(DomainType.PUBLIC_LIGHTING.name())) {
                        // DomainType.PUBLIC_LIGHTING
                        dataObject = new DeviceStatus(null, prefLinkType, actLinkType, lt, mask);
                    } else {
                        // DomainType.TARIFF_SWITCHING
                        dataObject = new DeviceStatusMapped(null, null, prefLinkType, actLinkType, lt, mask);
                    }
                    message = new ResponseMessage(correlationId, ORGANISATION_ID, deviceId, result, null, dataObject);
                }

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

    @DomainStep("the get status request is received")
    public void whenTheGetStatusRequestIsReceived() {
        LOGGER.info("WHEN: the get status request is received.");

        try {
            this.getStatusAsyncResponse = this.adHocManagementEndpoint.getStatus(ORGANISATION_ID, this.request);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("the get get status response request is received")
    public void whenTheGetGetStatusResponseRequestIsReceived() {
        LOGGER.info("WHEN: the get status request is received.");

        try {
            this.response = this.adHocManagementEndpoint.getGetStatusResponse(ORGANISATION_ID,
                    this.getStatusAsyncRequest);

            Assert.assertNotNull("Response should not be null", this.response);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("the get status request should return an async response with a correlationId and deviceId (.*)")
    public boolean thenTheGetStatusRequestShouldReturnAnAsyncResponseWithACorrelationIdAndDeviceId(final String deviceId) {
        LOGGER.info(
                "THEN: the get status request should return an async response with a correlationId and deviceId {}.",
                deviceId);

        try {
            Assert.assertNotNull("Response should not be null", this.getStatusAsyncResponse);
            Assert.assertNotNull("CorrelationId should not be null", this.getStatusAsyncResponse.getAsyncResponse()
                    .getCorrelationUid());
            Assert.assertNull("Throwable should be null", this.throwable);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("a get status oslp message is sent to the device should be (.*)")
    public boolean thenAnOslpMessageShouldBeSent(final Boolean isMessageSent) {
        LOGGER.info("THEN: a get status oslp message is sent to the device should be {}.", isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, timeout(10000).times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpRequest = argument.getValue();

                Assert.assertTrue("Message should contain get status request.", this.oslpRequest.getPayloadMessage()
                        .hasGetStatusRequest());
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("an ovl get status result message with result (.*) and description (.*) should be sent to the ovl out queue")
    public boolean thenAnOvlGetStatusResultMessageShouldBeSentToTheOvlOutQueue(final String result,
            final String description) {
        LOGGER.info("THEN: an ovl get status result message with result {} should be sent to the ovl out queue", result);

        try {
            final ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
            verify(this.webServiceResponseMessageSenderMock, timeout(10000).times(1)).send(argument.capture());

            final String expected = result.equals(NULL) ? null : result;
            final String actual = argument.getValue().getResult().getValue();

            Assert.assertTrue("Invalid result, found: " + actual + " , expected: " + expected, actual.equals(expected));
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }

        return true;
    }

    @DomainStep("an ovl get status result message with result (.*), description (.*), (.*), (.*), (.*), (.*), (.*), (.*), and (.*) should be sent to the ovl out queue")
    public boolean thenAnOvlGetStatusResultMessageWithResultShouldBeSentToTheOvlOutQueue(final String result,
            final String description, final String preferredLinktype, final String actualLinktype,
            final String lighttype, final String eventnotifications, final String index, final String on,
            final String dimValue) {
        LOGGER.info("THEN: an ovl get status result message with result {} should be sent to the ovl out queue", result);

        String expected;
        String actual;
        DeviceStatus deviceStatus;

        try {
            final ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
            verify(this.webServiceResponseMessageSenderMock, timeout(10000).times(1)).send(argument.capture());

            // Check the result.
            expected = result.equals(NULL) ? null : result;
            actual = argument.getValue().getResult().getValue();

            Assert.assertTrue("Invalid result, found: " + actual + " , expected: " + expected, actual.equals(expected));

            // Check the description.
            expected = description.equals(NULL) ? null : description;
            actual = argument.getValue().getOsgpException() == null ? "" : argument.getValue().getOsgpException()
                    .getMessage();

            Assert.assertTrue("Invalid description, found: " + actual + " , expected: " + expected,
                    actual.equals(expected));

            if (argument.getValue().getResult().getValue().equals("OK")) {
                deviceStatus = (DeviceStatus) argument.getValue().getDataObject();

                // Check if the DeviceStatus is not null.
                Assert.assertNotNull("DeviceStatus is null", deviceStatus);

                // Check the preferredLinktype.
                expected = preferredLinktype.equals(NULL) || preferredLinktype.equals(LINK_NOT_SET) ? EMPTY
                        : preferredLinktype;
                actual = deviceStatus.getPreferredLinkType() == null ? EMPTY : deviceStatus.getPreferredLinkType()
                        .toString();

                Assert.assertTrue("Invalid preferredLinktype, found: " + actual + " , expected: " + expected,
                        actual.equals(expected));

                // Check the actualLinktype.
                expected = actualLinktype.equals(NULL) || actualLinktype.equals(LINK_NOT_SET) ? EMPTY : actualLinktype;
                actual = deviceStatus.getActualLinkType() == null ? EMPTY : deviceStatus.getActualLinkType().toString();

                Assert.assertTrue("Invalid actualLinktype, found: " + actual + " , expected: " + expected,
                        actual.equals(expected));

                // Check the lighttype.
                expected = lighttype.equals(NULL) || lighttype.equals(LT_NOT_SET) ? EMPTY : lighttype;
                actual = deviceStatus.getLightType() == null ? EMPTY : deviceStatus.getLightType().toString();

                Assert.assertTrue("Invalid lighttype, found: " + actual + " , expected: " + expected,
                        actual.equals(expected));

                // Check the eventnotifications.
                final HashSet<EventNotificationType> expectedEventNotificationTypes = new HashSet<>();
                if (StringUtils.isNotBlank(eventnotifications)) {
                    for (final String event : eventnotifications.split(",")) {
                        expectedEventNotificationTypes.add(Enum.valueOf(EventNotificationType.class, event));
                    }
                }
                final HashSet<EventNotificationType> actualEventNotificationTypes = new HashSet<>(
                        deviceStatus.getEventNotifications());

                Assert.assertEquals("Event notifications should equal expected value", expectedEventNotificationTypes,
                        actualEventNotificationTypes);

                // Get the list of LightValues.
                final List<com.alliander.osgp.domain.core.valueobjects.LightValue> lightValues = deviceStatus
                        .getLightValues();

                // Check if the lightValues list is not null.
                Assert.assertNotNull("lightValues list is null", lightValues);

                for (final com.alliander.osgp.domain.core.valueobjects.LightValue lightValue : lightValues) {
                    // Check if the lightValue is not null.
                    Assert.assertNotNull("lightValue is null", lightValue);

                    // Check the on boolean.
                    expected = on.equals(NULL) ? null : on;
                    actual = lightValue.isOn() + "";

                    Assert.assertTrue("Invalid lightValue.isOn, found: " + actual + " , expected: " + expected,
                            actual.equals(expected));

                    // Check the dimValue.
                    expected = dimValue.equals(NULL) ? EMPTY : dimValue;
                    actual = lightValue.getDimValue() == null ? EMPTY : lightValue.getDimValue().toString();

                    Assert.assertTrue("Invalid lightValue.dimValue, found: " + actual + " , expected: " + expected,
                            actual.equals(expected));

                    // Check the index.
                    expected = index.equals(NULL) ? null : index;
                    actual = lightValue.getIndex() == null ? EMPTY : lightValue.getIndex().toString();

                    Assert.assertTrue("Invalid lightValue.index, found: " + actual + " , expected: " + expected,
                            actual.equals(expected));
                }

                final List<com.alliander.osgp.domain.core.valueobjects.TariffValue> tariffValues = ((DeviceStatusMapped) deviceStatus)
                        .getTariffValues();

                Assert.assertNotNull("tariffValues is null", tariffValues);

                for (final com.alliander.osgp.domain.core.valueobjects.TariffValue tariffValue : tariffValues) {
                    Assert.assertNotNull("tariffValue is null", tariffValue);

                    switch (this.deviceRelayType) {
                    case LIGHT:
                        // Do nothing.
                        break;
                    case TARIFF:
                        Assert.assertEquals("RelayType.TARIFF", on, (!tariffValue.isHigh()) + "");
                        break;
                    case TARIFF_REVERSED:
                        Assert.assertEquals("RelayType.TARIFF_REVERSED", on, (tariffValue.isHigh()) + "");
                        break;
                    default:
                        // Do nothing.
                        break;
                    }
                }
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }

        return true;
    }

    @DomainStep("the get get status response request should return a get status response with result (.*), description (.*), (.*), (.*), (.*), (.*), (.*), (.*), and (.*)")
    public boolean thenTheGetGetStatusResponseRequestShouldReturnAGetStatusResponse(final String result,
            final String description, final String preferredLinktype, final String actualLinktype,
            final String lighttype, final String eventnotifications, final String index, final String on,
            final String dimValue) {
        LOGGER.info(
                "THEN: the get get status response request should return a get status response with result: {}, description: {}, preferredLinktype: {}, actualLinktype: {}, lighttype: {}, eventnotification: {}, index: {}, on: {}, dimValue: {}",
                result, description, preferredLinktype, actualLinktype, lighttype, eventnotifications, index, on,
                dimValue);

        try {
            if ("NOT_OK".equals(result)) {
                Assert.assertNull("Set Schedule Response should be null", this.response);
                Assert.assertNotNull("Throwable should not be null", this.throwable);
                Assert.assertTrue("Throwable should contain a validation exception",
                        this.throwable.getCause() instanceof ValidationException);
            } else {

                // Check if the GetStatusResponse is not null.
                Assert.assertNotNull("GetStatusResponse response is null", this.response);

                // Check the result.
                String expected = result.equals(NULL) ? null : result;
                String actual = this.response.getResult().toString();

                Assert.assertTrue("Invalid result, found: " + actual + " , expected: " + expected,
                        actual.equals(expected));

                if (this.response.getResult().equals("OK")) {
                    // Check if the DeviceStatus is not null.
                    Assert.assertNotNull("DeviceStatus is null", this.response.getDeviceStatus());

                    try {
                        // Check the preferredLinktype.
                        expected = preferredLinktype.equals(NULL) ? null : preferredLinktype;
                        actual = this.response.getDeviceStatus().getPreferredLinkType() == null ? null : this.response
                                .getDeviceStatus().getPreferredLinkType().toString();

                        Assert.assertTrue("Invalid preferredLinktype, found: " + actual + " , expected: " + expected,
                                actual.equals(expected));
                    } catch (final Exception e) {

                    }

                    try {
                        // Check the actualLinktype.
                        expected = actualLinktype.equals(NULL) ? null : actualLinktype;
                        actual = this.response.getDeviceStatus().getActualLinkType() == null ? null : this.response
                                .getDeviceStatus().getActualLinkType().toString();

                        Assert.assertTrue("Invalid actualLinktype, found: " + actual + " , expected: " + expected,
                                actual.equals(expected));
                    } catch (final Exception e) {

                    }

                    try {
                        // Check the lighttype.
                        expected = lighttype.equals(NULL) ? null : lighttype;
                        actual = this.response.getDeviceStatus().getLightType() == null ? null : this.response
                                .getDeviceStatus().getLightType().toString();

                        Assert.assertTrue("Invalid lighttype, found: " + actual + " , expected: " + expected,
                                actual.equals(expected));
                    } catch (final Exception e) {

                    }

                    // Check the eventnotifications.
                    final HashSet<com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.EventNotificationType> expectedEventNotificationTypes = new HashSet<>();
                    if (StringUtils.isNotBlank(eventnotifications)) {
                        for (final String event : eventnotifications.split(",")) {
                            expectedEventNotificationTypes
                                    .add(Enum
                                            .valueOf(
                                                    com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.EventNotificationType.class,
                                                    event));
                        }
                    }
                    final HashSet<com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.EventNotificationType> actualEventNotificationTypes = new HashSet<>(
                            this.response.getDeviceStatus().getEventNotifications());

                    Assert.assertEquals("Event notifications should equal expected value",
                            expectedEventNotificationTypes, actualEventNotificationTypes);

                    final List<LightValue> lightValues = this.response.getDeviceStatus().getLightValues();

                    // Check if lightValues not is null.
                    Assert.assertNotNull("LightValues is null", lightValues);

                    for (final LightValue lightValue : lightValues) {
                        // Check the on boolean.
                        expected = on.equals(NULL) ? null : on;
                        actual = lightValue.isOn() + "";

                        Assert.assertTrue("Invalid lightValue.isOn, found: " + actual + " , expected: " + expected,
                                actual.equals(expected));

                        // Check the dimValue.
                        expected = dimValue.equals(NULL) ? null : dimValue;
                        actual = lightValue.getDimValue().toString();

                        Assert.assertTrue("Invalid lightValue.dimValue, found: " + actual + " , expected: " + expected,
                                actual.equals(expected));

                        // Check the index.
                        expected = index.equals(NULL) ? null : index;
                        actual = lightValue.getIndex().toString();

                        Assert.assertTrue("Invalid lightValue.index, found: " + actual + " , expected: " + expected,
                                actual.equals(expected));
                    }
                }
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
                this.organisationRepositoryMock, this.deviceAuthorizationRepositoryMock,
                this.deviceLogItemRepositoryMock, this.channelMock, this.webServiceResponseMessageSenderMock,
                this.oslpDeviceRepositoryMock });

        this.adHocManagementEndpoint = new PublicLightingAdHocManagementEndpoint(this.adHocManagementService,
                new AdHocManagementMapper());
        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        this.request = null;
        this.getStatusAsyncResponse = null;
        this.getStatusAsyncRequest = null;
        this.response = null;
        this.throwable = null;
    }

    private void createDevice(final String deviceIdentification, final boolean activated) {
        LOGGER.info("Creating device [{}] with active [{}]", deviceIdentification, activated);

        this.device = (Ssld) new DeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withNetworkAddress(activated ? InetAddress.getLoopbackAddress() : null)
                .withPublicKeyPresent(PUBLIC_KEY_PRESENT)
                .withProtocolInfo(ProtocolInfoTestUtils.getProtocolInfo(PROTOCOL, PROTOCOL_VERSION))
                .isActivated(activated).build();

        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withDeviceUid(DEVICE_UID).build();
    }
}
