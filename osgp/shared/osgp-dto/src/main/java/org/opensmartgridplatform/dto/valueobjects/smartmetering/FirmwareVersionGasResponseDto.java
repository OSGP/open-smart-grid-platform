// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import lombok.Getter;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionGasDto;

@Getter
public class FirmwareVersionGasResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = -2050652405322188213L;

  private final FirmwareVersionGasDto firmwareVersion;

  public FirmwareVersionGasResponseDto(final FirmwareVersionGasDto firmwareVersion) {
    this.firmwareVersion = firmwareVersion;
  }
}
