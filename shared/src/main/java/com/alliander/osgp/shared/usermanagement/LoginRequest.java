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
