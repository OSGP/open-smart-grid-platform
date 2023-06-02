//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.ArrayList;
import java.util.List;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;

public class FirmwareVersionResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = 4779593744529504287L;

  private final List<FirmwareVersionDto> firmwareVersions;

  public FirmwareVersionResponseDto(final List<FirmwareVersionDto> firmwareVersions) {
    this.firmwareVersions = new ArrayList<>(firmwareVersions);
  }

  public List<FirmwareVersionDto> getFirmwareVersions() {
    return new ArrayList<>(this.firmwareVersions);
  }
}
