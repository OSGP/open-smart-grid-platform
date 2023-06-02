//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
