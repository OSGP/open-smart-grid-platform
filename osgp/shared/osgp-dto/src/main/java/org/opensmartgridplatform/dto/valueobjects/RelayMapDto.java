/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;

public class RelayMapDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -8744650092009418556L;

  private final Integer index;
  private final Integer address;
  private RelayTypeDto relayType;
  private final String alias;

  public RelayMapDto(
      final Integer index,
      final Integer address,
      final RelayTypeDto relayType,
      final String alias) {
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

  public RelayTypeDto getRelayType() {
    return this.relayType;
  }

  public void changeRelayType(final RelayTypeDto relayType) {
    this.relayType = relayType;
  }

  @Override
  public String toString() {
    return String.format(
        "RelayMapDto [index: %d, address: %d relayType: %s, alias: %s]",
        this.index, this.address, this.relayType.name(), this.alias);
  }
}
