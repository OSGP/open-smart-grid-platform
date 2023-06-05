// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
