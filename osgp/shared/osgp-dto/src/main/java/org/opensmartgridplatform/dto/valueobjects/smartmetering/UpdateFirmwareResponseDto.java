/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.List;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;

public class UpdateFirmwareResponseDto extends FirmwareVersionResponseDto {

  private static final long serialVersionUID = -9159077783233215317L;

  private final String firmwareIdentification;

  public UpdateFirmwareResponseDto(
      final String firmwareIdentification, final List<FirmwareVersionDto> firmwareVersions) {
    super(firmwareVersions);
    this.firmwareIdentification = firmwareIdentification;
  }

  public String getFirmwareIdentification() {
    return this.firmwareIdentification;
  }
}
