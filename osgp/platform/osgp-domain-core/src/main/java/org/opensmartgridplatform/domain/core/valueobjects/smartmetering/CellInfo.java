/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class CellInfo implements Serializable {

  private static final long serialVersionUID = 8837201183740438728L;

  private final long cellId;

  private final long locationId;

  private final SignalQualityType signalQuality;

  private final BitErrorRateType bitErrorRate;

  private final long mobileCountryCode;

  private final long mobileNetworkCode;

  private final long channelNumber;

  public CellInfo(
      final long cellId,
      final long locationId,
      final SignalQualityType signalQuality,
      final BitErrorRateType bitErrorRate,
      final long mobileCountryCode,
      final long mobileNetworkCode,
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

  public long getLocationId() {
    return this.locationId;
  }

  public SignalQualityType getSignalQuality() {
    return this.signalQuality;
  }

  public BitErrorRateType getBitErrorRate() {
    return this.bitErrorRate;
  }

  public long getMobileCountryCode() {
    return this.mobileCountryCode;
  }

  public long getMobileNetworkCode() {
    return this.mobileNetworkCode;
  }

  public long getChannelNumber() {
    return this.channelNumber;
  }
}
