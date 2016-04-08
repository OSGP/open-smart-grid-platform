/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.exceptionhandling;

public enum FunctionalExceptionType {
    // Organisation exceptions
    UNKNOWN_ORGANISATION(101, "Organisation does not exist."),
    EXISTING_ORGANISATION(102, "Organisation already exists."),
    EXISTING_ORGANISATION_WITH_SAME_IDENTIFICATION(103, "Another organisation with the same identification already exists."),

    // Device exceptions
    UNKNOWN_DEVICE(201, "Device does not exist."),
    UNREGISTERED_DEVICE(202, "Device is not registered."),
    UNSCHEDULED_DEVICE(203, "Device does not have a schedule."),
    EXISTING_DEVICE(204, "Device already exists."),
    PROTOCOL_UNKNOWN_FOR_DEVICE(205, "Device has no protocol defined."),
    UNKNOWN_PROTOCOL_NAME_OR_VERSION(206, "Device has an unknown protocol name or version."),

    // Authorization exceptions
    UNAUTHORIZED(301, "Not authorized to perform this action."),
    EXISTING_DEVICE_AUTHORIZATIONS(302, "Device authorizations are still present for the current organisation."),
    METHOD_NOT_ALLOWED_FOR_OWNER(303, "Not allowed to set owner via this method."),
    DEVICE_IN_MAINTENANCE(304, "Device currently disabled for maintenance."),

    // Other exceptions
    VALIDATION_ERROR(401, "Validation error."),
    TARIFF_SCHEDULE_NOT_ALLOWED_FOR_PSLD(402, "Set tariff schedule is not allowed for PSLD."),
    ARGUMENT_NULL(403, "Argument null."),
    JMS_TEMPLATE_NULL(404, "JSM template was null. Restart server."),
    UNKNOWN_CORRELATION_UID(405, "No results found for the given correlation uid."),
    ACTION_NOT_ALLOWED_FOR_LIGHT_RELAY(406, "This relay is not configured for light switching"),
    ACTION_NOT_ALLOWED_FOR_TARIFF_RELAY(407, "This relay is not configured for tariff switching"),

    // Manufacturer exceptions
    UNKNOWN_MANUFACTURER(501, "Manufacturer does not exist."),
    EXISTING_MANUFACTURER(502, "Manufacturer already exists.");

    private int code;
    private String message;

    private FunctionalExceptionType(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
