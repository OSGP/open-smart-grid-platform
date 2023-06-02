//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class AdjacentCellInfo implements Serializable {

  private static final long serialVersionUID = -6863912695402483126L;

  private final long cellId;

  private final SignalQualityType signalQuality;

  public AdjacentCellInfo(final long cellId, final SignalQualityType signalQuality) {
    this.cellId = cellId;
    this.signalQuality = signalQuality;
  }

  public long getCellId() {
    return this.cellId;
  }

  public SignalQualityType getSignalQuality() {
    return this.signalQuality;
  }
}
