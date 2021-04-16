/**
 * Copyright 2020 Alliander N.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import org.joda.time.DateTime;

public class ModemInfoDto implements Serializable {

  private static final long serialVersionUID = 4137020381571730343L;

  private final String operator;
  private final ModemRegistrationStatusDto modemRegistrationStatus;
  private final CircuitSwitchedStatusDto circuitSwitchedStatus;
  private final PacketSwitchedStatusDto packetSwitchedStatus;
  private final byte[] cellId;
  private final byte[] locationId;
  private final SignalQualityDto signalQuality;
  private final BitErrorRateDto bitErrorRate;
  private final int mobileCountryCode;
  private final int mobileNetworkCode;
  private final Long channelNumber;
  private final int numberOfAdjacentCells;
  private final byte[] adjacentCellId;
  private final SignalQualityDto adjacentCellSignalQuality;
  private final DateTime captureTime;

  public ModemInfoDto(
      final String operator,
      final ModemRegistrationStatusDto modemRegistrationStatusDto,
      final CircuitSwitchedStatusDto circuitSwitchedStatusDto,
      final PacketSwitchedStatusDto packetSwitchedStatusDto,
      final byte[] cellId,
      final byte[] locationId,
      final SignalQualityDto signalQuality,
      final BitErrorRateDto bitErrorRate,
      final int mobileCountryCode,
      final int mobileNetworkCode,
      final Long channelNumber,
      final int numberOfAdjacentCells,
      final byte[] adjacentCellId,
      final SignalQualityDto adjacentCellSignalQuality,
      final DateTime captureTime) {
    this.operator = operator;
    this.modemRegistrationStatus = modemRegistrationStatusDto;
    this.circuitSwitchedStatus = circuitSwitchedStatusDto;
    this.packetSwitchedStatus = packetSwitchedStatusDto;
    this.cellId = cellId;
    this.locationId = locationId;
    this.signalQuality = signalQuality;
    this.bitErrorRate = bitErrorRate;
    this.mobileCountryCode = mobileCountryCode;
    this.mobileNetworkCode = mobileNetworkCode;
    this.channelNumber = channelNumber;
    this.numberOfAdjacentCells = numberOfAdjacentCells;
    this.adjacentCellId = adjacentCellId;
    this.adjacentCellSignalQuality = adjacentCellSignalQuality;
    this.captureTime = captureTime;
  }

  public String getOperator() {
    return this.operator;
  }

  public ModemRegistrationStatusDto getModemRegistrationStatus() {
    return this.modemRegistrationStatus;
  }

  public CircuitSwitchedStatusDto getCircuitSwitchedStatus() {
    return this.circuitSwitchedStatus;
  }

  public PacketSwitchedStatusDto getPacketSwitchedStatus() {
    return this.packetSwitchedStatus;
  }

  public byte[] getCellId() {
    return this.cellId;
  }

  public byte[] getLocationId() {
    return this.locationId;
  }

  public SignalQualityDto getSignalQuality() {
    return this.signalQuality;
  }

  public BitErrorRateDto getBitErrorRate() {
    return this.bitErrorRate;
  }

  public int getMobileCountryCode() {
    return this.mobileCountryCode;
  }

  public int getMobileNetworkCode() {
    return this.mobileNetworkCode;
  }

  public Long getChannelNumber() {
    return this.channelNumber;
  }

  public int getNumberOfAdjacentCells() {
    return this.numberOfAdjacentCells;
  }

  public byte[] getAdjacentCellId() {
    return this.adjacentCellId;
  }

  public SignalQualityDto getAdjacentCellSignalQuality() {
    return this.adjacentCellSignalQuality;
  }

  public DateTime getCaptureTime() {
    return this.captureTime;
  }

  @Override
  public String toString() {
    return String.format(
        "ModemInfoDto[operator=%s, modemRegistrationStatus=%s', circuitSwitchedStatus=%s, packetSwitchedStatus=%s, cellId=%s]",
        this.operator,
        this.modemRegistrationStatus.name(),
        this.circuitSwitchedStatus.name(),
        this.packetSwitchedStatus.name(),
        new String(this.cellId));
  }
}
