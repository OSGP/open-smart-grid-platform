/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Arrays;
import java.util.Optional;

public enum GprsOperationModeTypeDto {
  ALWAYS_ON(1),
  TRIGGERED(2);

  private final int number;

  GprsOperationModeTypeDto(final int number) {
    this.number = number;
  }

  public int getNumber() {
    return this.number;
  }

  public static Optional<GprsOperationModeTypeDto> forNumber(final int number) {
    return Arrays.stream(values()).filter(v -> v.number == number).findAny();
  }
}
