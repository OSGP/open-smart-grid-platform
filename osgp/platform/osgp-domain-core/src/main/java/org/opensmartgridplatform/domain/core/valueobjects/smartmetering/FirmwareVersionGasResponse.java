// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;

public class FirmwareVersionGasResponse extends ActionResponse {

  private static final long serialVersionUID = -5750461161369562141L;

  private final FirmwareVersion firmwareVersion;

  public FirmwareVersionGasResponse(final FirmwareVersion firmwareVersion) {
    this.firmwareVersion = firmwareVersion;
  }

  public FirmwareVersion getFirmwareVersion() {
    return this.firmwareVersion;
  }
}
