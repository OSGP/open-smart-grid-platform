//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Arrays;
import java.util.Optional;

public enum MessageTypeDto {
  A_XDR_ENCODED_X_DLMS_APDU(0),
  XML_ENCODED_X_DLMS_APDU(1),
  MANUFACTURER_SPECIFIC(128);

  private final int dlmsEnumValue;

  MessageTypeDto(final int number) {
    this.dlmsEnumValue = number;
  }

  public int getDlmsEnumValue() {
    return this.dlmsEnumValue;
  }

  public static Optional<MessageTypeDto> forNumber(final int number) {
    return Arrays.stream(values()).filter(v -> v.dlmsEnumValue == number).findAny();
  }
}
