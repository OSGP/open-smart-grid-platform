/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

public enum SortMethod {
  FIFO(1),
  LIFO(2),
  LARGEST(3),
  SMALLEST(4),
  NEAREST_TO_ZERO(5),
  FAREST_FROM_ZERO(6);

  int value;

  SortMethod(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }
}
