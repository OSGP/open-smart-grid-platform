//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
