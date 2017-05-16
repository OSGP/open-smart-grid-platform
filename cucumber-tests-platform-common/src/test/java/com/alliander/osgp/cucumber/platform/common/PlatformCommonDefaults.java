/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.common;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.LightType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.LinkType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.LongTermIntervalType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.MeterType;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceFunctionGroup;

public class PlatformCommonDefaults extends com.alliander.osgp.cucumber.platform.PlatformDefaults {

    public static final LightType CONFIGURATION_LIGHTTYPE = null;
    public static final MeterType CONFIGURATION_METER_TYPE = null;
    public static final LinkType CONFIGURATION_PREFERRED_LINKTYPE = null;
    public static final LightType DEFAULT_CONFIGURATION_LIGHTTYPE = LightType.RELAY;
    public static final MeterType DEFAULT_CONFIGURATION_METER_TYPE = MeterType.AUX;
    public static final LinkType DEFAULT_CONFIGURATION_PREFERRED_LINKTYPE = LinkType.ETHERNET;
    public static final LongTermIntervalType INTERVAL_TYPE = LongTermIntervalType.DAYS;
    public static final LongTermIntervalType DEFAULT_INTERVAL_TYPE = LongTermIntervalType.DAYS;
    public static final PlatformFunctionGroup DEFAULT_NEW_ORGANIZATION_PLATFORMFUNCTIONGROUP = PlatformFunctionGroup.ADMIN;
    public static final DeviceFunctionGroup DEVICE_FUNCTION_GROUP = DeviceFunctionGroup.OWNER;
}
