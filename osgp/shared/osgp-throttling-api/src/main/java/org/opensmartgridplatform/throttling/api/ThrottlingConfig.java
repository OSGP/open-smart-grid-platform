// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class ThrottlingConfig {

  @Positive private Short id;

  @NotBlank private String name;

  @NotNull @PositiveOrZero private Integer maxConcurrency;
  @NotNull @PositiveOrZero private int maxNewConnectionRequests;
  @NotNull private long maxNewConnectionResetTimeInMs;

  public ThrottlingConfig() {
    this(UUID.randomUUID().toString(), 0, 0, 0);
  }

  public ThrottlingConfig(
      final String name,
      final int maxConcurrency,
      final int maxNewConnectionRequests,
      final long maxNewConnectionResetTimeInMs) {
    this(null, name, maxConcurrency, maxNewConnectionRequests, maxNewConnectionResetTimeInMs);
  }

  public ThrottlingConfig(
      final Short id,
      final String name,
      final int maxConcurrency,
      final int maxNewConnectionRequests,
      final long maxNewConnectionResetTimeInMs) {
    this.id = id;
    this.name = name;
    this.maxConcurrency = maxConcurrency;
    this.maxNewConnectionRequests = maxNewConnectionRequests;
    this.maxNewConnectionResetTimeInMs = maxNewConnectionResetTimeInMs;
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

  public int getMaxNewConnectionRequests() {
    return this.maxNewConnectionRequests;
  }

  public void setMaxNewConnectionRequests(final int maxNewConnectionRequests) {
    this.maxNewConnectionRequests = maxNewConnectionRequests;
  }

  public long getMaxNewConnectionResetTimeInMs() {
    return this.maxNewConnectionResetTimeInMs;
  }

  public void setMaxNewConnectionResetTimeInMs(final long maxNewConnectionResetTimeInMs) {
    this.maxNewConnectionResetTimeInMs = maxNewConnectionResetTimeInMs;
  }

  @Override
  public String toString() {
    return String.format(
        "%s[id=%s, name=%s, maxConcurrency=%s, maxNewConnectionRequests=%s, maxNewConnectionResetTime=%s]",
        ThrottlingConfig.class.getSimpleName(),
        this.id,
        this.name,
        this.maxConcurrency,
        this.maxNewConnectionRequests,
        this.maxNewConnectionResetTimeInMs);
  }
}
