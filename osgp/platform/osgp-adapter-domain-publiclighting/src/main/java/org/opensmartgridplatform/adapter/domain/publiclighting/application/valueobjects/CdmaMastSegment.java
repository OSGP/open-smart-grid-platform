//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects;

import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class CdmaMastSegment implements Comparable<CdmaMastSegment> {

  public static final String DEFAULT_MASTSEGMENT = "DEVICE-WITHOUT-MASTSEGMENT";

  private final String mastSegmentName;
  private SortedMap<Short, CdmaBatch> cdmaBatches;

  public CdmaMastSegment(final String mastSegmentName) {
    if (mastSegmentName == null) {
      throw new IllegalArgumentException("mastSegmentName is not allowed to be null");
    }

    this.mastSegmentName = mastSegmentName;
    this.cdmaBatches = new TreeMap<>();
  }

  public void addCdmaBatchDevice(final Short batchNumber, final CdmaBatchDevice cdmaBatchDevice) {
    final Short nonNullBatchNumber = batchNumber == null ? CdmaBatch.MAX_BATCH_NUMBER : batchNumber;

    CdmaBatch cdmaBatch = this.cdmaBatches.get(nonNullBatchNumber);

    if (cdmaBatch == null) {
      cdmaBatch = new CdmaBatch(nonNullBatchNumber);
      this.cdmaBatches.put(nonNullBatchNumber, cdmaBatch);
    }
    cdmaBatch.addCdmaBatchDevice(cdmaBatchDevice);
  }

  /**
   * Returns the first (lowest) CdmaBatch of a CdmaMastSegment and removes the CdmaBatch from the
   * CdmaMastSegment.
   *
   * @return when there are CdmaBatches, the first CdmaBatch. Otherwise null.
   */
  public CdmaBatch popCdmaBatch() {
    if (this.cdmaBatches.isEmpty()) {
      return null;
    } else {
      final Short firstKey = this.cdmaBatches.firstKey();
      final CdmaBatch firstBatch = this.cdmaBatches.get(firstKey);
      this.cdmaBatches.remove(firstKey);

      return firstBatch;
    }
  }

  public String getMastSegment() {
    return this.mastSegmentName;
  }

  public boolean empty() {
    return this.cdmaBatches.isEmpty();
  }

  @Override
  public int hashCode() {
    return this.mastSegmentName.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof CdmaMastSegment)) {
      return false;
    }

    final CdmaMastSegment other = (CdmaMastSegment) obj;

    return Objects.equals(this.mastSegmentName, other.mastSegmentName);
  }

  @Override
  public String toString() {
    return "CdmaMastSegment [mastSegmentName="
        + this.mastSegmentName
        + ", cdmaBatches="
        + this.cdmaBatches.keySet()
        + "]";
  }

  @Override
  public int compareTo(final CdmaMastSegment other) {
    if (this.mastSegmentName.equals(DEFAULT_MASTSEGMENT)) {
      return this.mastSegmentName.equals(other.mastSegmentName) ? 0 : 1;
    } else if (other.mastSegmentName.equals(DEFAULT_MASTSEGMENT)) {
      return -1;
    }

    return this.mastSegmentName.compareTo(other.mastSegmentName);
  }
}
