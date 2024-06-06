// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class AmrProfileStatusCode implements Serializable {

  private static final long serialVersionUID = 2319359505656305783L;

  private final Set<AmrProfileStatusCodeFlag> amrProfileStatusCodeFlags;

  public AmrProfileStatusCode(final Set<AmrProfileStatusCodeFlag> amrProfileStatusCodeFlags) {
    this.amrProfileStatusCodeFlags = new TreeSet<>(amrProfileStatusCodeFlags);
  }

  @Override
  public String toString() {
    return "AmrProfileStatusCode[" + this.amrProfileStatusCodeFlags + "]";
  }

  public Set<AmrProfileStatusCodeFlag> getAmrProfileStatusCodeFlags() {
    return new TreeSet<>(this.amrProfileStatusCodeFlags);
  }
}
