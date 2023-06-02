//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.domain.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import org.apache.commons.lang3.StringUtils;

@Entity
public class NotificationWebServiceConfiguration implements Serializable {

  private static final long serialVersionUID = 6961792702877913333L;

  public static final boolean DEFAULT_USE_KEY_STORE = false;
  public static final boolean DEFAULT_USE_TRUST_STORE = false;
  public static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 2;
  public static final int DEFAULT_MAX_CONNECTIONS_TOTAL = 20;
  public static final int DEFAULT_CONNECTION_TIMEOUT = 120000;
  public static final boolean DEFAULT_USE_CIRCUIT_BREAKER = false;
  public static final int DEFAULT_CIRCUIT_BREAKER_THRESHOLD = 3;
  public static final int DEFAULT_CIRCUIT_BREAKER_DURATION_INITIAL = 15000;
  public static final int DEFAULT_CIRCUIT_BREAKER_DURATION_MAXIMUM = 600000;
  public static final int DEFAULT_CIRCUIT_BREAKER_DURATION_MULTIPLIER = 4;

  @EmbeddedId private ApplicationDataLookupKey id;
  private String marshallerContextPath;
  private String targetUri;
  private boolean useKeyStore = DEFAULT_USE_KEY_STORE;
  private String keyStoreType;
  private String keyStoreLocation;
  private String keyStorePassword;
  private boolean useTrustStore = DEFAULT_USE_TRUST_STORE;
  private String trustStoreType;
  private String trustStoreLocation;
  private String trustStorePassword;
  private int maxConnectionsPerRoute = DEFAULT_MAX_CONNECTIONS_PER_ROUTE;
  private int maxConnectionsTotal = DEFAULT_MAX_CONNECTIONS_TOTAL;
  private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
  private boolean useCircuitBreaker = DEFAULT_USE_CIRCUIT_BREAKER;
  private int circuitBreakerThreshold = DEFAULT_CIRCUIT_BREAKER_THRESHOLD;
  private int circuitBreakerDurationInitial = DEFAULT_CIRCUIT_BREAKER_DURATION_INITIAL;
  private int circuitBreakerDurationMaximum = DEFAULT_CIRCUIT_BREAKER_DURATION_MAXIMUM;
  private int circuitBreakerDurationMultiplier = DEFAULT_CIRCUIT_BREAKER_DURATION_MULTIPLIER;

  protected NotificationWebServiceConfiguration() {
    // No-argument constructor, required for JPA entity classes.
  }

  public NotificationWebServiceConfiguration(
      final ApplicationDataLookupKey id,
      final String marshallerContextPath,
      final String targetUri) {

    this.id = Objects.requireNonNull(id, "id must not be null");
    this.marshallerContextPath =
        Objects.requireNonNull(marshallerContextPath, "marshallerContextPath must not be null");
    this.targetUri = Objects.requireNonNull(targetUri, "targetUri must not be null");
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof NotificationWebServiceConfiguration)) {
      return false;
    }
    final NotificationWebServiceConfiguration other = (NotificationWebServiceConfiguration) obj;
    return Objects.equals(this.getId(), other.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.getId());
  }

  @Override
  public String toString() {
    return String.format(
        "%s[%s, context=%s, target=%s, useKeyStore=%b, useTrustStore=%b, useCircuitBreaker=%b]",
        this.getClass().getSimpleName(),
        this.getId(),
        this.getMarshallerContextPath(),
        this.getTargetUri(),
        this.isUseKeyStore(),
        this.isUseTrustStore(),
        this.isUseCircuitBreaker());
  }

  /**
   * Sets key store configuration values. If none of the provided values is blank, configures this
   * to use the key store, otherwise configures this not to use the key store.
   */
  public void useKeyStore(final String type, final String location, final String password) {
    this.setKeyStoreType(type);
    this.setKeyStoreLocation(location);
    this.setKeyStorePassword(password);
    this.setUseKeyStore(!StringUtils.isAnyBlank(type, location, password));
  }

  /**
   * Sets trust store configuration values. If none of the provided values is blank, configures this
   * to use the trust store, otherwise configures this not to use the trust store.
   */
  public void useTrustStore(final String type, final String location, final String password) {
    this.setTrustStoreType(type);
    this.setTrustStoreLocation(location);
    this.setTrustStorePassword(password);
    this.setUseTrustStore(!StringUtils.isAnyBlank(type, location, password));
  }

  /**
   * Sets circuit breaker configuration values. If all provided values are positive, configures this
   * to use the circuit breaker, otherwise configures this not to use the circuit breaker.
   */
  public void useCircuitBreaker(
      final int threshold,
      final int durationInitial,
      final int durationMaximum,
      final int durationMultiplier) {

    this.setCircuitBreakerThreshold(threshold);
    this.setCircuitBreakerDurationInitial(durationInitial);
    this.setCircuitBreakerDurationMaximum(durationMaximum);
    this.setCircuitBreakerDurationMultiplier(durationMultiplier);
    this.setUseCircuitBreaker(
        threshold > 0 && durationInitial > 0 && durationMaximum > 0 && durationMultiplier > 0);
  }

  public ApplicationDataLookupKey getId() {
    return this.id;
  }

  public void setId(final ApplicationDataLookupKey id) {
    this.id = id;
  }

  public String getMarshallerContextPath() {
    return this.marshallerContextPath;
  }

  public void setMarshallerContextPath(final String marshallerContextPath) {
    this.marshallerContextPath = marshallerContextPath;
  }

  public String getTargetUri() {
    return this.targetUri;
  }

  public void setTargetUri(final String targetUri) {
    this.targetUri = targetUri;
  }

  public boolean isUseKeyStore() {
    return this.useKeyStore;
  }

  public void setUseKeyStore(final boolean useKeyStore) {
    this.useKeyStore = useKeyStore;
  }

  public String getKeyStoreType() {
    return this.keyStoreType;
  }

  public void setKeyStoreType(final String keyStoreType) {
    this.keyStoreType = keyStoreType;
  }

  public String getKeyStoreLocation() {
    return this.keyStoreLocation;
  }

  public void setKeyStoreLocation(final String keyStoreLocation) {
    this.keyStoreLocation = keyStoreLocation;
  }

  public String getKeyStorePassword() {
    return this.keyStorePassword;
  }

  public void setKeyStorePassword(final String keyStorePassword) {
    this.keyStorePassword = keyStorePassword;
  }

  public boolean isUseTrustStore() {
    return this.useTrustStore;
  }

  public void setUseTrustStore(final boolean useTrustStore) {
    this.useTrustStore = useTrustStore;
  }

  public String getTrustStoreType() {
    return this.trustStoreType;
  }

  public void setTrustStoreType(final String trustStoreType) {
    this.trustStoreType = trustStoreType;
  }

  public String getTrustStoreLocation() {
    return this.trustStoreLocation;
  }

  public void setTrustStoreLocation(final String trustStoreLocation) {
    this.trustStoreLocation = trustStoreLocation;
  }

  public String getTrustStorePassword() {
    return this.trustStorePassword;
  }

  public void setTrustStorePassword(final String trustStorePassword) {
    this.trustStorePassword = trustStorePassword;
  }

  public int getMaxConnectionsPerRoute() {
    return this.maxConnectionsPerRoute;
  }

  public void setMaxConnectionsPerRoute(final int maxConnectionsPerRoute) {
    this.maxConnectionsPerRoute = maxConnectionsPerRoute;
  }

  public int getMaxConnectionsTotal() {
    return this.maxConnectionsTotal;
  }

  public void setMaxConnectionsTotal(final int maxConnectionsTotal) {
    this.maxConnectionsTotal = maxConnectionsTotal;
  }

  public int getConnectionTimeout() {
    return this.connectionTimeout;
  }

  public void setConnectionTimeout(final int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public boolean isUseCircuitBreaker() {
    return this.useCircuitBreaker;
  }

  public void setUseCircuitBreaker(final boolean useCircuitBreaker) {
    this.useCircuitBreaker = useCircuitBreaker;
  }

  public int getCircuitBreakerThreshold() {
    return this.circuitBreakerThreshold;
  }

  public void setCircuitBreakerThreshold(final int circuitBreakerThreshold) {
    this.circuitBreakerThreshold = circuitBreakerThreshold;
  }

  public int getCircuitBreakerDurationInitial() {
    return this.circuitBreakerDurationInitial;
  }

  public void setCircuitBreakerDurationInitial(final int circuitBreakerDurationInitial) {
    this.circuitBreakerDurationInitial = circuitBreakerDurationInitial;
  }

  public int getCircuitBreakerDurationMaximum() {
    return this.circuitBreakerDurationMaximum;
  }

  public void setCircuitBreakerDurationMaximum(final int circuitBreakerDurationMaximum) {
    this.circuitBreakerDurationMaximum = circuitBreakerDurationMaximum;
  }

  public int getCircuitBreakerDurationMultiplier() {
    return this.circuitBreakerDurationMultiplier;
  }

  public void setCircuitBreakerDurationMultiplier(final int circuitBreakerDurationMultiplier) {
    this.circuitBreakerDurationMultiplier = circuitBreakerDurationMultiplier;
  }
}
