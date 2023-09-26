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
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
  private final String OBIS_GAS_VALUE_DSMR4 = "0.<c>.24.2.1.255";

  private final int CLASS_ID_CLOCK = 8;
  private final int CLASS_ID_DATA = 1;
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

  private final List<Protocol> protocolsNoStatusMonthlyValues =
      List.of(Protocol.DSMR_2_2, Protocol.DSMR_4_2_2, Protocol.SMR_4_3);

  private static final List<Integer> ALL_CHANNELS = List.of(1, 2, 3, 4);

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

  private static Stream<Arguments> combinationsDsmr() {
    final List<Protocol> dsmrProtocols =
        Arrays.stream(Protocol.values())
            .filter(protocol -> protocol.isDsmr4() || protocol.isDsmr2())
            .collect(Collectors.toList());

    return generateCombinations(dsmrProtocols);
  }

  private static Stream<Arguments> combinationsSmr5() {
    final List<Protocol> smr5Protocols =
        Arrays.stream(Protocol.values()).filter(Protocol::isSmr5).toList();

    return generateCombinations(smr5Protocols);
  }

  private static Stream<Arguments> generateCombinations(final List<Protocol> protocols) {
    final List<Arguments> arguments = new ArrayList<>();

    for (final Protocol protocol : protocols) {
      for (final PeriodTypeDto periodType : PeriodTypeDto.values()) {
        for (final int channel : ALL_CHANNELS) {
          arguments.add(Arguments.of(protocol, periodType, channel));
        }
      }
    }

    return arguments.stream();
  }

  @ParameterizedTest
  @MethodSource("combinationsDsmr")
  void testExecuteDsmrNoSelectedValues(
      final Protocol protocol, final PeriodTypeDto type, final int channel) throws Exception {
    try {
      this.testExecute(protocol, type, false, false, channel);
    } catch (final IllegalArgumentException e) {
      assertThat(this.exceptionExpected(protocol, type))
          .withFailMessage(
              "Reading %s values should be supported for %s. Error: %s", type, protocol.name(), e)
          .isTrue();
    }
  }

  @ParameterizedTest
  @MethodSource("combinationsDsmr")
  void testExecuteDsmr(final Protocol protocol, final PeriodTypeDto type, final int channel)
      throws Exception {
    try {
      this.testExecute(protocol, type, false, true, channel);
    } catch (final IllegalArgumentException e) {
      assertThat(this.exceptionExpected(protocol, type))
          .withFailMessage(
              "Reading %s values should be supported for %s. Error: %s", type, protocol.name(), e)
          .isTrue();
    }
  }

  private boolean exceptionExpected(final Protocol protocol, final PeriodTypeDto type) {
    return protocol == Protocol.DSMR_2_2 && type == PeriodTypeDto.DAILY;
  }

  @ParameterizedTest
  @MethodSource("combinationsSmr5")
  void testExecuteSmr5(final Protocol protocol, final PeriodTypeDto type, final int channel)
      throws Exception {
    this.testExecute(protocol, type, false, true, channel);
  }

  @ParameterizedTest
  @MethodSource("combinationsSmr5")
  void testExecuteSmr5_WithNullData(
      final Protocol protocol, final PeriodTypeDto type, final int channel) throws Exception {
    this.testExecute(protocol, type, true, true, channel);
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

    if (protocol.isDsmr2() || protocol.isDsmr4()) {
      if (type == PeriodTypeDto.DAILY) {
        return this.createAttributeAddressDsmr4Daily(
            from, to, device.isSelectiveAccessPeriodicMeterReadsSupported(), channel);
      } else if (type == PeriodTypeDto.MONTHLY) {
        return this.createAttributeAddressDsmr4Monthly(
            from, to, device.isSelectiveAccessPeriodicMeterReadsSupported(), channel, protocol);
      } else if (type == PeriodTypeDto.INTERVAL) {
        return this.createAttributeAddressDsmr4Interval(from, to, channel);
      }
    } else if (protocol.isSmr5()) {
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

    final DataObject period1CaptureTime = DataObject.newDateTimeData(this.PERIOD_1_CAPTURE_TIME);

    final DataObject periodItem1 =
        this.createPeriodItem(
            type,
            protocol,
            selectedValuesSupported,
            channel,
            this.PERIOD_1_CLOCK,
            this.PERIOD1_AMR_STATUS_VALUE,
            this.PERIOD_1_LONG_VALUE_E,
            this.PERIOD_1_LONG_VALUE,
            period1CaptureTime);

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

    final DataObject periodItem2 =
        this.createPeriodItem(
            type,
            protocol,
            selectedValuesSupported,
            channel,
            period2Clock,
            this.PERIOD2_AMR_STATUS_VALUE,
            this.PERIOD_2_LONG_VALUE_E,
            this.PERIOD_2_LONG_VALUE,
            period2CaptureTime);

    // Create returnvalue and set in stub
    final DataObject responseDataObject =
        DataObject.newArrayData(Arrays.asList(periodItem1, periodItem2));
    this.connectionStub.addReturnValue(attributeAddressForProfile, responseDataObject);
  }

  private DataObject createPeriodItem(
      final PeriodTypeDto type,
      final Protocol protocol,
      final boolean selectedValuesSupported,
      final int channel,
      final DataObject clock,
      final short statusValue,
      final long longValueE,
      final long longValueG,
      final DataObject captureTime) {
    final DataObject periodStatus = DataObject.newUInteger8Data(statusValue);
    final DataObject periodValueE = DataObject.newUInteger32Data(longValueE);

    final List<DataObject> items = new ArrayList<>();

    // Overview protocols - periodtypes (Interval/Daily/Monthly) - selected values supported
    //
    //               DSMR2.2  DSMR4.2.2 / SMR4.3  SMR5.0-5.5
    //                I D M          I D M          I D M
    //
    // Clock          1 X 1          1 1 1          1 1 1
    // Status         1 X 0          1 1 0          1 1 1
    // E values       0 X 0          0 0 0          0 0 0
    // G values       1 X 1          1 1 1          1 1 1
    // Capture time   0 X 0          1 1 1          1 1 1

    // Overview protocols - periodtypes - selected values NOT supported
    //
    //               DSMR2.2  DSMR4.2.2 / SMR4.3
    //                I D M          I D M
    //
    // Clock          1 X 1          1 1 1
    // Status         1 X 0          1 1 0
    // E values       4 X 4          4 4 4
    // G values       4 X 4          4 4 4
    // Capture time   4 X 4          4 4 4

    // Always add clock first
    items.add(clock);

    // Add status
    if (type != PeriodTypeDto.MONTHLY || !this.protocolsNoStatusMonthlyValues.contains(protocol)) {
      items.add(periodStatus);
    }

    // Add E values (import rate 1 and 2, export rate 1 and 2)
    if (!selectedValuesSupported && type != PeriodTypeDto.INTERVAL) {
      items.addAll(List.of(periodValueE, periodValueE, periodValueE, periodValueE));
    }

    // Add G values and capture times
    if (selectedValuesSupported || type == PeriodTypeDto.INTERVAL) {
      items.add(this.createValue(longValueG, channel));
      if (protocol != Protocol.DSMR_2_2) {
        items.add(captureTime);
      }
    } else {
      for (final int c : this.ALL_CHANNELS) {
        items.add(this.createValue(longValueG, c));
        if (protocol != Protocol.DSMR_2_2) {
          items.add(captureTime);
        }
      }
    }

    return DataObject.newStructureData(items);
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

    if (type == PeriodTypeDto.MONTHLY && this.protocolsNoStatusMonthlyValues.contains(protocol)) {
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
      final int channel,
      final Protocol protocol) {
    final SelectiveAccessDescription expectedSelectiveAccess =
        this.createSelectiveAccessDescriptionDsmr4Monthly(
            from, to, selectiveAccessPeriodicMeterReadsSupported, channel, protocol);
    return new AttributeAddress(
        this.CLASS_ID_PROFILE,
        this.OBIS_MONTHLY_DSMR4,
        this.ATTR_ID_BUFFER,
        expectedSelectiveAccess);
  }

  private SelectiveAccessDescription createSelectiveAccessDescriptionDsmr4Monthly(
      final DataObject from,
      final DataObject to,
      final boolean selectedValuesSupported,
      final int channel,
      final Protocol protocol) {

    final List<DataObject> dataObjects;

    if (!selectedValuesSupported) {
      dataObjects = new ArrayList<>();
    } else if (protocol == Protocol.DSMR_2_2) {
      dataObjects =
          Arrays.asList(
              this.CLOCK, this.getDataObjectForGasValueDsmr4(channel, this.ATTR_ID_VALUE));
    } else {
      dataObjects =
          Arrays.asList(
              this.CLOCK,
              this.getDataObjectForGasValueDsmr4(channel, this.ATTR_ID_VALUE),
              this.getDataObjectForGasValueDsmr4(channel, this.ATTR_ID_CAPTURE_TIME));
    }

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
