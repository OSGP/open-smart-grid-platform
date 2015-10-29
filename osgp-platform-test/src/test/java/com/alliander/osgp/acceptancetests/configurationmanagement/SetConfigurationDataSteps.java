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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.IndexAddressMap;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.LightType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.LinkType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.LongTermIntervalType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.MeterType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.RelayConfiguration;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.RelayMap;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.RelayType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.SetConfigurationAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.SetConfigurationAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.SetConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.SetConfigurationResponse;
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
import com.alliander.osgp.oslp.Oslp;
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
public class SetConfigurationDataSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetConfigurationDataSteps.class);

    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";
    private static final String ORGANISATION_ID = "ORGANISATION-01";
    private static final String ORGANISATION_PREFIX = "ORG";

    // TODO - Add as parameters to tests
    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // WS Adapter fields
    private ConfigurationManagementEndpoint configurationManagementEndpoint;

    private SetConfigurationRequest request;
    private SetConfigurationAsyncResponse setConfigurationAsyncResponse;
    private SetConfigurationAsyncRequest setConfigurationAsyncRequest;
    private SetConfigurationResponse response;

    @Autowired
    @Qualifier("wsCoreConfigurationManagementService")
    private ConfigurationManagementService configurationManagementService;

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

    private OslpEnvelope oslpResponse;
    private OslpChannelHandlerClient oslpChannelHandler;
    @Autowired
    private Channel channelMock;

    // Test fields
    private Throwable throwable;

    private void setUp() {
        LOGGER.info("Setting up {}", SetConfigurationDataSteps.class.getSimpleName());

        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.organisationRepositoryMock,
                this.deviceAuthorizationRepositoryMock, this.deviceLogItemRepositoryMock,
                this.webServiceResponseMessageSenderMock, this.channelMock });

        this.configurationManagementEndpoint = new ConfigurationManagementEndpoint(this.configurationManagementService,
                new ConfigurationManagementMapper());
        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        this.setConfigurationAsyncRequest = null;
        this.setConfigurationAsyncResponse = null;

        this.request = null;
        this.device = null;
        this.oslpDevice = null;
        this.oslpChannelHandler = null;
        this.organisation = null;
        this.response = null;
        this.throwable = null;
        this.oslpResponse = null;
    }

    @DomainStep("a set configuration data request for device (.*) with data (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*) and (.*)")
    public void givenASetConfigurationRequest(final String device, final String lightType, final String dcLights,
            final String dcMap, final String rcType, final String rcMap, final String shortInterval,
            final String preferredLinkType, final String meterType, final String longInterval,
            final String longIntervalType) {
        this.setUp();

        LOGGER.info(
                "GIVEN: a set configuration data request for device {} with data {}, {}, {}, {}, {}, {}, {}, {}, {} and {}",
                new Object[] { device, lightType, dcLights, dcMap, rcType, rcMap, shortInterval, preferredLinkType,
                        meterType, longInterval, longIntervalType });

        this.request = new SetConfigurationRequest();
        final Configuration configuration = new Configuration();
        this.request.setConfiguration(configuration);

        if (StringUtils.isNotBlank(device)) {
            this.request.setDeviceIdentification(device);
        }

        if (StringUtils.isNotBlank(lightType)) {
            configuration.setLightType(LightType.valueOf(lightType));
        }

        if (StringUtils.isNotBlank(dcLights) || StringUtils.isNotBlank(dcMap)) {
            final DaliConfiguration daliConfiguration = new DaliConfiguration();

            if (StringUtils.isNotBlank(dcLights)) {
                daliConfiguration.setNumberOfLights(Integer.valueOf(dcLights));
            }

            if (StringUtils.isNotBlank(dcMap)) {
                for (final String dc : dcMap.split(";")) {
                    final String[] dcArray = dc.split(",");

                    final IndexAddressMap indexAddressMap = new IndexAddressMap();
                    indexAddressMap.setIndex(Integer.valueOf(dcArray[0]));
                    indexAddressMap.setAddress(Integer.valueOf(dcArray[1]));

                    daliConfiguration.getIndexAddressMap().add(indexAddressMap);
                }
            }

            configuration.setDaliConfiguration(daliConfiguration);
        }

        if (StringUtils.isNotBlank(rcType) || StringUtils.isNotBlank(rcMap)) {
            final RelayConfiguration relayConfiguration = new RelayConfiguration();

            // TODO add relay per configuration mapping to the tests, now they
            // all get the same
            if (StringUtils.isNotBlank(rcMap)) {
                for (final String rc : rcMap.split(";")) {
                    final String[] rcArray = rc.split(",");

                    final RelayMap relayMap = new RelayMap();
                    relayMap.setIndex(Integer.valueOf(rcArray[0]));
                    relayMap.setAddress(Integer.valueOf(rcArray[1]));

                    if (StringUtils.isNotBlank(rcType)) {
                        relayMap.setRelayType(RelayType.valueOf(rcType));
                    }
                    relayConfiguration.getRelayMap().add(relayMap);
                }
            }

            configuration.setRelayConfiguration(relayConfiguration);
        }

        if (StringUtils.isNotBlank(shortInterval)) {
            configuration.setShortTermHistoryIntervalMinutes(Integer.valueOf(shortInterval));
        }

        if (StringUtils.isNotBlank(preferredLinkType)) {
            configuration.setPreferredLinkType(LinkType.valueOf(preferredLinkType));
        }

        if (StringUtils.isNotBlank(meterType)) {
            configuration.setMeterType(MeterType.valueOf(meterType));
        }

        if (StringUtils.isNotBlank(longInterval)) {
            configuration.setLongTermHistoryInterval(Integer.valueOf(longInterval));
        }

        if (StringUtils.isNotBlank(longIntervalType)) {
            configuration.setLongTermHistoryIntervalType(LongTermIntervalType.valueOf(longIntervalType));
        }
    }

    @DomainStep("the set configuration data request refers to a device (.*) with status (.*) which always returns (.*)")
    public void givenTheRequestRefersToDevice(final String deviceIdentification, final String status,
            final String response) throws Exception {
        LOGGER.info(
                "GIVEN: the set configuration data request refers to a device {} with status {} which always returns {}",
                new Object[] { deviceIdentification, status, response });

        switch (status.toUpperCase()) {
        case "ACTIVE":
            this.createDevice(deviceIdentification, true);
            this.initializeOslp(response);
            when(this.deviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);
            when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(
                    this.oslpDevice);
            when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);
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

    @DomainStep("the set configuration data request refers to an organisation that is authorised")
    public void givenTheSetConfigurationRequestRefersToAnAuthorisedOrganisation() {
        LOGGER.info("GIVEN: the set configuration data request refers to an organisation that is authorised");

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

    @DomainStep("the set configuration data request is received")
    public void whenRequestIsReceived() {
        LOGGER.info("WHEN: the set configuration data request is received");
        try {

            this.setConfigurationAsyncResponse = this.configurationManagementEndpoint.setConfiguration(ORGANISATION_ID,
                    this.request);

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getStackTrace());
            this.throwable = t;
        }
    }

    @DomainStep("the set configuration request should return an async response with a correlationId and deviceId (.*)")
    public boolean thenTheSetConfigurationRequestShouldReturnASetConfigurationResponseWithACorrelationID(
            final String deviceId) {
        LOGGER.info(
                "THEN: \"the set configuration request should return a set configuration response with a correlationId and deviceId {}\".",
                deviceId);

        // TODO Add check on device id
        try {
            Assert.assertNotNull("Set Configuration Async Response should not be null",
                    this.setConfigurationAsyncResponse);
            Assert.assertNotNull("Async Response should not be null",
                    this.setConfigurationAsyncResponse.getAsyncResponse());
            Assert.assertNotNull("CorrelationId should not be null", this.setConfigurationAsyncResponse
                    .getAsyncResponse().getCorrelationUid());
            Assert.assertNull("Throwable should be null", this.throwable);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the set configuration request should return a validation error")
    public boolean thenTheSetConfigurationRequestShouldReturnAValidationError() {
        try {
            Assert.assertNull("Set Configuration Async Response should be null", this.setConfigurationAsyncResponse);
            Assert.assertNotNull("Throwable should not be null", this.throwable);
            Assert.assertTrue(this.throwable.getCause() instanceof ValidationException);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("a set configuration oslp message is sent to device (.*) should be (.*)")
    public boolean thenASetConfigurationOslpMessageShouldBeSent(final String device, final Boolean isMessageSent) {
        LOGGER.info("THEN: \"a set configuration oslp message is sent to device [{}] should be [{}]\".", device,
                isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, timeout(10000).times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpResponse = argument.getValue();

                Assert.assertTrue("Message should contain set configuration request.", this.oslpResponse
                        .getPayloadMessage().hasSetConfigurationRequest());
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("an ovl set configuration result message with result (.*) and description (.*) should be sent to the ovl out queue")
    public boolean thenAnOvlSetConfigurationResultMessageShouldBeSentToTheOvlOutQueue(final String result,
            final String description) {
        LOGGER.info(
                "THEN: \"an ovl set configuration result message with result [{}] and description [{}] should be sent to the ovl out queue\".",
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

    @DomainStep("a set configuration data oslp message is sent to device (.*) should be (.*)")
    public boolean thenOslpMessageIsSent(final String deviceIdentification, final Boolean isMessageSent) {
        LOGGER.info("THEN: a set configuration data oslp message is sent to device {} should be {}",
                deviceIdentification, isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpResponse = argument.getValue();

                Assert.assertTrue("Message should contain set configuration request.", this.oslpResponse
                        .getPayloadMessage().hasSetConfigurationRequest());
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }

        return true;
    }

    @DomainStep("the set configuration data request should return result (.*)")
    public boolean thenTheSetConfigurationRequestShouldReturn(final String result) {
        LOGGER.info("THEN: the set configuration request should return result {}", result);

        if (result.toUpperCase().equals("OK")) {
            try {
                Assert.assertNotNull("Response should not be null", this.response);
                Assert.assertNull("Throwable should be null", this.throwable);
            } catch (final Throwable t) {
                LOGGER.error("Throwable [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
                return false;
            }
        } else {
            try {
                Assert.assertEquals(result.toUpperCase(), this.throwable.getCause().getClass().getSimpleName()
                        .toUpperCase());
            } catch (final Throwable t) {
                LOGGER.error("Throwable [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
                return false;
            }
        }

        return true;
    }

    @DomainStep("a get set configuration response request with correlationId (.*) and deviceId (.*)")
    public void givenAGetSetConfigurationResultRequestWithCorrelationId(final String correlationId,
            final String deviceId) {
        LOGGER.info("GIVEN: \"a get set configuration response with correlationId {} and deviceId {}\".",
                correlationId, deviceId);

        this.setUp();

        this.setConfigurationAsyncRequest = new SetConfigurationAsyncRequest();

        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationId);
        asyncRequest.setDeviceId(deviceId);

        this.setConfigurationAsyncRequest.setAsyncRequest(asyncRequest);
    }

    @DomainStep("a set configuration response message with correlationId (.*), deviceId (.*), qresult (.*) and qdescription (.*) is found in the queue (.*)")
    public void givenASetConfigurationResponseMessageIsFoundInTheQueue(final String correlationId,
            final String deviceId, final String qresult, final String qdescription, final Boolean isFound) {
        LOGGER.info(
                "GIVEN: \"a set configuration response message with correlationId {}, deviceId {}, qresult {} and qdescription {} is found {}\".",
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

    @DomainStep("the get set configuration response request is received")
    public void whenTheGetSetConfigurationResultRequestIsReceived() {
        LOGGER.info("WHEN: \"the set configuration request is received\".");

        try {

            this.response = this.configurationManagementEndpoint.getSetConfigurationResponse(ORGANISATION_ID,
                    this.setConfigurationAsyncRequest);

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("the get set configuration response request should return a set configuration response with result (.*) and description (.*)")
    public boolean thenTheGetSetConfigurationResultRequestShouldReturnAGetSetConfigurationResultResponseWithResult(
            final String result, final String description) {
        LOGGER.info(
                "THEN: \"the get set configuration result request should return a get set configuration response with result {} and description {}",
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

    private void initializeOslp(final String response) {
        // device always responds ok
        final Oslp.SetConfigurationResponse oslpResponse = Oslp.SetConfigurationResponse.newBuilder()
                .setStatus(Status.valueOf(response)).build();

        this.oslpResponse = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setSetConfigurationResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpResponse,
                this.channelMock, this.device.getNetworkAddress());
        this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
        this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);
    }
}