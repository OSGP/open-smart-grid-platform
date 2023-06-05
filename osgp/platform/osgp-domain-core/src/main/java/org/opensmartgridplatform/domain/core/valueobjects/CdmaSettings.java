// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CdmaSettings implements Serializable {

  private static final long serialVersionUID = 7540392130297372145L;

  @Column private String mastSegment;
  @Column private Short batchNumber;

  protected CdmaSettings() {
    // Default constructor for hibernate
  }

  public CdmaSettings(final String mastSegment, final Short batchNumber) {
    this.mastSegment = mastSegment;
    this.batchNumber = batchNumber;
  }

  public String getMastSegment() {
    return this.mastSegment;
  }

  public Short getBatchNumber() {
    return this.batchNumber;
  }

  @Override
  public String toString() {
    return "CdmaSettings [mastSegment="
        + this.mastSegment
        + ", batchNumber="
        + this.batchNumber
        + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.mastSegment, this.batchNumber);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof CdmaSettings)) {
      return false;
    }

    final CdmaSettings other = (CdmaSettings) obj;
    return (Objects.equals(this.mastSegment, other.mastSegment)
        && Objects.equals(this.batchNumber, other.batchNumber));
  }
}
