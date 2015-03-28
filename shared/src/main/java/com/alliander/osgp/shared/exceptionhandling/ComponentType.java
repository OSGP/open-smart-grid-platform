package com.alliander.osgp.shared.exceptionhandling;

/**
 * Enum having list of ExceptionsCodes
 */

public enum ComponentType {
    WS_ADMIN("Osgp Web Service Adapter Admin"),
    WS_CORE("Osgp Web Service Adapter Core"),
    WS_PUBLIC_LIGHTING("Osgp Web Service Adapter Public Lighting"),
    WS_TARIFF_SWITCHING("Osgp Web Service Adapter Tariff Switching"),
    DOMAIN_ADMIN("Osgp Domain Adapter Admin"),
    DOMAIN_CORE("Osgp Domain Adapter Core"),
    DOMAIN_PUBLIC_LIGHTING("Osgp Domain Adapter Public Lighting"),
    DOMAIN_TARIFF_SWITCHING("Osgp Domain Adapter Tariff Switching"),
    OSGP_CORE("Osgp Core"),
    PROTOCOL_OSLP("Osgp Protocol Adapter OSLP"),

    UNKNOWN("Unknown");

    private ComponentType(final String componentName) {

    }
}
