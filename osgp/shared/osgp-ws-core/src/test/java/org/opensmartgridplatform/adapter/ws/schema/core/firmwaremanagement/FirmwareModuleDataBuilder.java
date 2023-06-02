//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement;

/** Creates instances, for testing purposes only. */
public class FirmwareModuleDataBuilder {
  private static int counter = 0;

  public FirmwareModuleData build() {
    counter += 1;
    final FirmwareModuleData firmwareModuleData = new FirmwareModuleData();
    firmwareModuleData.setModuleVersionComm("moduleVersionComm" + counter);
    firmwareModuleData.setModuleVersionFunc("moduleVersionFunc" + counter);
    firmwareModuleData.setModuleVersionMa("moduleVersionMa" + counter);
    firmwareModuleData.setModuleVersionMbus("moduleVersionMbus" + counter);
    firmwareModuleData.setModuleVersionSec("moduleVersionSec" + counter);
    firmwareModuleData.setModuleVersionMBusDriverActive("moduleVersionMBusDriverActive" + counter);
    firmwareModuleData.setModuleVersionSimple("moduleVersionSimple" + counter);
    return firmwareModuleData;
  }
}
