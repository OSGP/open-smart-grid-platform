/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Date;

public class GetModemInfoResponseDto extends ActionResponseDto {

    private String operator;

    private ModemRegistrationStatusDto modemRegistrationStatus;

    private CircuitSwitchedStatusDto circuitSwitchedStatus;

    private PacketSwitchedStatusDto packetSwitchedStatus;

    private byte[] cellId;

    private byte[] locationId;

    private SignalQualityDto signalQuality;

    private BitErrorRateDto bitErrorRate;

    private long mobileCountryCode;

    private long mobileNetworkCode;

    private long channelNumber;

    private long numberOfAdjacentCells;

    private byte[] adjacentCellId;

    private SignalQualityDto adjacentCellSignalQuality;

    private Date captureTime;

    private static final long serialVersionUID = 3953818299926960294L;

    public GetModemInfoResponseDto(final String operator,
        final ModemRegistrationStatusDto modemRegistrationStatus,
        final CircuitSwitchedStatusDto circuitSwitchedStatus,
        final PacketSwitchedStatusDto packetSwitchedStatus, final byte[] cellId, final byte[] locationId,
        final SignalQualityDto signalQuality,
        final BitErrorRateDto bitErrorRate, final long mobileCountryCode, final long mobileNetworkCode,
        final long channelNumber,
        final long numberOfAdjacentCells, final byte[] adjacentCellId,
        final SignalQualityDto adjacentCellSignalQuality, final Date captureTime) {
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

    public SignalQualityDto getAdjacentCellSignalQuality() {
        return this.adjacentCellSignalQuality;
    }

    public Date getCaptureTime() {
        return this.captureTime;
    }
}
