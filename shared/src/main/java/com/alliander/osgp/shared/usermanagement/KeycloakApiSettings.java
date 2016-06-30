package com.alliander.osgp.shared.usermanagement;

public class KeycloakApiSettings {

    private final String baseAddress;
    private final String apiClient;
    private final String apiClientSecret;
    private final String apiUser;
    private final String apiPassword;

    public KeycloakApiSettings(final String baseAddress, final String apiClient, final String apiClientSecret,
            final String apiUser, final String apiPassword) {

        this.baseAddress = baseAddress;
        this.apiClient = apiClient;
        this.apiClientSecret = apiClientSecret;
        this.apiUser = apiUser;
        this.apiPassword = apiPassword;
    }

    @Override
    public String toString() {
        return "KeycloakApiSettings[base=" + this.baseAddress + ", client=" + this.apiClient + ", user=" + this.apiUser
                + "]";
    }

    public String getBaseAddress() {
        return this.baseAddress;
    }

    public String getApiClient() {
        return this.apiClient;
    }

    public String getApiClientSecret() {
        return this.apiClientSecret;
    }

    public String getApiUser() {
        return this.apiUser;
    }

    public String getApiPassword() {
        return this.apiPassword;
    }
}
