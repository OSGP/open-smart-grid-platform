/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public enum PacketSwitchedStatusType {
  INACTIVE,
  GPRS,
  EDGE,
  UMTS,
  HSDPA,
  LTE,
  CDMA,
  LTE_CAT_M_18,
  LTE_NB_IOT,
  RESERVED;

  public String value() {
    return this.name();
  }

  public static PacketSwitchedStatusType fromValue(final String v) {
    return valueOf(v);
  }
}
