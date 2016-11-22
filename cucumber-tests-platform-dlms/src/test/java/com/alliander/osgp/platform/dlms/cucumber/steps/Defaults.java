/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps;

import java.util.Date;

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
    public static final String DEFAULT_DLMS_DEVICE_ID = null;

    public static final String DEFAULT_SECURITY_KEY_TYPE_M = "E_METER_MASTER";
    public static final String DEFAULT_SECURITY_KEY_TYPE_A = "E_METER_AUTHENTICATION";
    public static final String DEFAULT_SECURITY_KEY_TYPE_E = "E_METER_ENCRYPTION";
    public static final Date DEFAULT_VALID_FROM = new Date();
    public static final Date DEFAULT_VALID_TO = null;

    public static final String DEFAULT_SECURITY_KEY_M = "bc082efed278e1bbebddc0431877d4fa16374b00e96dd102beab666dcb72efbd1f0b868412497f6d3d0c62caa4700585";
    public static final String DEFAULT_SECURITY_KEY_A = "bc082efed278e1bbebddc0431877d4fae80fa4e72925b6ad0bc67c84b8721598eda8458bcc1b2827fe6e5e7918ce22fd";
    public static final String DEFAULT_SECURITY_KEY_E = "bc082efed278e1bbebddc0431877d4fa2df7728229f3e03c57b2549142b40d047b35011dbf9f77ad91db5fe6f19a7b9c";

    // Types
    public static final String SMART_METER_E = "SMART_METER_E";
    public static final String SMART_METER_G = "SMART_METER_G";
}
