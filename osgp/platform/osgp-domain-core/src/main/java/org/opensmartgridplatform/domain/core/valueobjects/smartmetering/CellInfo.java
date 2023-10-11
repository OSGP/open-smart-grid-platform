// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class CellInfo implements Serializable {

  private static final long serialVersionUID = 8837201183740438728L;

  private final long cellId;

  private final int locationId;

  private final SignalQualityType signalQuality;

  private final short bitErrorRate;

  private final int mobileCountryCode;

  private final int mobileNetworkCode;

  private final long channelNumber;

  public CellInfo(
      final long cellId,
      final int locationId,
      final SignalQualityType signalQuality,
      final short bitErrorRate,
      final int mobileCountryCode,
      final int mobileNetworkCode,
      final long channelNumber) {
    this.cellId = cellId;
    this.locationId = locationId;
    this.signalQuality = signalQuality;
    this.bitErrorRate = bitErrorRate;
    this.mobileCountryCode = mobileCountryCode;
    this.mobileNetworkCode = mobileNetworkCode;
    this.channelNumber = channelNumber;
  }

  public long getCellId() {
    return this.cellId;
  }

  public int getLocationId() {
    return this.locationId;
  }

  public SignalQualityType getSignalQuality() {
    return this.signalQuality;
  }

  public short getBitErrorRate() {
    return this.bitErrorRate;
  }

  public int getMobileCountryCode() {
    return this.mobileCountryCode;
  }

  public int getMobileNetworkCode() {
    return this.mobileNetworkCode;
  }

  public long getChannelNumber() {
    return this.channelNumber;
  }
}
