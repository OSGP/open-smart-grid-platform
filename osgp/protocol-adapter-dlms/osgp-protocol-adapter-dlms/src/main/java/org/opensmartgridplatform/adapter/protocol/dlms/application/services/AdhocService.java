// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AccessResultCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm.TestAlarmSchedulerCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime.SynchronizeTimeCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.ScanMbusChannelsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetAllAttributeValuesCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetAssociationLnObjectsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetSpecificAttributeValueCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.SetSpecificAttributeValueCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AssociationLnListTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ScanMbusChannelsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetSpecificAttributeValueRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecificAttributeValueRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SynchronizeTimeRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TestAlarmSchedulerRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "dlmsAdhocService")
public class AdhocService {

  private final TestAlarmSchedulerCommandExecutor testAlarmSchedulerCommandExecutor;
  private final SynchronizeTimeCommandExecutor synchronizeTimeCommandExecutor;
  private final GetAllAttributeValuesCommandExecutor getAllAttributeValuesCommandExecutor;
  private final SetSpecificAttributeValueCommandExecutor setSpecificAttributeValueCommandExecutor;
  private final GetSpecificAttributeValueCommandExecutor getSpecificAttributeValueCommandExecutor;
  private final GetAssociationLnObjectsCommandExecutor getAssociationLnObjectsCommandExecutor;
  private final ScanMbusChannelsCommandExecutor scanMbusChannelsCommandExecutor;

  public AdhocService(
      final TestAlarmSchedulerCommandExecutor testAlarmSchedulerCommandExecutor,
      final SynchronizeTimeCommandExecutor synchronizeTimeCommandExecutor,
      final GetAllAttributeValuesCommandExecutor getAllAttributeValuesCommandExecutor,
      final SetSpecificAttributeValueCommandExecutor setSpecificAttributeValueCommandExecutor,
      final GetSpecificAttributeValueCommandExecutor getSpecificAttributeValueCommandExecutor,
      final GetAssociationLnObjectsCommandExecutor getAssociationLnObjectsCommandExecutor,
      final ScanMbusChannelsCommandExecutor scanMbusChannelsCommandExecutor) {
    this.testAlarmSchedulerCommandExecutor = testAlarmSchedulerCommandExecutor;
    this.synchronizeTimeCommandExecutor = synchronizeTimeCommandExecutor;
    this.getAllAttributeValuesCommandExecutor = getAllAttributeValuesCommandExecutor;
    this.setSpecificAttributeValueCommandExecutor = setSpecificAttributeValueCommandExecutor;
    this.getSpecificAttributeValueCommandExecutor = getSpecificAttributeValueCommandExecutor;
    this.getAssociationLnObjectsCommandExecutor = getAssociationLnObjectsCommandExecutor;
    this.scanMbusChannelsCommandExecutor = scanMbusChannelsCommandExecutor;
  }

  // === REQUEST Synchronize Time DATA ===

  public void synchronizeTime(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SynchronizeTimeRequestDto synchronizeTimeRequestDataDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    final AccessResultCode accessResultCode =
        this.synchronizeTimeCommandExecutor.execute(
            conn, device, synchronizeTimeRequestDataDto, messageMetadata);

    if (!AccessResultCode.SUCCESS.equals(accessResultCode)) {
      throw new ProtocolAdapterException(
          "AccessResultCode for synchronizeTime: " + accessResultCode);
    }
  }

  public String getAllAttributeValues(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    return this.getAllAttributeValuesCommandExecutor.execute(conn, device, null, messageMetadata);
  }

  public AssociationLnListTypeDto getAssociationLnObjects(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    return this.getAssociationLnObjectsCommandExecutor.execute(conn, device, null, messageMetadata);
  }

  public Serializable getSpecificAttributeValue(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SpecificAttributeValueRequestDto specificAttributeValueRequestDataDto,
      final MessageMetadata messageMetadata)
      throws FunctionalException {
    return this.getSpecificAttributeValueCommandExecutor.execute(
        conn, device, specificAttributeValueRequestDataDto, messageMetadata);
  }

  public void setSpecificAttributeValue(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetSpecificAttributeValueRequestDto setSpecificAttributeValueRequestDataDto,
      final MessageMetadata messageMetadata)
      throws FunctionalException, ProtocolAdapterException {
    try {
      this.setSpecificAttributeValueCommandExecutor.execute(
          conn, device, setSpecificAttributeValueRequestDataDto, messageMetadata);
    } catch (final ProtocolAdapterException e) {
      log.error("Unexpected exception while setting value.", e);
      throw e;
    }
  }

  public ScanMbusChannelsResponseDto scanMbusChannels(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final MessageMetadata messageMetadata)
      throws OsgpException {
    return this.scanMbusChannelsCommandExecutor.execute(conn, device, null, messageMetadata);
  }

  public void scheduleTestAlarm(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final TestAlarmSchedulerRequestDto testAlarmSchedulerRequestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    final AccessResultCode accessResultCode =
        this.testAlarmSchedulerCommandExecutor.execute(
            conn, device, testAlarmSchedulerRequestDto, messageMetadata);

    if (!AccessResultCode.SUCCESS.equals(accessResultCode)) {
      throw new ProtocolAdapterException(
          "AccessResultCode for scheduleTestAlarm: " + accessResultCode);
    }
  }
}
