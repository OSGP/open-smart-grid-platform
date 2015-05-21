/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.configurationmanagement;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.alliander.osgp.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.application.services.DeviceRegistrationService;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDeviceBuilder;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerClient;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpDeviceService;
import com.alliander.osgp.adapter.ws.core.application.mapping.ConfigurationManagementMapper;
import com.alliander.osgp.adapter.ws.core.application.services.ConfigurationManagementService;
import com.alliander.osgp.adapter.ws.core.endpoints.ConfigurationManagementEndpoint;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonResponseMessageFinder;
import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.Configuration;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.DaliConfiguration;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.GetConfigurationAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.GetConfigurationAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.GetConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.GetConfigurationResponse;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.IndexAddressMap;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.LightType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.RelayConfiguration;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.RelayMap;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceAuthorizationBuilder;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.OslpLogItemRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.LinkType;
import com.alliander.osgp.domain.core.valueobjects.LongTermIntervalType;
import com.alliander.osgp.domain.core.valueobjects.MeterType;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
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

;

@Configurable
@DomainSteps
public class GetConfigurationDataSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetConfigurationDataSteps.class);

    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";
    private static final String ORGANISATION_ID = "ORGANISATION-01";
    private static final String ORGANISATION_PREFIX = "ORG";

    // TODO - Add as parameters to tests
    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // WS Adapter fields
    private ConfigurationManagementEndpoint configurationManagementEndpoint;

    private GetConfigurationRequest request;
    private GetConfigurationAsyncResponse getConfigurationAsyncResponse;
    private GetConfigurationAsyncRequest getConfigurationAsyncRequest;
    private GetConfigurationResponse response;

    // Application Service
    @Autowired
    @Qualifier("wsCoreConfigurationManagementService")
    private ConfigurationManagementService configurationManagementService;

    @Autowired
    @Qualifier("wsCoreIncomingResponsesMessageFinder")
    private CommonResponseMessageFinder commonResponseMessageFinder;

    @Autowired
    @Qualifier("wsCoreIncomingResponsesJmsTemplate")
    private JmsTemplate commonResponsesJmsTemplateMock;

    // Domain Adapter fields
    @Autowired
    @Qualifier("domainCoreOutgoingWebServiceResponsesMessageSender")
    private WebServiceResponseMessageSender webServiceResponseMessageSenderMock;

    private Device device;
    private Organisation organisation;

    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepositoryMock;
    @Autowired
    private OrganisationRepository organisationRepositoryMock;
    @Autowired
    private OslpLogItemRepository oslpLogItemRepositoryMock;

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

    @DomainStep("a get configuration data request for device (.*)")
    public void givenARequest(final String device) {
        LOGGER.info("GIVEN: a get configuration data request for device {}.", new Object[] { device });

        this.setUp();

        this.request = new GetConfigurationRequest();
        this.request.setDeviceIdentification(device);
    }

    @DomainStep("the get configuration data request refers to a device (.*) with status (.*) which always returns (.*)")
    public void givenADeviceWithStatusAndResponse(final String device, final String status, final String oslpResponse)
            throws Exception {
        LOGGER.info(
                "GIVEN: the get configuration data request refers to a device {} with status {} which always returns {}.",
                new Object[] { device, status, oslpResponse });

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

    @DomainStep("the get configuration data request refers to an organisation that is authorised")
    public void givenAnAuthorisedOrganisation() {
        LOGGER.info("GIVEN: the get configuration data request refers to an organisation that is authorised.");

        this.organisation = new Organisation(ORGANISATION_ID, ORGANISATION_ID, ORGANISATION_PREFIX,
                PlatformFunctionGroup.USER);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID)).thenReturn(
                this.organisation);

        final List<DeviceAuthorization> authorizations = new ArrayList<>();
        authorizations.add(new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.organisation)
                .withFunctionGroup(DeviceFunctionGroup.CONFIGURATION).build());
        when(this.deviceAuthorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device))
                .thenReturn(authorizations);
    }

    @DomainStep("the get configuration oslp message from the device contains (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*) and (.*)")
    public void givenTheOslpResponse(final String lightType, final String dcLights, final String dcMap,
            final String rcType, final String rcMap, final String shortInterval, final String preferredLinkType,
            final String meterType, final String longInterval, final String longIntervalType) {

        LOGGER.info(
                "GIVEN: the get configuration oslp message from the device contains {}, {}, {}, {}, {}, {}, {}, {}, {} and {}.",
                new Object[] { lightType, dcLights, dcMap, rcType, rcMap, shortInterval, preferredLinkType, meterType,
                        longInterval, longIntervalType });

        Oslp.GetConfigurationResponse.Builder builder = Oslp.GetConfigurationResponse.newBuilder().setStatus(Status.OK);

        if (StringUtils.isNotBlank(lightType)) {
            builder = builder.setLightType(StringUtils.isBlank(lightType) ? Oslp.LightType.LT_NOT_SET : Enum.valueOf(
                    Oslp.LightType.class, lightType));
        }

        // Dali Configuration
        if (lightType.equals("DALI") && (StringUtils.isNotBlank(dcLights) || StringUtils.isNotBlank(dcMap))) {
            Oslp.DaliConfiguration.Builder dcBuilder = Oslp.DaliConfiguration.newBuilder();
            if (StringUtils.isNotBlank(dcLights)) {
                dcBuilder = dcBuilder.setNumberOfLights(OslpUtils.integerToByteString(Integer.parseInt(dcLights)));
            }
            if (StringUtils.isNotBlank(dcMap)) {
                for (final String i : dcMap.split(";")) {
                    final String[] j = i.split(",");
                    dcBuilder = dcBuilder.addAddressMap(Oslp.IndexAddressMap.newBuilder()
                            .setIndex(OslpUtils.integerToByteString(Integer.parseInt(j[0])))
                            .setAddress(OslpUtils.integerToByteString(Integer.parseInt(j[1])))
                            .setRelayType(Oslp.RelayType.LIGHT));
                }
            }
            builder = builder.setDaliConfiguration(dcBuilder);
        }

        // Relay Configuration
        if (lightType.equals("RELAY") && (StringUtils.isNotBlank(rcType) && StringUtils.isNotBlank(rcMap))) {
            Oslp.RelayConfiguration.Builder rcBuilder = Oslp.RelayConfiguration.newBuilder();
            for (final String i : rcMap.split(";")) {
                final String[] j = i.split(",");
                rcBuilder = rcBuilder.addAddressMap(Oslp.IndexAddressMap.newBuilder()
                        .setIndex(OslpUtils.integerToByteString(Integer.parseInt(j[0])))
                        .setAddress(OslpUtils.integerToByteString(Integer.parseInt(j[1])))
                        .setRelayType(Enum.valueOf(Oslp.RelayType.class, rcType)));
            }
            builder = builder.setRelayConfiguration(rcBuilder);
        }

        if (StringUtils.isNotBlank(shortInterval)) {
            builder = builder.setShortTermHistoryIntervalMinutes(Integer.parseInt(shortInterval.trim()));
        }

        if (StringUtils.isNotBlank(preferredLinkType)) {
            builder = builder.setPreferredLinkType(Enum.valueOf(Oslp.LinkType.class, preferredLinkType.trim()));
        }

        if (StringUtils.isNotBlank(meterType)) {
            builder = builder.setMeterType(Enum.valueOf(Oslp.MeterType.class, meterType.trim()));
        }

        if (StringUtils.isNotBlank(longInterval)) {
            builder = builder.setLongTermHistoryInterval(Integer.parseInt(longInterval.trim()));
        }

        if (StringUtils.isNotBlank(longIntervalType)) {
            builder = builder.setLongTermHistoryIntervalType(Enum.valueOf(Oslp.LongTermIntervalType.class,
                    longIntervalType.trim()));
        }

        this.oslpResponse = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setGetConfigurationResponse(builder).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpResponse,
                this.channelMock, this.device.getNetworkAddress());
        this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
        this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);
    }

    @DomainStep("the get configuration oslp message returns failure (.*)")
    public void givenTheOslpResponse(final String failure) throws Exception {
        LOGGER.info("GIVEN: the get configuration oslp message returns failure {}.", failure);

        Oslp.GetConfigurationResponse.Builder builder = Oslp.GetConfigurationResponse.newBuilder();

        switch (failure) {
        case "FAILURE":
            builder = builder.setStatus(Status.FAILURE);
            this.oslpResponse = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                    .withPayloadMessage(Message.newBuilder().setGetConfigurationResponse(builder).build()).build();
            this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpResponse,
                    this.channelMock, this.device.getNetworkAddress());
            this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);
            break;
        case "REJECTED":
            builder = builder.setStatus(Status.REJECTED);
            this.oslpResponse = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                    .withPayloadMessage(Message.newBuilder().setGetConfigurationResponse(builder).build()).build();
            this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpResponse,
                    this.channelMock, this.device.getNetworkAddress());
            this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);
            break;
        case "N/A":
            this.oslpResponse = null;
            break;
        default:
            throw new Exception("Unknown oslp response failure status");
        }
    }

    // === WHEN ===

    @DomainStep("the get configuration data request is received")
    public void whenTheRequestIsReceived() {
        LOGGER.info("WHEN: the get configuration data request is received.");

        try {
            this.getConfigurationAsyncResponse = this.configurationManagementEndpoint.getConfiguration(ORGANISATION_ID,
                    this.request);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("the get configuration request should return an async response with a correlationId and deviceId (.*)")
    public boolean thenTheGetConfigurationRequestShouldReturnAnAsyncResponseWithACorrelationIdAndDeviceId(
            final String deviceId) {
        LOGGER.info(
                "THEN: the get configuration request should return an async response with a correlationId and deviceId {}.",
                deviceId);

        try {
            Assert.assertNotNull("Response should not be null", this.getConfigurationAsyncResponse);
            Assert.assertNotNull("CorrelationId should not be null", this.getConfigurationAsyncResponse
                    .getAsyncResponse().getCorrelationUid());
            Assert.assertNull("Throwable should be null", this.throwable);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("a get configuration data oslp message is sent to device (.*) should be (.*)")
    public boolean thenAnOslpMessageShouldBeSent(final String device, final Boolean isMessageSent) {
        LOGGER.info("THEN: a get configuration oslp message is sent to device {} should be {}.", device, isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, timeout(1000).times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpRequest = argument.getValue();

                Assert.assertTrue("Message should contain get configuration request.", this.oslpRequest
                        .getPayloadMessage().hasGetConfigurationRequest());
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the get configuration response should contain: (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*) and (.*)")
    public boolean thenTheResponseShouldContain(final String lightType, final String dcLights, final String dcMap,
            final String rcType, final String rcMap, final String shortInterval, final String preferredLinkType,
            final String meterType, final String longInterval, final String longIntervalType) {
        LOGGER.info("THEN: the get configuration response should return {}, {}, {}, {}, {}, {}, {}, {}, {} and {}.",
                new Object[] { lightType, dcLights, dcMap, rcType, rcMap, shortInterval, preferredLinkType, meterType,
                        longInterval, longIntervalType });

        try {
            Assert.assertNotNull("Response should not be null", this.response);
            Assert.assertNull("Throwable should be null", this.throwable);

            final Configuration configuration = this.response.getConfiguration();

            Assert.assertNotNull("Configuration should not be null", configuration);

            // light type
            Assert.assertEquals("Light type should equal expected value", lightType.trim(),
                    configuration.getLightType() != null ? configuration.getLightType().name() : "");

            // dali configuration
            if (configuration.getLightType() == LightType.DALI
                    && (StringUtils.isNotBlank(dcLights) || StringUtils.isNotBlank(dcMap))) {

                final DaliConfiguration daliConfiguration = configuration.getDaliConfiguration();

                Assert.assertNotNull("Dali configuration should not be null", daliConfiguration);

                // lights
                Assert.assertEquals("Dali configuration lights should equal expected value",
                        StringUtils.isNotBlank(dcLights.trim()) ? Integer.parseInt(dcLights.trim()) : 0,
                        daliConfiguration.getNumberOfLights());

                // index address map
                Assert.assertNotNull("Index address map should not be null", daliConfiguration.getIndexAddressMap());
                Assert.assertEquals("Index address map should equal expected value",
                        StringUtils.deleteWhitespace(dcMap),
                        this.convertIndexAddressMapToString(daliConfiguration.getIndexAddressMap()));
            }

            // relay configuration
            if (configuration.getLightType() == LightType.RELAY && StringUtils.isNotBlank(rcType)
                    && StringUtils.isNotBlank(rcMap)) {

                final RelayConfiguration relayConfiguration = configuration.getRelayConfiguration();

                Assert.assertNotNull("Relay configuration should not be null", relayConfiguration);

                // Type

                Assert.assertEquals("Relay type should equal expected value", rcType, (relayConfiguration.getRelayMap()
                        .get(0).getRelayType().value()));

                // Relay map
                Assert.assertEquals("Relay map should equal expected value", StringUtils.deleteWhitespace(rcMap),
                        this.convertRelayMapToString(relayConfiguration.getRelayMap()));
            }

            // shortInterval
            Assert.assertEquals("Short interval should equal expected value", StringUtils.isBlank(shortInterval) ? null
                    : Integer.parseInt(shortInterval.trim()), configuration.getShortTermHistoryIntervalMinutes());

            // preferredLinkType
            Assert.assertEquals("Preferred link type should equal expected value", preferredLinkType.trim(),
                    configuration.getPreferredLinkType() != null ? configuration.getPreferredLinkType().value() : "");

            // meterType
            Assert.assertEquals("Meter type should equal expected value", meterType.trim(),
                    configuration.getMeterType() != null ? configuration.getMeterType().value() : "");

            // longInterval
            Assert.assertEquals("Long interval should equal expected value", StringUtils.isBlank(longInterval) ? null
                    : Integer.parseInt(longInterval.trim()), configuration.getLongTermHistoryInterval());

            // longIntervalType
            Assert.assertEquals("Long interval type should equal expected value", longIntervalType.trim(),
                    configuration.getLongTermHistoryIntervalType() != null ? configuration
                            .getLongTermHistoryIntervalType().value() : "");
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }

        return true;
    }

    @DomainStep("an ovl get configuration result message with result (.*) and description (.*) should be sent to the ovl out queue")
    public boolean thenAnOvlGetConfigurationResultMessageShouldBeSentToTheOvlOutQueue(final String result,
            final String description) {
        LOGGER.info(
                "THEN: \"an ovl get configuration result message with result [{}] and description [{}] should be sent to the ovl out queue\".",
                result, description);

        try {
            final ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);

            verify(this.webServiceResponseMessageSenderMock, timeout(10000).times(1)).send(argument.capture());

            // Check the result.
            final String expected = result.equals("NULL") ? null : result;
            final String actual = argument.getValue().getResult().getValue();

            Assert.assertTrue("Invalid result, found: " + actual + " , expected: " + expected, actual.equals(expected));

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the get configuration response request is received")
    public void whenTheGetGetConfigurationResponseRequestIsReceived() {
        LOGGER.info("WHEN: the get configuration request is received.");

        try {

            this.response = this.configurationManagementEndpoint.getGetConfigurationResponse(ORGANISATION_ID,
                    this.getConfigurationAsyncRequest);

            Assert.assertNotNull("Response should not be null", this.response);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("a get get configuration response request with correlationId (.*) and deviceId (.*)")
    public void givenAGetConfigurationResponseRequest(final String correlationId, final String deviceId) {
        LOGGER.info("GIVEN: \"a get configuration response request with correlationId {} and deviceId {}\".",
                correlationId, deviceId);

        this.setUp();

        this.getConfigurationAsyncRequest = new GetConfigurationAsyncRequest();

        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationId);
        asyncRequest.setDeviceId(deviceId);
        this.getConfigurationAsyncRequest.setAsyncRequest(asyncRequest);
    }

    @DomainStep("a get configuration response message with correlationId (.*), deviceId (.*), qresult (.*), qdescription (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*) is found in the queue (.*)")
    public void givenAGetConfigurationResponseMessageIsFoundInQueue(final String correlationId, final String deviceId,
            final String qresult, final String qdescription, final String lightType, final String dcLights,
            final String dcMap, final String rcType, final String rcMap, final String shortInterval,
            final String preferredLinkType, final String meterType, final String longInterval,
            final String longIntervalType, final Boolean isFound) {
        LOGGER.info(
                "GIVEN: \"a get configuration response message with correlationId {}, deviceId {}, qresult {} and qdescription {} is found {}\".",
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
                } else {

                    final com.alliander.osgp.domain.core.valueobjects.LightType lighttype = StringUtils
                            .isBlank(lightType) ? null : Enum.valueOf(
                            com.alliander.osgp.domain.core.valueobjects.LightType.class, lightType);
                    final Map<Integer, Integer> indexAddressMap = new HashMap<Integer, Integer>();

                    final com.alliander.osgp.domain.core.valueobjects.DaliConfiguration daliconfiguration = new com.alliander.osgp.domain.core.valueobjects.DaliConfiguration(
                            22, indexAddressMap);

                    final List<com.alliander.osgp.domain.core.valueobjects.RelayMap> relayMap = new ArrayList<>();
                    final com.alliander.osgp.domain.core.valueobjects.RelayConfiguration relayConf = new com.alliander.osgp.domain.core.valueobjects.RelayConfiguration(
                            relayMap);

                    final MeterType metertype = StringUtils.isBlank(meterType) ? null : Enum.valueOf(MeterType.class,
                            meterType);
                    final LongTermIntervalType longtermintervalType = StringUtils.isBlank(longIntervalType) ? null
                            : Enum.valueOf(LongTermIntervalType.class, longIntervalType);

                    final Integer shortinterval = StringUtils.isBlank(shortInterval) ? 0 : Integer
                            .valueOf(shortInterval);
                    final Integer longinterval = StringUtils.isBlank(longInterval) ? 0 : Integer.valueOf(longInterval);

                    // construct new Configuration
                    dataObject = new com.alliander.osgp.domain.core.valueobjects.Configuration(lighttype,
                            daliconfiguration, relayConf, shortinterval, LinkType.ETHERNET, metertype, longinterval,
                            longtermintervalType);
                }

                final ResponseMessage message = new ResponseMessage(correlationId, ORGANISATION_ID, deviceId, result,
                        exception, dataObject);

                when(messageMock.getObject()).thenReturn(message);

            } catch (final JMSException e) {
                e.printStackTrace();
            }

            when(this.commonResponsesJmsTemplateMock.receiveSelected(any(String.class))).thenReturn(messageMock);
        } else {
            when(this.commonResponsesJmsTemplateMock.receiveSelected(any(String.class))).thenReturn(null);
        }
    }

    @DomainStep("the get get configuration response request should return a get configuration response with result (.*) and description (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*)")
    public boolean thenTheResponseShouldContain(final String result, final String description, final String lightType,
            final String dcLights, final String dcMap, final String rcType, final String rcMap,
            final String shortInterval, final String preferredLinkType, final String meterType,
            final String longInterval, final String longIntervalType) {

        LOGGER.info(
                "THEN: the get get configuration response request should return a get configuration response with result {} .",
                result);

        try {
            if ("NOT_OK".equals(result)) {
                Assert.assertNull("Set Schedule Response should be null", this.response);
                Assert.assertNotNull("Throwable should not be null", this.throwable);
                Assert.assertTrue("Throwable should contain a validation exception",
                        this.throwable.getCause() instanceof ValidationException);
            } else {

                Assert.assertNotNull("Response should not be null", this.response);

                // Check the result.
                final String expected = result.equals("NULL") ? null : result;
                final String actual = this.response.getResult().toString();

                Assert.assertTrue("Invalid result, found: " + actual + " , expected: " + expected,
                        actual.equals(expected));

                if (this.response.getResult().equals("OK")) {

                    Assert.assertNotNull("Configuration is null", this.response.getConfiguration());

                    // light type
                    Assert.assertEquals("Light type should equal expected value", lightType.trim(), this.response
                            .getConfiguration().getLightType() != null ? this.response.getConfiguration()
                            .getLightType().name() : "");

                    // dali configuration
                    if (this.response.getConfiguration().getLightType() == LightType.DALI
                            && (StringUtils.isNotBlank(dcLights) || StringUtils.isNotBlank(dcMap))) {

                        final DaliConfiguration daliConfiguration = this.response.getConfiguration()
                                .getDaliConfiguration();

                        Assert.assertNotNull("Dali configuration should not be null", daliConfiguration);

                        // lights
                        Assert.assertEquals("Dali configuration lights should equal expected value",
                                StringUtils.isNotBlank(dcLights.trim()) ? Integer.parseInt(dcLights.trim()) : 0,
                                daliConfiguration.getNumberOfLights());

                        // index address map
                        Assert.assertNotNull("Index address map should not be null",
                                daliConfiguration.getIndexAddressMap());
                        Assert.assertEquals("Index address map should equal expected value",
                                StringUtils.deleteWhitespace(dcMap),
                                this.convertIndexAddressMapToString(daliConfiguration.getIndexAddressMap()));
                    }

                    // relay configuration
                    if (this.response.getConfiguration().getLightType() == LightType.RELAY
                            && StringUtils.isNotBlank(rcType) && StringUtils.isNotBlank(rcMap)) {

                        final RelayConfiguration relayConfiguration = this.response.getConfiguration()
                                .getRelayConfiguration();

                        Assert.assertNotNull("Relay configuration should not be null", relayConfiguration);

                        // Type

                        Assert.assertEquals("Relay type should equal expected value", rcType, (relayConfiguration
                                .getRelayMap().get(0).getRelayType().value()));

                        // Relay map
                        Assert.assertEquals("Relay map should equal expected value",
                                StringUtils.deleteWhitespace(rcMap),
                                this.convertRelayMapToString(relayConfiguration.getRelayMap()));
                    }

                    // shortInterval
                    Assert.assertEquals("Short interval should equal expected value",
                            StringUtils.isBlank(shortInterval) ? null : Integer.parseInt(shortInterval.trim()),
                            this.response.getConfiguration().getShortTermHistoryIntervalMinutes());

                    // preferredLinkType
                    Assert.assertEquals("Preferred link type should equal expected value", preferredLinkType.trim(),
                            this.response.getConfiguration().getPreferredLinkType() != null ? this.response
                                    .getConfiguration().getPreferredLinkType().value() : "");

                    // meterType
                    Assert.assertEquals("Meter type should equal expected value", meterType.trim(), this.response
                            .getConfiguration().getMeterType() != null ? this.response.getConfiguration()
                            .getMeterType().value() : "");

                    // longInterval
                    Assert.assertEquals("Long interval should equal expected value",
                            StringUtils.isBlank(longInterval) ? null : Integer.parseInt(longInterval.trim()),
                            this.response.getConfiguration().getLongTermHistoryInterval());

                    // longIntervalType
                    Assert.assertEquals("Long interval type should equal expected value", longIntervalType.trim(),
                            this.response.getConfiguration().getLongTermHistoryIntervalType() != null ? this.response
                                    .getConfiguration().getLongTermHistoryIntervalType().value() : "");
                }
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }

        return true;
    }

    @DomainStep("the get configuration data response should return result (.*)")
    public boolean thenTheResponseShouldReturn(final String result) {
        LOGGER.info("THEN: the get status response should return {}.", result);

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

    // === Private methods ===

    private void setUp() {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.organisationRepositoryMock,
                this.deviceAuthorizationRepositoryMock, this.oslpLogItemRepositoryMock, this.oslpDeviceRepositoryMock,
                this.channelMock, this.webServiceResponseMessageSenderMock });

        this.configurationManagementEndpoint = new ConfigurationManagementEndpoint(this.configurationManagementService,
                new ConfigurationManagementMapper());
        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        this.getConfigurationAsyncRequest = null;
        this.getConfigurationAsyncResponse = null;

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

    private String convertIndexAddressMapToString(final List<IndexAddressMap> indexAddressMapList) {
        String result = "";
        for (final IndexAddressMap indexAddressMap : indexAddressMapList) {
            result += indexAddressMap.getIndex() + "," + indexAddressMap.getAddress() + ";";
        }
        result = StringUtils.removeEnd(result, ";");

        return result;
    }

    private String convertRelayMapToString(final List<RelayMap> relayMapList) {
        String result = "";
        for (final RelayMap relayMap : relayMapList) {
            result += relayMap.getIndex() + "," + relayMap.getAddress() + ";";
        }
        result = StringUtils.removeEnd(result, ";");

        return result;
    }
}
