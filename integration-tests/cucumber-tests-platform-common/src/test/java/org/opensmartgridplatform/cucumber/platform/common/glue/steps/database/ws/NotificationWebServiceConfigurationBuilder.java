/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws;

import org.opensmartgridplatform.adapter.ws.domain.entities.ApplicationDataLookupKey;
import org.opensmartgridplatform.adapter.ws.domain.entities.NotificationWebServiceConfiguration;

public class NotificationWebServiceConfigurationBuilder {

  private String organisationIdentification = "test-org";
  private String applicationName = "application-name";
  private String marshallerContextPath = "org.opensmartgridplatform.adapter.ws.schema";
  private String targetUri = "http://localhost:8088/notifications";
  private String keyStoreType = "pkcs12";
  private String keyStoreLocation = "/etc/ssl/certs/OSGP.pfx";
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

  public NotificationWebServiceConfiguration build() {
    final NotificationWebServiceConfiguration config =
        new NotificationWebServiceConfiguration(
            new ApplicationDataLookupKey(this.organisationIdentification, this.applicationName),
            this.marshallerContextPath,
            this.targetUri);
    config.useKeyStore(this.keyStoreType, this.keyStoreLocation, this.keyStorePassword);
    config.useTrustStore(this.trustStoreType, this.trustStoreLocation, this.trustStorePassword);
    config.setMaxConnectionsPerRoute(this.maxConnectionsPerRoute);
    config.setMaxConnectionsTotal(this.maxConnectionsTotal);
    config.setConnectionTimeout(this.connectionTimeout);
    config.useCircuitBreaker(
        this.circuitBreakerThreshold,
        this.circuitBreakerDurationInitial,
        this.circuitBreakerDurationMaximum,
        this.circuitBreakerDurationMultiplier);
    return config;
  }

  public NotificationWebServiceConfigurationBuilder withOrganisationIdentification(
      final String organisationIdentification) {
    this.organisationIdentification = organisationIdentification;
    return this;
  }

  public NotificationWebServiceConfigurationBuilder withApplicationName(
      final String applicationName) {
    this.applicationName = applicationName;
    return this;
  }

  public NotificationWebServiceConfigurationBuilder withMarshallerContextPath(
      final String marshallerContextPath) {
    this.marshallerContextPath = marshallerContextPath;
    return this;
  }

  public NotificationWebServiceConfigurationBuilder withTargetUri(final String targetUri) {
    this.targetUri = targetUri;
    return this;
  }

  public NotificationWebServiceConfigurationBuilder withKeyStoreConfig(
      final String type, final String location, final String password) {

    this.keyStoreType = type;
    this.keyStoreLocation = location;
    this.keyStorePassword = password;
    return this;
  }

  public NotificationWebServiceConfigurationBuilder withoutKeyStoreConfig() {
    this.keyStoreType = null;
    this.keyStoreLocation = null;
    this.keyStorePassword = null;
    return this;
  }

  public NotificationWebServiceConfigurationBuilder withTrustStoreConfig(
      final String type, final String location, final String password) {

    this.trustStoreType = type;
    this.trustStoreLocation = location;
    this.trustStorePassword = password;
    return this;
  }

  public NotificationWebServiceConfigurationBuilder withoutTrustStoreConfig() {

    this.trustStoreType = null;
    this.trustStoreLocation = null;
    this.trustStorePassword = null;
    return this;
  }

  public NotificationWebServiceConfigurationBuilder withMaxConnectionsPerRoute(
      final int maxConnectionsPerRoute) {
    this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    return this;
  }

  public NotificationWebServiceConfigurationBuilder withMaxConnectionsTotal(
      final int maxConnectionsTotal) {
    this.maxConnectionsTotal = maxConnectionsTotal;
    return this;
  }

  public NotificationWebServiceConfigurationBuilder withConnectionTimeout(
      final int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
    return this;
  }

  public NotificationWebServiceConfigurationBuilder withCircuitBreakerConfig(
      final int threshold,
      final int durationInitial,
      final int durationMaximum,
      final int durationMultiplier) {

    this.circuitBreakerThreshold = threshold;
    this.circuitBreakerDurationInitial = durationInitial;
    this.circuitBreakerDurationMaximum = durationMaximum;
    this.circuitBreakerDurationMultiplier = durationMultiplier;
    return this;
  }

  public NotificationWebServiceConfigurationBuilder withoutCircuitBreakerConfig() {

    this.circuitBreakerThreshold = 0;
    this.circuitBreakerDurationInitial = 0;
    this.circuitBreakerDurationMaximum = 0;
    this.circuitBreakerDurationMultiplier = 0;
    return this;
  }
}
