// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import lombok.Getter;
import org.opensmartgridplatform.dto.valueobjects.HashTypeDto;

@Getter
public class UpdateFirmwareRequestDataDto implements ActionRequestDto {

  private static final long serialVersionUID = 4779593744529504287L;

  private final String firmwareIdentification;
  private final HashTypeDto firmwareHashType;
  private final String firmwareDigest;

  public UpdateFirmwareRequestDataDto(
      final String firmwareIdentification,
      final HashTypeDto firmwareHashType,
      final String firmwareDigest) {
    this.firmwareIdentification = firmwareIdentification;
    this.firmwareHashType = firmwareHashType;
    this.firmwareDigest = firmwareDigest;
  }
}
