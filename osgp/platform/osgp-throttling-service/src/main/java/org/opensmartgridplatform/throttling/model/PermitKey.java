// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PermitKey {

  private static final String KEY_FORMAT = "osgp-network-segment-permit-%s_%s_%s";
  private static final String LOCK_FORMAT = "osgp-network-segment-lock-%s_%s_%s";

  private short throttlingConfigID;
  private int baseTransceiverStationId;
  private int cellId;

  public String keyPattern() {
    return String.format(
        KEY_FORMAT,
        this.asString(this.throttlingConfigID, true),
        this.asString(this.baseTransceiverStationId, true),
        this.asString(this.cellId, true));
  }

  public String key() {
    return String.format(
        KEY_FORMAT,
        this.asString(this.throttlingConfigID),
        this.asString(this.baseTransceiverStationId),
        this.asString(this.cellId));
  }

  public String lockId() {
    return String.format(
        LOCK_FORMAT,
        this.asString(this.throttlingConfigID),
        this.asString(this.baseTransceiverStationId),
        this.asString(this.cellId));
  }

  private String asString(final int value) {
    return this.asString(value, false);
  }

  private String asString(final int value, final boolean useWildCardForZero) {
    if (useWildCardForZero && value == 0) {
      return "*";
    }

    return String.format("%d", value);
  }

  public static class PermitKeyBuilder {
    public PermitKeyBuilder permit(final Permit permit) {
      return this.networkSegment(permit.networkSegment());
    }

    public PermitKeyBuilder networkSegment(final NetworkSegment networkSegment) {
      this.throttlingConfigID = networkSegment.throttlingConfigId();
      this.baseTransceiverStationId = networkSegment.baseTransceiverStationId();
      this.cellId = networkSegment.cellId();
      return this;
    }
  }
}
