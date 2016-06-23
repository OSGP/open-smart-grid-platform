/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.infra.jms;

/**
 * Enumeration of smart metering request message types
 *
 */
public enum SmartMeteringRequestMessageType {
    // insert message types for smart metering

    ADD_METER,
    FIND_EVENTS,
    REQUEST_PERIODIC_METER_DATA,
    SYNCHRONIZE_TIME,
    REQUEST_SPECIAL_DAYS,
    SET_ALARM_NOTIFICATIONS,
    SET_CONFIGURATION_OBJECT,
    SET_ADMINISTRATIVE_STATUS,
    GET_ADMINISTRATIVE_STATUS,
    SET_ACTIVITY_CALENDAR,
    SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER,
    REQUEST_ACTUAL_METER_DATA,
    READ_ALARM_REGISTER,
    REPLACE_KEYS,
    SET_PUSH_SETUP_ALARM,
    SET_PUSH_SETUP_SMS,
    GET_CONFIGURATION_OBJECTS,
    GET_SPECIFIC_CONFIGURATION_OBJECT,
    HANDLE_BUNDLED_ACTIONS,
    GET_ASSOCIATION_LN_OBJECTS,
    GET_FIRMWARE_VERSION;
}
