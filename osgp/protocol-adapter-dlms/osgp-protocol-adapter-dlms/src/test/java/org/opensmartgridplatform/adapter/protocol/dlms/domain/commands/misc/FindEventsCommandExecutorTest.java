// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.DataObjectToEventListConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsDateTimeConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventLogCategoryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FindEventsRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class FindEventsCommandExecutorTest {

  private static final Map<EventLogCategoryDto, String> OBIS_MAPPING = new HashMap<>();

  private static final String TIME_ZONE = "Europe/Amsterdam";

  private static final String DSMR_2_2 = "DSMR_2_2";
  private static final String DSMR_4_2_2 = "DSMR_4_2_2";
  private static final String SMR_5_0_0 = "SMR_5_0_0";
  private static final String SMR_5_1 = "SMR_5_1";
  private static final String SMR_5_2 = "SMR_5_2";

  static {
    OBIS_MAPPING.put(EventLogCategoryDto.POWER_QUALITY_EVENT_LOG, "0.0.99.98.5.255");
    OBIS_MAPPING.put(EventLogCategoryDto.COMMUNICATION_SESSION_LOG, "0.0.99.98.4.255");
    OBIS_MAPPING.put(EventLogCategoryDto.M_BUS_EVENT_LOG, "0.0.99.98.3.255");
    OBIS_MAPPING.put(EventLogCategoryDto.FRAUD_DETECTION_LOG, "0.0.99.98.1.255");
    OBIS_MAPPING.put(EventLogCategoryDto.STANDARD_EVENT_LOG, "0.0.99.98.0.255");
    OBIS_MAPPING.put(EventLogCategoryDto.AUXILIARY_EVENT_LOG, "0.0.99.98.6.255");
    OBIS_MAPPING.put(EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG, "0.0.99.98.7.255");
  }

  @Mock private DlmsHelper dlmsHelper;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnection dlmsConnection;

  @Mock private DataObject resultData;

  @Mock private GetResult getResult;

  @Captor private ArgumentCaptor<AttributeAddress> attributeAddressArgumentCaptor;

  private MessageMetadata messageMetadata;

  private FindEventsCommandExecutor executor;

  @BeforeEach
  public void before() throws ProtocolAdapterException, IOException, ObjectConfigException {

    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();

    final ObjectConfigService objectConfigService = new ObjectConfigService();
    final ObjectConfigServiceHelper objectConfigServiceHelper =
        new ObjectConfigServiceHelper(objectConfigService);

    final DataObjectToEventListConverter dataObjectToEventListConverter =
        new DataObjectToEventListConverter(this.dlmsHelper);

    this.executor =
        new FindEventsCommandExecutor(
            this.dlmsHelper, dataObjectToEventListConverter, objectConfigServiceHelper);
  }

  @ParameterizedTest
  @ValueSource(strings = {"UTC", "Europe/Amsterdam"})
  void testEventTimezone(final String timeZone) throws ProtocolAdapterException, IOException {
    this.testEventRetrieval(SMR_5_0_0, EventLogCategoryDto.STANDARD_EVENT_LOG, timeZone);
  }

  @ParameterizedTest
  @EnumSource(
      value = EventLogCategoryDto.class,
      names = {
        "POWER_QUALITY_EXTENDED_EVENT_LOG",
        "POWER_QUALITY_EVENT_LOG",
        "AUXILIARY_EVENT_LOG",
        "COMMUNICATION_SESSION_LOG"
      },
      mode = EnumSource.Mode.EXCLUDE)
  void testDsmr22Event(final EventLogCategoryDto eventLogCategoryDto)
      throws ProtocolAdapterException, IOException {
    this.testEventRetrieval(DSMR_2_2, eventLogCategoryDto);
  }

  @ParameterizedTest
  @EnumSource(
      value = EventLogCategoryDto.class,
      names = {
        "POWER_QUALITY_EXTENDED_EVENT_LOG",
        "POWER_QUALITY_EVENT_LOG",
        "AUXILIARY_EVENT_LOG"
      },
      mode = EnumSource.Mode.EXCLUDE)
  void testDsmr422Event(final EventLogCategoryDto eventLogCategoryDto)
      throws ProtocolAdapterException, IOException {
    this.testEventRetrieval(DSMR_4_2_2, eventLogCategoryDto);
  }

  @ParameterizedTest
  @EnumSource(
      value = EventLogCategoryDto.class,
      names = {"POWER_QUALITY_EXTENDED_EVENT_LOG", "AUXILIARY_EVENT_LOG"},
      mode = EnumSource.Mode.EXCLUDE)
  void testSmr500Event(final EventLogCategoryDto eventLogCategoryDto)
      throws ProtocolAdapterException, IOException {
    this.testEventRetrieval(SMR_5_0_0, eventLogCategoryDto);
  }

  @ParameterizedTest
  @EnumSource(
      value = EventLogCategoryDto.class,
      names = {"POWER_QUALITY_EXTENDED_EVENT_LOG"},
      mode = EnumSource.Mode.EXCLUDE)
  void testSmr51Event(final EventLogCategoryDto eventLogCategoryDto)
      throws ProtocolAdapterException, IOException {
    this.testEventRetrieval(SMR_5_1, eventLogCategoryDto);
  }

  @ParameterizedTest
  @EnumSource(value = EventLogCategoryDto.class)
  void testSmr52Event(final EventLogCategoryDto eventLogCategoryDto)
      throws ProtocolAdapterException, IOException {
    this.testEventRetrieval(SMR_5_2, eventLogCategoryDto);
  }

  @ParameterizedTest
  @EnumSource(
      value = EventLogCategoryDto.class,
      names = {
        "POWER_QUALITY_EXTENDED_EVENT_LOG",
        "POWER_QUALITY_EVENT_LOG",
        "AUXILIARY_EVENT_LOG",
        "COMMUNICATION_SESSION_LOG"
      },
      mode = EnumSource.Mode.INCLUDE)
  void testNoDsmr22Event(final EventLogCategoryDto eventLogCategoryDto)
      throws ProtocolAdapterException, IOException {
    this.testNoEventRetrieval(DSMR_2_2, eventLogCategoryDto);
  }

  @ParameterizedTest
  @EnumSource(
      value = EventLogCategoryDto.class,
      names = {
        "POWER_QUALITY_EXTENDED_EVENT_LOG",
        "POWER_QUALITY_EVENT_LOG",
        "AUXILIARY_EVENT_LOG"
      },
      mode = Mode.INCLUDE)
  void testNoDsmr422Event(final EventLogCategoryDto eventLogCategoryDto)
      throws ProtocolAdapterException, IOException {
    this.testNoEventRetrieval(DSMR_4_2_2, eventLogCategoryDto);
  }

  @ParameterizedTest
  @EnumSource(
      value = EventLogCategoryDto.class,
      names = {"POWER_QUALITY_EXTENDED_EVENT_LOG", "AUXILIARY_EVENT_LOG"},
      mode = Mode.INCLUDE)
  void testNoSmr500Event(final EventLogCategoryDto eventLogCategoryDto)
      throws ProtocolAdapterException, IOException {
    this.testNoEventRetrieval(SMR_5_0_0, eventLogCategoryDto);
  }

  @ParameterizedTest
  @EnumSource(
      value = EventLogCategoryDto.class,
      names = {"POWER_QUALITY_EXTENDED_EVENT_LOG"},
      mode = Mode.INCLUDE)
  void testNoSmr51Event(final EventLogCategoryDto eventLogCategoryDto)
      throws ProtocolAdapterException, IOException {
    this.testNoEventRetrieval(SMR_5_1, eventLogCategoryDto);
  }

  @Test
  void testOtherReasonResult() throws IOException {

    final DlmsDevice currentDevice = this.createDlmsDevice(SMR_5_0_0, TIME_ZONE);
    final EventLogCategoryDto eventLogCategoryDto = EventLogCategoryDto.STANDARD_EVENT_LOG;

    this.whenConnection();
    when(this.getResult.getResultCode()).thenReturn(AccessResultCode.OTHER_REASON);

    final FindEventsRequestDto findEventsRequestDto =
        this.createFindEventsRequestDto(eventLogCategoryDto);

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () ->
                this.executor.execute(
                    this.conn, currentDevice, findEventsRequestDto, this.messageMetadata));
    this.verifyAttributeAddress(eventLogCategoryDto);
  }

  private void testEventRetrieval(
      final String protocol, final EventLogCategoryDto eventLogCategoryDto)
      throws ProtocolAdapterException, IOException {
    this.testEventRetrieval(protocol, eventLogCategoryDto, TIME_ZONE);
  }

  private void testEventRetrieval(
      final String protocol, final EventLogCategoryDto eventLogCategoryDto, final String timeZone)
      throws ProtocolAdapterException, IOException {
    final DlmsDevice currentDevice = this.createDlmsDevice(protocol, timeZone);

    this.whenConnection();
    this.whenSuccessReturn(this.generateDataObject(eventLogCategoryDto), eventLogCategoryDto);

    final FindEventsRequestDto findEventsRequestDto =
        this.createFindEventsRequestDto(eventLogCategoryDto);
    final List<EventDto> events =
        this.executor.execute(this.conn, currentDevice, findEventsRequestDto, this.messageMetadata);

    this.verifyAttributeAddress(eventLogCategoryDto);
    this.verifyTimezoneConversion(currentDevice, findEventsRequestDto);
    assertThat(events).isNotEmpty();
  }

  private void verifyAttributeAddress(final EventLogCategoryDto eventLogCategoryDto) {
    final AttributeAddress address = this.attributeAddressArgumentCaptor.getValue();
    assertThat(address.getInstanceId().asDecimalString())
        .isEqualTo(OBIS_MAPPING.get(eventLogCategoryDto));
  }

  private void testNoEventRetrieval(
      final String protocol, final EventLogCategoryDto eventLogCategoryDto)
      throws ProtocolAdapterException, IOException {
    final DlmsDevice currentDevice = this.createDlmsDevice(protocol, TIME_ZONE);

    final FindEventsRequestDto findEventsRequestDto =
        this.createFindEventsRequestDto(eventLogCategoryDto);
    assertThatExceptionOfType(NotSupportedByProtocolException.class)
        .isThrownBy(
            () ->
                this.executor.execute(
                    this.conn, currentDevice, findEventsRequestDto, this.messageMetadata));

    this.verifyNoConnection();
  }

  private void whenConnection() throws IOException {
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.dlmsConnection.get(this.attributeAddressArgumentCaptor.capture()))
        .thenReturn(this.getResult);
  }

  private void verifyNoConnection() throws IOException {
    verifyNoInteractions(this.conn);
  }

  private void whenSuccessReturn(
      final List<DataObject> value, final EventLogCategoryDto eventLogCategoryDto) {
    when(this.getResult.getResultCode()).thenReturn(AccessResultCode.SUCCESS);
    when(this.getResult.getResultData()).thenReturn(this.resultData);
    when(this.dlmsHelper.convertDataObjectToDateTime(any(DataObject.class)))
        .thenReturn(new CosemDateTimeDto());
    when(this.resultData.getValue()).thenReturn(value);
  }

  private FindEventsRequestDto createFindEventsRequestDto(
      final EventLogCategoryDto powerQualityEventLog) {
    return new FindEventsRequestDto(
        powerQualityEventLog, DateTime.now().minusDays(70), DateTime.now());
  }

  private void verifyTimezoneConversion(
      final DlmsDevice currentDevice, final FindEventsRequestDto findEventsRequestDto) {
    final DateTime dateTimeFrom =
        DlmsDateTimeConverter.toDateTime(
            findEventsRequestDto.getFrom(), currentDevice.getTimezone());
    final DateTime dateTimeUntil =
        DlmsDateTimeConverter.toDateTime(
            findEventsRequestDto.getUntil(), currentDevice.getTimezone());
    verify(this.dlmsHelper).asDataObject(dateTimeFrom);
    verify(this.dlmsHelper).asDataObject(dateTimeUntil);
  }

  private List<DataObject> generateDataObject(final EventLogCategoryDto eventLogCategoryDto) {
    final CosemDateTime dateTime = new CosemDateTime(2018, 12, 31, 23, 1, 0, 0);
    final DataObject eventCode = DataObject.newInteger16Data((short) 1);
    final DataObject eventTime = DataObject.newDateTimeData(dateTime);
    switch (eventLogCategoryDto.getDetailsType()) {
      case NONE:
        return List.of(DataObject.newStructureData(eventTime, eventCode));
      case COUNTER:
        final DataObject eventCounter = DataObject.newInteger32Data(4);
        return List.of(DataObject.newStructureData(eventTime, eventCode, eventCounter));
      case MAGNITUDE_AND_DURATION:
        final DataObject magnitude = DataObject.newInteger32Data(5);
        final DataObject duration = DataObject.newInteger32Data(6);
        return List.of(DataObject.newStructureData(eventTime, eventCode, magnitude, duration));
      default:
        return List.of(DataObject.newStructureData(eventTime, eventCode));
    }
  }

  private DlmsDevice createDlmsDevice(final String protocol, final String timezone) {
    final DlmsDevice device = new DlmsDevice();
    device.setTimezone(timezone);
    device.setProtocol(Protocol.valueOf(protocol));
    return device;
  }
}
