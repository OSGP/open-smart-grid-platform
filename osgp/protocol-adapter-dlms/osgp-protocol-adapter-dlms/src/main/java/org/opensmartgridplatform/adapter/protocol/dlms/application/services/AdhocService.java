// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.io.Serializable;
import org.openmuc.jdlms.AccessResultCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm.TestAlarmSchedulerCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime.SynchronizeTimeCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.ScanMbusChannelsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetAllAttributeValuesCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetAssociationLnObjectsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetSpecificAttributeValueCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AssociationLnListTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ScanMbusChannelsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecificAttributeValueRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SynchronizeTimeRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TestAlarmSchedulerRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "dlmsAdhocService")
public class AdhocService {

  @Autowired private TestAlarmSchedulerCommandExecutor testAlarmSchedulerCommandExecutor;
  @Autowired private SynchronizeTimeCommandExecutor synchronizeTimeCommandExecutor;

  @Autowired private GetAllAttributeValuesCommandExecutor getAllAttributeValuesCommandExecutor;

  @Autowired
  private GetSpecificAttributeValueCommandExecutor getSpecificAttributeValueCommandExecutor;

  @Autowired private GetAssociationLnObjectsCommandExecutor getAssociationLnObjectsCommandExecutor;

  @Autowired private ScanMbusChannelsCommandExecutor scanMbusChannelsCommandExecutor;

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
