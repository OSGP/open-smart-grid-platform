/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.automatictests.platform;

import org.joda.time.DateTime;

import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.LongTermIntervalType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.TransitionType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.MeterType;
import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.RelayType;
import com.alliander.osgp.oslp.Oslp.LightType;
import com.alliander.osgp.oslp.Oslp.LinkType;
import com.alliander.osgp.oslp.Oslp.Status;

/**
 * Defaults within the database.
 */
public class Defaults {

    // Values
    public static final String ORGANIZATION_DESCRIPTION = "Test Organization";
    public static final String ORGANIZATION_IDENTIFICATION = "test-org";
    public static final String USER_NAME = "Cucumber";
    public static final String PREFIX = "MAA";
    public static final String ORGANIZATION_DOMAINS = "COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING";

    public static final String MANUFACTURER_ID = "Test";
    public static final String MANUFACTURER_NAME = "Test Manufacturer";
    public static final Boolean MANUFACTURER_USE_PREFIX = false;
    public static final String DEVICE_MODEL_MODEL_CODE = "TestModel";
    public static final String DEVICE_MODEL_DESCRIPTION = "Test Model";
    public static final String DEVICE_IDENTIFICATION = "TD01";

    public static final Boolean DEVICE_MODEL_METERED = true;

    public static final String SMART_METER_DEVICE_IDENTIFICATION = "TEST1024000000001";
    public static final String SMART_METER_GAS_DEVICE_IDENTIFICATION = "TESTG102400000001";
    public static final String DLMS_DEFAULT_COMMUNICATION_METHOD = "GPRS";
    public static final Boolean DLMS_DEFAULT_IP_ADDRESS_IS_STATIC = true;
    public static final long DLMS_DEFAULT_PORT = 1024L;
    public static final long DLMS_DEFAULT_LOGICAL_ID = 1L;
    public static final Boolean DLMS_DEFAULT_HSL5_ACTIVE = true;
    public static final String DLMS_DEFAULT_DEVICE_TYPE = "SMART_METER_E";

    public static final String DEVICE_TYPE = "OSLP";
    public static final String PROTOCOL = "OSLP";
    public static final String PROTOCOL_VERSION = "1.0";
    public static final Long PROTOCOL_INFO_ID = new java.util.Random().nextLong();
    public static final Long DEVICE_ID = new java.util.Random().nextLong();
    public static final Boolean IS_ACTIVATED = true;
    public static final Boolean ACTIVE = true;
    public static final String ALIAS = "";
    public static final String CONTAINER_CITY = "Maastricht";
    public static final String CONTAINER_POSTALCODE = "6221 BT";
    public static final String CONTAINER_STREET = "Stationsplein";
    public static final String CONTAINER_NUMBER = "12";
    public static final String CONTAINER_MUNICIPALITY = "Gemeente Maastricht";
    // The following GPS coordinates is the GPS location to Stationsplein 12,
    // 6221 BT Maastricht, Netherlands. Source:
    // (http://www.gps-coordinates.net/).
    public static final Float LATITUDE = new Float(50.848274);
    public static final Float LONGITUDE = new Float(5.706087);
    public static final Short CHANNEL = new Short((short) 1);
    public static final Short PAGE = 0;

    // Expected values
    public static final String EXPECTED_RESULT_OK = "OK";

    public static final Boolean HASSCHEDULE = false;

    public static final Boolean PUBLICKEYPRESENT = true;
    public static final String PUBLIC_KEY = "123456abcdef";

    public static final String PERIOD_TYPE = "INTERVAL";
    public static final String BEGIN_DATE = "";
    public static final String END_DATE = "";
    public static final Boolean INDEBUGMODE = false;
    public static final Boolean EVENTS_NODELIST_EXPECTED = false;

    // Types
    public static final String SMART_METER_E = "SMART_METER_E";
    public static final String SMART_METER_G = "SMART_METER_G";
    public static final int PAGE_SIZE = 25;
    public static final Boolean USE_PAGES = true;
    public static final Boolean ORGANIZATION_ENABLED = true;
    public static final String ORGANIZATION_NAME = "Test organization";
    public static final String ORGANIZATION_PREFIX = "cgi";

    public static final com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup PLATFORM_FUNCTION_GROUP = com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup.ADMIN;
    public static final String DOMAINS = "COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING";
    public static final String NEW_ORGANIZATION_NAME = "New Organization";
    public static final String NEW_ORGANIZATION_IDENTIFICATION = "NewOrganization";
    public static final com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup NEW_ORGANIZATION_PLATFORMFUNCTIONGROUP = com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup.ADMIN;

    public static final String OWNER = "test-org";
    public static final Boolean ACTIVATED = true;
    public static final String DEVICE_MODEL_MANUFACTURER = "Kaif";
    public static final Integer INDEX = 0;
    public static final Boolean ISIMMEDIATE = false;
    public static final Integer DIMVALUE = 100;
    public static final Boolean ON = true;
    public static final TransitionType TRANSITION_TYPE = TransitionType.DAY_NIGHT;
    public static final Integer INTERNALID = 0;
    public static final Integer EXTERNALID = 0;
    public static final String EVENTNOTIFICATIONS = "";
    public static final LinkType PREFERRED_LINKTYPE = LinkType.LINK_NOT_SET;
    public static final LinkType ACTUAL_LINKTYPE = LinkType.LINK_NOT_SET;
    public static final LightType LIGHTTYPE = LightType.LT_NOT_SET;
    public static final Status STATUS = Status.OK;
    public static final String LIGHTVALUES = "";
    public static final String EVENTNOTIFICATIONTYPES = "";
    public static final String DEVICE_OUTPUT_SETTING_ALIAS = "Continues burner";
    public static final Integer DEVICE_OUTPUT_SETTING_INTERNALID = 1;
    public static final Integer DEVICE_OUTPUT_SETTING_EXTERNALID = 1;
    public static final RelayType DEVICE_OUTPUT_SETTING_RELAY_TYPE = RelayType.LIGHT;
    public static final Boolean FILESTORAGE = true;
    public static final PlatformDomain PLATFORMDOMAIN = PlatformDomain.COMMON;
    public static final String SUPPLIER = "Kaifa";
    public static final DateTime TECHNICAL_INSTALLATION_DATE = DateTime.now().minusDays(10);
    
    public static final OsgpResultType PUBLICLIGHTING_STATUS = OsgpResultType.OK;
    public static final String PUBLICLIGHTING_DESCRIPTION = "";
    public static final Integer ACTUAL_CONSUMED_POWER = 48;
    public static final Integer ACTUAL_CONSUMED_ENERGY = 96;
    public static final MeterType METER_TYPE = MeterType.AUX;
    public static final String RECORD_TIME = "";
    public static final Integer TOTAL_LIGHTING_HOURS = 144;
    public static final Integer ACTUAL_CURRENT1 = 1;
    public static final Integer ACTUAL_CURRENT2 = 2;
    public static final Integer ACTUAL_CURRENT3 = 3;
    public static final Integer ACTUAL_POWER1 = 1;
    public static final Integer ACTUAL_POWER2 = 2;
    public static final Integer ACTUAL_POWER3 = 3;
    public static final Integer AVERAGE_POWER_FACTOR1 = 1;
    public static final Integer AVERAGE_POWER_FACTOR2 = 2;
    public static final Integer AVERAGE_POWER_FACTOR3 = 3;
    public static final com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.HistoryTermType HISTORY_TERM_TYPE = com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.HistoryTermType.SHORT;
    public static final com.alliander.osgp.oslp.Oslp.HistoryTermType OSLP_HISTORY_TERM_TYPE = com.alliander.osgp.oslp.Oslp.HistoryTermType.Short;
    public static final Integer SHORT_INTERVAL = null;
    public static final LongTermIntervalType INTERVAL_TYPE = LongTermIntervalType.DAYS;
    public static final Integer LONG_INTERVAL = null;
}
