package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class ActionValueObjectResponseDto implements Serializable {

    private static final long serialVersionUID = -6579443565899923397L;
    private String exception;
    private String resultString;

    public ActionValueObjectResponseDto() {
        // default constructor
    }

    public ActionValueObjectResponseDto(final Exception exception, final String resultString) {
        this.exception = exception.toString();
        this.resultString = resultString;
    }

    public String getException() {
        return this.exception;
    }

    public void setException(final Exception exception) {
        this.exception = exception.toString();
    }

    public boolean hasException() {
        return this.exception != null;
    }

    public String getResultString() {
        return this.resultString;
    }

    public void setResultString(final String resultString) {
        this.resultString = resultString;
    }

}
