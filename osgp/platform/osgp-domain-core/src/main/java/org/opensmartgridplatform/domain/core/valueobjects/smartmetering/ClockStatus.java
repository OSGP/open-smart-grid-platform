//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class ClockStatus implements Serializable {

  private static final long serialVersionUID = -8888711117555580479L;

  public static final int STATUS_NOT_SPECIFIED = 0xFF;

  private final EnumSet<ClockStatusBit> statusBits;

  public ClockStatus(final Set<ClockStatusBit> statusBits) {
    if (statusBits == null) {
      this.statusBits = null;
    } else {
      this.statusBits = EnumSet.copyOf(statusBits);
    }
  }

  public ClockStatus(final int status) {
    this(ClockStatusBit.forClockStatus(status));
  }

  public ClockStatus(final byte status) {
    this(ClockStatusBit.forClockStatus(status));
  }

  public ClockStatus(final ClockStatus clockStatus) {
    this(clockStatus.getStatus());
  }

  public int getStatus() {
    return ClockStatusBit.getStatus(this.statusBits);
  }

  public boolean isSpecified() {
    return this.hasStatusBits();
  }

  public boolean hasStatusBits() {
    return this.statusBits != null;
  }

  public Set<ClockStatusBit> getStatusBits() {
    if (this.statusBits == null) {
      return new HashSet<>();
    }
    return EnumSet.copyOf(this.statusBits);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ClockStatus[");
    sb.append("value=").append(String.format("0x%02X", this.getStatus())).append(", bits=");
    if (this.statusBits == null) {
      sb.append("not specified");
    } else {
      sb.append(this.statusBits);
    }
    return sb.append(']').toString();
  }
}
