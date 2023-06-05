// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class AmrProfileStatusCodeDto implements Serializable {

  private static final long serialVersionUID = 2319359505656305783L;

  private final Set<AmrProfileStatusCodeFlagDto> amrProfileStatusCodeFlags;

  public AmrProfileStatusCodeDto(final Set<AmrProfileStatusCodeFlagDto> amrProfileStatusCodeFlags) {
    this.amrProfileStatusCodeFlags = new TreeSet<>(amrProfileStatusCodeFlags);
  }

  @Override
  public String toString() {
    return "AmrProfileStatusCodeFlags[" + this.amrProfileStatusCodeFlags + "]";
  }

  public Set<AmrProfileStatusCodeFlagDto> getAmrProfileStatusCodeFlags() {
    return new TreeSet<>(this.amrProfileStatusCodeFlags);
  }
}
