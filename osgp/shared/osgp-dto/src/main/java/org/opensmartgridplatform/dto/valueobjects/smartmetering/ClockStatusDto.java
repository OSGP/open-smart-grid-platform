// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Set;

public class ClockStatusDto implements Serializable {

  private static final long serialVersionUID = -8888711117555580479L;

  public static final int STATUS_NOT_SPECIFIED = 0xFF;

  private final EnumSet<ClockStatusBitDto> statusBits;

  public ClockStatusDto(final Set<ClockStatusBitDto> statusBits) {
    if (statusBits == null) {
      this.statusBits = null;
    } else {
      this.statusBits = EnumSet.copyOf(statusBits);
    }
  }

  public ClockStatusDto(final int status) {
    this(ClockStatusBitDto.forClockStatus(status));
  }

  public ClockStatusDto(final byte status) {
    this(ClockStatusBitDto.forClockStatus(status));
  }

  public int getStatus() {
    return ClockStatusBitDto.getStatus(this.statusBits);
  }

  public boolean isSpecified() {
    return this.hasStatusBits();
  }

  public boolean hasStatusBits() {
    return this.statusBits != null;
  }

  public Set<ClockStatusBitDto> getStatusBits() {
    if (this.statusBits == null) {
      return null;
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
