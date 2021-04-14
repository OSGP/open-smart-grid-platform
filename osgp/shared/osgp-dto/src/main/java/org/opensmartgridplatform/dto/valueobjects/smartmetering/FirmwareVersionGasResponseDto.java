/**
 * Copyright 2021 Alliander N.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
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
