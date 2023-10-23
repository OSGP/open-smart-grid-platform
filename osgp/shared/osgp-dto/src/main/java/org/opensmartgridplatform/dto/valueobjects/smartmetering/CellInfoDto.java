// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class CellInfoDto implements Serializable {

  private static final long serialVersionUID = -148054976725401235L;

  private final long cellId;
  private final int locationId;
  private final SignalQualityDto signalQuality;
  private final int bitErrorRate;
  private final int mobileCountryCode;
  private final int mobileNetworkCode;
  private final long channelNumber;

  public CellInfoDto(
      final long cellId,
      final int locationId,
      final SignalQualityDto signalQuality,
      final int bitErrorRate,
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

  public SignalQualityDto getSignalQuality() {
    return this.signalQuality;
  }

  public int getBitErrorRate() {
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
