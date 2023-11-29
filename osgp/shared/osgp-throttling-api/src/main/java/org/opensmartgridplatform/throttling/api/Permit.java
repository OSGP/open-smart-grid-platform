// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.Instant;
import javax.validation.constraints.Positive;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class Permit {

  @Positive private short throttlingConfigId;

  @Positive private int clientId;

  private Integer requestId;

  private Integer baseTransceiverStationId;

  private Integer cellId;

  private Instant createdAt;

  public Permit() {}

  public Permit(final Integer requestId) {
    this((short) 0, 0, requestId, null, null, Instant.now());
  }

  public Permit(
      final short throttlingConfigId,
      final int clientId,
      final Integer requestId,
      final Integer baseTransceiverStationId,
      final Integer cellId,
      final Instant createdAt) {

    this.throttlingConfigId = throttlingConfigId;
    this.clientId = clientId;
    this.requestId = requestId;
    this.baseTransceiverStationId = baseTransceiverStationId;
    this.cellId = cellId;
    this.createdAt = createdAt;
  }

  public short getThrottlingConfigId() {
    return this.throttlingConfigId;
  }

  public void setThrottlingConfigId(final short throttlingConfigId) {
    this.throttlingConfigId = throttlingConfigId;
  }

  public int getClientId() {
    return this.clientId;
  }

  public void setClientId(final int clientId) {
    this.clientId = clientId;
  }

  public Integer getRequestId() {
    return this.requestId;
  }

  public void setRequestId(final Integer requestId) {
    this.requestId = requestId;
  }

  public Integer getBaseTransceiverStationId() {
    return this.baseTransceiverStationId;
  }

  public void setBaseTransceiverStationId(final Integer baseTransceiverStationId) {
    this.baseTransceiverStationId = baseTransceiverStationId;
  }

  public Integer getCellId() {
    return this.cellId;
  }

  public void setCellId(final Integer cellId) {
    this.cellId = cellId;
  }

  public Instant getCreatedAt() {
    return this.createdAt;
  }

  public void setCreatedAt(final Instant createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public String toString() {
    return String.format(
        "%s[clientId=%d, requestId=%s, btsId=%s, cellId=%s, createdAt=%s]",
        Permit.class.getSimpleName(),
        this.clientId,
        this.requestId,
        this.baseTransceiverStationId,
        this.cellId,
        this.createdAt);
  }
}
