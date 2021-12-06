/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Arrays;
import java.util.Optional;

public enum TransportServiceTypeDto {
  TCP(0),
  UDP(1),
  FTP(2),
  SMTP(3),
  SMS(4),
  HDLC(5),
  M_BUS(6),
  ZIG_BEE(7),
  DLMS_GATEWAY(8),
  MANUFACTURER_SPECIFIC(255);

  private final int dlmsEnumValue;

  TransportServiceTypeDto(final int number) {
    this.dlmsEnumValue = number;
  }

  public int getDlmsEnumValue() {
    return this.dlmsEnumValue;
  }

  public static Optional<TransportServiceTypeDto> forNumber(final int number) {
    return Arrays.stream(values()).filter(v -> v.dlmsEnumValue == number).findAny();
  }
}
