package com.alliander.osgp.shared.usermanagement;

public class AuthenticationResponse {

    private String feedbackMessage;
    private String errorMessage;
    private String token;
    private String userName;

    public AuthenticationResponse() {

    }

    public String getFeedbackMessage() {
        return this.feedbackMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String getToken() {
        return this.token;
    }

    public String getUserName() {
        return this.userName;
    }
}
