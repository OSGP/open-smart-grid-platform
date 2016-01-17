/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.schedulemanagement;

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

import com.alliander.osgp.acceptancetests.DateUtils;
import com.alliander.osgp.acceptancetests.OslpTestUtils;
import com.alliander.osgp.acceptancetests.ProtocolInfoTestUtils;
import com.alliander.osgp.adapter.domain.tariffswitching.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.application.services.DeviceRegistrationService;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDeviceBuilder;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerClient;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpDeviceService;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleRequest;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleResponse;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.TariffSchedule;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.TariffValue;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.WeekDayType;
import com.alliander.osgp.adapter.ws.tariffswitching.application.mapping.ScheduleManagementMapper;
import com.alliander.osgp.adapter.ws.tariffswitching.application.services.ScheduleManagementService;
import com.alliander.osgp.adapter.ws.tariffswitching.endpoints.TariffSwitchingScheduleManagementEndpoint;
import com.alliander.osgp.adapter.ws.tariffswitching.infra.jms.TariffSwitchingResponseMessageFinder;
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
import com.alliander.osgp.domain.core.valueobjects.RelayType;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.Oslp.Status;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Configurable
@DomainSteps
public class SetTariffScheduleSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetScheduleSteps.class);

    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";
    private static final String ORGANISATION_ID = "ORGANISATION-01";
    private static final String ORGANISATION_PREFIX = "ORG";

    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // WS Adapter fields
    private TariffSwitchingScheduleManagementEndpoint scheduleManagementEndpoint;

    private SetScheduleRequest request;
    private SetScheduleAsyncResponse setScheduleAsyncResponse;
    private SetScheduleAsyncRequest setTariffScheduleAsyncRequest;
    private SetScheduleResponse response;

    @Autowired
    @Qualifier("wsTariffSwitchingScheduleManagementService")
    private ScheduleManagementService scheduleManagementService;

    @Autowired
    @Qualifier("wsTariffSwitchingIncomingResponsesMessageFinder")
    private TariffSwitchingResponseMessageFinder tariffSwitchingResponseMessageFinder;

    @Autowired
    @Qualifier("wsTariffSwitchingIncomingResponsesJmsTemplate")
    private JmsTemplate tariffSwitchingResponsesJmsTemplate;

    // Domain Adapter fields
    @Autowired
    @Qualifier("domainTariffSwitchingOutgoingWebServiceResponseMessageSender")
    private WebServiceResponseMessageSender webServiceResponseMessageSenderMock;

    private Ssld device;
    private RelayType deviceRelayType;
    private Organisation organisation;

    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private SsldRepository ssldRepositoryMock;
    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepositoryMock;
    @Autowired
    private OrganisationRepository organisationRepositoryMock;
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
    private OslpEnvelope oslpMessage;
    private OslpChannelHandlerClient oslpChannelHandler;
    @Autowired
    private Channel channelMock;

    // Test fields
    private Throwable throwable;

    // === GIVEN ===

    @DomainStep("a set tariff schedule request for device (.*) with weekday (.*), startday (.*), endday (.*), time (.*), index (.*), relayType (.*) and ishigh (.*)")
    public void givenASetTariffScheduleRequestForDeviceWithWeekdayStartdayEnddayTimeAndIshigh(
            final String deviceIdentification, final String weekday, final String startday, final String endday,
            final String time, final Integer index, final String relayType, final boolean isHigh) throws Exception {

        LOGGER.info(
                "GIVEN: \"a set tariff schedule request for device {} with weekday {}, startday {}, endday {}, time {}, index {}, relayType {} and ishigh {}\".",
                new Object[] { deviceIdentification, weekday, startday, endday, time, index, relayType, isHigh });

        this.setUp();

        this.request = new SetScheduleRequest();

        this.request.setDeviceIdentification(deviceIdentification);

        final TariffSchedule schedule = new TariffSchedule();
        schedule.setWeekDay(weekday == null || weekday.isEmpty() ? null : WeekDayType.valueOf(weekday));
        schedule.setStartDay(DateUtils.convertToXMLGregorianCalendar(startday, DATE_FORMAT));
        schedule.setEndDay(DateUtils.convertToXMLGregorianCalendar(endday, DATE_FORMAT));
        schedule.setTime(time);

        final List<TariffValue> tariffValues = new ArrayList<>();
        final TariffValue tariffvalue = new TariffValue();

        // Set the relay index.
        tariffvalue.setIndex(index);

        // Set the relay type.
        if (!relayType.equals("NULL")) {
            this.deviceRelayType = RelayType.valueOf(relayType);
        }

        // Set the tariff relay value.
        if (this.deviceRelayType == null) {
            tariffvalue.setHigh(isHigh);
        } else {
            switch (this.deviceRelayType) {
            case LIGHT:
                // Do nothing.
                break;
            case TARIFF:
                tariffvalue.setHigh(isHigh);
                break;
            case TARIFF_REVERSED:
                tariffvalue.setHigh(!isHigh);
                break;
            default:
                // Do nothing.
                break;
            }
        }

        tariffValues.add(tariffvalue);
        schedule.getTariffValue().addAll(tariffValues);

        this.request.getSchedules().add(schedule);
    }

    @DomainStep("the set tariff schedule request refers device (.*) with status (.*) which always returns (.*)")
    public void givenTheSetTariffScheduleRequestRefersToDeviceWithStatus(final String deviceIdentification,
            final String status, final String response) throws Exception {
        LOGGER.info("GIVEN: \"the set tariff schedule request refers to a device {} with status {}\".", new Object[] {
                deviceIdentification, status });

        switch (status.toUpperCase()) {
        case "ACTIVE":
            this.createDevice(deviceIdentification, true);
            when(this.deviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);
            when(this.ssldRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);
            when(this.ssldRepositoryMock.findOne(1L)).thenReturn(this.device);
            when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(
                    this.oslpDevice);
            when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);

            // create oslp response
            final com.alliander.osgp.oslp.Oslp.SetScheduleResponse oslpResponse = com.alliander.osgp.oslp.Oslp.SetScheduleResponse
                    .newBuilder().setStatus(Status.valueOf(response)).build();

            this.oslpEnvelope = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                    .withPayloadMessage(Message.newBuilder().setSetScheduleResponse(oslpResponse).build()).build();

            this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpEnvelope,
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

    @DomainStep("the set tariff schedule request refers to an authorised organisation")
    public void givenTheSetTariffScheduleRequestRefersToAnAuthorisedOrganisation() {
        LOGGER.info("GIVEN: \"the set tariff schedule request refers to an authorised organisation\".");

        this.organisation = new Organisation(ORGANISATION_ID, ORGANISATION_ID, ORGANISATION_PREFIX,
                PlatformFunctionGroup.USER);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID)).thenReturn(
                this.organisation);

        final List<DeviceAuthorization> authorizations = new ArrayList<>();
        authorizations.add(new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.organisation)
                .withFunctionGroup(DeviceFunctionGroup.TARIFF_SCHEDULING).build());
        when(this.deviceAuthorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device))
        .thenReturn(authorizations);
    }

    @DomainStep("a get set tariff schedule response request with correlationId (.*) and deviceId (.*)")
    public void givenAGetSetTariffScheduleResultRequestWithCorrelationId(final String correlationId,
            final String deviceId) {
        LOGGER.info("GIVEN: \"a get set tariff schedule response with correlationId {} and deviceId {}\".",
                correlationId, deviceId);

        this.setUp();

        this.setTariffScheduleAsyncRequest = new SetScheduleAsyncRequest();

        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationId);
        asyncRequest.setDeviceId(deviceId);

        this.setTariffScheduleAsyncRequest.setAsyncRequest(asyncRequest);
    }

    @DomainStep("a set tariff schedule response message with correlationId (.*), deviceId (.*), qresult (.*) and qdescription (.*) is found in the queue (.*)")
    public void givenATariffScheduleLightResponseMessageIsFoundInTheQueue(final String correlationId,
            final String deviceId, final String qresult, final String qdescription, final Boolean isFound) {
        LOGGER.info(
                "GIVEN: \"a set tariff schedule response message with correlationId {}, deviceId {}, qresult {} and qdescription {} is found {}\".",
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
                    dataObject = new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, null,
                            new ValidationException());
                    exception = (OsgpException) dataObject;
                }
                final ResponseMessage message = new ResponseMessage(correlationId, ORGANISATION_ID, deviceId,
                        ResponseMessageResultType.valueOf(qresult), exception, dataObject);
                when(messageMock.getObject()).thenReturn(message);
            } catch (final JMSException e) {
                LOGGER.error("JMSException", e);
            }

            when(this.tariffSwitchingResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(messageMock);
        } else {
            when(this.tariffSwitchingResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(null);
        }
    }

    // === WHEN ===

    @DomainStep("the set tariff schedule request is received")
    public void whenTheSetTariffScheduleRequestIsReceived() {
        LOGGER.info("WHEN: \"the set tariff schedule request is received\".");

        try {
            this.setScheduleAsyncResponse = this.scheduleManagementEndpoint.setSchedule(ORGANISATION_ID, this.request);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("the get set tariff schedule response request is received")
    public void whenTheGetSetTariffScheduleResultReqeustIsReceived() {
        LOGGER.info("WHEN: \"the set tariff schedule request is received\".");

        try {
            this.response = this.scheduleManagementEndpoint.getSetScheduleResponse(ORGANISATION_ID,
                    this.setTariffScheduleAsyncRequest);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("the set tariff schedule request should return an async response with a correlationId and deviceId (.*)")
    public boolean thenASetTariffScheduleRequestShouldReturnAnAsyncResponseWithACorrelationIdAndDeviceId(
            final String deviceId) {
        LOGGER.info(
                "THEN: \"the set tariff schedule request should return a async response with a correlationId and deviceId {}\".",
                deviceId);

        try {
            Assert.assertNotNull("Set Tariff Schedule Async Response should not be null", this.setScheduleAsyncResponse);
            Assert.assertNotNull("Async Response should not be null", this.setScheduleAsyncResponse.getAsyncResponse());
            Assert.assertNotNull("CorrelationId should not be null", this.setScheduleAsyncResponse.getAsyncResponse()
                    .getCorrelationUid());
            Assert.assertNull("Throwable should be null", this.throwable);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the set tariff schedule request should return a validation error")
    public boolean thenTheSetTariffRequestShouldReturnAValidationError() {
        LOGGER.info("THEN: \"the set tariff schedule request should return a validation error\".");
        try {
            Assert.assertNull("Set Schedule Async Response should be null", this.setScheduleAsyncResponse);
            Assert.assertNotNull("Throwable should not be null", this.throwable);
            Assert.assertTrue(this.throwable.getCause() instanceof ValidationException);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("a set tariff schedule oslp message is sent to device (.*) should be (.*)")
    public boolean thenASetTariffScheduleOslpMessageShouldBeSent(final String deviceIdentification,
            final Boolean isMessageSent) {
        LOGGER.info("THEN: \"a set tariff schedule oslp message is sent to device [{}] should be [{}]\".",
                deviceIdentification, isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, timeout(10000).times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpMessage = argument.getValue();

                Assert.assertTrue("Message should contain set schedule request.", this.oslpMessage.getPayloadMessage()
                        .hasSetScheduleRequest());
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("an ovl set tariff schedule result message with result (.*) and description (.*) should be sent to the ovl out queue")
    public boolean thenAnOvlSetTariffScheduleResultMessageShouldBeSentToTheOvlOutQueue(final String result,
            final String description) {
        LOGGER.info(
                "THEN: \"an ovl set tariff schedule result message with result [{}] and description [{}] should be sent to the ovl out queue\".",
                result, description);

        try {
            final ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
            verify(this.webServiceResponseMessageSenderMock, timeout(10000).times(1)).send(argument.capture());

            final String expected = result.equals("NULL") ? null : result;
            final String actual = argument.getValue().getResult().getValue();

            LOGGER.info("THEN: message description: "
                    + (argument.getValue().getOsgpException() == null ? "" : argument.getValue().getOsgpException()
                            .getMessage()));

            Assert.assertTrue("Invalid result, found: " + actual + " , expected: " + expected, actual.equals(expected));
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the get set tariff schedule response request should return a set schedule response with result (.*) and description (.*)")
    public boolean thenTheGetSetTariffScheduleResultRequestShouldReturnAGetSetScheduleResultResponseWithResult(
            final String result, final String description) {
        LOGGER.info(
                "THEN: \"the get set tariff schedule result request should return a get set schedule response with result {} and description {}\".",
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
                this.organisationRepositoryMock, this.logItemRepositoryMock, this.channelMock,
                this.webServiceResponseMessageSenderMock, this.oslpDeviceRepositoryMock });

        this.scheduleManagementEndpoint = new TariffSwitchingScheduleManagementEndpoint(this.scheduleManagementService,
                new ScheduleManagementMapper());
        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        this.throwable = null;

        this.request = null;
        this.setScheduleAsyncResponse = null;
        this.setTariffScheduleAsyncRequest = null;
        this.response = null;
    }

    private void createDevice(final String deviceIdentification, final Boolean activated) {
        LOGGER.info("Creating device [{}] with active [{}]", deviceIdentification, activated);

        this.device = (Ssld) new DeviceBuilder().withDeviceIdentification(deviceIdentification).ofDeviceType("SSLD")
                .withNetworkAddress(activated ? InetAddress.getLoopbackAddress() : null)
                .withPublicKeyPresent(PUBLIC_KEY_PRESENT)
                .withProtocolInfo(ProtocolInfoTestUtils.getProtocolInfo(PROTOCOL, PROTOCOL_VERSION))
                .isActivated(activated).build();

        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withDeviceUid(DEVICE_UID).build();
    }
}
