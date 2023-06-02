//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.CosemDate;
import org.openmuc.jdlms.datatypes.CosemTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsDateTimeConverter;
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

    validate(scheduleDate, alarmTypeDto);

    final DlmsObjectType alarmObjectType = toAlarmObjectType(alarmTypeDto);

    final DlmsObject dlmsObject =
        this.dlmsObjectConfigService.getDlmsObject(device, alarmObjectType);
    final DateTime convertedDateTime =
        DlmsDateTimeConverter.toDateTime(scheduleDate, device.getTimezone());

    final AttributeAddress attributeAddress =
        new AttributeAddress(
            dlmsObject.getClassId(),
            dlmsObject.getObisCode(),
            SingleActionScheduleAttribute.EXECUTION_TIME.attributeId());

    final DataObject timeDataObject = getDataObjectTime(convertedDateTime);
    final DataObject dateDataObject = getDataObjectDate(convertedDateTime);

    final DataObject structure = DataObject.newStructureData(timeDataObject, dateDataObject);

    final DataObject commandArray = DataObject.newArrayData(Collections.singletonList(structure));

    final SetParameter setParameter = new SetParameter(attributeAddress, commandArray);

    try {
      return conn.getConnection().set(setParameter);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }

  protected static DlmsObjectType toAlarmObjectType(final TestAlarmTypeDto alarmTypeDto) {
    return TestAlarmTypeDto.PARTIAL_POWER_OUTAGE.equals(alarmTypeDto)
        ? DlmsObjectType.PHASE_OUTAGE_TEST
        : DlmsObjectType.LAST_GASP_TEST;
  }

  private static DataObject getDataObjectDate(final DateTime scheduledDateTime) {
    return DataObject.newDateData(
        new CosemDate(
            scheduledDateTime.getYear(),
            scheduledDateTime.getMonthOfYear(),
            scheduledDateTime.getDayOfMonth()));
  }

  private static DataObject getDataObjectTime(final DateTime scheduledDateTime) {
    return DataObject.newTimeData(
        new CosemTime(
            scheduledDateTime.getHourOfDay(),
            scheduledDateTime.getMinuteOfHour(),
            scheduledDateTime.getSecondOfMinute(),
            0));
  }

  private static void validate(final Date scheduleDate, final TestAlarmTypeDto alarmTypeDto)
      throws ProtocolAdapterException {
    if (scheduleDate == null) {
      throw new ProtocolAdapterException("No scheduled date-time set");
    } else if (alarmTypeDto == null) {
      throw new ProtocolAdapterException("No alarmtype set");

    } else if (scheduleDate.before(Date.from(Instant.now()))) {
      throw new ProtocolAdapterException(
          "Incorrect scheduled date time value set. It should not be a past date");
    }
  }
}
