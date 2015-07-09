/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.devicemonitoring;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.jboss.netty.channel.Channel;
import org.joda.time.DateTime;
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
import com.alliander.osgp.adapter.domain.publiclighting.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.application.services.DeviceRegistrationService;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDeviceBuilder;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerClient;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpDeviceService;
import com.alliander.osgp.adapter.ws.publiclighting.application.mapping.DeviceMonitoringMapper;
import com.alliander.osgp.adapter.ws.publiclighting.application.services.DeviceMonitoringService;
import com.alliander.osgp.adapter.ws.publiclighting.endpoints.DeviceMonitoringEndpoint;
import com.alliander.osgp.adapter.ws.publiclighting.infra.jms.PublicLightingResponseMessageFinder;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.HistoryTermType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.TimePeriod;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceAuthorizationBuilder;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.MeterType;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;
//import com.alliander.osgp.domain.core.valueobjects.TimePeriod;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.Oslp.PowerUsageData;
import com.alliander.osgp.oslp.Oslp.Status;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.oslp.OslpUtils;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Configurable
@DomainSteps
public class GetPowerUsageHistorySteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPowerUsageHistorySteps.class);

    private static final String DATETIME_FORMAT = "yyyyMMddHHmmss";

    private static final String ORGANISATION_ID = "ORGANISATION-01";
    private static final String ORGANISATION_PREFIX = "ORG";
    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";

    // TODO - Add as parameters to tests
    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // WS Adapter fields
    private DeviceMonitoringEndpoint deviceMonitoringEndpoint;

    private GetPowerUsageHistoryRequest request;
    private GetPowerUsageHistoryAsyncResponse getPowerUsageHistoryAsyncResponse;
    private GetPowerUsageHistoryAsyncRequest getPowerUsageHistoryAsyncRequest;
    private GetPowerUsageHistoryResponse response;

    @Autowired
    @Qualifier(value = "wsPublicLightingDeviceMonitoringService")
    private DeviceMonitoringService deviceMonitoringService;

    @Autowired
    @Qualifier(value = "wsPublicLightingIncomingResponsesMessageFinder")
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

    private OslpEnvelope oslpRequest;
    private OslpEnvelope oslpResponse;
    private OslpChannelHandlerClient oslpChannelHandler;
    @Autowired
    private Channel channelMock;

    // Test fields
    private Throwable throwable;

    // === GIVEN ===

    @DomainStep("a get power usage history request for device (.*) from (.*) until (.*)")
    public void givenARequest(final String device, final String fromDate, final String untilDate) throws Exception {
        LOGGER.info("GIVEN: a get power usage history request for device {}. from {} until {}", device, fromDate,
                untilDate);

        this.setUp();

        this.request = new GetPowerUsageHistoryRequest();
        this.request.setDeviceIdentification(device);

        final TimePeriod timePeriod = new TimePeriod();
        try {
            timePeriod.setStartTime(DateUtils.convertToXMLGregorianCalendar(fromDate, DATETIME_FORMAT));
            timePeriod.setEndTime(DateUtils.convertToXMLGregorianCalendar(untilDate, DATETIME_FORMAT));
        } catch (DatatypeConfigurationException | ParseException e) {
            LOGGER.error("Invalid date", e);
            throw new Exception("Invalid date");
        }

        // TODO: params for page and historyTerm
        this.request.setHistoryTermType(HistoryTermType.SHORT);
        this.request.setTimePeriod(timePeriod);
    }

    @DomainStep("the get power usage history request refers to a device (.*) with status (.*)")
    public void givenADevice(final String device, final String status) throws Exception {
        LOGGER.info("GIVEN: the get power usage history request refers to a device {} with status {}.", device, status);

        switch (status.toUpperCase()) {
        case "ACTIVE":
            this.createDevice(device, true);
            when(this.deviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.device);
            when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);
            when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.oslpDevice);
            when(this.deviceRepositoryMock.save(this.device)).thenReturn(this.device);
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

    @DomainStep("the get power usage history request refers to an organisation that is authorised")
    public void givenAnAuthorisedOrganisation() {
        LOGGER.info("GIVEN: the get power usage history request refers to an organisation that is authorised.");

        this.organisation = new Organisation(ORGANISATION_ID, ORGANISATION_ID, ORGANISATION_PREFIX,
                PlatformFunctionGroup.USER);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID)).thenReturn(
                this.organisation);

        final List<DeviceAuthorization> authorizations = new ArrayList<>();
        authorizations.add(new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.organisation)
                .withFunctionGroup(DeviceFunctionGroup.MONITORING).build());
        when(this.deviceAuthorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device))
                .thenReturn(authorizations);
    }

    @DomainStep("the get power usage history oslp message from the device contains (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*) and (.*)")
    public void givenAnOslpResponse(final String recordTime, final String index, final String meterType,
            final String totalConsumedEnergy, final String actualConsumedPower, final String psldDataTotalLightHours,
            final String actualCurrent1, final String actualCurrent2, final String actualCurrent3,
            final String actualPower1, final String actualPower2, final String actualPower3,
            final String averagePowerFactor1, final String averagePowerFactor2, final String averagePowerFactor3,
            final String relayData1Index, final String relayData1LightingMinutes, final String relayData2Index,
            final String relayData2LightingMinutes) {
        LOGGER.info(
                "GIVEN: the get actual power usage history oslp message from the device contains {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {} and {}.",
                new Object[] { recordTime, meterType, totalConsumedEnergy, actualConsumedPower,
                        psldDataTotalLightHours, actualCurrent1, actualCurrent2, actualCurrent3, actualPower1,
                        actualPower2, actualPower3, averagePowerFactor1, averagePowerFactor2, averagePowerFactor3,
                        relayData1Index, relayData1LightingMinutes, relayData2Index, relayData2LightingMinutes });

        MeterType puhMeterType = null;
        if (StringUtils.isNotBlank(meterType)) {
            puhMeterType = MeterType.valueOf(meterType);
        }

        long puhTotalConsumedEnergy = 0;
        if (StringUtils.isNotBlank(totalConsumedEnergy)) {
            puhTotalConsumedEnergy = Integer.valueOf(totalConsumedEnergy);
        }

        int puhActualConsumedPower = 0;
        if (StringUtils.isNotBlank(totalConsumedEnergy)) {
            puhActualConsumedPower = Integer.valueOf(actualConsumedPower);
        }

        int puhActualCurrent1 = 0;
        if (StringUtils.isNotBlank(actualCurrent1)) {
            puhActualCurrent1 = Integer.valueOf(actualCurrent1);
        }

        int puhActualCurrent2 = 0;
        if (StringUtils.isNotBlank(actualCurrent2)) {
            puhActualCurrent2 = Integer.valueOf(actualCurrent2);
        }

        int puhActualCurrent3 = 0;
        if (StringUtils.isNotBlank(actualCurrent3)) {
            puhActualCurrent3 = Integer.valueOf(actualCurrent3);
        }

        int puhActualPower1 = 0;
        if (StringUtils.isNotBlank(actualPower1)) {
            puhActualPower1 = Integer.valueOf(actualPower1);
        }

        int puhActualPower2 = 0;
        if (StringUtils.isNotBlank(actualPower2)) {
            puhActualPower2 = Integer.valueOf(actualPower2);
        }

        int puhActualPower3 = 0;
        if (StringUtils.isNotBlank(actualPower3)) {
            puhActualPower3 = Integer.valueOf(actualPower3);
        }

        int puhAveragePowerFactor1 = 0;
        if (StringUtils.isNotBlank(averagePowerFactor1)) {
            puhAveragePowerFactor1 = Integer.valueOf(averagePowerFactor1);
        }

        int puhAveragePowerFactor2 = 0;
        if (StringUtils.isNotBlank(averagePowerFactor2)) {
            puhAveragePowerFactor2 = Integer.valueOf(averagePowerFactor2);
        }

        int puhAveragePowerFactor3 = 0;
        if (StringUtils.isNotBlank(averagePowerFactor3)) {
            puhAveragePowerFactor3 = Integer.valueOf(averagePowerFactor3);
        }

        if (StringUtils.isNotBlank(psldDataTotalLightHours)) {
            Integer.valueOf(psldDataTotalLightHours);
        }

        int relayData1IndexInt = 0;
        if (StringUtils.isNotBlank(relayData1Index)) {
            relayData1IndexInt = Integer.valueOf(relayData1Index);
        }

        int relayData1LightingMinutesInt = 0;
        if (StringUtils.isNotBlank(relayData1LightingMinutes)) {
            relayData1LightingMinutesInt = Integer.valueOf(relayData1LightingMinutes);
        }

        final int relayData2IndexInt = 0;
        if (StringUtils.isNotBlank(relayData2Index)) {
            relayData1IndexInt = Integer.valueOf(relayData2Index);
        }

        int relayData2LightingMinutesInt = 0;
        if (StringUtils.isNotBlank(relayData2LightingMinutes)) {
            relayData2LightingMinutesInt = Integer.valueOf(relayData2LightingMinutes);
        }

        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.GetPowerUsageHistoryResponse oslpResponse = com.alliander.osgp.oslp.Oslp.GetPowerUsageHistoryResponse
                .newBuilder()
                .addPowerUsageData(
                        PowerUsageData
                                .newBuilder()
                                .setMeterType(
                                        puhMeterType == null ? null : com.alliander.osgp.oslp.Oslp.MeterType
                                                .valueOf(puhMeterType.name()))
                                .setRecordTime(recordTime)
                                .setActualConsumedPower(puhActualConsumedPower)
                                .setTotalConsumedEnergy(puhTotalConsumedEnergy)
                                .setSsldData(
                                        com.alliander.osgp.oslp.Oslp.SsldData
                                                .newBuilder()
                                                .setActualCurrent1(puhActualCurrent1)
                                                .setActualCurrent2(puhActualCurrent2)
                                                .setActualCurrent3(puhActualCurrent3)
                                                .setActualPower1(puhActualPower1)
                                                .setActualPower2(puhActualPower2)
                                                .setActualPower3(puhActualPower3)
                                                .setAveragePowerFactor1(puhAveragePowerFactor1)
                                                .setAveragePowerFactor2(puhAveragePowerFactor2)
                                                .setAveragePowerFactor3(puhAveragePowerFactor3)
                                                .addRelayData(
                                                        com.alliander.osgp.oslp.Oslp.RelayData
                                                                .newBuilder()
                                                                .setIndex(
                                                                        OslpUtils
                                                                                .integerToByteString(relayData1IndexInt))
                                                                .setTotalLightingMinutes(relayData1LightingMinutesInt)
                                                                .build())
                                                .addRelayData(
                                                        com.alliander.osgp.oslp.Oslp.RelayData
                                                                .newBuilder()
                                                                .setIndex(
                                                                        OslpUtils
                                                                                .integerToByteString(relayData2IndexInt))
                                                                .setTotalLightingMinutes(relayData2LightingMinutesInt)
                                                                .build())).build())

                .setStatus(Status.OK).build();

        this.oslpResponse = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setGetPowerUsageHistoryResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpResponse,
                this.channelMock, this.device.getNetworkAddress());
        this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
        this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);
    }

    // === WHEN ===

    @DomainStep("the get power usage history request is received")
    public void whenTheRequestIsReceived() {
        LOGGER.info("WHEN: the get actual power usage request is received.");
        try {
            this.getPowerUsageHistoryAsyncResponse = this.deviceMonitoringEndpoint.getPowerUsageHistory(
                    ORGANISATION_ID, this.request);

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("the get power usage history request should return an async response with a correlationId and deviceId (.*)")
    public boolean thenGetPowerUsageHistoryShouldReturnAsyncResponse(final String deviceId) {
        LOGGER.info(
                "THEN: \"the get power usage history request should return an async response with a correlationId and deviceId {}\".",
                deviceId);

        try {
            Assert.assertNotNull("Get Power Usage History Async Response should not be null",
                    this.getPowerUsageHistoryAsyncResponse);
            Assert.assertNotNull("Async Response should not be null",
                    this.getPowerUsageHistoryAsyncResponse.getAsyncResponse());
            Assert.assertNotNull("CorrelationId should not be null", this.getPowerUsageHistoryAsyncResponse
                    .getAsyncResponse().getCorrelationUid());
            Assert.assertNotNull("DeviceId should not be null", this.getPowerUsageHistoryAsyncResponse
                    .getAsyncResponse().getDeviceId());
            Assert.assertNull("Throwable should be null", this.throwable);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }

        return true;
    }

    @DomainStep("a get power usage history oslp message is sent to the device (.*) should be (.*)")
    public boolean thenAGetPowerUsageHistoryOslpMessageShouldBeSent(final String device, final Boolean isMessageSent) {
        LOGGER.info("THEN: a power usage history version oslp message is sent to device should be {}.", isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, timeout(1000).times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpRequest = argument.getValue();

                Assert.assertTrue("Message should contain get power usage history request.", this.oslpRequest
                        .getPayloadMessage().hasGetPowerUsageHistoryRequest());

            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("an ovl get power usage history result message with result (.*) and description (.*) should be sent to the ovl out queue")
    public boolean thenAnOvlGetPowerUsageHistoryResultMessageShouldBeSentToTheOvlOutQueue(final String result,
            final String description) {
        LOGGER.info(
                "THEN: \"an ovl get power usage history result message with result [{}] and description [{}] should be sent to the ovl out queue\".",
                result, description);

        try {
            final ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);

            verify(this.webServiceResponseMessageSenderMock, timeout(1000).times(1)).send(argument.capture(),
                    any(Long.class));

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

    @DomainStep("a get get power usage history response request with correlationId (.*) and deviceId (.*)")
    public void givenAGetPowerUsagehistoryResponseRequest(final String correlationId, final String deviceId) {
        LOGGER.info("GIVEN: \"a get power usage history response request with correlationId {} and deviceId {}\".",
                correlationId, deviceId);

        this.setUp();

        this.getPowerUsageHistoryAsyncRequest = new GetPowerUsageHistoryAsyncRequest();

        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationId);
        asyncRequest.setDeviceId(deviceId);
        this.getPowerUsageHistoryAsyncRequest.setAsyncRequest(asyncRequest);
    }

    @DomainStep("a get power usage history response message with correlationId (.*), deviceId (.*), qresult (.*), qdescription (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*) is found in the queue (.*)")
    public void givenAGetPowerUsageHistoryResponseMessageIsFoundInQueue(final String correlationId,
            final String deviceId, final String qresult, final String qdescription, final String fromDate,
            final String untilDate, final String recordTime, final String meterType, final String totalConsumedEnergy,
            final String actualConsumedPower, final String psldDataTotalLightHours, final String actualCurrent1,
            final String actualCurrent2, final String actualCurrent3, final String actualPower1,
            final String actualPower2, final String actualPower3, final String averagePowerFactor1,
            final String averagePowerFactor2, final String averagePowerFactor3, final String relayData1Index,
            final String relayData1LightingMinutes, final String relayData2Index,
            final String relayData2LightingMinutes, final Boolean isFound) throws ParseException {
        LOGGER.info(
                "GIVEN: \"a get power usage history response message with correlationId {}, deviceId {}, qresult {} and qdescription {} is found {}\".",
                correlationId, deviceId, qresult, qdescription, isFound);

        if (isFound) {
            final ObjectMessage messageMock = mock(ObjectMessage.class);

            try {
                when(messageMock.getJMSCorrelationID()).thenReturn(correlationId);
                when(messageMock.getStringProperty("OrganisationIdentification")).thenReturn(ORGANISATION_ID);
                when(messageMock.getStringProperty("DeviceIdentification")).thenReturn(deviceId);

                final MeterType metertype = StringUtils.isBlank(meterType) ? null : Enum.valueOf(MeterType.class,
                        meterType);

                DateTime dateTime = null;
                if (!recordTime.equals("")) {
                    final Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(recordTime);
                    dateTime = new DateTime(date);
                }

                final com.alliander.osgp.domain.core.valueobjects.PowerUsageData powerUsageData = new com.alliander.osgp.domain.core.valueobjects.PowerUsageData(
                        dateTime, metertype, Long.parseLong(totalConsumedEnergy), Long.parseLong(actualConsumedPower));

                final List<com.alliander.osgp.domain.core.valueobjects.RelayData> list = new ArrayList<>();
                list.add(new com.alliander.osgp.domain.core.valueobjects.RelayData(Integer.valueOf(relayData1Index),
                        Integer.valueOf(relayData1LightingMinutes)));
                list.add(new com.alliander.osgp.domain.core.valueobjects.RelayData(Integer.valueOf(relayData2Index),
                        Integer.valueOf(relayData2LightingMinutes)));

                final com.alliander.osgp.domain.core.valueobjects.SsldData ssldData = new com.alliander.osgp.domain.core.valueobjects.SsldData(
                        Integer.valueOf(actualCurrent1), Integer.valueOf(actualCurrent2),
                        Integer.valueOf(actualCurrent3), Integer.valueOf(actualPower1), Integer.valueOf(actualPower2),
                        Integer.valueOf(actualPower3), Integer.valueOf(averagePowerFactor1),
                        Integer.valueOf(averagePowerFactor2), Integer.valueOf(averagePowerFactor3), list);

                powerUsageData.setSsldData(ssldData);

                final List<com.alliander.osgp.domain.core.valueobjects.PowerUsageData> powerUsageDatas = new ArrayList<com.alliander.osgp.domain.core.valueobjects.PowerUsageData>();
                powerUsageDatas.add(powerUsageData);

                final com.alliander.osgp.domain.core.valueobjects.PowerUsageHistoryResponse powerUsageHistoryResponse = new com.alliander.osgp.domain.core.valueobjects.PowerUsageHistoryResponse(
                        powerUsageDatas);

                final ResponseMessage message = new ResponseMessage(correlationId, ORGANISATION_ID, deviceId,
                        ResponseMessageResultType.valueOf(qresult), null, powerUsageHistoryResponse);

                when(messageMock.getObject()).thenReturn(message);

            } catch (final JMSException e) {
                e.printStackTrace();
            }

            when(this.publicLightingResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(messageMock);
        } else {
            when(this.publicLightingResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(null);
        }
    }

    @DomainStep("the get get power usage history response request is received")
    public void whenTheGetPowerUsageHistoryResponseRequestIsReceived() {
        LOGGER.info("WHEN: \"the power usage history response request is received\".");

        try {
            this.response = this.deviceMonitoringEndpoint.getGetPowerUsageHistoryResponse(ORGANISATION_ID,
                    this.getPowerUsageHistoryAsyncRequest);

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("the power usage history response is as expected with result (.*) and description (.*)")
    public void thenThePowerUsageHistoryResponseIsAsExpectedWithResultAndDescription(final String result,
            final String description) {
        LOGGER.info("THEN: \"the power usage history response is as expected with result {} and description {}\".",
                result, description);

        try {
            if (result.equals("OK")) {
                Assert.assertEquals(result, this.response.getResult().name());
                Assert.assertNotNull(this.response);
                Assert.assertNull(this.throwable);
            } else if (result.equals("NOT_FOUND")) {
                Assert.assertEquals(result, this.response.getResult().name());
                Assert.assertNotNull(this.response);
                Assert.assertNull(this.throwable);
            } else {
                Assert.assertNotNull(this.response);
                Assert.assertNull(this.throwable);
            }
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    // === Private methods ===

    private void setUp() {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.organisationRepositoryMock,
                this.deviceAuthorizationRepositoryMock, this.deviceLogItemRepositoryMock, this.channelMock,
                this.webServiceResponseMessageSenderMock, this.oslpDeviceRepositoryMock });

        this.deviceMonitoringEndpoint = new DeviceMonitoringEndpoint(this.deviceMonitoringService,
                new DeviceMonitoringMapper());
        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        this.request = null;
        this.response = null;
        this.getPowerUsageHistoryAsyncRequest = null;
        this.getPowerUsageHistoryAsyncResponse = null;
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
