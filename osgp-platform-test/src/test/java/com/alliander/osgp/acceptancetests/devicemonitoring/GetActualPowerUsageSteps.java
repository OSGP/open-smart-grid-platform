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
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetActualPowerUsageAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetActualPowerUsageAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetActualPowerUsageRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetActualPowerUsageResponse;
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
import com.alliander.osgp.domain.core.valueobjects.MeterType;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.PowerUsageData;
import com.alliander.osgp.domain.core.valueobjects.PsldData;
import com.alliander.osgp.domain.core.valueobjects.RelayData;
import com.alliander.osgp.domain.core.valueobjects.SsldData;
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
public class GetActualPowerUsageSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetActualPowerUsageSteps.class);

    private static final String ORGANISATION_ID = "ORGANISATION-01";
    private static final String ORGANISATION_PREFIX = "ORG";
    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";

    // TODO - Add as parameters to tests
    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // WS Adapter fields
    private DeviceMonitoringEndpoint deviceMonitoringEndpoint;

    private GetActualPowerUsageRequest request;
    private GetActualPowerUsageAsyncResponse getActualPowerUsageAsyncResponse;
    private GetActualPowerUsageAsyncRequest getActualPowerUsageAsyncRequest;
    private GetActualPowerUsageResponse response;

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

    @DomainStep("a get actual power usage request for device (.*)")
    public void givenARequest(final String device) {
        LOGGER.info("GIVEN: a get actual power usage request for device {}.", device);

        this.setUp();

        this.request = new GetActualPowerUsageRequest();
        this.request.setDeviceIdentification(device);
    }

    @DomainStep("the get actual power usage request refers to a device (.*) with status (.*)")
    public void givenADevice(final String device, final String status) throws Exception {
        LOGGER.info("GIVEN: the get actual power usage request refers to a device {} with status {}.", device, status);

        switch (status.toUpperCase()) {
        case "ACTIVE":
            this.createDevice(device, true);
            when(this.deviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.device);
            when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);
            when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.oslpDevice);
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

    @DomainStep("the get actual power usage request refers to an organisation that is authorised")
    public void givenAnAuthorisedOrganisation() {
        LOGGER.info("GIVEN: the get actual power usage request refers to an organisation that is authorised.");

        this.organisation = new Organisation(ORGANISATION_ID, ORGANISATION_ID, ORGANISATION_PREFIX, PlatformFunctionGroup.USER);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID)).thenReturn(this.organisation);

        final List<DeviceAuthorization> authorizations = new ArrayList<>();
        authorizations.add(new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.organisation)
                .withFunctionGroup(DeviceFunctionGroup.MONITORING).build());
        when(this.deviceAuthorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device)).thenReturn(authorizations);
    }

    @DomainStep("the get actual power usage oslp message from the device contains (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*) and (.*)")
    public void givenAnOslpResponse(final String recordTime, final String meterType, final String totalConsumedEnergy, final String actualConsumedPower,
            final String psldDataTotalLightHours, final String actualCurrent1, final String actualCurrent2, final String actualCurrent3,
            final String actualPower1, final String actualPower2, final String actualPower3, final String averagePowerFactor1,
            final String averagePowerFactor2, final String averagePowerFactor3, final String relayData1Index, final String relayData1LightingMinutes,
            final String relayData2Index, final String relayData2LightingMinutes) {
        LOGGER.info(
                "GIVEN: the get actual power usage oslp message from the device contains {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {} and {}.",
                new Object[] { recordTime, meterType, totalConsumedEnergy, actualConsumedPower, psldDataTotalLightHours, actualCurrent1, actualCurrent2,
                        actualCurrent3, actualPower1, actualPower2, actualPower3, averagePowerFactor1, averagePowerFactor2, averagePowerFactor3,
                        relayData1Index, relayData1LightingMinutes, relayData2Index, relayData2LightingMinutes });

        MeterType apuMeterType = null;
        if (StringUtils.isNotBlank(meterType)) {
            apuMeterType = MeterType.valueOf(meterType);
        }

        long apuTotalConsumedEnergy = 0;
        if (StringUtils.isNotBlank(totalConsumedEnergy)) {
            apuTotalConsumedEnergy = Integer.valueOf(totalConsumedEnergy);
        }

        int apuActualConsumedPower = 0;
        if (StringUtils.isNotBlank(totalConsumedEnergy)) {
            apuActualConsumedPower = Integer.valueOf(actualConsumedPower);
        }

        int apuActualCurrent1 = 0;
        if (StringUtils.isNotBlank(actualCurrent1)) {
            apuActualCurrent1 = Integer.valueOf(actualCurrent1);
        }

        int apuActualCurrent2 = 0;
        if (StringUtils.isNotBlank(actualCurrent2)) {
            apuActualCurrent2 = Integer.valueOf(actualCurrent2);
        }

        int apuActualCurrent3 = 0;
        if (StringUtils.isNotBlank(actualCurrent3)) {
            apuActualCurrent3 = Integer.valueOf(actualCurrent3);
        }

        int apuActualPower1 = 0;
        if (StringUtils.isNotBlank(actualPower1)) {
            apuActualPower1 = Integer.valueOf(actualPower1);
        }

        int apuActualPower2 = 0;
        if (StringUtils.isNotBlank(actualPower2)) {
            apuActualPower2 = Integer.valueOf(actualPower2);
        }

        int apuActualPower3 = 0;
        if (StringUtils.isNotBlank(actualPower3)) {
            apuActualPower3 = Integer.valueOf(actualPower3);
        }

        int apuAveragePowerFactor1 = 0;
        if (StringUtils.isNotBlank(averagePowerFactor1)) {
            apuAveragePowerFactor1 = Integer.valueOf(averagePowerFactor1);
        }

        int apuAveragePowerFactor2 = 0;
        if (StringUtils.isNotBlank(averagePowerFactor2)) {
            apuAveragePowerFactor2 = Integer.valueOf(averagePowerFactor2);
        }

        int apuAveragePowerFactor3 = 0;
        if (StringUtils.isNotBlank(averagePowerFactor3)) {
            apuAveragePowerFactor3 = Integer.valueOf(averagePowerFactor3);
        }

        int apuPsldDataTotalLightHours = 0;
        if (StringUtils.isNotBlank(psldDataTotalLightHours)) {
            apuPsldDataTotalLightHours = Integer.valueOf(psldDataTotalLightHours);
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
        final com.alliander.osgp.oslp.Oslp.GetActualPowerUsageResponse oslpResponse = com.alliander.osgp.oslp.Oslp.GetActualPowerUsageResponse
                .newBuilder()
                .setPowerUsageData(
                        com.alliander.osgp.oslp.Oslp.PowerUsageData
                                .newBuilder()
                                .setSsldData(
                                        com.alliander.osgp.oslp.Oslp.SsldData
                                                .newBuilder()
                                                .setActualCurrent1(apuActualCurrent1)
                                                .setActualCurrent2(apuActualCurrent2)
                                                .setActualCurrent3(apuActualCurrent3)
                                                .setActualPower1(apuActualPower1)
                                                .setActualPower2(apuActualPower2)
                                                .setActualPower3(apuActualPower3)
                                                .setAveragePowerFactor1(apuAveragePowerFactor1)
                                                .setAveragePowerFactor2(apuAveragePowerFactor2)
                                                .setAveragePowerFactor3(apuAveragePowerFactor3)
                                                .addRelayData(
                                                        com.alliander.osgp.oslp.Oslp.RelayData.newBuilder()
                                                                .setIndex(OslpUtils.integerToByteString(relayData1IndexInt))
                                                                .setTotalLightingMinutes(relayData1LightingMinutesInt).build())
                                                .addRelayData(
                                                        com.alliander.osgp.oslp.Oslp.RelayData.newBuilder()
                                                                .setIndex(OslpUtils.integerToByteString(relayData2IndexInt))
                                                                .setTotalLightingMinutes(relayData2LightingMinutesInt).build()))
                                .setPsldData(com.alliander.osgp.oslp.Oslp.PsldData.newBuilder().setTotalLightingHours(apuPsldDataTotalLightHours).build())
                                .setRecordTime(recordTime).setTotalConsumedEnergy(apuTotalConsumedEnergy).setActualConsumedPower(apuActualConsumedPower)
                                .setMeterType(apuMeterType == null ? null : com.alliander.osgp.oslp.Oslp.MeterType.valueOf(apuMeterType.name())).build())
                .setStatus(Status.OK).build();

        this.oslpResponse = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setGetActualPowerUsageResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpResponse, this.channelMock, this.device.getNetworkAddress());
        this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
        this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);
    }

    @DomainStep("a get get actual power usage response request with correlationId (.*) and deviceId (.*)")
    public void givenAGetActualPowerUsageResponseRequest(final String correlationId, final String deviceId) {
        LOGGER.info("GIVEN: \"a get actual power usage response request with correlationId {} and deviceId {}\".", correlationId, deviceId);

        this.setUp();

        this.getActualPowerUsageAsyncRequest = new GetActualPowerUsageAsyncRequest();

        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationId);
        asyncRequest.setDeviceId(deviceId);
        this.getActualPowerUsageAsyncRequest.setAsyncRequest(asyncRequest);
    }

    @DomainStep("a get actual power usage response message with correlationId (.*), deviceId (.*), qresult (.*), qdescription (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*) is found in the queue (.*)")
    public void givenAGetActualPowerUsageResponseMessageIsFoundInQueue(final String correlationId, final String deviceId, final String qresult,
            final String qdescription, final String recordTime, final String meterType, final String totalConsumedEnergy, final String actualConsumedPower,
            final String psldDataTotalLightHours, final String actualCurrent1, final String actualCurrent2, final String actualCurrent3,
            final String actualPower1, final String actualPower2, final String actualPower3, final String averagePowerFactor1,
            final String averagePowerFactor2, final String averagePowerFactor3, final String relayData1Index, final String relayData1LightingMinutes,
            final String relayData2Index, final String relayData2LightingMinutes, final Boolean isFound) {
        LOGGER.info("GIVEN: \"a get actual power usage response message with correlationId {}, deviceId {}, qresult {} and qdescription {} is found {}\".",
                correlationId, deviceId, qresult, qdescription, isFound);

        if (isFound) {
            final ObjectMessage messageMock = mock(ObjectMessage.class);

            try {
                when(messageMock.getJMSCorrelationID()).thenReturn(correlationId);
                when(messageMock.getStringProperty("OrganisationIdentification")).thenReturn(ORGANISATION_ID);
                when(messageMock.getStringProperty("DeviceIdentification")).thenReturn(deviceId);

                final ResponseMessageResultType result = ResponseMessageResultType.valueOf(qresult);
                Object dataObject = null;
                OsgpException exception=null;

                if (result.equals(ResponseMessageResultType.NOT_OK)) {
                    dataObject = new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.UNKNOWN, new ValidationException());
                    exception=(OsgpException) dataObject;
                } else {

                    final MeterType metertype = StringUtils.isBlank(meterType) ? null : Enum.valueOf(MeterType.class, meterType);

                    DateTime dateTime = null;
                    if (!recordTime.equals("")) {
                        final Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(recordTime);
                        dateTime = new DateTime(date);
                    }

                    PowerUsageData powerUsageData = null;
                    if (!totalConsumedEnergy.equals("") && !actualConsumedPower.equals("")) {
                        powerUsageData = new PowerUsageData(dateTime, metertype, Long.parseLong(totalConsumedEnergy), Long.parseLong(actualConsumedPower));

                        final PsldData psldData = new PsldData(Integer.valueOf(psldDataTotalLightHours));

                        final List<RelayData> relayDataList = new ArrayList<RelayData>();
                        // Create RelayData instances for relay indexes and minutes
                        // lighting time.
                        relayDataList.add(new RelayData(Integer.parseInt(relayData1Index), Integer.parseInt(relayData1LightingMinutes)));
                        relayDataList.add(new RelayData(Integer.parseInt(relayData2Index), Integer.parseInt(relayData2LightingMinutes)));

                        // Construct SsldData using the list of RelayData.
                        final SsldData ssldData = new SsldData(Integer.valueOf(actualCurrent1), Integer.valueOf(actualCurrent2),
                                Integer.valueOf(actualCurrent3), Integer.valueOf(actualPower1), Integer.valueOf(actualPower2), Integer.valueOf(actualPower3),
                                Integer.valueOf(averagePowerFactor1), Integer.valueOf(averagePowerFactor2), Integer.valueOf(averagePowerFactor3), relayDataList);

                        powerUsageData.setPsldData(psldData);
                        powerUsageData.setSsldData(ssldData);

                        dataObject = powerUsageData;
                    }
                }
                final ResponseMessage message = new ResponseMessage(correlationId, ORGANISATION_ID, deviceId, result, exception, dataObject);

                when(messageMock.getObject()).thenReturn(message);

            } catch (final JMSException e) {
                e.printStackTrace();
            } catch (final ParseException e) {
                e.printStackTrace();
            }

            when(this.publicLightingResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(messageMock);
        } else {
            when(this.publicLightingResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(null);
        }
    }

    // === WHEN ===

    @DomainStep("the get actual power usage request is received")
    public void whenTheRequestIsReceived() {
        LOGGER.info("WHEN: the get actual power usage request is received.");

        try {

            this.getActualPowerUsageAsyncResponse = this.deviceMonitoringEndpoint.getActualPowerUsage(ORGANISATION_ID, this.request);

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("the get get actual power usage response request is received")
    public void whenTheGetActualPowerUsageResponseRequestIsReceived() {
        LOGGER.info("WHEN: \"the actual power usage response request is received\".");

        try {
            this.response = this.deviceMonitoringEndpoint.getGetActualPowerUsageResponse(ORGANISATION_ID, this.getActualPowerUsageAsyncRequest);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("the get actual power usage request should return an async response with a correlationId and deviceId (.*)")
    public boolean thenGetActualPowerUsageShouldReturnAsyncResponse(final String deviceId) {
        LOGGER.info("THEN: \"the get actual power usage request should return an async response with a correlationId and deviceId {}\".", deviceId);

        try {
            Assert.assertNotNull("Get Actual Power Async Response should not be null", this.getActualPowerUsageAsyncResponse);
            Assert.assertNotNull("Async Response should not be null", this.getActualPowerUsageAsyncResponse.getAsyncResponse());
            Assert.assertNotNull("CorrelationId should not be null", this.getActualPowerUsageAsyncResponse.getAsyncResponse().getCorrelationUid());
            Assert.assertNotNull("DeviceId should not be null", this.getActualPowerUsageAsyncResponse.getAsyncResponse().getDeviceId());
            Assert.assertNull("Throwable should be null", this.throwable);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("a get actual power usage oslp message is sent to the device (.*) should be (.*)")
    public boolean thenAGetActualPowerUsageOslpMessageShouldBeSent(final String device, final Boolean isMessageSent) {
        LOGGER.info("THEN: a actual power usage version oslp message is sent to device should be {}.", isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, timeout(1000).times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpRequest = argument.getValue();

                Assert.assertTrue("Message should contain get actual power usage request.", this.oslpRequest.getPayloadMessage()
                        .hasGetActualPowerUsageRequest());
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("an ovl get actual power usage result message with result (.*) and description (.*) should be sent to the ovl out queue")
    public boolean thenAnOvlGetActualPowerUsageResultMessageShouldBeSentToTheOvlOutQueue(final String result, final String description) {
        LOGGER.info("THEN: \"an ovl get actual power usage result message with result [{}] and description [{}] should be sent to the ovl out queue\".",
                result, description);

        try {
            final ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);

            verify(this.webServiceResponseMessageSenderMock, timeout(1000).times(1)).send(argument.capture());

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

    @DomainStep("the get get actual power usage response request should return a get actual power usage response with result (.*) and description (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*), (.*)")
    public boolean thenTheGetActualPowerUsageResponseRequestShouldReturnAGetActualPowerUsageResponse(final String result, final String description,
            final String recordTime, final String meterType, final String totalConsumedEnergy, final String actualConsumedPower,
            final String psldDataTotalLightHours, final String actualCurrent1, final String actualCurrent2, final String actualCurrent3,
            final String actualPower1, final String actualPower2, final String actualPower3, final String averagePowerFactor1,
            final String averagePowerFactor2, final String averagePowerFactor3) {
        LOGGER.info(
                "THEN: \"the get get actual power usage response request should return a get actual power usage response with result {}, description {} and recordTime {}, meterType {}, totalConsumedEnergy {}"
                        + "	actualConsumedPower {}, psldDataTotalLightHours {}, actualCurrent1 {}, actualCurrent2 {}, actualCurrent3 {} ,"
                        + "	actualPower1 {}, actualPower2 {}, actualPower3 {}, 	averagePowerFactor1 {}, averagePowerFactor2 {}, averagePowerFactor3 {} ",
                result, description, recordTime, meterType, totalConsumedEnergy, actualConsumedPower, psldDataTotalLightHours, actualCurrent1, actualCurrent2,
                actualCurrent3, actualPower1, actualPower2, actualPower3, averagePowerFactor1, averagePowerFactor2, averagePowerFactor3);

        try {
            if ("NOT_OK".equals(result)) {
                Assert.assertNull("Set Schedule Response should be null", this.response);
                Assert.assertNotNull("Throwable should not be null", this.throwable);
                Assert.assertTrue("Throwable should contain a validation exception", this.throwable.getCause() instanceof ValidationException);
            } else {

                Assert.assertNotNull("Response should not be null", this.response);

                // check the result
                String expected = result.equals("NULL") ? null : result;
                String actual = this.response.getResult().toString();

                Assert.assertTrue("Invalid result, found: " + actual + " , expected: " + expected, actual.equals(expected));

                if (this.response.getResult().equals("OK")) {
                    // Check if the PowerUsageData is not null.
                    Assert.assertNotNull("PowerUsageData is null", this.response.getPowerUsageData());

                    // check if recordTime is not null
                    expected = recordTime.equals("NULL") ? null : recordTime;
                    actual = this.response.getPowerUsageData().getRecordTime() == null ? null : this.response.getPowerUsageData().getRecordTime().toString();

                    Assert.assertTrue("Invalid recordTime, found: " + actual + " , expected: " + expected, actual.equals(expected));

                    // check if the meterType is not null
                    expected = meterType.equals("NULL") ? null : meterType;
                    actual = this.response.getPowerUsageData().getMeterType() == null ? null : this.response.getPowerUsageData().getMeterType().toString();

                    Assert.assertTrue("Invalid metertype, found: " + actual + " , expected: " + expected, actual.equals(expected));

                }
            }

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    // === Private methods ===

    private void setUp() {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.organisationRepositoryMock, this.deviceAuthorizationRepositoryMock,
                this.oslpLogItemRepositoryMock, this.channelMock, this.webServiceResponseMessageSenderMock, this.oslpDeviceRepositoryMock });

        this.deviceMonitoringEndpoint = new DeviceMonitoringEndpoint(this.deviceMonitoringService, new DeviceMonitoringMapper());
        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        this.request = null;
        this.response = null;
        this.getActualPowerUsageAsyncRequest = null;
        this.getActualPowerUsageAsyncResponse = null;
        this.throwable = null;
    }

    private void createDevice(final String deviceIdentification, final boolean activated) {
        LOGGER.info("Creating device [{}] with active [{}]", deviceIdentification, activated);

        this.device = new DeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withNetworkAddress(activated ? InetAddress.getLoopbackAddress() : null).withPublicKeyPresent(PUBLIC_KEY_PRESENT)
                .withProtocolInfo(ProtocolInfoTestUtils.getProtocolInfo(PROTOCOL, PROTOCOL_VERSION)).isActivated(activated).build();

        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(deviceIdentification).withDeviceUid(DEVICE_UID).build();
    }
}
