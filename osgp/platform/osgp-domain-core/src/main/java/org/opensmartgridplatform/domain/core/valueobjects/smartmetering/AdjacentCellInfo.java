/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */
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
