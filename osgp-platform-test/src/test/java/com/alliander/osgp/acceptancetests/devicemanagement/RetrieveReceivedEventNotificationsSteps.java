/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;

import com.alliander.osgp.adapter.ws.core.application.mapping.DeviceManagementMapper;
import com.alliander.osgp.adapter.ws.core.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.ws.core.endpoints.DeviceManagementEndpoint;
import com.alliander.osgp.adapter.ws.infra.specifications.JpaEventSpecifications;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindEventsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindEventsResponse;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceAuthorizationBuilder;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.entities.EventBuilder;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.specifications.EventSpecifications;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.EventType;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;

@DomainSteps
@Configurable
public class RetrieveReceivedEventNotificationsSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveReceivedEventNotificationsSteps.class);

    private static final String DEVICE_UID = "AAAAAAAAAAYAAA==";
    private static final String ORGANISATION_PREFIX = "ORG";
    private static final int PAGESIZELIMIT = 300;
    private static final Integer DEFAULT_PAGE = 1;
    private static final Integer DEFAULT_PAGESIZE = 50;

    // Domain fields
    private DeviceManagementEndpoint deviceManagementEndpoint;

    @Autowired
    @Qualifier("wsCoreDeviceManagementService")
    private DeviceManagementService deviceManagementService;

    private Organisation organisation;
    private Device device;
    private final EventSpecifications eventSpecifications = new JpaEventSpecifications();
    @SuppressWarnings("unused")
    private Specifications<Event> specifications;
    private Event event;
    private Pageable pageRequest;
    private Page<Event> eventsPage;
    private FindEventsRequest request;
    private FindEventsResponse response;
    @Captor
    private ArgumentCaptor<Specifications<Event>> actualSpecifications;
    @Captor
    private ArgumentCaptor<Pageable> actualPageRequest;

    // Repository Mocks
    @Autowired
    private OrganisationRepository organisationRepositoryMock;
    @Autowired
    private EventRepository eventRepositoryMock;
    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepositoryMock;

    // === SET UP ===

    private void setUp() {
        // init mocks to set ArgumentCaptors
        MockitoAnnotations.initMocks(this);
        // reset the mocks
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.organisationRepositoryMock,
                this.eventRepositoryMock, this.deviceAuthorizationRepositoryMock });

        this.deviceManagementEndpoint = new DeviceManagementEndpoint(this.deviceManagementService,
                new DeviceManagementMapper());

        this.request = null;
        this.response = null;
    }

    // === GIVEN ===

    @DomainStep("an OSGP client (.*)")
    public void givenAnOsgpClientOrganisation(final String organisation) {
        LOGGER.info("GIVEN: an OSGP client {}", organisation);

        this.setUp();

        // Create the organisation
        this.organisation = new Organisation(organisation, organisation, ORGANISATION_PREFIX,
                PlatformFunctionGroup.USER);

        when(this.organisationRepositoryMock.findByOrganisationIdentification(organisation)).thenReturn(
                this.organisation);
    }

    @DomainStep("an authorized device (.*)")
    public void givenAnAuthorizedDevice(final String deviceIdentification) {
        LOGGER.info("GIVEN: an authorized device: {}", deviceIdentification);

        // Create the device
        this.device = new DeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withNetworkAddress(InetAddress.getLoopbackAddress()).isActivated(true).build();

        when(this.deviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);

        final List<DeviceAuthorization> authorizations = new ArrayList<>();
        authorizations.add(new DeviceAuthorization(this.device, this.organisation, DeviceFunctionGroup.MANAGEMENT));

        when(this.deviceAuthorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device))
                .thenReturn(authorizations);
    }

    @DomainStep("a received event notification (.*), (.*) and (.*) from (.*)")
    public void givenAReceivedEventNotificationFrom(final String event, final String description, final String index,
            final String device) {
        LOGGER.info("GIVEN: a received event notification {}, {} and {} from (.*)", new Object[] { event, description,
                index, device });

        this.event = new Event(this.device, EventType.valueOf(event), description, Integer.parseInt(index));

        final List<Event> eventList = new ArrayList<Event>();
        eventList.add(this.event);

        this.pageRequest = new PageRequest(DEFAULT_PAGE, DEFAULT_PAGESIZE, Sort.Direction.DESC, "creationTime");
        this.eventsPage = new PageImpl<Event>(eventList, this.pageRequest, eventList.size());

        when(this.eventRepositoryMock.findAll(Matchers.<Specifications<Event>> any(), any(PageRequest.class)))
                .thenReturn(this.eventsPage);
    }

    @DomainStep("a retrieve event notification request")
    public void givenARetrieveEventNotificationsRequest() {
        LOGGER.info("GIVEN: a retrieve event notification request");

        // Create the request
        this.request = new FindEventsRequest();
        this.request.setPage(DEFAULT_PAGE);
        this.request.setPageSize(DEFAULT_PAGESIZE);

        this.request.setDeviceIdentification(this.device.getDeviceIdentification());
        //         this.specifications =
        //         Specifications.where(this.eventSpecifications.isAuthorized(this.organisation));
        //         this.pageRequest = new PageRequest(DEFAULT_PAGE, DEFAULT_PAGESIZE,
        //         Sort.Direction.DESC, "creationTime");

    }

    @DomainStep("a retrieve event notification request with requested page (.*) and pageSize (.*)")
    public void givenARetrieveEventNotificationsRequestWithRequestPageAndPageSize(final String requestedPage,
            final String pageSize) throws ArgumentNullOrEmptyException {
        LOGGER.info("GIVEN: a retrieve event notification request with requested page {} and pageSize {}",
                requestedPage, pageSize);

        // Create the request
        this.request = new FindEventsRequest();
        this.request.setPage(Integer.parseInt(requestedPage));
        this.request.setPageSize(Integer.parseInt(pageSize));
        this.request.setDeviceIdentification(this.device.getDeviceIdentification());

        this.specifications = Specifications.where(this.eventSpecifications.isAuthorized(this.organisation));
        this.pageRequest = new PageRequest(Integer.parseInt(requestedPage), Math.min(Integer.parseInt(pageSize),
                PAGESIZELIMIT), Sort.Direction.DESC, "creationTime");
    }

    @DomainStep("a received event notification at (.*) from (.*)")
    public void givenAReceivedEventNotificationAtFrom(final String timestamp, final String device) {
        LOGGER.info("GIVEN: a received event notification at (.*) from (.*)", timestamp, device);

        // TODO: parse timestamp
        this.event = new EventBuilder().withDevice(new DeviceBuilder().withDeviceIdentification(device).build())
                .build();

        final List<Event> eventList = new ArrayList<Event>();
        eventList.add(this.event);

        this.eventsPage = new PageImpl<Event>(eventList);
        LOGGER.info("events: {}", this.eventsPage.getContent().size());

        when(this.eventRepositoryMock.findAll(Matchers.<Specifications<Event>> any(), any(PageRequest.class)))
                .thenReturn(this.eventsPage);
    }

    @DomainStep("(.*) received event notifications")
    public void givenNumberReceivedEventNotifications(final String count) {
        LOGGER.info("GIVEN: {} received event notifications", count);

        final List<Event> eventList = new ArrayList<Event>();
        int totalPages = 0;
        if (count != null && count != "EMPTY") {
            for (int i = 0; i < Math.min(Integer.parseInt(count), Math.min(this.request.getPageSize(), PAGESIZELIMIT)); i++) {
                eventList.add(new Event(this.device, this.event.getEvent(), this.event.getDescription(), this.event
                        .getIndex()));
            }
            totalPages = Integer.parseInt(count);
        }
        this.eventsPage = new PageImpl<Event>(eventList, new PageRequest(this.request.getPage(), Math.min(
                this.request.getPageSize(), PAGESIZELIMIT)), totalPages);

        when(this.eventRepositoryMock.findAll(Matchers.<Specifications<Event>> any(), any(PageRequest.class)))
                .thenReturn(this.eventsPage);
    }

    @DomainStep("the event notification must be filtered on (.*), (.*), and (.*)")
    public void givenTheEventNotificationMustBeFilteredOn(final String deviceIdentification,
            final String fromTimestamp, final String untilTimestamp) {
        LOGGER.info("GIVEN: the event notification must be filtered on {}, {}, and {}", new Object[] {
                deviceIdentification, fromTimestamp, untilTimestamp });

        // TODO: Filter on fromTimestamp
        // TODO: Filter on untilTimestamp
        if (deviceIdentification != null && !deviceIdentification.equals("EMPTY")) {
            this.request.setDeviceIdentification(deviceIdentification);
            when(this.deviceRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);
            final List<DeviceAuthorization> authorizations = new ArrayList<DeviceAuthorization>();
            authorizations.add(new DeviceAuthorizationBuilder().withOrganisation(this.organisation)
                    .withDevice(new DeviceBuilder().withDeviceIdentification(deviceIdentification).build())
                    .withFunctionGroup(DeviceFunctionGroup.MANAGEMENT).build());
            when(
                    this.deviceAuthorizationRepositoryMock.findByOrganisationAndDevice(any(Organisation.class),
                            any(Device.class))).thenReturn(authorizations);
        }
    }

    // === WHEN ===

    @DomainStep("the retrieve event notification request is received")
    public void whenTheRetrieveEventNotificationRequestIsReceived() {
        LOGGER.info("WHEN: the retrieve event notification request is received");

        try {
            // Send the find events request.
            this.response = this.deviceManagementEndpoint.findEventsRequest(
                    this.organisation.getOrganisationIdentification(), this.request);
            if (this.response == null) {
                LOGGER.info("Response is null");
            }
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}\nStacktrace: {}", t.getClass().getSimpleName(), t.getMessage(),
                    t.getStackTrace());
        }
    }

    // === THEN ===

    @DomainStep("the OSGP should send an event notification response")
    public boolean thenTheOSGPShouldSendAnEventNotificationResponse() {
        LOGGER.info("THEN: the OSGP should send an event notification response");

        try {
            // TODO: Verify the correct Specifications class and Page request
            // class

            verify(this.eventRepositoryMock, times(1)).findAll(this.actualSpecifications.capture(),
                    this.actualPageRequest.capture());
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }

        // Expect the response to be returned.
        return this.response != null;
    }

    @DomainStep("the response should contain the event notification (.*), (.*), (.*), (.*) and (.*)")
    public boolean thenTheResponseShouldContainTheEventNotification(final String timestamp,
            final String deviceIdentification, final String event, final String description, final String index) {
        LOGGER.info("THEN: the response should contain the event notification (.*), (.*), (.*), (.*) and (.*)",
                new Object[] { timestamp, deviceIdentification, event, description, index });

        final boolean sizeCorrect = this.response.getEvents().size() == 1;
        final boolean deviceIdentificationCorrect = this.response.getEvents().get(0).getDeviceIdentification()
                .equals(deviceIdentification);
        final boolean descriptionCorrect = this.response.getEvents().get(0).getDescription().equals(description);
        final boolean eventCorrect = this.response.getEvents().get(0).getEventType().ordinal() == EventType.valueOf(
                event).ordinal();
        final boolean timeStampCorrect = true;
        // TODO: Verify the timstamp of the event, not the creationTime of the
        // event.
        // && this.response
        // .getEvents()
        // .get(0)
        // .getTimestamp()
        // .toGregorianCalendar()
        // .equals(DateTime.parse(timestamp).toGregorianCalendar())

        boolean indexCorrect = true;
        if (index != null && index != "" && index != "EMPTY") {
            indexCorrect = this.response.getEvents().get(0).getIndex().equals(Integer.parseInt(index));
        }

        return sizeCorrect && deviceIdentificationCorrect && descriptionCorrect && eventCorrect && timeStampCorrect
                && indexCorrect;
    }

    /**
     * Verify that the correct number events are returned.
     * 
     * @param number
     *            The number of expected events
     * @return
     */
    @DomainStep("the response should contain (.*) event notifications")
    public boolean andTheResponseShouldContainNumberEventNotifications(final String number) {

        LOGGER.info("THEN: the response should contain {} event notifications", number);

        if (this.response == null) {
            LOGGER.info("the actual response is null");
            return false;
        }
        if (this.response.getEvents() == null) {
            LOGGER.info("the events list in the actual response is null");
            return false;
        }
        if (this.response.getEvents().size() != Integer.parseInt(number)) {
            LOGGER.info("the actual response contains {} events", this.response.getEvents().size());
            return false;
        }
        return true;
    }

    /**
     * Verify that the page object has the correct total values for total
     * entities and total pages.
     * 
     * @param totalNofEventNotifications
     * @param totalNofPages
     * @return
     */
    @DomainStep("the response should contain total number of pages (.*)")
    public boolean andTheResponseShouldContainTotalNumberOfPages(final String totalNofPages) {
        LOGGER.info("THEN: the response should contain total number of pages (.*)", totalNofPages);

        if (this.response == null) {
            LOGGER.info("the actual response is null");
            return false;
        }
        if (this.response.getPage() == null) {
            LOGGER.info("the page in the actual response is null");
            return false;
        }
        if (this.response.getPage().getTotalPages() != Integer.parseInt(totalNofPages)) {
            LOGGER.info("the actual total number of pages is {}", this.response.getPage().getTotalPages());
            return false;
        }
        return true;
    }
}
