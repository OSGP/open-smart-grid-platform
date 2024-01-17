// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class ThrottlingConfig {

  @Positive private Short id;

  @NotBlank private String name;

  @NotNull @PositiveOrZero private Integer maxConcurrency;

  public ThrottlingConfig() {
    this(UUID.randomUUID().toString(), 0);
  }

  public ThrottlingConfig(final String name, final int maxConcurrency) {
    this(null, name, maxConcurrency);
  }

  public ThrottlingConfig(final Short id, final String name, final int maxConcurrency) {
    this.id = id;
    this.name = name;
    this.maxConcurrency = maxConcurrency;
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

  @Override
  public String toString() {
    return String.format(
        "%s[id=%s, name=%s, maxConcurrency=%s]",
        ThrottlingConfig.class.getSimpleName(), this.id, this.name, this.maxConcurrency);
  }
}
