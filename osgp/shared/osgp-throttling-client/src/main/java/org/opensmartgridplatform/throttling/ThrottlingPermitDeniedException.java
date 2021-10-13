/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.throttling;

import java.util.Optional;

public class ThrottlingPermitDeniedException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final String configurationName;
  private final Integer baseTransceiverStationId;
  private final Integer cellId;

  public ThrottlingPermitDeniedException(
      final String configurationName, final int baseTransceiverStationId, final int cellId) {

    super(
        String.format(
            "Permit denied for network segment (bts_id=%d, cell_id=%d) and configuration \"%s\"",
            baseTransceiverStationId, cellId, configurationName));
    this.configurationName = configurationName;
    this.baseTransceiverStationId = baseTransceiverStationId;
    this.cellId = cellId;
  }

  public ThrottlingPermitDeniedException(final String configurationName) {

    super(String.format("Permit denied for network with configuration \"%s\"", configurationName));
    this.configurationName = configurationName;
    this.baseTransceiverStationId = null;
    this.cellId = null;
  }

  public String getConfigurationName() {
    return this.configurationName;
  }

  public Optional<Integer> getBaseTransceiverStationId() {
    return Optional.ofNullable(this.baseTransceiverStationId);
  }

  public Optional<Integer> getCellId() {
    return Optional.ofNullable(this.cellId);
  }
}
