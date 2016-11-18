/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps;

import org.joda.time.DateTime;

public class Defaults {

    // Defaults values dlms_device
    public static final String DEFAULT_DEVICE_IDENTIFICATION = null;
    public static final Long DEFAULT_VERSION = 0L;
    public static final String DEFAULT_ICC_ID = "iccid";
    public static final String DEFAULT_COMMUNICATION_PROVIDER = "KPN";
    public static final String DEFAULT_COMMUNICATION_METHOD = "GPRS";
    public static final boolean DEFAULT_HLS3ACTIVE = false;
    public static final boolean DEFAULT_HLS4ACTIVE = false;
    public static final boolean DEFAULT_HLS5ACTIVE = true;
    public static final Long DEFAULT_CHALLENGE_LENGTH = null;
    public static final boolean DEFAULT_WITH_LIST_SUPPORTED = false;
    public static final boolean DEFAULT_SELECTIVE_ACCESS_SUPPORTED = false;
    public static final boolean DEFAULT_IP_ADDRESS_IS_STATIC = true;
    public static final Long DEFAULT_PORT = 1024L;
    public static final Long DEFAULT_CLIENT_ID = null;
    public static final Long DEFAULT_LOGICAL_ID = 1L;
    public static final boolean DEFAULT_IN_DEBUG_MODE = false;

    // Default values security_key
    public static final String KEY_DLMS_DEVICE_ID = null;
    public static final String KEY_SECURITY_KEY_TYPE_M = "";
    public static final String KEY_SECURITY_KEY_TYPE_A = "";
    public static final String KEY_SECURITY_KEY_TYPE_E = "";
    public static final DateTime KEY_VALID_FROM = DateTime.now();
    public static final DateTime KEY_VALID_TO = null;
    public static final String KEY_SECURITY_KEY_M = "";
    public static final String KEY_SECURITY_KEY_A = "";
    public static final String KEY_SECURITY_KEY_E = "";

    // Types
    public static final String SMART_METER_E = "SMART_METER_E";
    public static final String SMART_METER_G = "SMART_METER_G";
}
