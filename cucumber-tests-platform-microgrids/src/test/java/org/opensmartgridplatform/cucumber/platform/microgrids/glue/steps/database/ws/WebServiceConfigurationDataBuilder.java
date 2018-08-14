/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.microgrids.glue.steps.database.ws;

import org.opensmartgridplatform.adapter.ws.domain.entities.WebServiceConfigurationData;
import org.opensmartgridplatform.adapter.ws.domain.entities.WebServiceConfigurationData.WebServiceConfigurationDataId;

public class WebServiceConfigurationDataBuilder {

    private String organisationIdentification = "test-org";
    private String applicationName = "ZownStream";
    private String marshallerContextPath = "org.opensmartgridplatform.adapter.ws.schema.microgrids.notification";
    private String targetUri = "http://localhost:8088/notifications";
    private String keyStoreType = "pkcs12";
    private String keyStoreLocation = "/etc/ssl/certs/test-org.pfx";
    private String keyStorePassword = "1234";
    private String trustStoreType = "jks";
    private String trustStoreLocation = "/etc/ssl/certs/trust.jks";
    private String trustStorePassword = "123456";
    private int maxConnectionsPerRoute = 10;
    private int maxConnectionsTotal = 20;
    private int connectionTimeout = 10_000;
    private int circuitBreakerThreshold = 3;
    private int circuitBreakerDurationInitial = 15_000;
    private int circuitBreakerDurationMaximum = 600_000;
    private int circuitBreakerDurationMultiplier = 4;

    public WebServiceConfigurationData build() {
        final WebServiceConfigurationData config = new WebServiceConfigurationData(
                new WebServiceConfigurationDataId(this.organisationIdentification, this.applicationName),
                this.marshallerContextPath, this.targetUri);
        config.useKeyStore(this.keyStoreType, this.keyStoreLocation, this.keyStorePassword);
        config.useTrustStore(this.trustStoreType, this.trustStoreLocation, this.trustStorePassword);
        config.setMaxConnectionsPerRoute(this.maxConnectionsPerRoute);
        config.setMaxConnectionsTotal(this.maxConnectionsTotal);
        config.setConnectionTimeout(this.connectionTimeout);
        config.useCircuitBreaker(this.circuitBreakerThreshold, this.circuitBreakerDurationInitial,
                this.circuitBreakerDurationMaximum, this.circuitBreakerDurationMultiplier);
        return config;
    }

    public WebServiceConfigurationDataBuilder withOrganisationIdentification(final String organisationIdentification) {
        this.organisationIdentification = organisationIdentification;
        return this;
    }

    public WebServiceConfigurationDataBuilder withApplicationName(final String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public WebServiceConfigurationDataBuilder withMarshallerContextPath(final String marshallerContextPath) {
        this.marshallerContextPath = marshallerContextPath;
        return this;
    }

    public WebServiceConfigurationDataBuilder withKeyStoreConfig(final String type, final String location,
            final String password) {

        this.keyStoreType = type;
        this.keyStoreLocation = location;
        this.keyStorePassword = password;
        return this;
    }

    public WebServiceConfigurationDataBuilder withoutKeyStoreConfig() {
        this.keyStoreType = null;
        this.keyStoreLocation = null;
        this.keyStorePassword = null;
        return this;
    }

    public WebServiceConfigurationDataBuilder withTrustStoreConfig(final String type, final String location,
            final String password) {

        this.trustStoreType = type;
        this.trustStoreLocation = location;
        this.trustStorePassword = password;
        return this;
    }

    public WebServiceConfigurationDataBuilder withoutTrustStoreConfig() {

        this.trustStoreType = null;
        this.trustStoreLocation = null;
        this.trustStorePassword = null;
        return this;
    }

    public WebServiceConfigurationDataBuilder withMaxConnectionsPerRoute(final int maxConnectionsPerRoute) {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
        return this;
    }

    public WebServiceConfigurationDataBuilder withMaxConnectionsTotal(final int maxConnectionsTotal) {
        this.maxConnectionsTotal = maxConnectionsTotal;
        return this;
    }

    public WebServiceConfigurationDataBuilder withConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public WebServiceConfigurationDataBuilder withCircuitBreakerConfig(final int threshold, final int durationInitial,
            final int durationMaximum, final int durationMultiplier) {

        this.circuitBreakerThreshold = threshold;
        this.circuitBreakerDurationInitial = durationInitial;
        this.circuitBreakerDurationMaximum = durationMaximum;
        this.circuitBreakerDurationMultiplier = durationMultiplier;
        return this;
    }

    public WebServiceConfigurationDataBuilder withoutCircuitBreakerConfig() {

        this.circuitBreakerThreshold = 0;
        this.circuitBreakerDurationInitial = 0;
        this.circuitBreakerDurationMaximum = 0;
        this.circuitBreakerDurationMultiplier = 0;
        return this;
    }

}
