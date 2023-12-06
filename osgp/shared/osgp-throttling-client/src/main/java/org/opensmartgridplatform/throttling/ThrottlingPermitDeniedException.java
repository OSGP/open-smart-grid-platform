// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import java.util.Optional;

public class ThrottlingPermitDeniedException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final String configurationName;
  private final Integer baseTransceiverStationId;
  private final Integer cellId;
  private final Integer priority;

  public ThrottlingPermitDeniedException(
      final String configurationName,
      final int baseTransceiverStationId,
      final int cellId,
      final int priority) {

    super(
        String.format(
            "Permit denied for network segment (bts_id=%d, cell_id=%d) with priority %d and configuration \"%s\"",
            baseTransceiverStationId, cellId, priority, configurationName));
    this.configurationName = configurationName;
    this.baseTransceiverStationId = baseTransceiverStationId;
    this.cellId = cellId;
    this.priority = priority;
  }

  public ThrottlingPermitDeniedException(final String configurationName, final int priority) {

    super(String.format("Permit denied for network with configuration \"%s\"", configurationName));
    this.configurationName = configurationName;
    this.baseTransceiverStationId = null;
    this.cellId = null;
    this.priority = null;
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
