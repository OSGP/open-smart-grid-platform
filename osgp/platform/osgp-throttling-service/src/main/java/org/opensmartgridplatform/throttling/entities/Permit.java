// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.entities;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

@Entity
public class Permit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, updatable = false)
  private short throttlingConfigId;

  @Column(nullable = false, updatable = false)
  private int clientId;

  @Column(name = "bts_id", nullable = false, updatable = false)
  private int baseTransceiverStationId;

  @Column(name = "cell_id", nullable = false, updatable = false)
  private int cellId;

  @Column(name = "request_id", nullable = false, updatable = false)
  private int requestId;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  protected Permit() {
    // no-arg constructor required by JPA specification
  }

  public Permit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId) {

    this(null, null, throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId);
  }

  public Permit(
      final Long id,
      final Instant createdAt,
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId) {

    this.id = id;
    this.createdAt = createdAt;
    this.throttlingConfigId = throttlingConfigId;
    this.clientId = clientId;
    this.baseTransceiverStationId = baseTransceiverStationId;
    this.cellId = cellId;
    this.requestId = requestId;
  }

  public Long getId() {
    return this.id;
  }

  public short getThrottlingConfigId() {
    return this.throttlingConfigId;
  }

  public int getClientId() {
    return this.clientId;
  }

  public int getBaseTransceiverStationId() {
    return this.baseTransceiverStationId;
  }

  public int getCellId() {
    return this.cellId;
  }

  public int getRequestId() {
    return this.requestId;
  }

  public Instant getCreatedAt() {
    return this.createdAt;
  }

  @PrePersist
  public void prePersist() {
    this.createdAt = Instant.now();
  }

  @Override
  public String toString() {
    return String.format(
        "%s[id=%s, throttlingConfigId=%d, clientId=%d, btsId=%d, cellId=%d, requestId=%s, created=%s]",
        Permit.class.getSimpleName(),
        this.id,
        this.throttlingConfigId,
        this.clientId,
        this.baseTransceiverStationId,
        this.cellId,
        this.requestId,
        this.createdAt);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Permit)) {
      return false;
    }
    final Permit other = (Permit) obj;
    return this.id != null && this.id.equals(other.getId());
  }

  @Override
  public int hashCode() {
    return Permit.class.hashCode();
  }
}
