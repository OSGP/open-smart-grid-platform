// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm.ClearAlarmRegisterCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm.ReadAlarmRegisterCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetActualMeterReadsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetActualMeterReadsGasCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetActualPowerQualityCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetThdFingerprintCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.monitoring.GetPowerQualityProfileCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.GetPeriodicMeterReadsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.GetPeriodicMeterReadsGasCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmRegisterResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearAlarmRegisterRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetThdFingerprintResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ReadAlarmRegisterRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Service;

@Service(value = "dlmsDeviceMonitoringService")
public class MonitoringService {

  private final GetPeriodicMeterReadsCommandExecutor getPeriodicMeterReadsCommandExecutor;
  private final GetPeriodicMeterReadsGasCommandExecutor getPeriodicMeterReadsGasCommandExecutor;
  private final GetActualMeterReadsCommandExecutor actualMeterReadsCommandExecutor;
  private final GetActualMeterReadsGasCommandExecutor actualMeterReadsGasCommandExecutor;
  private final GetActualPowerQualityCommandExecutor getActualPowerQualityCommandExecutor;
  private final ReadAlarmRegisterCommandExecutor readAlarmRegisterCommandExecutor;
  private final GetPowerQualityProfileCommandExecutor getPowerQualityProfileCommandExecutor;
  private final ClearAlarmRegisterCommandExecutor clearAlarmRegisterCommandExecutor;
  private final GetThdFingerprintCommandExecutor getThdFingerprintCommandExecutor;

  public MonitoringService(
      final GetPeriodicMeterReadsCommandExecutor getPeriodicMeterReadsCommandExecutor,
      final GetPeriodicMeterReadsGasCommandExecutor getPeriodicMeterReadsGasCommandExecutor,
      final GetActualMeterReadsCommandExecutor actualMeterReadsCommandExecutor,
      final GetActualMeterReadsGasCommandExecutor actualMeterReadsGasCommandExecutor,
      final GetActualPowerQualityCommandExecutor getActualPowerQualityCommandExecutor,
      final ReadAlarmRegisterCommandExecutor readAlarmRegisterCommandExecutor,
      final GetPowerQualityProfileCommandExecutor getPowerQualityProfileCommandExecutor,
      final ClearAlarmRegisterCommandExecutor clearAlarmRegisterCommandExecutor,
      final GetThdFingerprintCommandExecutor getThdFingerprintCommandExecutor) {

    this.getPeriodicMeterReadsCommandExecutor = getPeriodicMeterReadsCommandExecutor;
    this.getPeriodicMeterReadsGasCommandExecutor = getPeriodicMeterReadsGasCommandExecutor;
    this.actualMeterReadsCommandExecutor = actualMeterReadsCommandExecutor;
    this.actualMeterReadsGasCommandExecutor = actualMeterReadsGasCommandExecutor;
    this.getActualPowerQualityCommandExecutor = getActualPowerQualityCommandExecutor;
    this.readAlarmRegisterCommandExecutor = readAlarmRegisterCommandExecutor;
    this.getPowerQualityProfileCommandExecutor = getPowerQualityProfileCommandExecutor;
    this.clearAlarmRegisterCommandExecutor = clearAlarmRegisterCommandExecutor;
    this.getThdFingerprintCommandExecutor = getThdFingerprintCommandExecutor;
  }

  // === REQUEST PERIODIC METER DATA ===

  public Serializable requestPeriodicMeterReads(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final PeriodicMeterReadsRequestDto periodicMeterReadsQuery,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final Serializable response;
    if (periodicMeterReadsQuery.isMbusQuery()) {
      response =
          this.getPeriodicMeterReadsGasCommandExecutor.execute(
              conn, device, periodicMeterReadsQuery, messageMetadata);
    } else {
      response =
          this.getPeriodicMeterReadsCommandExecutor.execute(
              conn, device, periodicMeterReadsQuery, messageMetadata);
    }

    return response;
  }

  public Serializable requestActualMeterReads(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ActualMeterReadsQueryDto actualMeterReadsRequest,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final Serializable response;
    if (actualMeterReadsRequest.isMbusQuery()) {
      response =
          this.actualMeterReadsGasCommandExecutor.execute(
              conn, device, actualMeterReadsRequest, messageMetadata);
    } else {
      response =
          this.actualMeterReadsCommandExecutor.execute(
              conn, device, actualMeterReadsRequest, messageMetadata);
    }

    return response;
  }

  public Serializable requestActualPowerQuality(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ActualPowerQualityRequestDto actualPowerQualityRequestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    return this.getActualPowerQualityCommandExecutor.execute(
        conn, device, actualPowerQualityRequestDto, messageMetadata);
  }

  public AlarmRegisterResponseDto requestReadAlarmRegister(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ReadAlarmRegisterRequestDto readAlarmRegisterRequest,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    return this.readAlarmRegisterCommandExecutor.execute(
        conn, device, readAlarmRegisterRequest, messageMetadata);
  }

  public Serializable requestPowerQualityProfile(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetPowerQualityProfileRequestDataDto powerQualityProfileRequestDataDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    return this.getPowerQualityProfileCommandExecutor.execute(
        conn, device, powerQualityProfileRequestDataDto, messageMetadata);
  }

  public void setClearAlarmRegister(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ClearAlarmRegisterRequestDto clearAlarmRegisterRequestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    this.clearAlarmRegisterCommandExecutor.execute(
        conn, device, clearAlarmRegisterRequestDto, messageMetadata);
  }

  public GetThdFingerprintResponseDto requestThdFingerprint(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    return this.getThdFingerprintCommandExecutor.execute(conn, device, null, messageMetadata);
  }
}
