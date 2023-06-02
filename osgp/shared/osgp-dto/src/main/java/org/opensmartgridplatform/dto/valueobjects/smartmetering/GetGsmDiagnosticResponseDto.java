//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Date;
import java.util.List;

public class GetGsmDiagnosticResponseDto extends ActionResponseDto {

  private final String operator;

  private final ModemRegistrationStatusDto modemRegistrationStatus;

  private final CircuitSwitchedStatusDto circuitSwitchedStatus;

  private final PacketSwitchedStatusDto packetSwitchedStatus;

  private final CellInfoDto cellInfo;

  private final List<AdjacentCellInfoDto> adjacentCells;

  private final Date captureTime;

  private static final long serialVersionUID = 3953818299926960294L;

  public GetGsmDiagnosticResponseDto(
      final String operator,
      final ModemRegistrationStatusDto modemRegistrationStatus,
      final CircuitSwitchedStatusDto circuitSwitchedStatus,
      final PacketSwitchedStatusDto packetSwitchedStatus,
      final CellInfoDto cellInfo,
      final List<AdjacentCellInfoDto> adjacentCells,
      final Date captureTime) {
    this.operator = operator;
    this.modemRegistrationStatus = modemRegistrationStatus;
    this.circuitSwitchedStatus = circuitSwitchedStatus;
    this.packetSwitchedStatus = packetSwitchedStatus;
    this.cellInfo = cellInfo;
    this.adjacentCells = adjacentCells;
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

  public CellInfoDto getCellInfo() {
    return this.cellInfo;
  }

  public List<AdjacentCellInfoDto> getAdjacentCells() {
    return this.adjacentCells;
  }

  public Date getCaptureTime() {
    return this.captureTime;
  }
}
