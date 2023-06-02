//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;

public class FirmwareUpdateMessageDataContainer implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -1767816115878168922L;

  private final FirmwareModuleData firmwareModuleData;
  private final String firmwareUrl;

  public FirmwareUpdateMessageDataContainer(
      final FirmwareModuleData firmwareModuleData, final String firmwareUrl) {
    this.firmwareModuleData = firmwareModuleData;
    this.firmwareUrl = firmwareUrl;
  }

  public FirmwareModuleData getFirmwareModuleData() {
    return this.firmwareModuleData;
  }

  public String getFirmwareUrl() {
    return this.firmwareUrl;
  }
}
