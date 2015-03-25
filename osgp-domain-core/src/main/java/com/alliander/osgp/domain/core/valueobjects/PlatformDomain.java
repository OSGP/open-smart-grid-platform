package com.alliander.osgp.domain.core.valueobjects;

/**
 * Enumeration indicating the domain of the platform.
 */
public enum PlatformDomain {
    /**
     * Domain containing all common functionality, like installation and
     * management.
     */
    COMMON,
    /**
     * Domain containing all public lighting functionality, like setlight and
     * light schedules.
     */
    PUBLIC_LIGHTING,
    /**
     * Domain containing all tariff switching functionality, like tariff
     * schedules.
     */
    TARIFF_SWITCHING
}
