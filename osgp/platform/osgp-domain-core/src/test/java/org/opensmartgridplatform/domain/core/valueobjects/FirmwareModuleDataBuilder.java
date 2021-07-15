/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

public class FirmwareModuleDataBuilder {
  private static int counter = 0;

  public FirmwareModuleData build() {
    counter += 1;
    return new FirmwareModuleData(
        "moduleVersionComm" + counter,
        "moduleVersionFunc" + counter,
        "moduleVersionMa" + counter,
        "moduleVersionMbus" + counter,
        "moduleVersionSec" + counter,
        "moduleVersionMBusDriverActive" + counter,
        "moduleVersionSimple" + counter);
  }
}
