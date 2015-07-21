/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.usermanagement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {

    private String username;
    private String password;
    private String application;

    @JsonCreator
    public LoginRequest() {

    }

    @JsonCreator
    public LoginRequest(@JsonProperty("username") final String username,
            @JsonProperty("password") final String password, @JsonProperty("application") final String application) {
        this.username = username;
        this.password = password;
        this.application = application;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getApplication() {
        return this.application;
    }
}
