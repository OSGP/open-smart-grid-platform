package com.alliander.osgp.dto.valueobjects.smartmetering;

public class ActionValueObjectResponseDto {

    private Exception exception;
    private String resultString;

    public Exception getException() {
        return this.exception;
    }

    public void setException(final Exception exception) {
        this.exception = exception;
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
