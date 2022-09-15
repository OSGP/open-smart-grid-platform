/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.SingleActionScheduleAttribute;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TestAlarmSchedulerRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TestAlarmTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestAlarmSchedulerCommandExecutor
    extends AbstractCommandExecutor<TestAlarmSchedulerRequestDto, AccessResultCode> {

  @Autowired private DlmsHelper dlmsHelper;

  @Autowired private DlmsObjectConfigService dlmsObjectConfigService;

  public TestAlarmSchedulerCommandExecutor() {
    super(TestAlarmSchedulerRequestDto.class);
  }

  @Override
  public TestAlarmSchedulerRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    return (TestAlarmSchedulerRequestDto) bundleInput;
  }

  @Override
  public ActionResponseDto asBundleResponse(final AccessResultCode executionResult)
      throws ProtocolAdapterException {

    this.checkAccessResultCode(executionResult);

    return new ActionResponseDto("Schedule test alarm was successful");
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final TestAlarmSchedulerRequestDto testAlarmSchedulerRequestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    log.debug("Execute TestAlarmSchedulerCommandExecutor");

    final Date scheduleDate = testAlarmSchedulerRequestDto.getScheduleTime();
    final TestAlarmTypeDto alarmTypeDto = testAlarmSchedulerRequestDto.getAlarmType();

    if (scheduleDate == null || alarmTypeDto == null) {
      throw new ProtocolAdapterException("Incorrect scheduleDate of alarmtype set");
    }

    final DlmsObjectType alarmObjectType =
        TestAlarmTypeDto.PARTIAL_POWER_OUTAGE.equals(alarmTypeDto)
            ? DlmsObjectType.PHASE_OUTAGE_TEST
            : DlmsObjectType.LAST_GASP_TEST;

    final DlmsObject dlmsObject =
        this.dlmsObjectConfigService.getDlmsObject(device, alarmObjectType);
    final DateTime scheduledDateTime = new DateTime(scheduleDate);

    final AttributeAddress attributeAddress =
        new AttributeAddress(
            dlmsObject.getClassId(),
            dlmsObject.getObisCode(),
            SingleActionScheduleAttribute.EXECUTION_TIME.attributeId());

    final DataObject scheduleDateTime = this.dlmsHelper.asDataObject(scheduledDateTime);

    final DataObject commandArray = DataObject.newArrayData(Arrays.asList(scheduleDateTime));

    final SetParameter setParameter = new SetParameter(attributeAddress, commandArray);

    try {
      return conn.getConnection().set(setParameter);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }
}
