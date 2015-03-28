package com.alliander.osgp.shared.infra.jms;

public enum ResponseMessageResultType {
    OK("OK"),
    NOT_FOUND("NOT FOUND"),
    NOT_OK("NOT OK");

    private String value;

    private ResponseMessageResultType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
