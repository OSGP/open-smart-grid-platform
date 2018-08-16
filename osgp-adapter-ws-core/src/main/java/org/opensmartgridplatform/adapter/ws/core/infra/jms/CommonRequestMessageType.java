/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.infra.jms;

/**
 * Enumeration of common request message types
 */
public enum CommonRequestMessageType {
    GET_FIRMWARE_VERSION,
    UPDATE_FIRMWARE,
    START_SELF_TEST,
    STOP_SELF_TEST,
    SET_REBOOT,
    SET_EVENT_NOTIFICATIONS,
    SET_CONFIGURATION,
    SET_TRANSITION,
    GET_ACTUAL_POWER_USAGE,
    GET_POWER_USAGE_HISTORY,
    GET_CONFIGURATION,
    GET_STATUS,
    UPDATE_KEY,
    REVOKE_KEY,
    SWITCH_CONFIGURATION_BANK,
    SWITCH_FIRMWARE,
    UPDATE_DEVICE_SSL_CERTIFICATION,
    SET_DEVICE_VERIFICATION_KEY,
    SET_DEVICE_LIFECYCLE_STATUS,
    UPDATE_DEVICE_CDMA_SETTINGS;
}
