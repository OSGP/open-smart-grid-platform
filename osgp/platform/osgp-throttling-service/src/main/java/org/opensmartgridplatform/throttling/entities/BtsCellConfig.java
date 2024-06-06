// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Entity
@Getter
@EqualsAndHashCode(exclude = {"id", "maxConcurrency"})
public class BtsCellConfig {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Short id;

  @Column(name = "bts_id", nullable = false, updatable = false)
  private int baseTransceiverStationId;

  @Column(name = "cell_id", nullable = false, updatable = false)
  private int cellId;

  @Column(nullable = false)
  private int maxConcurrency;

  public BtsCellConfig() {
    // no-arg constructor required by JPA specification
  }

  public BtsCellConfig(
      final int baseTransceiverStationId, final int cellId, final int maxConcurrency) {
    this(null, baseTransceiverStationId, cellId, maxConcurrency);
  }

  public BtsCellConfig(
      final Short id,
      final int baseTransceiverStationId,
      final int cellId,
      final int maxConcurrency) {
    this.id = id;
    this.baseTransceiverStationId = baseTransceiverStationId;
    this.cellId = cellId;
    this.maxConcurrency = this.requireNonNegativeMaxConcurrency(maxConcurrency);
  }

  private int requireNonNegativeMaxConcurrency(final int maxConcurrency) {
    if (maxConcurrency < 0) {
      throw new IllegalArgumentException("maxConcurrency must be non-negative: " + maxConcurrency);
    }
    return maxConcurrency;
  }

  public void setMaxConcurrency(final int maxConcurrency) {
    this.maxConcurrency = this.requireNonNegativeMaxConcurrency(maxConcurrency);
  }

  @Override
  public String toString() {
    return String.format(
        "%s[id=%s, btsId=%s, cellId=%s, maxConcurrency=%d]",
        BtsCellConfig.class.getSimpleName(),
        this.id,
        this.baseTransceiverStationId,
        this.cellId,
        this.maxConcurrency);
  }
}
