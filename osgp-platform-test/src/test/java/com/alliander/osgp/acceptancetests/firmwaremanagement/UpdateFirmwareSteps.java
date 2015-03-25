package com.alliander.osgp.acceptancetests.firmwaremanagement;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
import com.alliander.osgp.adapter.protocol.oslp.device.FirmwareLocation;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDeviceBuilder;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.processors.CommonUpdateFirmwareRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerClient;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpDeviceService;
import com.alliander.osgp.adapter.ws.core.application.services.FirmwareManagementService;
import com.alliander.osgp.adapter.ws.core.endpoints.FirmwareManagementEndpoint;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonResponseMessageFinder;
import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareResponse;
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
public class UpdateFirmwareSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateFirmwareSteps.class);

    private static final String ORGANISATION_ID = "ORGANISATION-01";
    private static final String ORGANISATION_PREFIX = "ORG";
    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";

    // TODO - Add as parameters to tests
    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // WS Adapter fields
    private FirmwareManagementEndpoint firmwareManagementEndpoint;

    private UpdateFirmwareRequest request;
    private UpdateFirmwareAsyncResponse updateFirmwareAsyncResponse;
    private UpdateFirmwareAsyncRequest updateFirmwareAsyncRequest;
    private UpdateFirmwareResponse response;

    @Autowired
    @Qualifier("wsCoreFirmwareManagementService")
    FirmwareManagementService wsFirmwareManagementService;

    @Autowired
    @Qualifier("wsCoreIncomingResponsesMessageFinder")
    private CommonResponseMessageFinder commonResponseMessageFinder;
    @Autowired
    @Qualifier("wsCoreIncomingResponsesJmsTemplate")
    private JmsTemplate commonResponsesJmsTemplate;

    // Domain adapter fields
    @Autowired
    @Qualifier("domainCoreOutgoingWebServiceResponsesMessageSender")
    private WebServiceResponseMessageSender webServiceResponseMessageSenderMock;
    @Autowired
    private OrganisationRepository organisationRepositoryMock;
    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepositoryMock;
    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private OslpLogItemRepository oslpLogItemRepositoryMock;
    private Organisation organisation;
    private Device device;

    // Protocol adapter fields
    @Autowired
    private DeviceRegistrationService deviceRegistrationService;

    @Autowired
    private OslpDeviceService oslpDeviceService;
    @Autowired
    private OslpDeviceRepository oslpDeviceRepositoryMock;
    private OslpDevice oslpDevice;

    @Autowired
    private Channel channelMock;
    private OslpChannelHandlerClient oslpChannelHandler;
    private OslpEnvelope oslpRequest;
    private OslpEnvelope oslpResponse;

    @Autowired
    @Qualifier(value = "oslpCommonUpdateFirmwareRequestMessageProcessor")
    private CommonUpdateFirmwareRequestMessageProcessor messageProcessor;

    // Test fields
    private Throwable throwable;

    @DomainStep("a firmware update request for device (.*), firmwareName (.*)")
    public void givenAFirmwareUpdateRequest(final String device, final String firmwareName) throws NoSuchAlgorithmException, IOException,
            InvalidKeySpecException {
        LOGGER.info("Given a firmware update request for device with firmwareName");

        this.setUp();

        this.request = new UpdateFirmwareRequest();
        this.request.setDeviceIdentification(device);
        this.request.setFirmwareIdentification(firmwareName);
    }

    @DomainStep("an OSGP client (.*)")
    public void givenAnOsgpClient(final String organisationIdentification) {
        LOGGER.info("[Given an OSGP client {}]", organisationIdentification);

        this.organisation = new Organisation(organisationIdentification, organisationIdentification, ORGANISATION_PREFIX, PlatformFunctionGroup.USER);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(organisationIdentification)).thenReturn(this.organisation);
    }

    @DomainStep("a firmware location configuration with (.*), (.*), and (.*)")
    public void givenAFirmwareLocationConfigurationWith(final String firmwareDomainConfig, final String firmwarePathConfig, final String firmwareExtensionConfig) {
        LOGGER.info("[Given a firmware location configuration with {}, {}, and {}]", new Object[] { firmwareDomainConfig, firmwarePathConfig,
                firmwareExtensionConfig });

        try {
            final FirmwareLocation firmwareLocation = new FirmwareLocation(firmwareDomainConfig, firmwarePathConfig, firmwareExtensionConfig);
            this.messageProcessor.setFirmwareLocation(firmwareLocation);
            // this.oslpFirmwareManagementService.setFirmwareLocation(firmwareLocation);
        } catch (final IllegalArgumentException e) {
            // this.firmwareLocation = null;
            // Silencing the IllegalArgumentException thrown by the
            // FirmwareLocation constructor.
            // This constructor is usually called when setting up the Spring
            // context and not while
            // sending the request being tested.
            // this.oslpFirmwareManagementService.setFirmwareLocation(null);
            this.messageProcessor.setFirmwareLocation(null);
        }
    }

    @DomainStep("the update firmware oslp message from the device")
    public void givenTheOslpResponse() {
        LOGGER.info("GIVEN: the update firmware version oslp message from the device.");

        final com.alliander.osgp.oslp.Oslp.UpdateFirmwareResponse updateFirmwareResponse = com.alliander.osgp.oslp.Oslp.UpdateFirmwareResponse.newBuilder()
                .setStatus(Status.OK).build();

        this.oslpResponse = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setUpdateFirmwareResponse(updateFirmwareResponse).build()).build();

        this.oslpChannelHandler = OslpTestUtils.createOslpChannelHandlerWithResponse(this.oslpResponse, this.channelMock, this.device.getNetworkAddress());
        this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
        this.oslpDeviceService.setOslpChannelHandler(this.oslpChannelHandler);
    }

    @DomainStep("an authorized device (.*)")
    public void givenAnAuthorizedDevice(final String deviceIdentification) {
        LOGGER.info("[Given an authorized device {}]", deviceIdentification);

        this.createDevice(deviceIdentification, true);
        this.createOslpDevice(deviceIdentification);

        when(this.deviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);
        when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);

        final List<DeviceAuthorization> authorizations = new ArrayList<>();
        authorizations.add(new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.organisation)
                .withFunctionGroup(DeviceFunctionGroup.FIRMWARE).build());
        when(this.deviceAuthorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device)).thenReturn(authorizations);
    }

    // @DomainStep("a received firmware update request for device (.*) with (.*)")
    // public void givenAReceivedFirmwareUpdateRequestWith(final String
    // deviceIdentification, final String firmwareName) {
    // LOGGER.info("[Given a received firmware update request for device {} with [{}]",
    // firmwareName);
    // this.request.setDeviceIdentification(deviceIdentification);
    // this.request.setFirmwareIdentification(firmwareName);
    // }

    @DomainStep("the update firmware request is received")
    public void whenTheUpdateFirmwareRequestIsReceived() throws Exception {
        LOGGER.info("[When the update firmware request is received]");

        try {
            this.updateFirmwareAsyncResponse = this.firmwareManagementEndpoint.updateFirmware(ORGANISATION_ID, this.request);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("the update firmware request should return an async response with a correlationId and deviceId (.*)")
    public boolean thenTheUpdateFirmwareRequestShouldReturnAnAsyncResponse(final String device) {
        LOGGER.info("THEN: the update firmware request should return an async response with a correlationId and deviceId {}", device);
        // TODO Add check on device id
        try {
            Assert.assertNotNull("asyncResponse should not be null", this.updateFirmwareAsyncResponse);
            Assert.assertNotNull("CorrelationId should not be null", this.updateFirmwareAsyncResponse.getAsyncResponse().getCorrelationUid());
            Assert.assertNull("Throwable should be null", this.throwable);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the update firmware request should return a validation error")
    public boolean thenTheUpdateFirmwareRequestShouldReturnAValidationError() {
        LOGGER.info("the update firmware request should return a validation error");
        try {
            Assert.assertNull("asyncResponse should not null", this.updateFirmwareAsyncResponse);
            Assert.assertNotNull("Throwable should not be null", this.throwable);
            Assert.assertTrue("Throwable should be ValidationException", this.throwable.getCause() instanceof ValidationException);
        } catch (final Exception e) {
            LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("an update firmware oslp message is sent to device (.*) with firmwareName (.*) and firmwareDomain (.*) should be (.*)")
    public boolean thenAnUpdateFirmwareOslpMessageShouldBeSent(final String device, final String firmwareName, final String firmwareDomain,
            final Boolean isMessageSent) {
        LOGGER.info("THEN: an update firmware oslp message is sent to device {} with firmwareName {} and firmwareDomain {} should be {}.", device,
                firmwareName, firmwareDomain, isMessageSent);

        final int count = isMessageSent ? 1 : 0;

        try {
            final ArgumentCaptor<OslpEnvelope> argument = ArgumentCaptor.forClass(OslpEnvelope.class);
            verify(this.channelMock, timeout(1000).times(count)).write(argument.capture());

            if (isMessageSent) {
                this.oslpRequest = argument.getValue();

                Assert.assertTrue("Message should contain update firmware request.", this.oslpRequest.getPayloadMessage().hasUpdateFirmwareRequest());
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("an ovl update firmware message with result (.*) should be sent to the ovl out queue")
    public boolean thenAnOvlUpdateFirmwareMessage(final String result) {
        LOGGER.info("THEN: an ovl update firmware message with result {} should be sent to the ovl out queue.", result);

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

    // Response message handling

    @DomainStep("an update firmware response request with correlationId (.*) and deviceId (.*)")
    public void givenAnUpdateFirmwareResponseRequest(final String correlationId, final String deviceId) throws NoSuchAlgorithmException,
            InvalidKeySpecException, IOException {
        LOGGER.info("an update firmware response request with correlationId {} and deviceId {}.", correlationId, deviceId);

        this.setUp();

        this.updateFirmwareAsyncRequest = new UpdateFirmwareAsyncRequest();

        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setCorrelationUid(correlationId);
        asyncRequest.setDeviceId(deviceId);

        this.updateFirmwareAsyncRequest.setAsyncRequest(asyncRequest);
    }

    @DomainStep("an update firmware response message with correlationId (.*), deviceId (.*), qresult (.*) and qdescription (.*) is found in the queue (.*)")
    public void givenAnUpdateFirmwareVersionResponseMessageIsFoundInTheQueue(final String correlationId, final String deviceId, final String qresult,
            final String qdescription, final Boolean isFound) {
        LOGGER.info("a update firmware response message with correlationId {}, deviceId {}, qresult {} and qdescription {} is found in the queue {}",
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

            when(this.commonResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(messageMock);
        } else {
            when(this.commonResponsesJmsTemplate.receiveSelected(any(String.class))).thenReturn(null);
        }
    }

    @DomainStep("the update firmware response request is received")
    public void whenTheUpdateFirmwareResponseRequestIsReceived() {
        LOGGER.info("WHEN: \"the update firmware response request is received\".");

        try {
            this.response = this.firmwareManagementEndpoint.getUpdateFirmwareResponse(ORGANISATION_ID, this.updateFirmwareAsyncRequest);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    @DomainStep("the update firmware response request should return a firmware response with result (.*) and description (.*)")
    public boolean thenTheUpdateFirmwareResponseRequestShouldReturnAGetFirmwareVersionResponse(final String result, final String description) {
        LOGGER.info("THEN: \"the update firmware response request should return a firmware response with result {} and description {}", result, description);

        try {
            if ("NOT_OK".equals(result)) {
                Assert.assertNull("Set Schedule Response should be null", this.response);
                Assert.assertNotNull("Throwable should not be null", this.throwable);
                Assert.assertTrue("Throwable should contain a validation exception", this.throwable.getCause() instanceof ValidationException);
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

    private void setUp() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.organisationRepositoryMock, this.deviceAuthorizationRepositoryMock,
                this.oslpLogItemRepositoryMock, this.channelMock, this.webServiceResponseMessageSenderMock, this.oslpDeviceRepositoryMock });

        this.firmwareManagementEndpoint = new FirmwareManagementEndpoint(this.wsFirmwareManagementService);
        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        this.request = null;
        this.response = null;
        this.updateFirmwareAsyncRequest = null;
        this.updateFirmwareAsyncResponse = null;
        this.throwable = null;
    }

    private void createDevice(final String deviceIdentification, final boolean activated) {
        LOGGER.info("Creating device [{}] with active [{}]", deviceIdentification, activated);

        this.device = new DeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withNetworkAddress(activated ? InetAddress.getLoopbackAddress() : null).withPublicKeyPresent(PUBLIC_KEY_PRESENT)
                .withProtocolInfo(ProtocolInfoTestUtils.getProtocolInfo(PROTOCOL, PROTOCOL_VERSION)).isActivated(activated).build();
    }

    private void createOslpDevice(final String deviceIdentification) {
        LOGGER.info("Creating oslp device [{}]", deviceIdentification);

        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(deviceIdentification).withDeviceUid(DEVICE_UID).build();
    }

}
