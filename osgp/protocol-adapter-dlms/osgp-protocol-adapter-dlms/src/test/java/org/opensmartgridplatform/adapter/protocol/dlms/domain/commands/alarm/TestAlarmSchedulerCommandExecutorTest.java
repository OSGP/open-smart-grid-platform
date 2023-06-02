//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.openmuc.jdlms.AccessResultCode.SUCCESS;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.CosemDate;
import org.openmuc.jdlms.datatypes.CosemTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionManagerStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DateTimeParserUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.SingleActionScheduleAttribute;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TestAlarmSchedulerRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TestAlarmTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class TestAlarmSchedulerCommandExecutorTest {

  private static final Integer SINGLE_ACTION_CLASS_ID = 22;

  private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @InjectMocks private TestAlarmSchedulerCommandExecutor testAlarmSchedulerCommandExecutor;

  @Mock private DlmsDevice device;

  @Mock private TestAlarmSchedulerRequestDto testAlarmSchedulerRequestDto;

  @Mock private MessageMetadata messageMetadata;

  @Mock private DlmsObjectConfigService dlmsObjectConfigService;

  @Mock private DlmsObject dlmsObject;

  @Mock private ObisCode obisCode;

  private DlmsConnectionManagerStub connectionManagerStub;
  private DlmsConnectionStub connectionStub;

  @BeforeEach
  void setup() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    this.sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    this.connectionStub = new DlmsConnectionStub();
    this.connectionManagerStub = new DlmsConnectionManagerStub(this.connectionStub);

    this.connectionStub.setDefaultReturnValue(DataObject.newArrayData(Collections.emptyList()));
  }

  @ParameterizedTest
  @CsvSource({
    "EMPTY,LAST_GASP,No scheduled date-time set",
    "EMPTY,PARTIAL_POWER_OUTAGE,No scheduled date-time set",
    "2020-10-07 15:33:00,PARTIAL_POWER_OUTAGE,Incorrect scheduled date time value set. It should not be a past date",
    "2020-10-07 15:33:00,LAST_GASP,Incorrect scheduled date time value set. It should not be a past date",
    "2060-10-07 15:33:00,EMPTY,No alarmtype set"
  })
  void execute_fails(final String dateTime, final String alarmType, final String expectedMessage)
      throws ParseException {
    when(this.testAlarmSchedulerRequestDto.getAlarmType())
        .thenReturn("EMPTY".equals(alarmType) ? null : TestAlarmTypeDto.valueOf(alarmType));

    when(this.testAlarmSchedulerRequestDto.getScheduleTime())
        .thenReturn("EMPTY".equals(dateTime) ? null : this.sdf.parse(dateTime));

    Assertions.assertThatThrownBy(
            () ->
                this.testAlarmSchedulerCommandExecutor.execute(
                    this.connectionManagerStub,
                    this.device,
                    this.testAlarmSchedulerRequestDto,
                    this.messageMetadata))
        .isInstanceOf(ProtocolAdapterException.class)
        .hasMessageContaining(expectedMessage);
  }

  @ParameterizedTest
  @CsvSource({
    "2060-10-07 22:00:00,Europe/Amsterdam,PARTIAL_POWER_OUTAGE,0.0.15.1.4.255,2060-10-08T00:00:00+02:00[Europe/Amsterdam]",
    "2060-10-07 22:00:00,Europe/Amsterdam,LAST_GASP,0.0.15.2.4.255, 2060-10-08T00:00:00+02:00[Europe/Amsterdam]",
    "2060-10-08 00:00:00,UTC,PARTIAL_POWER_OUTAGE,0.0.15.1.4.255,2060-10-08T00:00:00+00:00[UTC]",
    "2060-10-08 00:00:00,UTC,LAST_GASP,0.0.15.2.4.255, 2060-10-08T00:00:00+00:00[UTC]"
  })
  void executeSuccess(
      final String dateTimeParameterInUtc,
      final String deviceTimezone,
      final String alarmTypeParameter,
      final String expectedObisCode,
      final String expectedTimeString)
      throws ParseException, ProtocolAdapterException {

    Mockito.when(this.device.getTimezone()).thenReturn(deviceTimezone);

    final Date date = this.sdf.parse(dateTimeParameterInUtc);
    final ZonedDateTime expectedDateTime =
        DateTimeParserUtil.parseToZonedDateTime(expectedTimeString);

    this.setupConfigService(alarmTypeParameter, expectedObisCode);
    this.setupAlarmSchedulerRequest(alarmTypeParameter, date);

    final AccessResultCode accessResultCode =
        this.testAlarmSchedulerCommandExecutor.execute(
            this.connectionManagerStub,
            this.device,
            this.testAlarmSchedulerRequestDto,
            this.messageMetadata);

    assertThat(accessResultCode).isEqualTo(SUCCESS);

    final List<SetParameter> setParameters =
        this.connectionStub.getSetParameters(SingleActionScheduleAttribute.EXECUTION_TIME);
    assertThat(setParameters).hasSize(1);

    // validate setParameter
    final SetParameter setParameter = setParameters.get(0);
    final AttributeAddress address = setParameter.getAttributeAddress();

    assertThat(address.getClassId()).isEqualTo(SINGLE_ACTION_CLASS_ID);
    assertThat(address.getInstanceId()).hasToString(expectedObisCode);

    final DataObject dataObject = setParameter.getData();
    final List<DataObject> value = dataObject.getValue();
    assertThat(value).isNotEmpty().hasAtLeastOneElementOfType(DataObject.class);

    final DataObject array = value.get(0);
    final List<DataObject> structure = array.getValue();
    assertThat(structure).isNotEmpty().hasSize(2);

    final DataObject time = structure.get(0);
    final CosemTime cosemTime = time.getValue();

    final DataObject dateObject = structure.get(1);
    final CosemDate cosemDate = dateObject.getValue();

    final String expectedTime =
        String.format(
            "[%s, %s, %s, 0]",
            expectedDateTime.getHour(), expectedDateTime.getMinute(), expectedDateTime.getSecond());

    final String expectedDate =
        String.format(
            "[%s, %s, %s, %s, -1]",
            expectedDateTime.getYear() / 256,
            expectedDateTime.getYear() % 256,
            expectedDateTime.getMonthValue(),
            expectedDateTime.getDayOfMonth());

    assertThat(Arrays.toString(cosemTime.encode())).isEqualTo(expectedTime);
    assertThat(Arrays.toString(cosemDate.encode())).isEqualTo(expectedDate);
  }

  private void setupAlarmSchedulerRequest(final String alarmType, final Date date) {
    when(this.testAlarmSchedulerRequestDto.getAlarmType())
        .thenReturn(TestAlarmTypeDto.valueOf(alarmType));

    when(this.testAlarmSchedulerRequestDto.getScheduleTime()).thenReturn(date);
  }

  private void setupConfigService(final String alarmType, final String obisCodeString)
      throws ProtocolAdapterException {
    when(this.dlmsObjectConfigService.getDlmsObject(
            this.device,
            TestAlarmSchedulerCommandExecutor.toAlarmObjectType(
                TestAlarmTypeDto.valueOf(alarmType))))
        .thenReturn(this.dlmsObject);

    when(this.obisCode.toString()).thenReturn(obisCodeString);
    when(this.dlmsObject.getClassId()).thenReturn(SINGLE_ACTION_CLASS_ID);
    when(this.dlmsObject.getObisCode()).thenReturn(this.obisCode);
  }
}
