/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.publiclighting.infra.jms;

/**
 * Enumeration of public lighting request message types
 */
public enum PublicLightingRequestMessageType {
    SET_LIGHT,
    GET_LIGHT_STATUS,
    SET_LIGHT_SCHEDULE,
    RESUME_SCHEDULE,
    SET_TRANSITION,
    GET_ACTUAL_POWER_USAGE,
    GET_POWER_USAGE_HISTORY,
    SET_LIGHT_MEASUREMENT_DEVICE
}
