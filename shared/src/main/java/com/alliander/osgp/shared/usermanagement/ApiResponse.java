package com.alliander.osgp.shared.usermanagement;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiResponse {

    @JsonProperty("feedbackMessage")
    private String feedbackMessage;

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonCreator
    public ApiResponse() {

    }

    public String getFeedbackMessage() {
        return this.feedbackMessage;
    }

    public void setFeedbackMessage(final String message) {
        this.feedbackMessage = message;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(final String message) {
        this.errorMessage = message;
    }

    public void setApiResponseToSuccessful() {
        this.setFeedbackMessage("OK");
        this.setErrorMessage(StringUtils.EMPTY);
    }

    public void setApiResponseToError(final Exception e) {
        this.setFeedbackMessage(StringUtils.EMPTY);
        this.setErrorMessage(e.getMessage() + " | " + e.getCause());
    }
}
