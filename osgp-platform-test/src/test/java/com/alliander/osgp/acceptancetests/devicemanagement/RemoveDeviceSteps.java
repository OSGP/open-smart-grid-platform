package com.alliander.osgp.acceptancetests.devicemanagement;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.jboss.netty.channel.Channel;
import org.junit.Assert;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpDeviceService;
import com.alliander.osgp.adapter.ws.admin.application.mapping.DeviceManagementMapper;
import com.alliander.osgp.adapter.ws.admin.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.ws.admin.endpoints.DeviceManagementEndpoint;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveDeviceResponse;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceAuthorizationBuilder;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.OslpLogItemRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.EventType;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.oslp.OslpEnvelope;

@DomainSteps
@Configurable
public class RemoveDeviceSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetEventNotificationsSteps.class);

    private static final String ORGANISATION_ID = "ORGANISATION-01";

    private static final String ORGANISATION_PREFIX = "ORG";

    private static final String DEVICE_UID = "AAAAAAAAAAYAAA==";

    private DeviceManagementEndpoint deviceManagementEndpoint;
    private RemoveDeviceRequest request;
    private RemoveDeviceResponse response;
    private Throwable throwable;

    private Device device;
    private Organisation organisation;
    private List<DeviceAuthorization> authorizations;
    private List<Event> events;

    // private OslpChannelHandlerClient oslpChannelHandler;
    // private OslpEnvelope oslpRequest;
    // private OslpEnvelope oslpResponse;

    // Repository mocks
    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private OrganisationRepository organisationRepositoryMock;
    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepositoryMock;
    @Autowired
    private EventRepository eventRepositoryMock;
    @Autowired
    private OslpLogItemRepository oslpLogItemRepositoryMock;

    // Channel mock
    @Autowired
    private Channel channelMock;

    // Application Services
    @Autowired
    private DeviceManagementService deviceManagementService;

    // Oslp Service
    @Autowired
    private OslpDeviceService oslpDeviceService;

    // === GIVEN ===

    @DomainStep("a remove device request for device (.*)")
    public void givenARequest(final String device) {
        LOGGER.info("WHEN: a remove device request for device {}.", device);

        this.setUp();

        this.request = new RemoveDeviceRequest();
        this.request.setDeviceIdentification(device);
    }

    @DomainStep("the remove device request refers to a device (.*) with status (.*), (.*) authorisations and (.*) events")
    public void givenADevice(final String device, final String status, final Integer numberOfAuthorizations, final Integer numberOfEvents) throws Exception {
        LOGGER.info("GIVEN: the remove device request refers to a device {} with status {}.", device, status);

        switch (status.toUpperCase()) {
        case "ACTIVE":
            this.createDevice(device, true);
            when(this.deviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.device);
            this.createAuthorizations(device, numberOfAuthorizations);
            when(this.deviceAuthorizationRepositoryMock.findByDevice(this.device)).thenReturn(this.authorizations);
            this.createEvents(device, numberOfEvents);
            when(this.eventRepositoryMock.findByDevice(this.device)).thenReturn(this.events);
            break;
        case "UNKNOWN":
            when(this.deviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(null);
            when(this.deviceAuthorizationRepositoryMock.findByDevice(any(Device.class))).thenReturn(null);
            when(this.eventRepositoryMock.findByDevice(any(Device.class))).thenReturn(null);
            break;
        case "UNREGISTERED":
            this.createDevice(device, false);
            when(this.deviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.device);
            this.createAuthorizations(device, numberOfAuthorizations);
            when(this.deviceAuthorizationRepositoryMock.findByDevice(this.device)).thenReturn(this.authorizations);
            this.createEvents(device, numberOfEvents);
            when(this.eventRepositoryMock.findByDevice(this.device)).thenReturn(this.events);
            break;
        default:
            throw new Exception("Unknown device status");
        }
    }

    @DomainStep("the remove device request refers to an organisation that is authorised (.*)")
    public void givenAnOrganisation(final Boolean isAuthorized) {
        LOGGER.info("GIVEN: the remove device request refers to an organisation that is authorised: {}.", isAuthorized);

        this.organisation = new Organisation(ORGANISATION_ID, ORGANISATION_ID, ORGANISATION_PREFIX, PlatformFunctionGroup.USER);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID)).thenReturn(this.organisation);

        List<DeviceAuthorization> authorizations = null;

        authorizations = new ArrayList<>();
        if (isAuthorized) {
            authorizations.add(new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.organisation)
                    .withFunctionGroup(DeviceFunctionGroup.MANAGEMENT).build());
        }
        when(this.deviceAuthorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device)).thenReturn(authorizations);
    }

    // === WHEN ===

    @DomainStep("the remove device request is received")
    public void whenTheRequestIsReceived() {
        LOGGER.info("WHEN: the remove device request is received.");

        try {
            this.response = this.deviceManagementEndpoint.removeDevice(ORGANISATION_ID, this.request);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("no oslp message should be sent to device (.*)")
    public boolean thenNoOslpMessageShouldBeSent(final String device) {
        LOGGER.info("THEN: no oslp message should be sent to device {}.", device);

        try {
            verify(this.channelMock, times(0)).write(any(OslpEnvelope.class));
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the device (.*) should be removed (.*)")
    public boolean thenTheDeviceShouldBeRemoved(final String device, final Boolean removed) {
        LOGGER.info("THEN: the device {} should be removed: {}.", device, removed);

        try {
            final int count = removed ? 1 : 0;
            verify(this.deviceRepositoryMock, times(count)).delete(this.device);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }

        return true;
    }

    @DomainStep("(.*) authorisations for device (.*) should be removed (.*)")
    public boolean thenTheAuthorizationsForTheDeviceShouldBeRemoved(final Integer numberOfAuthorizations, final String device, final Boolean removed) {
        LOGGER.info("THEN: {} authorizations for device {} should be removed: {}.", new Object[] { numberOfAuthorizations, device, removed });

        try {
            final int count = removed ? numberOfAuthorizations : 0;
            verify(this.deviceAuthorizationRepositoryMock, times(count)).delete(any(DeviceAuthorization.class));
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }

        return true;
    }

    @DomainStep("(.*) events for device (.*) should be removed (.*)")
    public boolean thenTheEventsForTheDeviceShouldBeRemoved(final Integer numberOfEvents, final String device, final Boolean removed) {
        LOGGER.info("THEN: {} events for device {} should be removed: {}.", new Object[] { numberOfEvents, device, removed });

        try {
            final int count = removed ? numberOfEvents : 0;
            verify(this.eventRepositoryMock, times(count)).delete(any(Event.class));
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }

        return true;
    }

    @DomainStep("the remove device response should return (.*)")
    public boolean thenTheResponseShouldReturn(final String result) {
        LOGGER.info("THEN: the remove device response should return {}.", result);

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
                Assert.assertEquals(result.toUpperCase(), this.throwable.getCause().getClass().getSimpleName().toUpperCase());
            } catch (final AssertionError e) {
                LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
                return false;
            }
        }

        return true;
    }

    // === private methods ===

    private void setUp() {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.organisationRepositoryMock, this.deviceAuthorizationRepositoryMock,
                this.oslpLogItemRepositoryMock, this.eventRepositoryMock, this.channelMock });

        this.deviceManagementEndpoint = new DeviceManagementEndpoint(this.deviceManagementService, new DeviceManagementMapper());

        this.request = null;
        this.response = null;
        this.throwable = null;
    }

    private void createDevice(final String deviceIdentification, final boolean activated) {
        this.device = new DeviceBuilder().withDeviceIdentification(deviceIdentification)
        // .withDeviceUid(activated ? DEVICE_UID : null)
                .withNetworkAddress(activated ? InetAddress.getLoopbackAddress() : null).isActivated(activated).build();
    }

    private void createAuthorizations(final String device, final int numberOfAuthorizations) {
        this.authorizations = new ArrayList<>();
        for (int i = 1; i <= numberOfAuthorizations; i++) {
            final String organisation = "org" + i;
            this.authorizations.add(new DeviceAuthorization(this.device, new Organisation(organisation, organisation, ORGANISATION_PREFIX,
                    PlatformFunctionGroup.USER), DeviceFunctionGroup.AD_HOC));
        }
    }

    private void createEvents(final String device, final int numberOfEvents) {
        this.events = new ArrayList<>();
        for (int i = 1; i <= numberOfEvents; i++) {
            this.events.add(new Event(this.device, EventType.LIGHT_EVENTS_LIGHT_ON, "desc" + i, 1));
        }
    }
}
