// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigConfiguration;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionManagerStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.AttributeAddressAssert;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.AmrProfileStatusCodeHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsDateTimeConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseItemDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class GetPeriodicMeterReadsCommandExecutorIntegrationTest {

  private GetPeriodicMeterReadsCommandExecutor executor;

  private DlmsHelper dlmsHelper;
  private AmrProfileStatusCodeHelper amrProfileStatusCodeHelper;
  private DlmsObjectConfigService dlmsObjectConfigService;

  private DlmsConnectionManagerStub connectionManagerStub;
  private DlmsConnectionStub connectionStub;

  private final ObisCode OBIS_DAILY_DSMR4 = new ObisCode("1.0.99.2.0.255");
  private final ObisCode OBIS_INTERVAL_DSMR4 = new ObisCode("1.0.99.1.0.255");
  private final ObisCode OBIS_MONTHLY_DSMR4 = new ObisCode("0.0.98.1.0.255");

  private final ObisCode OBIS_DAILY_SMR5 = new ObisCode("1.0.99.2.0.255");
  private final ObisCode OBIS_INTERVAL_SMR5 = new ObisCode("1.0.99.1.0.255");
  private final ObisCode OBIS_MONTHLY_SMR5 = new ObisCode("1.0.98.1.0.255");

  private final ObisCode OBIS_CLOCK = new ObisCode("0.0.1.0.0.255");
  private final ObisCode OBIS_STATUS = new ObisCode("0.0.96.10.2.255");
  private final ObisCode OBIS_ACTIVE_ENERGY_IMPORT = new ObisCode("1.0.1.8.0.255");
  private final ObisCode OBIS_ACTIVE_ENERGY_EXPORT = new ObisCode("1.0.2.8.0.255");
  private final ObisCode OBIS_ACTIVE_ENERGY_IMPORT_RATE_1 = new ObisCode("1.0.1.8.1.255");
  private final ObisCode OBIS_ACTIVE_ENERGY_IMPORT_RATE_2 = new ObisCode("1.0.1.8.2.255");
  private final ObisCode OBIS_ACTIVE_ENERGY_EXPORT_RATE_1 = new ObisCode("1.0.2.8.1.255");
  private final ObisCode OBIS_ACTIVE_ENERGY_EXPORT_RATE_2 = new ObisCode("1.0.2.8.2.255");
  private final ObisCode OBIS_MBUS_CHANNEL_1 = new ObisCode("0.1.24.2.1.255");
  private final ObisCode OBIS_MBUS_CHANNEL_2 = new ObisCode("0.2.24.2.1.255");
  private final ObisCode OBIS_MBUS_CHANNEL_3 = new ObisCode("0.3.24.2.1.255");
  private final ObisCode OBIS_MBUS_CHANNEL_4 = new ObisCode("0.4.24.2.1.255");

  private final int CLASS_ID_CLOCK = 8;
  private final int CLASS_ID_DATA = 1;
  private final int CLASS_ID_REGISTER = 3;
  private final int CLASS_ID_EXTENDED_REGISTER = 4;
  private final int CLASS_ID_PROFILE = 7;

  private final byte ATTR_ID_VALUE = 2;
  private final byte ATTR_ID_BUFFER = 2;
  private final byte ATTR_ID_SCALER_UNIT = 3;

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

  private final DataObject ACTIVE_ENERGY_IMPORT_RATE_1 =
      DataObject.newStructureData(
          Arrays.asList(
              DataObject.newUInteger16Data(this.CLASS_ID_REGISTER),
              DataObject.newOctetStringData(this.OBIS_ACTIVE_ENERGY_IMPORT_RATE_1.bytes()),
              DataObject.newInteger8Data(this.ATTR_ID_VALUE),
              DataObject.newUInteger16Data(0)));

  private final DataObject ACTIVE_ENERGY_IMPORT_RATE_2 =
      DataObject.newStructureData(
          Arrays.asList(
              DataObject.newUInteger16Data(this.CLASS_ID_REGISTER),
              DataObject.newOctetStringData(this.OBIS_ACTIVE_ENERGY_IMPORT_RATE_2.bytes()),
              DataObject.newInteger8Data(this.ATTR_ID_VALUE),
              DataObject.newUInteger16Data(0)));

  private final DataObject ACTIVE_ENERGY_EXPORT_RATE_1 =
      DataObject.newStructureData(
          Arrays.asList(
              DataObject.newUInteger16Data(this.CLASS_ID_REGISTER),
              DataObject.newOctetStringData(this.OBIS_ACTIVE_ENERGY_EXPORT_RATE_1.bytes()),
              DataObject.newInteger8Data(this.ATTR_ID_VALUE),
              DataObject.newUInteger16Data(0)));

  private final DataObject ACTIVE_ENERGY_EXPORT_RATE_2 =
      DataObject.newStructureData(
          Arrays.asList(
              DataObject.newUInteger16Data(this.CLASS_ID_REGISTER),
              DataObject.newOctetStringData(this.OBIS_ACTIVE_ENERGY_EXPORT_RATE_2.bytes()),
              DataObject.newInteger8Data(this.ATTR_ID_VALUE),
              DataObject.newUInteger16Data(0)));

  private Date timeFrom;
  private Date timeTo;
  private DataObject period1Clock;
  private DataObject period2Clock;
  private Date period1ClockValue;
  private Date period2ClockValue;
  private Date period2ClockValueNullDataPeriod15Min;
  private Date period2ClockValueNullDataPeriodDaily;
  private Date period2ClockValueNullDataPeriodMonthly;

  private final int AMOUNT_OF_PERIODS = 2;

  private final byte AMR_STATUS_VALUE = 8;
  private final long PERIOD_1_LONG_VALUE_1 = 1000L;
  private final long PERIOD_1_LONG_VALUE_2 = 2000L;
  private final long PERIOD_1_LONG_VALUE_3 = 3000L;
  private final long PERIOD_1_LONG_VALUE_4 = 4000L;
  private final long PERIOD_2_LONG_VALUE_1 = 1500L;
  private final long PERIOD_2_LONG_VALUE_2 = 2500L;
  private final long PERIOD_2_LONG_VALUE_3 = 3500L;
  private final long PERIOD_2_LONG_VALUE_4 = 4500L;

  private final int DLMS_ENUM_VALUE_WH = 30;

  private void initDates() {

    this.timeFrom = new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime();
    this.timeTo = new GregorianCalendar(2019, Calendar.FEBRUARY, 2).getTime();
    this.period1Clock = this.getDateAsOctetString(2019, 1, 1);
    this.period2Clock = this.getDateAsOctetString(2019, 1, 2);
    this.period1ClockValue = new GregorianCalendar(2019, Calendar.JANUARY, 1, 0, 0).getTime();
    this.period2ClockValue = new GregorianCalendar(2019, Calendar.JANUARY, 2, 0, 0).getTime();
    this.period2ClockValueNullDataPeriod15Min =
        new GregorianCalendar(2019, Calendar.JANUARY, 1, 0, 15).getTime();
    this.period2ClockValueNullDataPeriodDaily =
        new GregorianCalendar(2019, Calendar.JANUARY, 2, 0, 0).getTime();
    this.period2ClockValueNullDataPeriodMonthly =
        new GregorianCalendar(2019, Calendar.FEBRUARY, 1, 0, 0).getTime();
  }

  @BeforeEach
  public void setUp() {

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
    this.dlmsObjectConfigService =
        new DlmsObjectConfigService(
            this.dlmsHelper, dlmsObjectConfigConfiguration.getDlmsObjectConfigs());

    this.executor =
        new GetPeriodicMeterReadsCommandExecutor(
            this.dlmsHelper, this.amrProfileStatusCodeHelper, this.dlmsObjectConfigService);
    this.connectionStub = new DlmsConnectionStub();
    this.connectionManagerStub = new DlmsConnectionManagerStub(this.connectionStub);

    this.connectionStub.setDefaultReturnValue(DataObject.newArrayData(Collections.emptyList()));

    // reset to original TimeZone
    TimeZone.setDefault(defaultTimeZone);
    DateTimeZone.setDefault(defaultDateTimeZone);
  }

  @ParameterizedTest
  @EnumSource(PeriodTypeDto.class)
  void testExecuteDsmr4NoSelectiveAccess(final PeriodTypeDto type) throws Exception {
    this.testExecute(Protocol.DSMR_4_2_2, type, false, false);
  }

  @ParameterizedTest
  @EnumSource(PeriodTypeDto.class)
  void testExecuteDsmr4(final PeriodTypeDto type) throws Exception {
    this.testExecute(Protocol.DSMR_4_2_2, type, false, true);
  }

  @ParameterizedTest
  @EnumSource(PeriodTypeDto.class)
  void testExecuteSmr5_0(final PeriodTypeDto type) throws Exception {
    this.testExecute(Protocol.SMR_5_0_0, type, false, true);
  }

  @ParameterizedTest
  @EnumSource(PeriodTypeDto.class)
  void testExecuteSmr5_0_WithNullData(final PeriodTypeDto type) throws Exception {
    this.testExecute(Protocol.SMR_5_0_0, type, true, true);
  }

  @ParameterizedTest
  @EnumSource(PeriodTypeDto.class)
  void testExecuteSmr5_1(final PeriodTypeDto type) throws Exception {
    this.testExecute(Protocol.SMR_5_1, type, false, true);
  }

  @ParameterizedTest
  @EnumSource(PeriodTypeDto.class)
  void testExecuteSmr5_1_WithNullData(final PeriodTypeDto type) throws Exception {
    this.testExecute(Protocol.SMR_5_1, type, true, true);
  }

  private void testExecute(
      final Protocol protocol,
      final PeriodTypeDto type,
      final boolean useNullData,
      final boolean selectiveAccessPeriodicMeterReadsSupported)
      throws Exception {

    // SETUP
    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder().withCorrelationUid("123456").build();

    // Reset stub
    this.connectionStub.clearRequestedAttributeAddresses();

    // Create device with requested protocol version
    final DlmsDevice device =
        this.createDlmsDevice(protocol, selectiveAccessPeriodicMeterReadsSupported);

    // Create request object
    final PeriodicMeterReadsRequestDto request =
        new PeriodicMeterReadsRequestDto(type, this.timeFrom, this.timeTo);

    // Get expected values
    final AttributeAddress expectedAddressProfile =
        this.createAttributeAddress(protocol, type, this.timeFrom, this.timeTo, device);
    final List<AttributeAddress> expectedScalerUnitAddresses =
        this.getScalerUnitAttributeAddresses(type, selectiveAccessPeriodicMeterReadsSupported);
    final int expectedTotalNumberOfAttributeAddresses = expectedScalerUnitAddresses.size() + 1;

    // Set response in stub
    this.setResponseForProfile(expectedAddressProfile, protocol, type, useNullData);
    this.setResponsesForScalerUnit(expectedScalerUnitAddresses);

    // CALL
    final PeriodicMeterReadsResponseDto response =
        this.executor.execute(this.connectionManagerStub, device, request, messageMetadata);

    // VERIFY

    // Get resulting requests from connection stub
    final List<AttributeAddress> requestedAttributeAddresses =
        this.connectionStub.getRequestedAttributeAddresses();
    assertThat(requestedAttributeAddresses).hasSize(expectedTotalNumberOfAttributeAddresses);

    // There should be 1 request to the buffer (id = 2) of a profile
    // (class-id = 7)
    final AttributeAddress actualAttributeAddressProfile =
        requestedAttributeAddresses.stream()
            .filter(a -> a.getClassId() == this.CLASS_ID_PROFILE)
            .collect(Collectors.toList())
            .get(0);

    AttributeAddressAssert.is(actualAttributeAddressProfile, expectedAddressProfile);

    // Check the amount of requests to the scaler_units of the meter values
    // in the registers
    final List<AttributeAddress> attributeAddressesScalerUnit =
        requestedAttributeAddresses.stream()
            .filter(
                a ->
                    (a.getClassId() == this.CLASS_ID_REGISTER
                            || a.getClassId() == this.CLASS_ID_EXTENDED_REGISTER)
                        && a.getId() == this.ATTR_ID_SCALER_UNIT)
            .collect(Collectors.toList());
    assertThat(attributeAddressesScalerUnit).hasSize(expectedScalerUnitAddresses.size());

    // Check response
    assertThat(response.getPeriodType()).isEqualTo(type);
    final List<PeriodicMeterReadsResponseItemDto> periodicMeterReads =
        response.getPeriodicMeterReads();
    assertThat(periodicMeterReads).hasSize(this.AMOUNT_OF_PERIODS);

    this.checkClockValues(periodicMeterReads, type, useNullData);
    this.checkValues(periodicMeterReads, type);
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
      final DlmsDevice device)
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
            from, to, device.isSelectiveAccessPeriodicMeterReadsSupported());
      } else if (type == PeriodTypeDto.MONTHLY) {
        return this.createAttributeAddressDsmr4Monthly(
            from, to, device.isSelectiveAccessPeriodicMeterReadsSupported());
      } else if (type == PeriodTypeDto.INTERVAL) {
        return this.createAttributeAddressDsmr4Interval(from, to);
      }
    } else if (protocol == Protocol.SMR_5_0_0 || protocol == Protocol.SMR_5_1) {
      if (type == PeriodTypeDto.DAILY) {
        return this.createAttributeAddressSmr5Daily(from, to);
      } else if (type == PeriodTypeDto.MONTHLY) {
        return this.createAttributeAddressSmr5Monthly(from, to);
      } else if (type == PeriodTypeDto.INTERVAL) {
        return this.createAttributeAddressSmr5Interval(from, to);
      }
    }

    throw new Exception(
        "Invalid combination of protocol "
            + protocol.getName()
            + " and version "
            + protocol.getVersion());
  }

  private List<AttributeAddress> getScalerUnitAttributeAddresses(
      final PeriodTypeDto type, final boolean selectedValuesSupported) throws Exception {
    final List<AttributeAddress> attributeAddresses = new ArrayList<>();

    switch (type) {
      case MONTHLY:
      case DAILY:
        attributeAddresses.add(
            new AttributeAddress(
                this.CLASS_ID_REGISTER,
                this.OBIS_ACTIVE_ENERGY_IMPORT_RATE_1,
                this.ATTR_ID_SCALER_UNIT,
                null));
        attributeAddresses.add(
            new AttributeAddress(
                this.CLASS_ID_REGISTER,
                this.OBIS_ACTIVE_ENERGY_IMPORT_RATE_2,
                this.ATTR_ID_SCALER_UNIT,
                null));
        attributeAddresses.add(
            new AttributeAddress(
                this.CLASS_ID_REGISTER,
                this.OBIS_ACTIVE_ENERGY_EXPORT_RATE_1,
                this.ATTR_ID_SCALER_UNIT,
                null));
        attributeAddresses.add(
            new AttributeAddress(
                this.CLASS_ID_REGISTER,
                this.OBIS_ACTIVE_ENERGY_EXPORT_RATE_2,
                this.ATTR_ID_SCALER_UNIT,
                null));
        if (!selectedValuesSupported) {
          attributeAddresses.add(
              new AttributeAddress(
                  this.CLASS_ID_EXTENDED_REGISTER,
                  this.OBIS_MBUS_CHANNEL_1,
                  this.ATTR_ID_SCALER_UNIT,
                  null));
          attributeAddresses.add(
              new AttributeAddress(
                  this.CLASS_ID_EXTENDED_REGISTER,
                  this.OBIS_MBUS_CHANNEL_2,
                  this.ATTR_ID_SCALER_UNIT,
                  null));
          attributeAddresses.add(
              new AttributeAddress(
                  this.CLASS_ID_EXTENDED_REGISTER,
                  this.OBIS_MBUS_CHANNEL_3,
                  this.ATTR_ID_SCALER_UNIT,
                  null));
          attributeAddresses.add(
              new AttributeAddress(
                  this.CLASS_ID_EXTENDED_REGISTER,
                  this.OBIS_MBUS_CHANNEL_4,
                  this.ATTR_ID_SCALER_UNIT,
                  null));
        }
        break;
      case INTERVAL:
        attributeAddresses.add(
            new AttributeAddress(
                this.CLASS_ID_REGISTER,
                this.OBIS_ACTIVE_ENERGY_IMPORT,
                this.ATTR_ID_SCALER_UNIT,
                null));
        attributeAddresses.add(
            new AttributeAddress(
                this.CLASS_ID_REGISTER,
                this.OBIS_ACTIVE_ENERGY_EXPORT,
                this.ATTR_ID_SCALER_UNIT,
                null));
        break;
      default:
        throw new Exception("Unexpected period type " + type);
    }
    return attributeAddresses;
  }

  private void setResponseForProfile(
      final AttributeAddress attributeAddressForProfile,
      final Protocol protocol,
      final PeriodTypeDto type,
      final boolean useNullData) {

    // PERIOD 1

    final DataObject period1Clock = this.period1Clock;
    final DataObject period1Status = DataObject.newUInteger8Data(this.AMR_STATUS_VALUE);
    final DataObject period1Value1 = DataObject.newUInteger32Data(this.PERIOD_1_LONG_VALUE_1);
    final DataObject period1Value2 = DataObject.newUInteger32Data(this.PERIOD_1_LONG_VALUE_2);
    final DataObject period1Value3 = DataObject.newUInteger32Data(this.PERIOD_1_LONG_VALUE_3);
    final DataObject period1Value4 = DataObject.newUInteger32Data(this.PERIOD_1_LONG_VALUE_4);

    final DataObject periodItem1;
    if (type == PeriodTypeDto.MONTHLY && protocol == Protocol.DSMR_4_2_2) {
      periodItem1 =
          DataObject.newStructureData(
              Arrays.asList(
                  period1Clock, period1Value1, period1Value2, period1Value3, period1Value4));
    } else {
      periodItem1 =
          DataObject.newStructureData(
              Arrays.asList(
                  period1Clock,
                  period1Status,
                  period1Value1,
                  period1Value2,
                  period1Value3,
                  period1Value4));
    }

    // PERIOD 2

    final DataObject period2Clock;
    if (useNullData) {
      period2Clock = DataObject.newNullData();
    } else {
      period2Clock = this.period2Clock;
    }
    final DataObject period2Status = DataObject.newUInteger8Data(this.AMR_STATUS_VALUE);
    final DataObject period2Value1 = DataObject.newUInteger32Data(this.PERIOD_2_LONG_VALUE_1);
    final DataObject period2Value2 = DataObject.newUInteger32Data(this.PERIOD_2_LONG_VALUE_2);
    final DataObject period2Value3 = DataObject.newUInteger32Data(this.PERIOD_2_LONG_VALUE_3);
    final DataObject period2Value4 = DataObject.newUInteger32Data(this.PERIOD_2_LONG_VALUE_4);

    final DataObject periodItem2;
    if (type == PeriodTypeDto.MONTHLY && protocol == Protocol.DSMR_4_2_2) {
      // No status for Monthly values in DSMR4.2.2
      periodItem2 =
          DataObject.newStructureData(
              Arrays.asList(
                  period2Clock, period2Value1, period2Value2, period2Value3, period2Value4));
    } else {
      periodItem2 =
          DataObject.newStructureData(
              Arrays.asList(
                  period2Clock,
                  period2Status,
                  period2Value1,
                  period2Value2,
                  period2Value3,
                  period2Value4));
    }

    // Create returnvalue and set in stub
    final DataObject responseDataObject =
        DataObject.newArrayData(Arrays.asList(periodItem1, periodItem2));
    this.connectionStub.addReturnValue(attributeAddressForProfile, responseDataObject);
  }

  private void setResponsesForScalerUnit(
      final List<AttributeAddress> attributeAddressesForScalerUnit) {
    final DataObject responseDataObject =
        DataObject.newStructureData(
            DataObject.newInteger8Data((byte) 0),
            DataObject.newEnumerateData(this.DLMS_ENUM_VALUE_WH));

    for (final AttributeAddress attributeAddress : attributeAddressesForScalerUnit) {
      this.connectionStub.addReturnValue(attributeAddress, responseDataObject);
    }
  }

  private DataObject getDateAsOctetString(final int year, final int month, final int day) {
    final CosemDateTime dateTime = new CosemDateTime(year, month, day, 0, 0, 0, 0);

    return DataObject.newOctetStringData(dateTime.encode());
  }

  private void checkClockValues(
      final List<PeriodicMeterReadsResponseItemDto> periodicMeterReads,
      final PeriodTypeDto type,
      final boolean useNullData) {

    final PeriodicMeterReadsResponseItemDto periodicMeterRead1 = periodicMeterReads.get(0);

    assertThat(periodicMeterRead1.getLogTime()).isEqualTo(this.period1ClockValue);

    final PeriodicMeterReadsResponseItemDto periodicMeterRead2 = periodicMeterReads.get(1);

    if (!useNullData) { // The timestamps should be the same as the times
      // set in the test
      assertThat(periodicMeterRead2.getLogTime()).isEqualTo(this.period2ClockValue);
    } else { // The timestamps should be calculated using the periodType,
      // starting from the time of period 1
      if (type == PeriodTypeDto.INTERVAL) {
        assertThat(periodicMeterRead2.getLogTime())
            .isEqualTo(this.period2ClockValueNullDataPeriod15Min);
      } else if (type == PeriodTypeDto.DAILY) {
        assertThat(periodicMeterRead2.getLogTime())
            .isEqualTo(this.period2ClockValueNullDataPeriodDaily);
      } else if (type == PeriodTypeDto.MONTHLY) {
        assertThat(periodicMeterRead2.getLogTime())
            .isEqualTo(this.period2ClockValueNullDataPeriodMonthly);
      }
    }
  }

  private void checkValues(
      final List<PeriodicMeterReadsResponseItemDto> periodicMeterReads, final PeriodTypeDto type) {

    final PeriodicMeterReadsResponseItemDto period1 = periodicMeterReads.get(0);
    final PeriodicMeterReadsResponseItemDto period2 = periodicMeterReads.get(1);

    if (type == PeriodTypeDto.MONTHLY || type == PeriodTypeDto.DAILY) {
      assertThat(period1.getActiveEnergyImportTariffOne().getValue().longValue())
          .isEqualTo(this.PERIOD_1_LONG_VALUE_1);
      assertThat(period1.getActiveEnergyImportTariffTwo().getValue().longValue())
          .isEqualTo(this.PERIOD_1_LONG_VALUE_2);
      assertThat(period1.getActiveEnergyExportTariffOne().getValue().longValue())
          .isEqualTo(this.PERIOD_1_LONG_VALUE_3);
      assertThat(period1.getActiveEnergyExportTariffTwo().getValue().longValue())
          .isEqualTo(this.PERIOD_1_LONG_VALUE_4);
      assertThat(period2.getActiveEnergyImportTariffOne().getValue().longValue())
          .isEqualTo(this.PERIOD_2_LONG_VALUE_1);
      assertThat(period2.getActiveEnergyImportTariffTwo().getValue().longValue())
          .isEqualTo(this.PERIOD_2_LONG_VALUE_2);
      assertThat(period2.getActiveEnergyExportTariffOne().getValue().longValue())
          .isEqualTo(this.PERIOD_2_LONG_VALUE_3);
      assertThat(period2.getActiveEnergyExportTariffTwo().getValue().longValue())
          .isEqualTo(this.PERIOD_2_LONG_VALUE_4);
    } else { // INTERVAL, only total values
      assertThat(period1.getActiveEnergyImport().getValue().longValue())
          .isEqualTo(this.PERIOD_1_LONG_VALUE_1);
      assertThat(period1.getActiveEnergyExport().getValue().longValue())
          .isEqualTo(this.PERIOD_1_LONG_VALUE_2);
      assertThat(period2.getActiveEnergyImport().getValue().longValue())
          .isEqualTo(this.PERIOD_2_LONG_VALUE_1);
      assertThat(period2.getActiveEnergyExport().getValue().longValue())
          .isEqualTo(this.PERIOD_2_LONG_VALUE_2);
    }
  }

  // DSMR4

  private AttributeAddress createAttributeAddressDsmr4Daily(
      final DataObject from,
      final DataObject to,
      final boolean selectiveAccessPeriodicMeterReadsSupported) {
    final SelectiveAccessDescription expectedSelectiveAccess =
        this.createSelectiveAccessDescriptionDsmr4Daily(
            from, to, selectiveAccessPeriodicMeterReadsSupported);
    return new AttributeAddress(
        this.CLASS_ID_PROFILE, this.OBIS_DAILY_DSMR4, this.ATTR_ID_BUFFER, expectedSelectiveAccess);
  }

  private SelectiveAccessDescription createSelectiveAccessDescriptionDsmr4Daily(
      final DataObject from,
      final DataObject to,
      final boolean selectiveAccessPeriodicMeterReadsSupported) {

    final List<DataObject> dataObjects =
        selectiveAccessPeriodicMeterReadsSupported
            ? Arrays.asList(
                this.CLOCK,
                this.STATUS,
                this.ACTIVE_ENERGY_IMPORT_RATE_1,
                this.ACTIVE_ENERGY_IMPORT_RATE_2,
                this.ACTIVE_ENERGY_EXPORT_RATE_1,
                this.ACTIVE_ENERGY_EXPORT_RATE_2)
            : new ArrayList<>();

    final DataObject selectedValues = DataObject.newArrayData(dataObjects);

    final DataObject expectedAccessParam =
        DataObject.newStructureData(Arrays.asList(this.CLOCK, from, to, selectedValues));

    return new SelectiveAccessDescription(1, expectedAccessParam);
  }

  private AttributeAddress createAttributeAddressDsmr4Monthly(
      final DataObject from,
      final DataObject to,
      final boolean selectiveAccessPeriodicMeterReadsSupported) {
    final SelectiveAccessDescription expectedSelectiveAccess =
        this.createSelectiveAccessDescriptionDsmr4Monthly(
            from, to, selectiveAccessPeriodicMeterReadsSupported);
    return new AttributeAddress(
        this.CLASS_ID_PROFILE,
        this.OBIS_MONTHLY_DSMR4,
        this.ATTR_ID_BUFFER,
        expectedSelectiveAccess);
  }

  private SelectiveAccessDescription createSelectiveAccessDescriptionDsmr4Monthly(
      final DataObject from,
      final DataObject to,
      final boolean selectiveAccessPeriodicMeterReadsSupported) {

    final List<DataObject> dataObjects =
        selectiveAccessPeriodicMeterReadsSupported
            ? Arrays.asList(
                this.CLOCK,
                this.ACTIVE_ENERGY_IMPORT_RATE_1,
                this.ACTIVE_ENERGY_IMPORT_RATE_2,
                this.ACTIVE_ENERGY_EXPORT_RATE_1,
                this.ACTIVE_ENERGY_EXPORT_RATE_2)
            : new ArrayList<>();

    final DataObject selectedValues = DataObject.newArrayData(dataObjects);

    final DataObject expectedAccessParam =
        DataObject.newStructureData(Arrays.asList(this.CLOCK, from, to, selectedValues));

    return new SelectiveAccessDescription(1, expectedAccessParam);
  }

  private AttributeAddress createAttributeAddressDsmr4Interval(
      final DataObject from, final DataObject to) {
    final SelectiveAccessDescription expectedSelectiveAccess =
        this.createSelectiveAccessDescriptionDsmr4Interval(from, to);
    return new AttributeAddress(
        this.CLASS_ID_PROFILE,
        this.OBIS_INTERVAL_DSMR4,
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
      final DataObject from, final DataObject to) {
    final SelectiveAccessDescription expectedSelectiveAccess =
        this.createSelectiveAccessDescriptionSmr5(from, to);
    return new AttributeAddress(
        this.CLASS_ID_PROFILE, this.OBIS_DAILY_SMR5, this.ATTR_ID_BUFFER, expectedSelectiveAccess);
  }

  private AttributeAddress createAttributeAddressSmr5Monthly(
      final DataObject from, final DataObject to) {
    final SelectiveAccessDescription expectedSelectiveAccess =
        this.createSelectiveAccessDescriptionSmr5(from, to);
    return new AttributeAddress(
        this.CLASS_ID_PROFILE,
        this.OBIS_MONTHLY_SMR5,
        this.ATTR_ID_BUFFER,
        expectedSelectiveAccess);
  }

  private AttributeAddress createAttributeAddressSmr5Interval(
      final DataObject from, final DataObject to) {
    final SelectiveAccessDescription expectedSelectiveAccess =
        this.createSelectiveAccessDescriptionSmr5(from, to);
    return new AttributeAddress(
        this.CLASS_ID_PROFILE,
        this.OBIS_INTERVAL_SMR5,
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
}
