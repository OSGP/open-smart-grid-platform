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

    // Authorization exceptions
    UNAUTHORIZED(301, "Not authorized to perform this action."),
    EXISTING_DEVICE_AUTHORIZATIONS(302, "Device authorizations are still present for the current organisation."),
    METHOD_NOT_ALLOWED_FOR_OWNER(303, "Not allowed to set owner via this method."),

    // Other exceptions
    VALIDATION_ERROR(401, "Validation error."),
    TARIFF_SCHEDULE_NOT_ALLOWED_FOR_PSLD(402, "Set tariff schedule is not allowed for PSLD."),
    ARGUMENT_NULL(403, "Argument null.")

    ;

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