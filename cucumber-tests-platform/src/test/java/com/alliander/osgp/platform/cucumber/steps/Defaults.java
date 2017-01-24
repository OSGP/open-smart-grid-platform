/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps;

import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.TransitionType;
import com.alliander.osgp.oslp.Oslp.LightType;
import com.alliander.osgp.oslp.Oslp.LinkType;
import com.alliander.osgp.domain.core.valueobjects.RelayType;
import com.alliander.osgp.oslp.Oslp.Status;

/**
 * Defaults within the database.
 */
public class Defaults {

    // Values
    public static final String DEFAULT_ORGANIZATION_DESCRIPTION = "Test Organization";
    public static final String DEFAULT_ORGANIZATION_IDENTIFICATION = "test-org";
    public static final String DEFAULT_USER_NAME = "Cucumber";
    public static final String DEFAULT_PREFIX = "MAA";
    public static final String DEFAULT_MANUFACTURER_ID = "Test";
    public static final String DEFAULT_MANUFACTURER_NAME = "Test Manufacturer";
    public static final Boolean DEFAULT_MANUFACTURER_USE_PREFIX = false;
    public static final String DEFAULT_DEVICE_MODEL_MODEL_CODE = "TestModel";
    public static final String DEFAULT_DEVICE_MODEL_DESCRIPTION = "Test Model";
    public static final String DEFAULT_DEVICE_IDENTIFICATION = "TD01";
    public static final String DEFAULT_SMART_METER_DEVICE_IDENTIFICATION = "TEST1024000000001";
    public static final String DEFAULT_SMART_METER_GAS_DEVICE_IDENTIFICATION = "TESTG102400000001";
    
    public static final Boolean DEFAULT_DEVICE_MODEL_METERED = true;

    public static final String DLMS_DEFAULT_COMMUNICATION_METHOD = "GPRS";
    public static final Boolean DLMS_DEFAULT_IP_ADDRESS_IS_STATIC = true;
    public static final long DLMS_DEFAULT_PORT = 1024L;
    public static final long DLMS_DEFAULT_LOGICAL_ID = 1L;
    public static final Boolean DLMS_DEFAULT_HSL5_ACTIVE = true;
    public static final String DLMS_DEFAULT_DEVICE_TYPE = "SMART_METER_E";

    public static final String DEFAULT_DEVICE_TYPE = "OSLP";
    public static final String DEFAULT_PROTOCOL = "OSLP";
    public static final String DEFAULT_PROTOCOL_VERSION = "1.0";
    public static final Long DEFAULT_PROTOCOL_INFO_ID = new java.util.Random().nextLong();
    public static final Long DEFAULT_DEVICE_ID = new java.util.Random().nextLong();
    public static final Boolean DEFAULT_IS_ACTIVATED = true;
    public static final Boolean DEFAULT_ACTIVE = true;
    public static final String DEFAULT_ALIAS = "";
    public static final String DEFAULT_CONTAINER_CITY = "";
    public static final String DEFAULT_CONTAINER_POSTALCODE = "";
    public static final String DEFAULT_CONTAINER_STREET = "";
    public static final String DEFAULT_CONTAINER_NUMBER = "";
    public static final String DEFAULT_CONTAINER_MUNICIPALITY = "";
    public static final Float DEFAULT_LATITUDE = new Float(0);
    public static final Float DEFAULT_LONGITUDE = new Float(0);
    public static final Short DEFAULT_CHANNEL = new Short((short) 1);
    public static final Short DEFAULT_PAGE = 0;

    // Expected values
    public static final String EXPECTED_RESULT_OK = "OK";

    public static final Boolean DEFAULT_HASSCHEDULE = false;

    public static final Boolean DEFAULT_PUBLICKEYPRESENT = true;
    public static final String DEFAULT_PUBLIC_KEY = "123456abcdef";

    public static final String DEFAULT_PERIOD_TYPE = "INTERVAL";
    public static final String DEFAULT_BEGIN_DATE = "";
    public static final String DEFAULT_END_DATE = "";
    public static final Boolean DEFAULT_INDEBUGMODE = false;
    public static final Boolean EVENTS_NODELIST_EXPECTED = false;

    // Types
    public static final String SMART_METER_E = "SMART_METER_E";
    public static final String SMART_METER_G = "SMART_METER_G";
    public static final int DEFAULT_PAGE_SIZE = 25;
    public static final Boolean DEFAULT_USE_PAGES = true;
    public static final Boolean DEFAULT_ORGANIZATION_ENABLED = true;
    public static final String DEFAULT_ORGANIZATION_NAME = "Test organization";
    public static final String DEFAULT_ORGANIZATION_PREFIX = "cgi";

    public static final com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup DEFAULT_PLATFORM_FUNCTION_GROUP = com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup.ADMIN;
    public static final String DEFAULT_DOMAINS = "COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING";
    public static final String DEFAULT_NEW_ORGANIZATION_NAME = "New Organization";
    public static final String DEFAULT_NEW_ORGANIZATION_IDENTIFICATION = "NewOrganization";
    public static final com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup DEFAULT_NEW_ORGANIZATION_PLATFORMFUNCTIONGROUP = com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup.ADMIN;

    public static final String DEFAULT_OWNER = "test-org";
    public static final Boolean DEFAULT_ACTIVATED = true;
    public static final String DEFAULT_DEVICE_MODEL_MANUFACTURER = "Kaif";
    public static final Integer DEFAULT_INDEX = 0;
    public static final Boolean DEFAULT_ISIMMEDIATE = false;
    public static final Integer DEFAULT_DIMVALUE = 100;
    public static final Boolean DEFAULT_ON = true;
    public static final TransitionType DEFAULT_TRANSITION_TYPE = TransitionType.DAY_NIGHT;
    public static final Integer DEFAULT_INTERNALID = 0;
    public static final Integer DEFAULT_EXTERNALID = 0;
    public static final String DEFAULT_EVENTNOTIFICATIONS = "";
    public static final LinkType DEFAULT_PREFERRED_LINKTYPE = LinkType.LINK_NOT_SET;
    public static final LinkType DEFAULT_ACTUAL_LINKTYPE = LinkType.LINK_NOT_SET;
    public static final LightType DEFAULT_LIGHTTYPE = LightType.LT_NOT_SET;
    public static final Status DEFAULT_STATUS = Status.OK;
    public static final String DEFAULT_LIGHTVALUES = "";
    public static final String DEFAULT_EVENTNOTIFICATIONTYPES = "";
    public static final String DEVICE_OUTPUT_SETTING_ALIAS = "Continues burner";
    public static final Integer DEVICE_OUTPUT_SETTING_INTERNALID = 1;
    public static final Integer DEVICE_OUTPUT_SETTING_EXTERNALID = 1;
    public static final RelayType DEVICE_OUTPUT_SETTING_RELAY_TYPE = RelayType.LIGHT;
}
