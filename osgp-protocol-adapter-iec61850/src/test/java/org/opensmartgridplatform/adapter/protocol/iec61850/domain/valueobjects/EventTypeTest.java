package org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.junit.Test;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationTypeDto;

public class EventTypeTest {

    private static final String FILTER_MASK_FOR_ALL_EVENTS = "3FFFFFF";

    @Test
    public void testNoFilterForEvents() {

        assertEquals("No filter for events", "", EventType.getEventTypeFilterMask(null));

        assertNull(EventType.getEventTypesForFilter(null));
    }

    @Test
    public void testFilterForNoEvents() {

        assertEquals("Event filter for no events", "0",
                EventType.getEventTypeFilterMask(EnumSet.noneOf(EventType.class)));

        assertEquals(Collections.emptySet(), EventType.getEventTypesForFilter("0"));
    }

    @Test
    public void testFilterForAllEvents() {

        final Set<EventType> allEvents = EnumSet.allOf(EventType.class);

        assertEquals("Event filter for all events", FILTER_MASK_FOR_ALL_EVENTS,
                EventType.getEventTypeFilterMask(allEvents));

        assertEquals(allEvents, EventType.getEventTypesForFilter(FILTER_MASK_FOR_ALL_EVENTS));
    }

    @Test
    public void testFilterForFirstSixEvents() {

        final String filterForFirstSixEvents = "3F";

        final Set<EventType> firstSixEvents = EnumSet.of(EventType.DIAG_EVENTS_GENERAL, EventType.LIGHT_EVENTS_LIGHT_ON,
                EventType.LIGHT_EVENTS_LIGHT_OFF, EventType.TARIFF_EVENTS_TARIFF_ON, EventType.TARIFF_EVENTS_TARIFF_OFF,
                EventType.MONITOR_EVENTS_LOSS_OF_POWER);

        assertEquals("Event filter for first six events", filterForFirstSixEvents,
                EventType.getEventTypeFilterMask(firstSixEvents));

        assertEquals(firstSixEvents, EventType.getEventTypesForFilter(filterForFirstSixEvents));
    }

    @Test
    public void testFilterForAllEventNotificationTypes() {

        assertEquals("Event filter for all event notification types", FILTER_MASK_FOR_ALL_EVENTS,
                EventType.getEventTypeFilterMaskForNotificationTypes(EnumSet.allOf(EventNotificationTypeDto.class)));
    }

    @Test
    public void testFilterForFirmwareEvents() {

        final String filterForFirmwareEvents = "1FFFC0";

        assertEquals("Event filter for firmware events", filterForFirmwareEvents, EventType
                .getEventTypeFilterMaskForNotificationTypes(EnumSet.of(EventNotificationTypeDto.FIRMWARE_EVENTS)));

        final Set<EventType> firmwareEvents = EnumSet.of(EventType.FUNCTION_FIRMWARE_EVENTS_ACTIVATING,
                EventType.FUNCTION_FIRMWARE_EVENTS_AUTHENTICATION_FAIL,
                EventType.FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_FAILED,
                EventType.FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND,
                EventType.FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_SUCCESS, EventType.SECURITY_FIRMWARE_EVENTS_ACTIVATING,
                EventType.SECURITY_FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND,
                EventType.SECURITY_FIRMWARE_EVENTS_AUTHENTICATION_FAIL,
                EventType.SECURITY_FIRMWARE_EVENTS_DOWNLOAD_FAILED, EventType.SECURITY_FIRMWARE_EVENTS_DOWNLOAD_SUCCESS,
                EventType.CA_FILE_EVENTS_ACTIVATING, EventType.CA_FILE_EVENTS_DOWNLOAD_NOTFOUND,
                EventType.CA_FILE_EVENTS_AUTHENTICATION_FAIL, EventType.CA_FILE_EVENTS_DOWNLOAD_FAILED,
                EventType.CA_FILE_EVENTS_DOWNLOAD_SUCCESS);

        assertEquals(firmwareEvents, EventType.getEventTypesForFilter(filterForFirmwareEvents));
    }

    @Test
    public void testFilterShouldContainNotificationTypeForEvent() {
        // When a filter contains at least one of the events for an
        // EventNotificationType, the EventNotificationType should be returned
        // by getNotificationTypesForFilter().
        final String filterForLightEventsWithoutLightOn = EventType
                .getEventTypeFilterMask(EnumSet.of(EventType.LIGHT_EVENTS_LIGHT_OFF));
        final Set<EventNotificationTypeDto> actualNotificationTypes = EventType
                .getNotificationTypesForFilter(filterForLightEventsWithoutLightOn);
        final Set<EventNotificationTypeDto> expectedNotificationTypes = EnumSet
                .of(EventNotificationTypeDto.LIGHT_EVENTS);

        assertEquals(expectedNotificationTypes, actualNotificationTypes);
    }

    @Test
    public void testOneNotificationTypeForAllEventTypes() {
        final String filterForTariffEvents = EventType.getEventTypeFilterMask(
                EnumSet.of(EventType.TARIFF_EVENTS_TARIFF_OFF, EventType.TARIFF_EVENTS_TARIFF_ON));
        final Set<EventNotificationTypeDto> actualNotificationTypes = EventType
                .getNotificationTypesForFilter(filterForTariffEvents);
        final Set<EventNotificationTypeDto> expectedNotificationTypes = EnumSet
                .of(EventNotificationTypeDto.TARIFF_EVENTS);
        assertEquals(expectedNotificationTypes, actualNotificationTypes);
    }

    @Test
    public void testSomeNotificationTypesForAllEventTypes() {
        final String filterForDiagTariffAndSecurityEvents = EventType
                .getEventTypeFilterMask(EnumSet.of(EventType.DIAG_EVENTS_GENERAL, EventType.TARIFF_EVENTS_TARIFF_OFF,
                        EventType.TARIFF_EVENTS_TARIFF_ON, EventType.AUTHENTICATION_FAIL));
        final Set<EventNotificationTypeDto> actualNotificationTypes = EventType
                .getNotificationTypesForFilter(filterForDiagTariffAndSecurityEvents);
        final Set<EventNotificationTypeDto> expectedNotificationTypes = EnumSet.of(EventNotificationTypeDto.DIAG_EVENTS,
                EventNotificationTypeDto.TARIFF_EVENTS, EventNotificationTypeDto.SECURITY_EVENTS);
        assertEquals(expectedNotificationTypes, actualNotificationTypes);
    }

    @Test
    public void testAllNotificationTypesForAllEventTypes() {
        final Set<EventNotificationTypeDto> actualNotificationTypes = EventType
                .getNotificationTypesForFilter(FILTER_MASK_FOR_ALL_EVENTS);
        final Set<EventNotificationTypeDto> expectedNotificationTypes = EnumSet.allOf(EventNotificationTypeDto.class);
        // HARDWARE_FAILURE notifications not reported, because there's no event
        // with this notification type yet.
        expectedNotificationTypes.remove(EventNotificationTypeDto.HARDWARE_FAILURE);
        assertEquals(expectedNotificationTypes, actualNotificationTypes);
    }

    @Test
    public void testAllNotificationTypesForMoreThanAllEventTypes() {
        // When the filter contains events, which OSGP doesn't know yet,
        // all notification types should be returned.
        // No errors should occur.
        final Set<EventNotificationTypeDto> actualNotificationTypes = EventType
                .getNotificationTypesForFilter("7FFFFFF");
        final Set<EventNotificationTypeDto> expectedNotificationTypes = EnumSet.allOf(EventNotificationTypeDto.class);
        // HARDWARE_FAILURE notifications not reported, because there's no event
        // with this notification type yet.
        expectedNotificationTypes.remove(EventNotificationTypeDto.HARDWARE_FAILURE);
        assertEquals(expectedNotificationTypes, actualNotificationTypes);
    }
}
