// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import org.hibernate.annotations.NaturalId;

@Entity
@Getter
public class ThrottlingConfig {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Short id;

  @NaturalId
  @Column(nullable = false, updatable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private int maxConcurrency;

  @Column(nullable = false)
  private int maxNewConnectionRequests;

  @Column(nullable = false)
  private long maxNewConnectionResetTimeInMs;

  public ThrottlingConfig() {
    // no-arg constructor required by JPA specification
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
    this.name = Objects.requireNonNull(name, "name must not be null");
    this.maxConcurrency = this.requireNonNegative("maxConcurrency", maxConcurrency);
    this.maxNewConnectionRequests =
        this.requireNonNegative("maxNewConnectionRequests", maxNewConnectionRequests);
    this.maxNewConnectionResetTimeInMs =
        this.requireNonNegative("maxNewConnectionResetTimeInMs", maxNewConnectionResetTimeInMs);
  }

  private int requireNonNegative(final String fieldName, final int value) {
    if (value < 0) {
      throw new IllegalArgumentException(fieldName + " must be non-negative: " + value);
    }
    return value;
  }

  private long requireNonNegative(final String fieldName, final long value) {
    if (value < 0) {
      throw new IllegalArgumentException(fieldName + " must be non-negative: " + value);
    }
    return value;
  }

  public void setMaxConcurrency(final int maxConcurrency) {
    this.maxConcurrency = this.requireNonNegative("maxConcurrency", maxConcurrency);
  }

  public void setMaxNewConnectionRequests(final int maxNewConnectionRequests) {
    this.maxNewConnectionRequests =
        this.requireNonNegative("maxNewConnectionRequests", maxNewConnectionRequests);
  }

  public void setMaxNewConnectionResetTimeInMs(final long maxNewConnectionResetTimeInMs) {
    this.maxNewConnectionResetTimeInMs = maxNewConnectionResetTimeInMs;
  }

  @Override
  public String toString() {
    return String.format(
        "%s[id=%s, name=%s, maxConcurrency=%d, maxNewConnectionRequests=%d, maxNewConnectionResetTimeInMs=%s]",
        ThrottlingConfig.class.getSimpleName(),
        this.id,
        this.name,
        this.maxConcurrency,
        this.maxNewConnectionRequests,
        this.maxNewConnectionResetTimeInMs);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ThrottlingConfig)) {
      return false;
    }
    final ThrottlingConfig other = (ThrottlingConfig) obj;
    return Objects.equals(this.name, other.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.name);
  }
}
