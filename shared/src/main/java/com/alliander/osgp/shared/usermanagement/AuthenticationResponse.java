/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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
