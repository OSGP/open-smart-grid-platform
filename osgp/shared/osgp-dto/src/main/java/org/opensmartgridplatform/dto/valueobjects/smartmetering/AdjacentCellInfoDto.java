/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class AdjacentCellInfoDto implements Serializable {

  private static final long serialVersionUID = 7464070322360476966L;

  private final long cellId;
  private final SignalQualityDto signalQuality;

  public AdjacentCellInfoDto(final long cellId, final SignalQualityDto signalQuality) {
    this.cellId = cellId;
    this.signalQuality = signalQuality;
  }

  public long getCellId() {
    return this.cellId;
  }

  public SignalQualityDto getSignalQuality() {
    return this.signalQuality;
  }
}
