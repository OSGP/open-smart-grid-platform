/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.publiclighting;

import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.TransitionType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.HistoryTermType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.MeterType;
import com.alliander.osgp.oslp.Oslp.LightType;
import com.alliander.osgp.oslp.Oslp.LinkType;
import com.alliander.osgp.oslp.Oslp.Status;

public class PlatformPubliclightingDefaults extends com.alliander.osgp.cucumber.platform.PlatformDefaults {
    public static final LinkType DEFAULT_ACTUAL_LINKTYPE = LinkType.LINK_NOT_SET;
    public static final HistoryTermType DEFAULT_HISTORY_TERM_TYPE = HistoryTermType.SHORT;
    public static final MeterType DEFAULT_METER_TYPE = MeterType.AUX;
    public static final com.alliander.osgp.oslp.Oslp.HistoryTermType DEFAULT_OSLP_HISTORY_TERM_TYPE = com.alliander.osgp.oslp.Oslp.HistoryTermType.Short;
    public static final Status DEFAULT_STATUS = Status.OK;
    public static final TransitionType DEFAULT_TRANSITION_TYPE = TransitionType.DAY_NIGHT;
    public static final OsgpResultType DEFAULT_PUBLICLIGHTING_STATUS = OsgpResultType.OK;
    public static final LinkType DEFAULT_PREFERRED_LINKTYPE = LinkType.LINK_NOT_SET;
    public static final LightType DEFAULT_LIGHTTYPE = LightType.LT_NOT_SET;
    public static final String FIRMARE_DOMAIN = "localhost";
    public static final String FIRMWARE_URL = "firmware/" + FIRMWARE_IDENTIFICATION;
}
