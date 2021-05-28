/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public enum ModemRegistrationStatusDto {
  NOT_REGISTERED(0),
  REGISTERED_HOME_NETWORK(1),
  NOT_REGISTERED_CURRENT_SEARCH_OPERATOR_TO_REGISTER_TO(2),
  REGISTRATION_DENIED(3),
  UNKNOWN(4),
  REGISTERED_ROAMING(5),
  RESERVED(6); // 6 - 255

  private final int index;

  private ModemRegistrationStatusDto(final int index) {
    this.index = index;
  }

  public int getIndex() {
    return this.index;
  }

  public static ModemRegistrationStatusDto fromIndexValue(final int value) {
    if (value < 0 || value > 255) {
      throw new IllegalArgumentException(
          "IndexValue " + value + " not found for ModemRegistrationStatusDto");
    }

    for (final ModemRegistrationStatusDto status : ModemRegistrationStatusDto.values()) {
      if (status.index == value) {
        return status;
      }
    }
    return RESERVED;
  }

  public String value() {
    return this.name();
  }

  public static ModemRegistrationStatusDto fromValue(final String v) {
    return valueOf(v);
  }
}
