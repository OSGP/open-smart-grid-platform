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
import java.util.List;

public class GetModemInfoResponseData extends ActionResponse implements Serializable {

    private static final long serialVersionUID = 4966055518516878043L;

    private String operator;

    private ModemRegistrationStatusType modemRegistrationStatus;

    private CircuitSwitchedStatusType circuitSwitchedStatus;

    private PacketSwitchedStatusType packetSwitchedStatus;

    private byte[] cellId;

    private byte[] locationId;

    private SignalQualityType signalQuality;

    private BitErrorRateType bitErrorRate;

    private long mobileCountryCode;

    private long mobileNetworkCode;

    private long channelNumber;

    private long numberOfAdjacentCells;

    private byte[] adjacantCellId;

    private SignalQualityType adjacantCellSignalQuality;

    private Date captureTime;

    public GetModemInfoResponseData(final String operator,
            final ModemRegistrationStatusType modemRegistrationStatus,
            final CircuitSwitchedStatusType circuitSwitchedStatus,
            final PacketSwitchedStatusType packetSwitchedStatus, final byte[] cellId, final byte[] locationId,
            final SignalQualityType signalQuality,
            final BitErrorRateType bitErrorRate, final long mobileCountryCode, final long mobileNetworkCode,
            final long channelNumber,
            final long numberOfAdjacentCells, final byte[] adjacantCellId,
            final SignalQualityType adjacantCellSignalQuality, final Date captureTime) {
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
        this.adjacantCellId = adjacantCellId;
        this.adjacantCellSignalQuality = adjacantCellSignalQuality;
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

    public byte[] getAdjacantCellId() {
        return this.adjacantCellId;
    }

    public SignalQualityType getAdjacantCellSignalQuality() {
        return this.adjacantCellSignalQuality;
    }

    public Date getCaptureTime() {
        return this.captureTime;
    }
}
