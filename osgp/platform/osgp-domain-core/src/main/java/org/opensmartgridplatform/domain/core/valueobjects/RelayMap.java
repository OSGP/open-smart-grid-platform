/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class RelayMap implements Serializable, Comparable<RelayMap> {

  /** Serial Version UID. */
  private static final long serialVersionUID = -8997468148053647259L;

  @NotNull
  @Min(1)
  @Max(6)
  private final Integer index;

  @NotNull
  @Min(1)
  @Max(255)
  private final Integer address;

  @NotNull private RelayType relayType;

  private String alias;

  public RelayMap(
      final Integer index, final Integer address, final RelayType relayType, final String alias) {
    this.index = index;
    this.address = address;
    this.relayType = relayType;
    this.alias = alias;
  }

  public String getAlias() {
    return this.alias;
  }

  public Integer getIndex() {
    return this.index;
  }

  public Integer getAddress() {
    return this.address;
  }

  public RelayType getRelayType() {
    return this.relayType;
  }

  public void changeRelayType(final RelayType relayType) {
    this.relayType = relayType;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == null || !(o instanceof RelayMap)) {
      return false;
    }

    return this.compareTo((RelayMap) o) == 0;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  @Override
  public int compareTo(final RelayMap that) {
    if (that == null) {
      throw new NullPointerException();
    }

    return this.toString().compareTo(that.toString());
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    return sb.append(this.index)
        .append('-')
        .append(this.address)
        .append('-')
        .append(this.relayType)
        .append('-')
        .append(this.alias)
        .toString();
  }
}
