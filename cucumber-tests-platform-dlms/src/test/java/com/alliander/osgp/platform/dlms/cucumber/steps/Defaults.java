/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceModel;

public class Defaults {

    public static final String ORGANISATION_IDENTIFICATION = "test-org";

    public static final String DEVICE_IDENTIFICATION = null;
    public static final Long VERSION = 0L;
    public static final String ICC_ID = "iccid";
    public static final String COMMUNICATION_PROVIDER = "KPN";
    public static final String COMMUNICATION_METHOD = "GPRS";
    public static final boolean HLS3ACTIVE = false;
    public static final boolean HLS4ACTIVE = false;
    public static final boolean HLS5ACTIVE = true;
    public static final Integer CHALLENGE_LENGTH = null;
    public static final boolean WITH_LIST_SUPPORTED = false;
    public static final boolean SELECTIVE_ACCESS_SUPPORTED = false;
    public static final boolean IP_ADDRESS_IS_STATIC = true;
    public static final Long PORT = 1024L;
    public static final Long CLIENT_ID = null;
    public static final Long LOGICAL_ID = 1L;
    public static final boolean IN_DEBUG_MODE = false;

    public static final String SECURITY_KEY_TYPE_M = "E_METER_MASTER";
    public static final String SECURITY_KEY_TYPE_A = "E_METER_AUTHENTICATION";
    public static final String SECURITY_KEY_TYPE_E = "E_METER_ENCRYPTION";
    public static final Date VALID_TO = null;

    public static final String SECURITY_KEY_M = "bc082efed278e1bbebddc0431877d4fa16374b00e96dd102beab666dcb72efbd1f0b868412497f6d3d0c62caa4700585";
    public static final String SECURITY_KEY_A = "bc082efed278e1bbebddc0431877d4fae80fa4e72925b6ad0bc67c84b8721598eda8458bcc1b2827fe6e5e7918ce22fd";
    public static final String SECURITY_KEY_E = "bc082efed278e1bbebddc0431877d4fa2df7728229f3e03c57b2549142b40d047b35011dbf9f77ad91db5fe6f19a7b9c";
    public static final String SECURITY_KEY_G_ENCRYPTION = "bc082efed278e1bbebddc0431877d4fa2df7728229f3e03c57b2549142b40d047b35011dbf9f77ad91db5fe6f19a7b9c";
    public static final String SECURITY_KEY_G_MASTER = "bc082efed278e1bbebddc0431877d4fa2df7728229f3e03c57b2549142b40d047b35011dbf9f77ad91db5fe6f19a7b9c";

    public static final String SMART_METER_E = "SMART_METER_E";
    public static final String SMART_METER_G = "SMART_METER_G";
    public static final String DEVICE_TYPE = null;
    public static final boolean IS_ACTIVATED = true;
    public static final String CONTAINER_CITY = null;
    public static final String CONTAINER_STREET = null;
    public static final Float GPS_LATITUDE = null;
    public static final Float GPS_LONGITUDE = null;
    public static final String CONTAINER_POSTAL_CODE = null;
    public static final String CONTAINER_NUMBER = null;
    public static final String PROTOCOL = "DSMR";
    public static final String PROTOCOL_VERSION = "4.2.2";

    public static final InetAddress NETWORK_ADDRESS;
    public static final String CONTAINER_MUNICIPALITY = null;
    public static final String ALIAS = null;
    public static final boolean IN_MAINTENANCE = false;
    public static final Device GATEWAY_DEVICE = null;
    public static final DeviceModel DEVICE_MODEL = null;
    public static final Date TECHNICAL_INSTALLATION_DATE = new Date();
    public static final boolean IS_ACTIVE = true;

    static {
        InetAddress localhost;
        try {
            localhost = InetAddress.getLocalHost();
        } catch (final UnknownHostException e) {
            localhost = null;
        }
        NETWORK_ADDRESS = localhost;
    }
}
