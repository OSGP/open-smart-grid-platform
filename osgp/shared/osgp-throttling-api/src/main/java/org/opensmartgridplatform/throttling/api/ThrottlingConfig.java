// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class ThrottlingConfig {

  @Positive private Short id;

  @NotBlank private String name;

  @NotNull
  @Min(value = -1)
  private Integer maxConcurrency;

  @NotNull
  @Min(value = -1)
  private int maxNewConnections;

  @NotNull @PositiveOrZero private long maxNewConnectionsResetTimeInMs;
  @NotNull @PositiveOrZero private long maxNewConnectionsWaitTimeInMs;

  public ThrottlingConfig() {
    this(UUID.randomUUID().toString(), 0, 0, 0, 0);
  }

  public ThrottlingConfig(
      final String name,
      final int maxConcurrency,
      final int maxNewConnections,
      final long maxNewConnectionsResetTimeInMs,
      final long maxNewConnectionsWaitTimeInMs) {
    this(
        null,
        name,
        maxConcurrency,
        maxNewConnections,
        maxNewConnectionsResetTimeInMs,
        maxNewConnectionsWaitTimeInMs);
  }

  public ThrottlingConfig(
      final Short id,
      final String name,
      final int maxConcurrency,
      final int maxNewConnections,
      final long maxNewConnectionsResetTimeInMs,
      final long maxNewConnectionsWaitTimeInMs) {
    this.id = id;
    this.name = name;
    this.maxConcurrency = maxConcurrency;
    this.maxNewConnections = maxNewConnections;
    this.maxNewConnectionsResetTimeInMs = maxNewConnectionsResetTimeInMs;
    this.maxNewConnectionsWaitTimeInMs = maxNewConnectionsWaitTimeInMs;
  }

  public Short getId() {
    return this.id;
  }

  public void setId(final Short id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Integer getMaxConcurrency() {
    return this.maxConcurrency;
  }

  public void setMaxConcurrency(final Integer maxConcurrency) {
    this.maxConcurrency = maxConcurrency;
  }

  public int getMaxNewConnections() {
    return this.maxNewConnections;
  }

  public void setMaxNewConnections(final int maxNewConnections) {
    this.maxNewConnections = maxNewConnections;
  }

  public long getMaxNewConnectionsResetTimeInMs() {
    return this.maxNewConnectionsResetTimeInMs;
  }

  public void setMaxNewConnectionsResetTimeInMs(final long maxNewConnectionsResetTimeInMs) {
    this.maxNewConnectionsResetTimeInMs = maxNewConnectionsResetTimeInMs;
  }

  public long getMaxNewConnectionsWaitTimeInMs() {
    return this.maxNewConnectionsWaitTimeInMs;
  }

  public void setMaxNewConnectionsWaitTimeInMs(final long maxNewConnectionsWaitTimeInMs) {
    this.maxNewConnectionsWaitTimeInMs = maxNewConnectionsWaitTimeInMs;
  }

  @Override
  public String toString() {
    return String.format(
        "%s[id=%s, name=%s, maxConcurrency=%s, maxNewConnections=%s, maxNewConnectionsResetTime=%s, maxNewConnectionsWaitTime=%s]",
        ThrottlingConfig.class.getSimpleName(),
        this.id,
        this.name,
        this.maxConcurrency,
        this.maxNewConnections,
        this.maxNewConnectionsResetTimeInMs,
        this.maxNewConnectionsWaitTimeInMs);
  }
}
