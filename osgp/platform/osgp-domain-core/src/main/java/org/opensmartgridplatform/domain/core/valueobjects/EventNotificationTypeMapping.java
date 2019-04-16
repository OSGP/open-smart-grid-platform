/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventNotificationTypeMapping {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventNotificationTypeMapping.class);

    private static final Map<EventNotificationType, Set<EventType>> mapping = new EnumMap<>(
            EventNotificationType.class);

    static {
        mapping.put(EventNotificationType.DIAG_EVENTS,
                EnumSet.range(EventType.DIAG_EVENTS_GENERAL, EventType.DIAG_EVENTS_UNKNOWN_MESSAGE_TYPE));
        mapping.put(EventNotificationType.HARDWARE_FAILURE,
                EnumSet.range(EventType.HARDWARE_FAILURE_RELAY, EventType.HARDWARE_FAILURE_RTC_NOT_SET));
        mapping.put(EventNotificationType.LIGHT_EVENTS,
                EnumSet.copyOf(Arrays.asList(EventType.LIGHT_FAILURE_DALI_COMMUNICATION,
                        EventType.LIGHT_FAILURE_BALLAST, EventType.LIGHT_EVENTS_LIGHT_ON,
                        EventType.LIGHT_EVENTS_LIGHT_OFF, EventType.LIGHT_FAILURE_TARIFF_SWITCH_ATTEMPT,
                        EventType.LIGHT_SENSOR_REPORTS_DARK, EventType.LIGHT_SENSOR_REPORTS_LIGHT)));
        mapping.put(EventNotificationType.TARIFF_EVENTS,
                EnumSet.range(EventType.TARIFF_EVENTS_TARIFF_ON, EventType.TARIFF_EVENTS_TARIFF_OFF));
        mapping.put(EventNotificationType.MONITOR_EVENTS,
                EnumSet.copyOf(Arrays.asList(EventType.MONITOR_EVENTS_LONG_BUFFER_FULL,
                        EventType.MONITOR_FAILURE_P1_COMMUNICATION, EventType.MONITOR_SHORT_DETECTED,
                        EventType.MONITOR_SHORT_RESOLVED, EventType.MONITOR_DOOR_OPENED, EventType.MONITOR_DOOR_CLOSED,
                        EventType.MONITOR_EVENTS_TEST_RELAY_ON, EventType.MONITOR_EVENTS_TEST_RELAY_OFF,
                        EventType.MONITOR_EVENTS_LOSS_OF_POWER, EventType.MONITOR_EVENTS_LOCAL_MODE,
                        EventType.MONITOR_EVENTS_REMOTE_MODE, EventType.NTP_SERVER_NOT_REACH,
                        EventType.NTP_SYNC_ALARM_OFFSET, EventType.NTP_SYNC_MAX_OFFSET, EventType.NTP_SYNC_SUCCESS)));
        mapping.put(EventNotificationType.FIRMWARE_EVENTS,
                EnumSet.copyOf(Arrays.asList(EventType.FIRMWARE_EVENTS_ACTIVATING,
                        EventType.FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND, EventType.FIRMWARE_EVENTS_DOWNLOAD_FAILED,
                        EventType.FIRMWARE_EVENTS_CONFIGURATION_CHANGED, EventType.FIRMWARE_EVENTS_DOWNLOAD_SUCCESS,
                        EventType.CA_FILE_EVENTS_ACTIVATING, EventType.CA_FILE_FIRMWARE_EVENTS_DOWNLOAD_NOT_FOUND,
                        EventType.CA_FILE_EVENTS_DOWNLOAD_FAILED, EventType.CA_FILE_EVENTS_DOWNLOAD_SUCCESS,
                        EventType.ALARM_NOTIFICATION, EventType.SMS_NOTIFICATION)));
        mapping.put(EventNotificationType.COMM_EVENTS,
                EnumSet.range(EventType.COMM_EVENTS_ALTERNATIVE_CHANNEL, EventType.COMM_EVENTS_RECOVERED_CHANNEL));
        mapping.put(EventNotificationType.SECURITY_EVENTS,
                EnumSet.copyOf(Arrays.asList(EventType.SECURITY_EVENTS_OUT_OF_SEQUENCE,
                        EventType.SECURITY_EVENTS_OSLP_VERIFICATION_FAILED,
                        EventType.SECURITY_EVENTS_INVALID_CERTIFICATE, EventType.AUTHENTICATION_FAIL)));
    }

    public Map<EventNotificationType, Set<EventType>> getMapping() {
        return mapping;
    }

    public void printMapping() {
        LOGGER.info("EventNotificationType to EventType mapping, size: {}", mapping.size());
        for (final Map.Entry<EventNotificationType, Set<EventType>> entry : mapping.entrySet()) {
            LOGGER.info("  EventNotificationType: {} maps to the following EventTypes.", entry.getKey().name());
            for (final EventType eventType : entry.getValue()) {
                LOGGER.info("    EventType: {}", eventType.name());
            }
        }
    }
}
