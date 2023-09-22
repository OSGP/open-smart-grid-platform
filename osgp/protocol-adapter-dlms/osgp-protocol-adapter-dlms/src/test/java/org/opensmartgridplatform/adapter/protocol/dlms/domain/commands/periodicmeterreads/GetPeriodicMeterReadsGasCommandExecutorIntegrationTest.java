// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto.CLOCK_ADJUSTED;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto.CLOCK_INVALID;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto.CRITICAL_ERROR;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto.DATA_NOT_VALID;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto.DAYLIGHT_SAVING;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto.NOT_USED;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto.POWER_DOWN;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto.RECOVERED_VALUE;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Stream;
import org.assertj.core.util.Lists;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigConfiguration;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionManagerStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.AttributeAddressAssert;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.AmrProfileStatusCodeHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsDateTimeConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadGasResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsGasResponseItemDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class GetPeriodicMeterReadsGasCommandExecutorIntegrationTest {

  private GetPeriodicMeterReadsGasCommandExecutor executor;

  private DlmsHelper dlmsHelper;
  private AmrProfileStatusCodeHelper amrProfileStatusCodeHelper;
  private ObjectConfigService objectConfigService;

  private DlmsConnectionManagerStub connectionManagerStub;
  private DlmsConnectionStub connectionStub;

  private final ObisCode OBIS_DAILY_DSMR4 = new ObisCode("1.0.99.2.0.255");
  private final ObisCode OBIS_MONTHLY_DSMR4 = new ObisCode("0.0.98.1.0.255");
  private final String OBIS_INTERVAL_DSMR4 = "0.<c>.24.3.0.255";

  private final String OBIS_DAILY_SMR5 = "0.<c>.24.3.1.255";
  private final String OBIS_INTERVAL_SMR5 = "0.<c>.24.3.0.255";
  private final String OBIS_MONTHLY_SMR5 = "0.<c>.24.3.2.255";

  private final ObisCode OBIS_CLOCK = new ObisCode("0.0.1.0.0.255");
  private final ObisCode OBIS_STATUS = new ObisCode("0.0.96.10.2.255");
  private final String OBIS_GAS_VALUE_SMR5 = "0.<c>.24.2.2.255";
  private final ObisCode OBIS_ACTIVE_ENERGY_IMPORT_RATE_1 = new ObisCode("1.0.1.8.1.255");
  private final ObisCode OBIS_ACTIVE_ENERGY_IMPORT_RATE_2 = new ObisCode("1.0.1.8.2.255");
  private final ObisCode OBIS_ACTIVE_ENERGY_EXPORT_RATE_1 = new ObisCode("1.0.2.8.1.255");
  private final ObisCode OBIS_ACTIVE_ENERGY_EXPORT_RATE_2 = new ObisCode("1.0.2.8.2.255");
  private final String OBIS_GAS_VALUE_DSMR4 = "0.<c>.24.2.1.255";

  private final int CLASS_ID_CLOCK = 8;
  private final int CLASS_ID_DATA = 1;
  private final int CLASS_ID_REGISTER = 3;
  private final int CLASS_ID_EXTENDED_REGISTER = 4;
  private final int CLASS_ID_PROFILE = 7;

  private final byte ATTR_ID_VALUE = 2;
  private final byte ATTR_ID_BUFFER = 2;
  private final byte ATTR_ID_CAPTURE_TIME = 5;

  private final DataObject CLOCK =
      DataObject.newStructureData(
          Arrays.asList(
              DataObject.newUInteger16Data(this.CLASS_ID_CLOCK),
                  DataObject.newOctetStringData(this.OBIS_CLOCK.bytes()),
              DataObject.newInteger8Data(this.ATTR_ID_VALUE), DataObject.newUInteger16Data(0)));

  private final DataObject STATUS =
      DataObject.newStructureData(
          Arrays.asList(
              DataObject.newUInteger16Data(this.CLASS_ID_DATA),
                  DataObject.newOctetStringData(this.OBIS_STATUS.bytes()),
              DataObject.newInteger8Data(this.ATTR_ID_VALUE), DataObject.newUInteger16Data(0)));

  private Date TIME_FROM;
  private Date TIME_TO;
  private DataObject PERIOD_1_CLOCK;
  private DataObject PERIOD_2_CLOCK;
  private Date PERIOD_1_CLOCK_VALUE;
  private Date PERIOD_2_CLOCK_VALUE;
  private Date PERIOD_2_CLOCK_VALUE_NULL_DATA_PERIOD_HOURLY;
  private Date PERIOD_2_CLOCK_VALUE_NULL_DATA_PERIOD_DAILY;
  private Date PERIOD_2_CLOCK_VALUE_NULL_DATA_PERIOD_MONTHLY;

  private final CosemDateTime PERIOD_1_CAPTURE_TIME = new CosemDateTime(2018, 12, 31, 23, 50, 0, 0);
  private final CosemDateTime PERIOD_2_CAPTURE_TIME = new CosemDateTime(2019, 1, 1, 0, 7, 0, 0);

  private final long PERIOD_1_LONG_VALUE = 1000L;
  private final long PERIOD_2_LONG_VALUE = 1500L;

  private final BigDecimal SCALER = BigDecimal.valueOf(1000);

  private final long PERIOD_1_LONG_VALUE_E = 33L;
  private final long PERIOD_2_LONG_VALUE_E = 44L;

  private final short PERIOD1_AMR_STATUS_VALUE = 0x0F; // First 4 status bits set
  private final short PERIOD2_AMR_STATUS_VALUE = 0xF0; // Last 4 status bits set

  @BeforeEach
  public void setUp() throws IOException, ObjectConfigException {

    final TimeZone defaultTimeZone = TimeZone.getDefault();
    final DateTimeZone defaultDateTimeZone = DateTimeZone.getDefault();

    // all time based tests must use UTC time.
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    DateTimeZone.setDefault(DateTimeZone.UTC);

    this.initDates();

    this.dlmsHelper = new DlmsHelper();
    this.amrProfileStatusCodeHelper = new AmrProfileStatusCodeHelper();
    final DlmsObjectConfigConfiguration dlmsObjectConfigConfiguration =
        new DlmsObjectConfigConfiguration();
    this.objectConfigService = new ObjectConfigService();

    this.executor =
        new GetPeriodicMeterReadsGasCommandExecutor(
            this.dlmsHelper, this.amrProfileStatusCodeHelper, this.objectConfigService);
    this.connectionStub = new DlmsConnectionStub();
    this.connectionManagerStub = new DlmsConnectionManagerStub(this.connectionStub);

    this.connectionStub.setDefaultReturnValue(DataObject.newArrayData(Collections.emptyList()));

    // reset to original TimeZone
    TimeZone.setDefault(defaultTimeZone);
    DateTimeZone.setDefault(defaultDateTimeZone);
  }

  private void initDates() {

    this.TIME_FROM = new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime();
    this.TIME_TO = new GregorianCalendar(2019, Calendar.FEBRUARY, 5).getTime();
    this.PERIOD_1_CLOCK = this.getDateAsOctetString(2019, 1, 1);
    this.PERIOD_2_CLOCK = this.getDateAsOctetString(2019, 1, 2);
    this.PERIOD_1_CLOCK_VALUE = new GregorianCalendar(2019, Calendar.JANUARY, 1, 0, 0).getTime();
    this.PERIOD_2_CLOCK_VALUE = new GregorianCalendar(2019, Calendar.JANUARY, 2, 0, 0).getTime();
    this.PERIOD_2_CLOCK_VALUE_NULL_DATA_PERIOD_HOURLY =
        new GregorianCalendar(2019, Calendar.JANUARY, 1, 1, 0).getTime();
    this.PERIOD_2_CLOCK_VALUE_NULL_DATA_PERIOD_DAILY =
        new GregorianCalendar(2019, Calendar.JANUARY, 2, 0, 0).getTime();
    this.PERIOD_2_CLOCK_VALUE_NULL_DATA_PERIOD_MONTHLY =
        new GregorianCalendar(2019, Calendar.FEBRUARY, 1, 0, 0).getTime();
  }

  private static Stream<Arguments> combinePeriodTypesWithChannels() {
    return Arrays.stream(PeriodTypeDto.values())
        .map(GetPeriodicMeterReadsGasCommandExecutorIntegrationTest::combineWithChannels)
        .flatMap(List::stream);
  }

  private static List<Arguments> combineWithChannels(final PeriodTypeDto periodType) {
    final List<Integer> channels = Lists.newArrayList(1, 2, 3, 4);
    return channels.stream().map(channel -> Arguments.of(periodType, channel)).toList();
  }

  @ParameterizedTest
  @MethodSource("combinePeriodTypesWithChannels")
  void testExecuteDsmr4NoSelectedValues(final PeriodTypeDto type, final int channel)
      throws Exception {
    this.testExecute(Protocol.DSMR_4_2_2, type, false, false, channel);
  }

  @ParameterizedTest
  @MethodSource("combinePeriodTypesWithChannels")
  void testExecuteDsmr4(final PeriodTypeDto type, final int channel) throws Exception {
    this.testExecute(Protocol.DSMR_4_2_2, type, false, true, channel);
  }

  @ParameterizedTest
  @MethodSource("combinePeriodTypesWithChannels")
  void testExecuteSmr5_0(final PeriodTypeDto type, final int channel) throws Exception {
    this.testExecute(Protocol.SMR_5_0_0, type, false, true, channel);
  }

  @ParameterizedTest
  @MethodSource("combinePeriodTypesWithChannels")
  void testExecuteSmr5_0_WithNullData(final PeriodTypeDto type, final int channel)
      throws Exception {
    this.testExecute(Protocol.SMR_5_0_0, type, true, true, channel);
  }

  @ParameterizedTest
  @MethodSource("combinePeriodTypesWithChannels")
  void testExecuteSmr5_1(final PeriodTypeDto type, final int channel) throws Exception {
    this.testExecute(Protocol.SMR_5_1, type, false, true, channel);
  }

  @ParameterizedTest
  @MethodSource("combinePeriodTypesWithChannels")
  void testExecuteSmr5_1_WithNullData(final PeriodTypeDto type, final int channel)
      throws Exception {
    this.testExecute(Protocol.SMR_5_1, type, true, true, channel);
  }

  private void testExecute(
      final Protocol protocol,
      final PeriodTypeDto type,
      final boolean useNullData,
      final boolean selectedValuesSupported,
      final int channel)
      throws Exception {

    // SETUP
    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder().withCorrelationUid("123456").build();

    // Reset stub
    this.connectionStub.clearRequestedAttributeAddresses();

    // Create device with requested protocol version
    final DlmsDevice device = this.createDlmsDevice(protocol, selectedValuesSupported);

    // Create request object
    final PeriodicMeterReadsRequestDto request =
        new PeriodicMeterReadsRequestDto(
            type, this.TIME_FROM, this.TIME_TO, ChannelDto.fromNumber(channel));

    // Get expected values
    final AttributeAddress expectedAddressProfile =
        this.createAttributeAddress(protocol, type, this.TIME_FROM, this.TIME_TO, device, channel);

    // Set response in stub
    this.setResponseForProfile(
        expectedAddressProfile, protocol, type, useNullData, selectedValuesSupported, channel);

    // CALL
    final PeriodicMeterReadGasResponseDto response =
        this.executor.execute(this.connectionManagerStub, device, request, messageMetadata);

    // VERIFY

    // Get resulting requests from connection stub
    final List<AttributeAddress> requestedAttributeAddresses =
        this.connectionStub.getRequestedAttributeAddresses();
    assertThat(requestedAttributeAddresses).hasSize(1);

    // There should be 1 request to the buffer (id = 2) of a profile
    // (class-id = 7)
    final AttributeAddress actualAttributeAddressProfile =
        requestedAttributeAddresses.stream()
            .filter(a -> a.getClassId() == this.CLASS_ID_PROFILE)
            .toList()
            .get(0);

    AttributeAddressAssert.is(actualAttributeAddressProfile, expectedAddressProfile);

    // Check response
    assertThat(response.getPeriodType()).isEqualTo(type);
    final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads =
        response.getPeriodicMeterReadsGas();
    final int AMOUNT_OF_PERIODS = 2;
    assertThat(periodicMeterReads).hasSize(AMOUNT_OF_PERIODS);

    this.checkClockValues(periodicMeterReads, type, useNullData);
    this.checkValues(periodicMeterReads, channel);
    this.checkAmrStatus(periodicMeterReads, protocol, type);
  }

  private DlmsDevice createDlmsDevice(
      final Protocol protocol, final boolean selectiveAccessPeriodicMeterReadsSupported) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    device.setSelectiveAccessSupported(true);
    device.setSelectiveAccessPeriodicMeterReadsSupported(
        selectiveAccessPeriodicMeterReadsSupported);
    return device;
  }

  private AttributeAddress createAttributeAddress(
      final Protocol protocol,
      final PeriodTypeDto type,
      final Date timeFrom,
      final Date timeTo,
      final DlmsDevice device,
      final int channel)
      throws Exception {
    final DataObject from =
        this.dlmsHelper.asDataObject(
            DlmsDateTimeConverter.toDateTime(timeFrom, device.getTimezone()));
    final DataObject to =
        this.dlmsHelper.asDataObject(
            DlmsDateTimeConverter.toDateTime(timeTo, device.getTimezone()));

    if (protocol == Protocol.DSMR_4_2_2) {
      if (type == PeriodTypeDto.DAILY) {
        return this.createAttributeAddressDsmr4Daily(
            from, to, device.isSelectiveAccessPeriodicMeterReadsSupported(), channel);
      } else if (type == PeriodTypeDto.MONTHLY) {
        return this.createAttributeAddressDsmr4Monthly(
            from, to, device.isSelectiveAccessPeriodicMeterReadsSupported(), channel);
      } else if (type == PeriodTypeDto.INTERVAL) {
        return this.createAttributeAddressDsmr4Interval(from, to, channel);
      }
    } else if (protocol == Protocol.SMR_5_0_0 || protocol == Protocol.SMR_5_1) {
      if (type == PeriodTypeDto.DAILY) {
        return this.createAttributeAddressSmr5Daily(from, to, channel);
      } else if (type == PeriodTypeDto.MONTHLY) {
        return this.createAttributeAddressSmr5Monthly(from, to, channel);
      } else if (type == PeriodTypeDto.INTERVAL) {
        return this.createAttributeAddressSmr5Interval(from, to, channel);
      }
    }

    throw new Exception(
        "Invalid combination of protocol "
            + protocol.getName()
            + " and version "
            + protocol.getVersion());
  }

  private void setResponseForProfile(
      final AttributeAddress attributeAddressForProfile,
      final Protocol protocol,
      final PeriodTypeDto type,
      final boolean useNullData,
      final boolean selectedValuesSupported,
      final int channel) {

    // PERIOD 1

    final DataObject period1Clock = this.PERIOD_1_CLOCK;
    final DataObject period1Status = DataObject.newUInteger8Data(this.PERIOD1_AMR_STATUS_VALUE);
    final DataObject period1CaptureTime = DataObject.newDateTimeData(this.PERIOD_1_CAPTURE_TIME);
    final DataObject period1ValueE = DataObject.newUInteger32Data(this.PERIOD_1_LONG_VALUE_E);

    final DataObject periodItem1;
    if (type == PeriodTypeDto.MONTHLY && protocol == Protocol.DSMR_4_2_2) {
      if (selectedValuesSupported) {
        periodItem1 =
            DataObject.newStructureData(
                Arrays.asList(
                    period1Clock,
                    this.createValue(this.PERIOD_1_LONG_VALUE, channel),
                    period1CaptureTime));
      } else {
        periodItem1 =
            DataObject.newStructureData(
                Arrays.asList(
                    period1Clock,
                    period1ValueE,
                    period1ValueE,
                    period1ValueE,
                    period1ValueE,
                    this.createValue(this.PERIOD_1_LONG_VALUE, 1),
                    period1CaptureTime,
                    this.createValue(this.PERIOD_1_LONG_VALUE, 2),
                    period1CaptureTime,
                    this.createValue(this.PERIOD_1_LONG_VALUE, 3),
                    period1CaptureTime,
                    this.createValue(this.PERIOD_1_LONG_VALUE, 4),
                    period1CaptureTime));
      }
    } else {
      if (selectedValuesSupported || type == PeriodTypeDto.INTERVAL) {
        periodItem1 =
            DataObject.newStructureData(
                Arrays.asList(
                    period1Clock,
                    period1Status,
                    this.createValue(this.PERIOD_1_LONG_VALUE, channel),
                    period1CaptureTime));
      } else {
        periodItem1 =
            DataObject.newStructureData(
                Arrays.asList(
                    period1Clock,
                    period1Status,
                    period1ValueE,
                    period1ValueE,
                    period1ValueE,
                    period1ValueE,
                    this.createValue(this.PERIOD_1_LONG_VALUE, 1),
                    period1CaptureTime,
                    this.createValue(this.PERIOD_1_LONG_VALUE, 2),
                    period1CaptureTime,
                    this.createValue(this.PERIOD_1_LONG_VALUE, 3),
                    period1CaptureTime,
                    this.createValue(this.PERIOD_1_LONG_VALUE, 4),
                    period1CaptureTime));
      }
    }

    // PERIOD 2

    final DataObject period2Clock;
    final DataObject period2CaptureTime;
    if (useNullData) {
      period2Clock = DataObject.newNullData();
      period2CaptureTime = DataObject.newNullData();
    } else {
      period2Clock = this.PERIOD_2_CLOCK;
      period2CaptureTime = DataObject.newDateTimeData(this.PERIOD_2_CAPTURE_TIME);
    }
    final DataObject period2Status = DataObject.newUInteger8Data(this.PERIOD2_AMR_STATUS_VALUE);
    final DataObject period2ValueE = DataObject.newUInteger32Data(this.PERIOD_2_LONG_VALUE_E);

    final DataObject periodItem2;
    if (type == PeriodTypeDto.MONTHLY && protocol == Protocol.DSMR_4_2_2) {
      // No status for Monthly values in DSMR4.2.2
      if (selectedValuesSupported) {
        periodItem2 =
            DataObject.newStructureData(
                Arrays.asList(
                    period2Clock,
                    this.createValue(this.PERIOD_2_LONG_VALUE, channel),
                    period2CaptureTime));
      } else {
        periodItem2 =
            DataObject.newStructureData(
                Arrays.asList(
                    period2Clock,
                    period2ValueE,
                    period2ValueE,
                    period2ValueE,
                    period2ValueE,
                    this.createValue(this.PERIOD_2_LONG_VALUE, 1),
                    period1CaptureTime,
                    this.createValue(this.PERIOD_2_LONG_VALUE, 2),
                    period1CaptureTime,
                    this.createValue(this.PERIOD_2_LONG_VALUE, 3),
                    period1CaptureTime,
                    this.createValue(this.PERIOD_2_LONG_VALUE, 4),
                    period1CaptureTime));
      }
    } else {
      if (selectedValuesSupported || type == PeriodTypeDto.INTERVAL) {
        periodItem2 =
            DataObject.newStructureData(
                Arrays.asList(
                    period2Clock,
                    period2Status,
                    this.createValue(this.PERIOD_2_LONG_VALUE, channel),
                    period2CaptureTime));
      } else {
        periodItem2 =
            DataObject.newStructureData(
                Arrays.asList(
                    period2Clock,
                    period2Status,
                    period2ValueE,
                    period2ValueE,
                    period2ValueE,
                    period2ValueE,
                    this.createValue(this.PERIOD_2_LONG_VALUE, 1),
                    period1CaptureTime,
                    this.createValue(this.PERIOD_2_LONG_VALUE, 2),
                    period1CaptureTime,
                    this.createValue(this.PERIOD_2_LONG_VALUE, 3),
                    period1CaptureTime,
                    this.createValue(this.PERIOD_2_LONG_VALUE, 4),
                    period1CaptureTime));
      }
    }

    // Create returnvalue and set in stub
    final DataObject responseDataObject =
        DataObject.newArrayData(Arrays.asList(periodItem1, periodItem2));
    this.connectionStub.addReturnValue(attributeAddressForProfile, responseDataObject);
  }

  private DataObject getDateAsOctetString(final int year, final int month, final int day) {
    final CosemDateTime dateTime = new CosemDateTime(year, month, day, 0, 0, 0, 0);

    return DataObject.newOctetStringData(dateTime.encode());
  }

  private void checkClockValues(
      final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads,
      final PeriodTypeDto type,
      final boolean useNullData) {

    final PeriodicMeterReadsGasResponseItemDto periodicMeterRead1 = periodicMeterReads.get(0);
    assertThat(periodicMeterRead1.getLogTime()).isEqualTo(this.PERIOD_1_CLOCK_VALUE);

    final PeriodicMeterReadsGasResponseItemDto periodicMeterRead2 = periodicMeterReads.get(1);

    if (!useNullData) { // The timestamps should be the same as the times
      // set in the test
      assertThat(periodicMeterRead2.getLogTime()).isEqualTo(this.PERIOD_2_CLOCK_VALUE);
    } else { // The timestamps should be calculated using the periodType,
      // starting from the time of period 1
      if (type == PeriodTypeDto.INTERVAL) {
        assertThat(periodicMeterRead2.getLogTime())
            .isEqualTo(this.PERIOD_2_CLOCK_VALUE_NULL_DATA_PERIOD_HOURLY);
      } else if (type == PeriodTypeDto.DAILY) {
        assertThat(periodicMeterRead2.getLogTime())
            .isEqualTo(this.PERIOD_2_CLOCK_VALUE_NULL_DATA_PERIOD_DAILY);
      } else if (type == PeriodTypeDto.MONTHLY) {
        assertThat(periodicMeterRead2.getLogTime())
            .isEqualTo(this.PERIOD_2_CLOCK_VALUE_NULL_DATA_PERIOD_MONTHLY);
      }
    }
  }

  private void checkValues(
      final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads, final int channel) {

    final PeriodicMeterReadsGasResponseItemDto period1 = periodicMeterReads.get(0);
    final PeriodicMeterReadsGasResponseItemDto period2 = periodicMeterReads.get(1);

    assertThat(period1.getConsumption().getValue().multiply(this.SCALER).intValue())
        .isEqualTo(this.PERIOD_1_LONG_VALUE + channel);
    assertThat(period1.getConsumption().getDlmsUnit()).isEqualTo(DlmsUnitTypeDto.M3);

    assertThat(period2.getConsumption().getValue().multiply(this.SCALER).intValue())
        .isEqualTo(this.PERIOD_2_LONG_VALUE + channel);
    assertThat(period2.getConsumption().getDlmsUnit()).isEqualTo(DlmsUnitTypeDto.M3);
  }

  private void checkAmrStatus(
      final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads,
      final Protocol protocol,
      final PeriodTypeDto type) {

    final PeriodicMeterReadsGasResponseItemDto period1 = periodicMeterReads.get(0);
    final PeriodicMeterReadsGasResponseItemDto period2 = periodicMeterReads.get(1);

    if (protocol == Protocol.DSMR_4_2_2 && type == PeriodTypeDto.MONTHLY) {
      assertThat(period1.getAmrProfileStatusCode()).isNull();
      assertThat(period2.getAmrProfileStatusCode()).isNull();
    } else {
      assertThat(period1.getAmrProfileStatusCode().getAmrProfileStatusCodeFlags())
          .containsExactly(CRITICAL_ERROR, CLOCK_INVALID, DATA_NOT_VALID, DAYLIGHT_SAVING);
      assertThat(period2.getAmrProfileStatusCode().getAmrProfileStatusCodeFlags())
          .containsExactlyInAnyOrder(NOT_USED, CLOCK_ADJUSTED, RECOVERED_VALUE, POWER_DOWN);
    }
  }

  // DSMR4

  private AttributeAddress createAttributeAddressDsmr4Daily(
      final DataObject from,
      final DataObject to,
      final boolean selectiveAccessPeriodicMeterReadsSupported,
      final int channel) {
    final SelectiveAccessDescription expectedSelectiveAccess =
        this.createSelectiveAccessDescriptionDsmr4Daily(
            from, to, selectiveAccessPeriodicMeterReadsSupported, channel);
    return new AttributeAddress(
        this.CLASS_ID_PROFILE, this.OBIS_DAILY_DSMR4, this.ATTR_ID_BUFFER, expectedSelectiveAccess);
  }

  private SelectiveAccessDescription createSelectiveAccessDescriptionDsmr4Daily(
      final DataObject from,
      final DataObject to,
      final boolean selectiveAccessPeriodicMeterReadsSupported,
      final int channel) {

    final List<DataObject> dataObjects =
        selectiveAccessPeriodicMeterReadsSupported
            ? Arrays.asList(
                this.CLOCK,
                this.STATUS,
                this.getDataObjectForGasValueDsmr4(channel, this.ATTR_ID_VALUE),
                this.getDataObjectForGasValueDsmr4(channel, this.ATTR_ID_CAPTURE_TIME))
            : new ArrayList<>();

    final DataObject selectedValues = DataObject.newArrayData(dataObjects);

    final DataObject expectedAccessParam =
        DataObject.newStructureData(Arrays.asList(this.CLOCK, from, to, selectedValues));

    return new SelectiveAccessDescription(1, expectedAccessParam);
  }

  private AttributeAddress createAttributeAddressDsmr4Monthly(
      final DataObject from,
      final DataObject to,
      final boolean selectiveAccessPeriodicMeterReadsSupported,
      final int channel) {
    final SelectiveAccessDescription expectedSelectiveAccess =
        this.createSelectiveAccessDescriptionDsmr4Monthly(
            from, to, selectiveAccessPeriodicMeterReadsSupported, channel);
    return new AttributeAddress(
        this.CLASS_ID_PROFILE,
        this.OBIS_MONTHLY_DSMR4,
        this.ATTR_ID_BUFFER,
        expectedSelectiveAccess);
  }

  private SelectiveAccessDescription createSelectiveAccessDescriptionDsmr4Monthly(
      final DataObject from,
      final DataObject to,
      final boolean selectiveAccessPeriodicMeterReadsSupported,
      final int channel) {

    final List<DataObject> dataObjects =
        selectiveAccessPeriodicMeterReadsSupported
            ? Arrays.asList(
                this.CLOCK,
                this.getDataObjectForGasValueDsmr4(channel, this.ATTR_ID_VALUE),
                this.getDataObjectForGasValueDsmr4(channel, this.ATTR_ID_CAPTURE_TIME))
            : new ArrayList<>();

    final DataObject selectedValues = DataObject.newArrayData(dataObjects);

    final DataObject expectedAccessParam =
        DataObject.newStructureData(Arrays.asList(this.CLOCK, from, to, selectedValues));

    return new SelectiveAccessDescription(1, expectedAccessParam);
  }

  private AttributeAddress createAttributeAddressDsmr4Interval(
      final DataObject from, final DataObject to, final int channel) {
    final SelectiveAccessDescription expectedSelectiveAccess =
        this.createSelectiveAccessDescriptionDsmr4Interval(from, to);
    return new AttributeAddress(
        this.CLASS_ID_PROFILE,
        this.getObisCodeForChannel(this.OBIS_INTERVAL_DSMR4, channel),
        this.ATTR_ID_BUFFER,
        expectedSelectiveAccess);
  }

  private SelectiveAccessDescription createSelectiveAccessDescriptionDsmr4Interval(
      final DataObject from, final DataObject to) {

    final DataObject selectedValues = DataObject.newArrayData(Collections.emptyList());

    final DataObject expectedAccessParam =
        DataObject.newStructureData(Arrays.asList(this.CLOCK, from, to, selectedValues));

    return new SelectiveAccessDescription(1, expectedAccessParam);
  }

  // SMR5

  private AttributeAddress createAttributeAddressSmr5Daily(
      final DataObject from, final DataObject to, final int channel) {
    final SelectiveAccessDescription expectedSelectiveAccess =
        this.createSelectiveAccessDescriptionSmr5(from, to);
    return new AttributeAddress(
        this.CLASS_ID_PROFILE,
        this.getObisCodeForChannel(this.OBIS_DAILY_SMR5, channel),
        this.ATTR_ID_BUFFER,
        expectedSelectiveAccess);
  }

  private AttributeAddress createAttributeAddressSmr5Monthly(
      final DataObject from, final DataObject to, final int channel) {
    final SelectiveAccessDescription expectedSelectiveAccess =
        this.createSelectiveAccessDescriptionSmr5(from, to);
    return new AttributeAddress(
        this.CLASS_ID_PROFILE,
        this.getObisCodeForChannel(this.OBIS_MONTHLY_SMR5, channel),
        this.ATTR_ID_BUFFER,
        expectedSelectiveAccess);
  }

  private AttributeAddress createAttributeAddressSmr5Interval(
      final DataObject from, final DataObject to, final int channel) {
    final SelectiveAccessDescription expectedSelectiveAccess =
        this.createSelectiveAccessDescriptionSmr5(from, to);
    return new AttributeAddress(
        this.CLASS_ID_PROFILE,
        this.getObisCodeForChannel(this.OBIS_INTERVAL_SMR5, channel),
        this.ATTR_ID_BUFFER,
        expectedSelectiveAccess);
  }

  private SelectiveAccessDescription createSelectiveAccessDescriptionSmr5(
      final DataObject from, final DataObject to) {

    final DataObject selectedValues = DataObject.newArrayData(Collections.emptyList());

    final DataObject expectedAccessParam =
        DataObject.newStructureData(Arrays.asList(this.CLOCK, from, to, selectedValues));

    return new SelectiveAccessDescription(1, expectedAccessParam);
  }

  private ObisCode getObisCodeForChannel(final String obis, final int channel) {
    return new ObisCode(obis.replace("<c>", String.valueOf(channel)));
  }

  private DataObject getDataObjectForGasValueDsmr4(final int channel, final byte attributeId) {
    return DataObject.newStructureData(
        Arrays.asList(
            DataObject.newUInteger16Data(this.CLASS_ID_EXTENDED_REGISTER),
            DataObject.newOctetStringData(
                this.getObisCodeForChannel(this.OBIS_GAS_VALUE_DSMR4, channel).bytes()),
            DataObject.newInteger8Data(attributeId),
            DataObject.newUInteger16Data(0)));
  }

  private DataObject createValue(final long value, final int channel) {
    // Add channel to value to make each value different
    return DataObject.newUInteger32Data(value + channel);
  }
}
