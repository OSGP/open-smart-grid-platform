// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationTypeDto;
import org.opensmartgridplatform.dto.valueobjects.EventTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum EventType {
  DIAG_EVENTS_GENERAL(
      1,
      "DIAG_EVENTS_GENERAL",
      EventNotificationTypeDto.DIAG_EVENTS,
      EventTypeDto.DIAG_EVENTS_GENERAL),
  LIGHT_EVENTS_LIGHT_ON(
      2,
      "LIGHT_EVENTS_LIGHT_ON",
      EventNotificationTypeDto.LIGHT_EVENTS,
      EventTypeDto.LIGHT_EVENTS_LIGHT_ON),
  LIGHT_EVENTS_LIGHT_OFF(
      3,
      "LIGHT_EVENTS_LIGHT_OFF",
      EventNotificationTypeDto.LIGHT_EVENTS,
      EventTypeDto.LIGHT_EVENTS_LIGHT_OFF),
  TARIFF_EVENTS_TARIFF_ON(
      4,
      "TARIFF_EVENTS_TARIFF_ON",
      EventNotificationTypeDto.TARIFF_EVENTS,
      EventTypeDto.TARIFF_EVENTS_TARIFF_ON),
  TARIFF_EVENTS_TARIFF_OFF(
      5,
      "TARIFF_EVENTS_TARIFF_OFF",
      EventNotificationTypeDto.TARIFF_EVENTS,
      EventTypeDto.TARIFF_EVENTS_TARIFF_OFF),
  MONITOR_EVENTS_LOSS_OF_POWER(
      6,
      "MONITOR_EVENTS_LOSS_OF_POWER",
      EventNotificationTypeDto.MONITOR_EVENTS,
      EventTypeDto.MONITOR_EVENTS_LOSS_OF_POWER),
  FUNCTION_FIRMWARE_EVENTS_ACTIVATING(
      7,
      "FUNCTION_FIRMWARE_EVENTS_ACTIVATING",
      EventNotificationTypeDto.FIRMWARE_EVENTS,
      EventTypeDto.FIRMWARE_EVENTS_ACTIVATING),
  FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND(
      8,
      "FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND",
      EventNotificationTypeDto.FIRMWARE_EVENTS,
      EventTypeDto.FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND),
  FUNCTION_FIRMWARE_EVENTS_AUTHENTICATION_FAIL(
      9,
      "FUNCTION_FIRMWARE_EVENTS_AUTHENTICATION_FAIL",
      EventNotificationTypeDto.FIRMWARE_EVENTS,
      EventTypeDto.AUTHENTICATION_FAIL),
  FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_FAILED(
      10,
      "FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_FAILED",
      EventNotificationTypeDto.FIRMWARE_EVENTS,
      EventTypeDto.FIRMWARE_EVENTS_DOWNLOAD_FAILED),
  FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_SUCCESS(
      11,
      "FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_SUCCESS",
      EventNotificationTypeDto.FIRMWARE_EVENTS,
      EventTypeDto.FIRMWARE_EVENTS_DOWNLOAD_SUCCESS),
  SECURITY_FIRMWARE_EVENTS_ACTIVATING(
      12,
      "SECURITY_FIRMWARE_EVENTS_ACTIVATING",
      EventNotificationTypeDto.FIRMWARE_EVENTS,
      EventTypeDto.FIRMWARE_EVENTS_ACTIVATING),
  SECURITY_FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND(
      13,
      "SECURITY_FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND",
      EventNotificationTypeDto.FIRMWARE_EVENTS,
      EventTypeDto.FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND),
  SECURITY_FIRMWARE_EVENTS_AUTHENTICATION_FAIL(
      14,
      "SECURITY_FIRMWARE_EVENTS_AUTHENTICATION_FAIL",
      EventNotificationTypeDto.FIRMWARE_EVENTS,
      EventTypeDto.AUTHENTICATION_FAIL),
  SECURITY_FIRMWARE_EVENTS_DOWNLOAD_FAILED(
      15,
      "SECURITY_FIRMWARE_EVENTS_DOWNLOAD_FAILED",
      EventNotificationTypeDto.FIRMWARE_EVENTS,
      EventTypeDto.FIRMWARE_EVENTS_DOWNLOAD_FAILED),
  SECURITY_FIRMWARE_EVENTS_DOWNLOAD_SUCCESS(
      16,
      "SECURITY_FIRMWARE_EVENTS_DOWNLOAD_SUCCESS",
      EventNotificationTypeDto.FIRMWARE_EVENTS,
      EventTypeDto.FIRMWARE_EVENTS_DOWNLOAD_SUCCESS),
  CA_FILE_EVENTS_ACTIVATING(
      17,
      "CA_FILE_EVENTS_ACTIVATING",
      EventNotificationTypeDto.FIRMWARE_EVENTS,
      EventTypeDto.CA_FILE_EVENTS_ACTIVATING),
  CA_FILE_EVENTS_DOWNLOAD_NOTFOUND(
      18,
      "CA_FILE_EVENTS_DOWNLOAD_NOTFOUND",
      EventNotificationTypeDto.FIRMWARE_EVENTS,
      EventTypeDto.CA_FILE_FIRMWARE_EVENTS_DOWNLOAD_NOT_FOUND),
  CA_FILE_EVENTS_AUTHENTICATION_FAIL(
      19,
      "CA_FILE_EVENTS_AUTHENTICATION_FAIL",
      EventNotificationTypeDto.FIRMWARE_EVENTS,
      EventTypeDto.AUTHENTICATION_FAIL),
  CA_FILE_EVENTS_DOWNLOAD_FAILED(
      20,
      "CA_FILE_EVENTS_DOWNLOAD_FAILED",
      EventNotificationTypeDto.FIRMWARE_EVENTS,
      EventTypeDto.CA_FILE_EVENTS_DOWNLOAD_FAILED),
  CA_FILE_EVENTS_DOWNLOAD_SUCCESS(
      21,
      "CA_FILE_EVENTS_DOWNLOAD_SUCCESS",
      EventNotificationTypeDto.FIRMWARE_EVENTS,
      EventTypeDto.CA_FILE_EVENTS_DOWNLOAD_SUCCESS),
  NTP_SERVER_NOT_REACH(
      22,
      "NTP_SERVER_NOT_REACH",
      EventNotificationTypeDto.COMM_EVENTS,
      EventTypeDto.NTP_SERVER_NOT_REACH),
  NTP_SYNC_ALARM_OFFSET(
      23,
      "NTP_SYNC_ALARM_OFFSET",
      EventNotificationTypeDto.COMM_EVENTS,
      EventTypeDto.NTP_SYNC_ALARM_OFFSET),
  NTP_SYNC_MAX_OFFSET(
      24,
      "NTP_SYNC_MAX_OFFSET",
      EventNotificationTypeDto.COMM_EVENTS,
      EventTypeDto.NTP_SYNC_MAX_OFFSET),
  AUTHENTICATION_FAIL(
      25,
      "AUTHENTICATION_FAIL",
      EventNotificationTypeDto.SECURITY_EVENTS,
      EventTypeDto.AUTHENTICATION_FAIL),
  NTP_SYNC_SUCCESS(
      26, "NTP_SYNC_SUCCESS", EventNotificationTypeDto.COMM_EVENTS, EventTypeDto.NTP_SYNC_SUCCESS);

  private static final Logger LOGGER = LoggerFactory.getLogger(EventType.class);

  private final int code;
  private final String description;
  private final EventNotificationTypeDto notificationType;
  private final EventTypeDto osgpEventType;

  EventType(
      final int code,
      final String description,
      final EventNotificationTypeDto notificationType,
      final EventTypeDto osgpEventType) {
    this.code = code;
    this.description = description;
    this.notificationType = notificationType;
    this.osgpEventType = osgpEventType;
  }

  public static EventType forCode(final int code) {
    for (final EventType eventType : EventType.values()) {
      if (code == eventType.code) {
        return eventType;
      }
    }
    throw new IllegalArgumentException("Unknown evnType: " + code);
  }

  public static final String getEventTypeFilterMaskForNotificationTypes(
      final Collection<EventNotificationTypeDto> notificationTypes) {

    if (notificationTypes == null) {
      return "";
    }

    final Set<EventType> eventTypes = EnumSet.noneOf(EventType.class);
    for (final EventType eventType : EventType.values()) {
      if (notificationTypes.contains(eventType.notificationType)) {
        eventTypes.add(eventType);
      }
    }

    return getEventTypeFilterMask(eventTypes);
  }

  public static final String getEventTypeFilterMask(final Collection<EventType> eventTypes) {

    if (eventTypes == null) {
      return "";
    }

    int mask = 0;
    for (final EventType eventType : eventTypes) {
      mask |= eventType.bitmaskValue();
    }

    return Integer.toHexString(mask).toUpperCase();
  }

  public static final Set<EventType> getEventTypesForFilter(final String filter) {
    if (filter == null) {
      return null;
    }
    final int mask = Integer.parseInt(filter, 16);
    final Set<EventType> eventTypes = EnumSet.noneOf(EventType.class);
    for (final EventType eventType : EventType.values()) {
      final int eventTypeBitmask = eventType.bitmaskValue();
      if (eventTypeBitmask == (mask & eventTypeBitmask)) {
        eventTypes.add(eventType);
      }
    }
    return eventTypes;
  }

  public static final Set<EventNotificationTypeDto> getNotificationTypesForFilter(
      final String filter) {
    final Set<EventType> eventTypesForFilter = getEventTypesForFilter(filter);
    if (eventTypesForFilter == null) {
      return null;
    }

    final Set<EventNotificationTypeDto> notificationTypes =
        EnumSet.noneOf(EventNotificationTypeDto.class);
    for (final EventType eventType : eventTypesForFilter) {
      notificationTypes.add(eventType.getNotificationType());
    }

    verifyFilter(filter, notificationTypes);

    return notificationTypes;
  }

  /*
   * Verify that either all or none of the event types per notification type
   * were set, by checking the filter for the observed notification types
   * against the filter used to determine these notification types.
   *
   * Mismatches between OSGP and device firmware (when the firmware supports
   * less or more events than OSGP) are a possible cause for a failing
   * verification.
   */
  private static void verifyFilter(
      final String filter, final Set<EventNotificationTypeDto> notificationTypes) {
    final String verifyFilter = getEventTypeFilterMaskForNotificationTypes(notificationTypes);

    final boolean verified = filter.equals(verifyFilter);
    if (!verified) {
      final int filterMask = Integer.parseInt(filter, 16);
      final int verifyFilterMask = Integer.parseInt(verifyFilter, 16);

      final String additionalDescription =
          filterMask < verifyFilterMask
              ? "The event filter maps to notification types for which some, but not all of the events are filtered."
              : "The event filter has unknown events.";

      LOGGER.warn(
          "Filter ({}) does not match VerifyFilter ({}). {}",
          filter,
          verifyFilter,
          additionalDescription);
    }
  }

  @Override
  public String toString() {
    return String.format(
        "EventType[evnType: %d, description: %s, notificationType: %s, osgpEventType: %s]",
        this.code, this.description, this.notificationType, this.osgpEventType);
  }

  public int getCode() {
    return this.code;
  }

  public int bitmaskValue() {
    return 1 << (this.code - 1);
  }

  public String getDescription() {
    return this.description;
  }

  public EventNotificationTypeDto getNotificationType() {
    return this.notificationType;
  }

  public EventTypeDto getOsgpEventType() {
    return this.osgpEventType;
  }
}
