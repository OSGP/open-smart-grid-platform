// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsDateTimeConverter.toDateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.joda.time.DateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.DataObjectToEventListConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigConfiguration;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventLogCategoryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FindEventsRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FindEventsCommandExecutorTest {
  @Mock private DlmsHelper dlmsHelper;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnection dlmsConnection;

  @Mock private DataObject resultData;

  @Mock private GetResult getResult;

  private FindEventsRequestDto findEventsRequestDto;

  private MessageMetadata messageMetadata;

  private FindEventsCommandExecutor executor;

  private DlmsDevice currentDevice;

  @BeforeEach
  public void before() throws ProtocolAdapterException, IOException {

    final DataObject fromDate = mock(DataObject.class);
    final DataObject toDate = mock(DataObject.class);
    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();

    this.findEventsRequestDto =
        new FindEventsRequestDto(
            EventLogCategoryDto.POWER_QUALITY_EVENT_LOG,
            DateTime.now().minusDays(70),
            DateTime.now());

    final DataObjectToEventListConverter dataObjectToEventListConverter =
        new DataObjectToEventListConverter(this.dlmsHelper);
    final DlmsObjectConfigService dlmsObjectConfigService =
        new DlmsObjectConfigService(
            this.dlmsHelper, new DlmsObjectConfigConfiguration().getDlmsObjectConfigs());

    this.executor =
        new FindEventsCommandExecutor(
            this.dlmsHelper, dataObjectToEventListConverter, dlmsObjectConfigService);

    when(this.dlmsHelper.asDataObject(this.findEventsRequestDto.getFrom())).thenReturn(fromDate);
    when(this.dlmsHelper.asDataObject(this.findEventsRequestDto.getUntil())).thenReturn(toDate);
    when(this.dlmsHelper.convertDataObjectToDateTime(any(DataObject.class)))
        .thenReturn(new CosemDateTimeDto());
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.get(any(AttributeAddress.class))).thenReturn(this.getResult);
  }

  @AfterEach
  public void after() throws ProtocolAdapterException {
    final DateTime toDate =
        toDateTime(this.findEventsRequestDto.getFrom().toDate(), this.currentDevice.getTimezone());

    final DateTime endDate =
        toDateTime(this.findEventsRequestDto.getUntil().toDate(), this.currentDevice.getTimezone());

    verify(this.dlmsHelper).asDataObject(toDate);
    verify(this.dlmsHelper).asDataObject(endDate);
  }

  @ParameterizedTest
  @CsvSource({"SMR_5_1, Europe/Amsterdam", "SMR_5_1, UTC"})
  void testRetrievalOfPowerQualityEvents(final String protocol, final String timezoneString)
      throws ProtocolAdapterException, IOException {
    this.currentDevice = this.createDlmsDevice(protocol, timezoneString);

    // SETUP
    when(this.getResult.getResultCode()).thenReturn(AccessResultCode.SUCCESS);
    when(this.getResult.getResultData()).thenReturn(this.resultData);
    when(this.resultData.getValue()).thenReturn(this.generateDataObjects());

    // CALL
    final List<EventDto> events =
        this.executor.execute(
            this.conn, this.currentDevice, this.findEventsRequestDto, this.messageMetadata);

    // VERIFY
    assertThat(events).hasSize(13);

    int firstEventCode = 77;
    for (final EventDto event : events) {
      assertThat(event.getEventCode()).isEqualTo(firstEventCode++);
    }

    verify(this.dlmsHelper, times(events.size()))
        .convertDataObjectToDateTime(any(DataObject.class));
    verify(this.conn).getDlmsMessageListener();
    verify(this.conn).getConnection();
    verify(this.dlmsConnection).get(any(AttributeAddress.class));
  }

  @ParameterizedTest
  @CsvSource({"SMR_5_1, Europe/Amsterdam", "SMR_5_1, UTC"})
  void testRetrievalOfAuxiliaryLogEvents(final String protocol, final String timezoneString)
      throws ProtocolAdapterException, IOException {
    // SETUP
    this.currentDevice = this.createDlmsDevice(protocol, timezoneString);
    this.findEventsRequestDto =
        new FindEventsRequestDto(
            EventLogCategoryDto.AUXILIARY_EVENT_LOG, DateTime.now().minusDays(70), DateTime.now());

    when(this.getResult.getResultCode()).thenReturn(AccessResultCode.SUCCESS);
    when(this.getResult.getResultData()).thenReturn(this.resultData);
    when(this.resultData.getValue()).thenReturn(this.generateDataObjectsAuxiliary());

    // CALL
    final List<EventDto> events =
        this.executor.execute(
            this.conn, this.currentDevice, this.findEventsRequestDto, this.messageMetadata);

    // VERIFY
    assertThat(events).hasSize(34);

    int firstEventCode = 33664;
    for (final EventDto event : events) {
      assertThat(event.getEventCode()).isEqualTo(firstEventCode++);
    }

    verify(this.dlmsHelper, times(events.size()))
        .convertDataObjectToDateTime(any(DataObject.class));
    verify(this.conn).getDlmsMessageListener();
    verify(this.conn).getConnection();
    verify(this.dlmsConnection).get(any(AttributeAddress.class));
  }

  @ParameterizedTest
  @CsvSource({"SMR_5_0_0, Europe/Amsterdam", "SMR_5_0_0, UTC"})
  void testRetrievalOfEventsForWrongCombinationOfProtocolAndLogType(
      final String protocol, final String timezoneString) {
    this.currentDevice = this.createDlmsDevice(protocol, timezoneString);
    // SETUP
    this.findEventsRequestDto =
        new FindEventsRequestDto(
            EventLogCategoryDto.AUXILIARY_EVENT_LOG, DateTime.now().minusDays(70), DateTime.now());

    when(this.getResult.getResultCode()).thenReturn(AccessResultCode.SUCCESS);
    when(this.getResult.getResultData()).thenReturn(this.resultData);
    when(this.resultData.getValue()).thenReturn(this.generateDataObjectsAuxiliary());

    // CALL
    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () ->
                this.executor.execute(
                    this.conn,
                    this.currentDevice,
                    this.findEventsRequestDto,
                    this.messageMetadata));
  }

  @ParameterizedTest
  @CsvSource({"SMR_5_2, Europe/Amsterdam", "SMR_5_2, UTC"})
  void testRetrievalOfEventsFromPowerQualityExtendedEventLog(
      final String protocol, final String timezoneString)
      throws ProtocolAdapterException, IOException {
    // SETUP
    this.currentDevice = this.createDlmsDevice(protocol, timezoneString);
    this.findEventsRequestDto =
        new FindEventsRequestDto(
            EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG,
            DateTime.now().minusDays(70),
            DateTime.now());

    when(this.getResult.getResultCode()).thenReturn(AccessResultCode.SUCCESS);
    when(this.getResult.getResultData()).thenReturn(this.resultData);
    when(this.resultData.getValue()).thenReturn(this.generateDataObjectsExtendedPowerQuality());

    // CALL
    final List<EventDto> events =
        this.executor.execute(
            this.conn, this.currentDevice, this.findEventsRequestDto, this.messageMetadata);

    // VERIFY
    assertThat(events).hasSize(6);

    int firstEventCode = 93;
    for (final EventDto event : events) {
      assertThat(event.getEventCode()).isEqualTo(firstEventCode++);
    }

    verify(this.dlmsHelper, times(events.size()))
        .convertDataObjectToDateTime(any(DataObject.class));
    verify(this.conn).getDlmsMessageListener();
    verify(this.conn).getConnection();
    verify(this.dlmsConnection).get(any(AttributeAddress.class));
  }

  @ParameterizedTest
  @CsvSource({"SMR_5_0_0, Europe/Amsterdam", "SMR_5_0_0, UTC"})
  void testRetrievalOfEventsFromPowerQualityExtendedEventLogThrowsExceptionWhenNotSupportedByDevice(
      final String protocol, final String timezoneString) {
    this.currentDevice = this.createDlmsDevice(protocol, timezoneString);
    this.findEventsRequestDto =
        new FindEventsRequestDto(
            EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG,
            DateTime.now().minusDays(70),
            DateTime.now());

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () ->
                this.executor.execute(
                    this.conn,
                    this.currentDevice,
                    this.findEventsRequestDto,
                    this.messageMetadata));
  }

  @ParameterizedTest
  @CsvSource({"SMR_5_1, Europe/Amsterdam", "SMR_5_1, UTC"})
  void testOtherReasonResult(final String protocol, final String timezoneString)
      throws IOException {
    // SETUP
    this.currentDevice = this.createDlmsDevice(protocol, timezoneString);
    when(this.getResult.getResultCode()).thenReturn(AccessResultCode.OTHER_REASON);

    // CALL
    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () ->
                this.executor.execute(
                    this.conn,
                    this.currentDevice,
                    this.findEventsRequestDto,
                    this.messageMetadata));

    // VERIFY
    verify(this.conn).getDlmsMessageListener();
    verify(this.conn).getConnection();
    verify(this.dlmsConnection).get(any(AttributeAddress.class));
  }

  @ParameterizedTest
  @CsvSource({"SMR_5_1, Europe/Amsterdam", "SMR_5_1, UTC"})
  void testEmptyGetResult(final String protocol, final String timezoneString) throws IOException {
    // SETUP
    this.currentDevice = this.createDlmsDevice(protocol, timezoneString);
    when(this.dlmsConnection.get(any(AttributeAddress.class))).thenReturn(null);

    // CALL
    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () ->
                this.executor.execute(
                    this.conn,
                    this.currentDevice,
                    this.findEventsRequestDto,
                    this.messageMetadata));

    // VERIFY
    verify(this.conn).getDlmsMessageListener();
    verify(this.conn).getConnection();
    verify(this.dlmsConnection).get(any(AttributeAddress.class));
  }

  private List<DataObject> generateDataObjects() {

    final List<DataObject> dataObjects = new ArrayList<>();

    IntStream.rangeClosed(77, 89)
        .forEach(
            code -> {
              final DataObject eventCode = DataObject.newInteger16Data((short) code);
              final DataObject eventTime =
                  DataObject.newDateTimeData(new CosemDateTime(2018, 12, 31, 23, code - 60, 0, 0));

              final DataObject struct = DataObject.newStructureData(eventTime, eventCode);

              dataObjects.add(struct);
            });

    return dataObjects;
  }

  private List<DataObject> generateDataObjectsAuxiliary() {

    final List<DataObject> dataObjects = new ArrayList<>();

    IntStream.rangeClosed(33664, 33697)
        .forEach(
            code -> {
              final DataObject eventCode = DataObject.newUInteger16Data(code);
              final DataObject eventTime =
                  DataObject.newDateTimeData(new CosemDateTime(2018, 12, 31, 23, code % 60, 0, 0));

              final DataObject struct = DataObject.newStructureData(eventTime, eventCode);

              dataObjects.add(struct);
            });

    return dataObjects;
  }

  private List<DataObject> generateDataObjectsExtendedPowerQuality() {

    final List<DataObject> dataObjects = new ArrayList<>();

    IntStream.rangeClosed(93, 98)
        .forEach(
            code -> {
              final DataObject eventCode = DataObject.newInteger16Data((short) code);
              final DataObject eventTime =
                  DataObject.newDateTimeData(new CosemDateTime(2018, 12, 31, 23, code - 60, 0, 0));
              final DataObject magnitude = DataObject.newInteger32Data(5);
              final DataObject duration = DataObject.newInteger32Data(6);

              final DataObject struct =
                  DataObject.newStructureData(eventTime, eventCode, magnitude, duration);

              dataObjects.add(struct);
            });

    return dataObjects;
  }

  private DlmsDevice createDlmsDevice(final String protocol, final String timezone) {
    final DlmsDevice device = new DlmsDevice();
    device.setDeviceIdentification("123456789012");
    device.setTimezone(timezone);
    device.setProtocol(Protocol.valueOf(protocol));
    return device;
  }
}
