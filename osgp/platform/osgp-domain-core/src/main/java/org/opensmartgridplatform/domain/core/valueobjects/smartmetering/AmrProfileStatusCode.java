/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
