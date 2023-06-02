//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.util.List;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;

public class FirmwareVersionResponse extends ActionResponse {

  private static final long serialVersionUID = 4471818892560224779L;

  private List<FirmwareVersion> firmwareVersions;

  public FirmwareVersionResponse(final List<FirmwareVersion> firmwareVersions) {
    super();
    this.firmwareVersions = firmwareVersions;
  }

  public List<FirmwareVersion> getFirmwareVersions() {
    return this.firmwareVersions;
  }
}
