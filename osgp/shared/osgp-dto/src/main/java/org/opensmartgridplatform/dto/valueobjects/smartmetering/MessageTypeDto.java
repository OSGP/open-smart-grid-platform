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

public enum MessageTypeDto {
  A_XDR_ENCODED_X_DLMS_APDU(0),
  XML_ENCODED_X_DLMS_APDU(1),
  MANUFACTURER_SPECIFIC(255);

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
