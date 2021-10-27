/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

public enum AdministrativeStatusType {
  UNDEFINED(0),
  OFF(1),
  ON(2);

  int value;

  AdministrativeStatusType(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }
}
