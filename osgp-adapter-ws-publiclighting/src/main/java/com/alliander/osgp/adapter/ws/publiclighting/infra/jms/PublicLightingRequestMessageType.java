package com.alliander.osgp.adapter.ws.publiclighting.infra.jms;

/**
 * Enumeration of public lighting request message types
 * 
 * @author CGI
 * 
 */
public enum PublicLightingRequestMessageType {
    SET_LIGHT,
    GET_LIGHT_STATUS,
    SET_LIGHT_SCHEDULE,
    RESUME_SCHEDULE,
    SET_TRANSITION,
    GET_ACTUAL_POWER_USAGE,
    GET_POWER_USAGE_HISTORY
}
