/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;

public class GetGsmDiagnosticResponseData extends ActionResponse implements Serializable {

  private static final long serialVersionUID = 4966055518516878043L;

  private final String operator;

  private final ModemRegistrationStatusType modemRegistrationStatus;

  private final CircuitSwitchedStatusType circuitSwitchedStatus;

  private final PacketSwitchedStatusType packetSwitchedStatus;

  private final byte[] cellId;

  private final byte[] locationId;

  private final SignalQualityType signalQuality;

  private final BitErrorRateType bitErrorRate;

  private final long mobileCountryCode;

  private final long mobileNetworkCode;

  private final long channelNumber;

  private final long numberOfAdjacentCells;

  private final byte[] adjacentCellId;

  private final SignalQualityType adjacentCellSignalQuality;

  private final Date captureTime;

  public GetGsmDiagnosticResponseData(
      final String operator,
      final ModemRegistrationStatusType modemRegistrationStatus,
      final CircuitSwitchedStatusType circuitSwitchedStatus,
      final PacketSwitchedStatusType packetSwitchedStatus,
      final byte[] cellId,
      final byte[] locationId,
      final SignalQualityType signalQuality,
      final BitErrorRateType bitErrorRate,
      final long mobileCountryCode,
      final long mobileNetworkCode,
      final long channelNumber,
      final long numberOfAdjacentCells,
      final byte[] adjacentCellId,
      final SignalQualityType adjacentCellSignalQuality,
      final Date captureTime) {
    super();
    this.operator = operator;
    this.modemRegistrationStatus = modemRegistrationStatus;
    this.circuitSwitchedStatus = circuitSwitchedStatus;
    this.packetSwitchedStatus = packetSwitchedStatus;
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

  public ModemRegistrationStatusType getModemRegistrationStatus() {
    return this.modemRegistrationStatus;
  }

  public CircuitSwitchedStatusType getCircuitSwitchedStatus() {
    return this.circuitSwitchedStatus;
  }

  public PacketSwitchedStatusType getPacketSwitchedStatus() {
    return this.packetSwitchedStatus;
  }

  public byte[] getCellId() {
    return this.cellId;
  }

  public byte[] getLocationId() {
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

  public long getNumberOfAdjacentCells() {
    return this.numberOfAdjacentCells;
  }

  public byte[] getAdjacentCellId() {
    return this.adjacentCellId;
  }

  public SignalQualityType getAdjacentCellSignalQuality() {
    return this.adjacentCellSignalQuality;
  }

  public Date getCaptureTime() {
    return this.captureTime;
  }
}
