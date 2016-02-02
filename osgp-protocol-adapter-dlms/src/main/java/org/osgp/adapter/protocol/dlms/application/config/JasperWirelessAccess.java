/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.config;

public class JasperWirelessAccess {

    private String smsUri;
    private String licenseKey;
    private String username;
    private String password;
    private String apiVersion;

    public JasperWirelessAccess(final String smsUri, final String licenseKey, final String username,
            final String password, final String apiVersion) {
        this.smsUri = smsUri;
        this.licenseKey = licenseKey;
        this.username = username;
        this.password = password;
        this.apiVersion = apiVersion;
    }

    public String getSmsUri() {
        return this.smsUri;
    }

    public String getLicenseKey() {
        return this.licenseKey;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getApiVersion() {
        return this.apiVersion;
    }
}
