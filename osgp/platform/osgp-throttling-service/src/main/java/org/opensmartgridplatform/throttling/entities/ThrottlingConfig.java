/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.throttling.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.annotations.NaturalId;

@Entity
public class ThrottlingConfig {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Short id;

  @NaturalId
  @Column(nullable = false, updatable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private int maxConcurrency;

  public ThrottlingConfig() {
    // no-arg constructor required by JPA specification
  }

  public ThrottlingConfig(final String name, final int maxConcurrency) {
    this(null, name, maxConcurrency);
  }

  public ThrottlingConfig(final Short id, final String name, final int maxConcurrency) {
    this.id = id;
    this.name = Objects.requireNonNull(name, "name must not be null");
    this.maxConcurrency = this.requireNonNegativeMaxConcurrency(maxConcurrency);
  }

  private int requireNonNegativeMaxConcurrency(final int maxConcurrency) {
    if (maxConcurrency < 0) {
      throw new IllegalArgumentException("maxConcurrency must be non-negative: " + maxConcurrency);
    }
    return maxConcurrency;
  }

  public Short getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public int getMaxConcurrency() {
    return this.maxConcurrency;
  }

  public void setMaxConcurrency(final int maxConcurrency) {
    this.maxConcurrency = this.requireNonNegativeMaxConcurrency(maxConcurrency);
  }

  @Override
  public String toString() {
    return String.format(
        "%s[id=%s, name=%s, maxConcurrency=%d]",
        ThrottlingConfig.class.getSimpleName(), this.id, this.name, this.maxConcurrency);
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
