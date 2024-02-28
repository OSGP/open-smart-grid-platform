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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

@Entity
@Getter
@Setter
public class ThrottlingConfig {

  @Setter(AccessLevel.NONE)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Short id;

  @Setter(AccessLevel.NONE)
  @NotNull
  @NaturalId
  @Column(nullable = false, updatable = false, unique = true)
  private String name;

  @Min(-1)
  @Column(nullable = false)
  private int maxConcurrency;

  @Min(-1)
  @Column(nullable = false)
  private int maxNewConnections;

  @Min(0)
  @Column(nullable = false)
  private long maxNewConnectionsResetTimeInMs;

  @Min(0)
  @Column(nullable = false)
  private long maxNewConnectionsWaitTimeInMs;

  public ThrottlingConfig() {
    // no-arg constructor required by JPA specification
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

  @Override
  public String toString() {
    return String.format(
        "%s[id=%s, name=%s, maxConcurrency=%d, maxNewConnections=%d, maxNewConnectionsResetTimeInMs=%s, maxNewConnectionsWaitTimeInMs=%s]",
        ThrottlingConfig.class.getSimpleName(),
        this.id,
        this.name,
        this.maxConcurrency,
        this.maxNewConnections,
        this.maxNewConnectionsResetTimeInMs,
        this.maxNewConnectionsWaitTimeInMs);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof final ThrottlingConfig other)) {
      return false;
    }
    return Objects.equals(this.name, other.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.name);
  }
}
