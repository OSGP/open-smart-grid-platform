package com.alliander.osgp.acceptancetests.adhocmanagement;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.xml.datatype.DatatypeConfigurationException;

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
import com.alliander.osgp.adapter.ws.publiclighting.application.mapping.AdHocManagementMapper;
import com.alliander.osgp.adapter.ws.publiclighting.application.services.AdHocManagementService;
import com.alliander.osgp.adapter.ws.publiclighting.endpoints.PublicLightingAdHocManagementEndpoint;
import com.alliander.osgp.adapter.ws.publiclighting.infra.jms.PublicLightingResponseMessageFinder;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.TransitionType;
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
import com.alliander.osgp.domain.core.repositories.OslpLogItemRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.Oslp.Status;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Configurable
@DomainSteps
public class SetTransitionSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetTransitionSteps.class);

    private static final String TIME_FORMAT = "HHmmss";
    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";
    private static final String ORGANISATION_ID = "ORGANISATION-01";
    private static final String ORGANISATION_PREFIX = "ORG";

    // TODO - Add as parameters to tests
    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // WS Adapter fields
    private PublicLightingAdHocManagementEndpoint adHocManagementEndpoint;

    private SetTransitionRequest request;
    private SetTransitionAsyncResponse setTransitionAsyncResponse;
    private SetTransitionAsyncRequest setTransitionAsyncRequest;
    private SetTransitionResponse response;

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

    @DomainStep("a set transition request for device (.*) with transitiontype (.*) and time (.*)")
    public void givenARequest(final String device, final String transitionType, final String time) throws DatatypeConfigurationException, ParseException {
        LOGGER.info("GIVEN: a set transition request for device {} with transitiontype {} and time {}.", new Object[] { device, transitionType, time });

        this.setUp();

        this.request = new SetTransitionRequest();
        if (StringUtils.isNotBlank(transitionType)) {
            this.request.setTransitionType(Enum.valueOf(TransitionType.class, transitionType));
        }
        if (StringUtils.isNotBlank(time)) {
            this.request.setTime(DateUtils.convertToXMLGregorianCalendar(time, TIME_FORMAT));
        }
        this.request.setDeviceIdentification(device);
    }

    @DomainStep("the set transition request refers to a device (.*) with status (.*)")
    public void givenADevice(final String device, final String status) throws Exception {
        LOGGER.info("GIVEN: the set transition request refers to a device {} with status {}.", device, status);

        switch (status.toUpperCase()) {
        case "ACTIVE":
            this.createDevice(device, true);
            this.initializeOslp();
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

    @DomainStep("the set transition request refers to an organisation that is authorised")
    public void givenAnAuthorisedOrganisation() {
        LOGGER.info("GIVEN: the set transition request refers to an organisation that is authorised.");

        this.organisation = new Organisation(ORGANISATION_ID, ORGANISATION_ID, ORGANISATION_PREFIX, PlatformFunctionGroup.USER);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID)).thenReturn(this.organisation);

        final List<DeviceAuthorization> authorizations = new ArrayList<>();
        authorizations.add(new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.organisation)
                .withFunctionGroup(DeviceFunctionGroup.AD_HOC).build());
        when(this.deviceAuthorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device)).thenReturn(authorizations);
    }

    // === WHEN ===
    @DomainStep("the set transition request is received")
    public void whenTheSetTransitionRequestIsReceived() {
        LOGGER.info("WHEN: the set transition request is received.");

        try {
            this.setTransitionAsyncResponse = this.adHocManagementEndpoint.setTransition(ORGANISATION_ID, this.request);

            // Add sleep to enable queue processing
            for (int i = 0; i < 1000; i++) {
                Thread.sleep(1);
            }

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("the set transition request should return an async response with a correlationId and deviceId (.*)")
    public boolean thenTheSetTransitionRequestShouldReturnASetTransitionResponseWithACorrelationID(final String deviceId) {
        LOGGER.info("THEN: \"the set transition request should return a set transition response with a correlationId and deviceId {}\".", deviceId);

        // TODO Add check on device id
        try {
            Assert.assertNotNull("Set Transition Async Response should not be null", this.setTransitionAsyncResponse);
            Assert.assertNotNull("Async Response should not be null", this.setTransitionAsyncResponse.getAsyncResponse());
            Assert.assertNotNull("CorrelationId should not be null", this.setTransitionAsyncResponse.getAsyncResponse().getCorrelationUid());
            Assert.assertNull("Throwable should be null", this.throwable);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("a set transition oslp message is sent to device (.*) should be (.*)")
    public boolean thenASetTransitionOslpMessageShouldBeSent(final String device, final Boolean isMessageSent) {
        LOGGER.info("THEN: \"a set transition oslp message is sent to device [{}] should be [{}]\".", device, isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpRequest = argument.getValue();

                Assert.assertTrue("Message should contain set transition request.", this.oslpRequest.getPayloadMessage().hasSetTransitionRequest());
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("an ovl set transition result message with result (.*) and description (.*) should be sent to the ovl out queue")
    public boolean thenAnOvlSetTransitionResultMessageShouldBeSentToTheOvlOutQueue(final String result, final String description) {
        LOGGER.info("THEN: \"an ovl set transition result message with result [{}] and description [{}] should be sent to the ovl out queue\".", result,
                description);

        try {
            final ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
            verify(this.webServiceResponseMessageSenderMock, times(1)).send(argument.capture());

            final String expected = result.equals("NULL") ? null : result;
            final String actual = argument.getValue().getResult().getValue();

            Assert.assertTrue("Invalid result, found: " + actual + " , expected: " + expected, actual.equals(expected));

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("a set transition oslp message is sent to the device should be (.*)")
    public boolean thenAnOslpMessageShouldBeSent(final Boolean isMessageSent) {
        LOGGER.info("THEN: a set transition oslp message is sent to the device should be {}.", isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpRequest = argument.getValue();

                Assert.assertTrue("Message should contain set transition request.", this.oslpRequest.getPayloadMessage().hasSetTransitionRequest());
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the set transition request should return result (.*)")
    public boolean thenTheRequestShouldReturn(final String result) {
        LOGGER.info("THEN: the set transition request should return {}.", result);

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

    @DomainStep("a get set transition response request with correlationId (.*) and deviceId (.*)")
    public void givenAGetSetTransitionResultRequestWithCorrelationId(final String correlationId, final String deviceId) {
        LOGGER.info("GIVEN: \"a get set transition response with correlationId {} and deviceId {}\".", correlationId, deviceId);

        this.setUp();

        this.setTransitionAsyncRequest = new SetTransitionAsyncRequest();

        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationId);
        asyncRequest.setDeviceId(deviceId);

        this.setTransitionAsyncRequest.setAsyncRequest(asyncRequest);
    }

    @DomainStep("a set transition response message with correlationId (.*), deviceId (.*), qresult (.*) and qdescription (.*) is found in the queue (.*)")
    public void givenASetTransitionResponseMessageIsFoundInTheQueue(final String correlationId, final String deviceId, final String qresult,
            final String qdescription, final Boolean isFound) {
        LOGGER.info("GIVEN: \"a set transition response message with correlationId {}, deviceId {}, qresult {} and qdescription {} is found {}\".",
                correlationId, deviceId, qresult, qdescription, isFound);

        if (isFound) {
            final ObjectMessage messageMock = mock(ObjectMessage.class);

            try {
                when(messageMock.getJMSCorrelationID()).thenReturn(correlationId);
                when(messageMock.getStringProperty("OrganisationIdentification")).thenReturn(ORGANISATION_ID);
                when(messageMock.getStringProperty("DeviceIdentification")).thenReturn(deviceId);
                final ResponseMessageResultType result = ResponseMessageResultType.valueOf(qresult);
                Object dataObject = null;
                if (result.equals(ResponseMessageResultType.NOT_OK)) {
                    dataObject = new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.UNKNOWN, new ValidationException());
                }
                final ResponseMessage message = new ResponseMessage(correlationId, ORGANISATION_ID, deviceId, result, qdescription, dataObject);
                when(messageMock.getObject()).thenReturn(message);
            } catch (final JMSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            when(this.publicLightingResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(messageMock);

        } else {
            when(this.publicLightingResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(null);
        }
    }

    @DomainStep("the get set transition response request is received")
    public void whenTheGetSetTransitionResultRequestIsReceived() {
        LOGGER.info("WHEN: \"the set transition request is received\".");

        try {

            this.response = this.adHocManagementEndpoint.getSetTransitionResponse(ORGANISATION_ID, this.setTransitionAsyncRequest);

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("the get set transition response request should return a set transition response with result (.*) and description (.*)")
    public boolean thenTheGetSetTransitionResultRequestShouldReturnAGetSetTransitionResultResponseWithResult(final String result, final String description) {
        LOGGER.info("THEN: \"the get set transition result request should return a get set transition response with result {} and description {}", result,
                description);

        try {
            if ("NOT_OK".equals(result)) {
                Assert.assertNull("Set Schedule Response should be null", this.response);
                Assert.assertNotNull("Throwable should not be null", this.throwable);
                Assert.assertTrue("Throwable should contain a validation exception", this.throwable.getCause() instanceof ValidationException);
            } else {

                Assert.assertNotNull("Response should not be null", this.response);

                final String expectedResult = result.equals("NULL") ? null : result;
                final String actualResult = this.response.getResult().toString();

                Assert.assertTrue("Invalid result, found: " + actualResult + " , expected: " + expectedResult, (actualResult == null && expectedResult == null)
                        || actualResult.equals(expectedResult));

                // TODO: check description
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
                this.oslpLogItemRepositoryMock, this.oslpDeviceRepositoryMock, this.webServiceResponseMessageSenderMock, this.channelMock });

        this.adHocManagementEndpoint = new PublicLightingAdHocManagementEndpoint(this.adHocManagementService, new AdHocManagementMapper());
        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        this.setTransitionAsyncRequest = null;
        this.setTransitionAsyncResponse = null;

        this.request = null;
        this.response = null;
        this.throwable = null;
    }

    private void createDevice(final String deviceIdentification, final boolean activated) {
        LOGGER.info("Creating device [{}] with active [{}]", deviceIdentification, activated);

        this.device = new DeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withNetworkAddress(activated ? InetAddress.getLoopbackAddress() : null).withPublicKeyPresent(PUBLIC_KEY_PRESENT)
                .withProtocolInfo(ProtocolInfoTestUtils.getProtocolInfo(PROTOCOL, PROTOCOL_VERSION)).isActivated(activated).build();

        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(deviceIdentification).withDeviceUid(DEVICE_UID).build();
    }

    private void initializeOslp() {
        // device always responds ok
        final com.alliander.osgp.oslp.Oslp.SetTransitionResponse oslpResponse = com.alliander.osgp.oslp.Oslp.SetTransitionResponse.newBuilder()
                .setStatus(Status.OK).build();

        this.oslpResponse = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setSetTransitionResponse(oslpResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpResponse, this.channelMock, this.device.getNetworkAddress());
        this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
        this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);
    }
}
