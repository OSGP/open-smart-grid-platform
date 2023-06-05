// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CdmaBatch implements Comparable<CdmaBatch> {

  public static final Short MAX_BATCH_NUMBER = (short) 99;

  private final Short batchNumber;
  private Set<CdmaBatchDevice> cdmaBatchDevices;

  public CdmaBatch(final Short batchNumber) {
    if (batchNumber == null) {
      throw new IllegalArgumentException("batchNumber is not allowed to be null");
    }

    if (batchNumber > MAX_BATCH_NUMBER) {
      throw new IllegalArgumentException(
          "batchNumber is not allowed to be larger than " + MAX_BATCH_NUMBER);
    }

    this.batchNumber = batchNumber;
    this.cdmaBatchDevices = new HashSet<>();
  }

  public void addCdmaBatchDevice(final CdmaBatchDevice cdmaBatchDevice) {
    this.cdmaBatchDevices.add(cdmaBatchDevice);
  }

  public Short getBatchNumber() {
    return this.batchNumber;
  }

  public Set<CdmaBatchDevice> getCdmaBatchDevices() {
    return this.cdmaBatchDevices;
  }

  @Override
  public int hashCode() {
    return this.batchNumber.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof CdmaBatch)) {
      return false;
    }

    final CdmaBatch other = (CdmaBatch) obj;

    return Objects.equals(this.batchNumber, other.batchNumber);
  }

  @Override
  public String toString() {
    return "CdmaBatch [batchNumber="
        + this.batchNumber
        + ", cdmaBatchDevices="
        + this.cdmaBatchDevices
        + "]";
  }

  @Override
  public int compareTo(final CdmaBatch other) {
    return this.batchNumber.compareTo(other.batchNumber);
  }
}
