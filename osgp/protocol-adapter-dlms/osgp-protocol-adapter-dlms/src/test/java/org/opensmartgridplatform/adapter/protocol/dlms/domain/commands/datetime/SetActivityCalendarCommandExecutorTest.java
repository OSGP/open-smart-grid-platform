/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.joda.time.DateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActivityCalendarDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClockStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DayProfileActionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DayProfileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SeasonProfileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.WeekProfileDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class SetActivityCalendarCommandExecutorTest {

  protected static final int CLASS_ID = 20;

  private static final ObisCode OBIS_CODE = new ObisCode("0.0.13.0.0.255");
  private static final ObisCode OBIS_CODE_SCRIPT = new ObisCode("0.0.10.0.100.255");

  private static final int ATTRIBUTE_ID_CALENDAR_NAME_PASSIVE = 6;
  private static final int ATTRIBUTE_ID_SEASON_PROFILE_PASSIVE = 7;
  private static final int ATTRIBUTE_ID_WEEK_PROFILE_TABLE_PASSIVE = 8;
  private static final int ATTRIBUTE_ID_DAY_PROFILE_TABLE_PASSIVE = 9;

  private final DlmsDevice DLMS_DEVICE = new DlmsDevice();

  private static final String CALENDAR_NAME = "Calendar";

  private static final CosemDateTimeDto ACTIVATE_PASSIVE_CALENDAR_TIME =
      new CosemDateTimeDto(DateTime.now());

  @Captor ArgumentCaptor<SetParameter> setParameterArgumentCaptor;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnection dlmsConnection;

  @Mock private MessageMetadata messageMetadata;

  @Mock private SetActivityCalendarCommandActivationExecutor activationExecutor;

  private SetActivityCalendarCommandExecutor executor;

  private MockedStatic<ActivityCalendarValidator> activityCalendarValidator;

  @BeforeEach
  public void setUp() {
    this.executor =
        new SetActivityCalendarCommandExecutor(
            new DlmsHelper(), this.activationExecutor, new ConfigurationMapper());

    this.activityCalendarValidator = mockStatic(ActivityCalendarValidator.class);
  }

  @AfterEach
  public void tearDown() throws Exception {
    this.activityCalendarValidator.close();
  }

  @Test
  void testSetActivityCalendarEmpty()
      throws ProtocolAdapterException, IOException, FunctionalException {

    // SETUP
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);
    when(this.activationExecutor.execute(this.conn, this.DLMS_DEVICE, null, this.messageMetadata))
        .thenReturn(MethodResultCode.SUCCESS);

    final ActivityCalendarDto activityCalendar =
        new ActivityCalendarDto(
            CALENDAR_NAME, ACTIVATE_PASSIVE_CALENDAR_TIME, Collections.emptyList());

    // CALL
    final AccessResultCode resultCode =
        this.executor.execute(this.conn, this.DLMS_DEVICE, activityCalendar, this.messageMetadata);

    // VERIFY
    assertThat(resultCode).isEqualTo(AccessResultCode.SUCCESS);
    verify(this.dlmsConnection, times(4)).set(this.setParameterArgumentCaptor.capture());
    this.activityCalendarValidator.verify(
        () -> ActivityCalendarValidator.validate(activityCalendar), times(1));

    final List<SetParameter> setParameters = this.setParameterArgumentCaptor.getAllValues();
    this.verifySetParameters(
        setParameters,
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList());
  }

  @Test
  void testSetActivityCalendarWithSingleSeason()
      throws ProtocolAdapterException, IOException, FunctionalException {
    // SETUP
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);
    when(this.activationExecutor.execute(this.conn, this.DLMS_DEVICE, null, this.messageMetadata))
        .thenReturn(MethodResultCode.SUCCESS);

    final List<Short> dayIds = Collections.singletonList((short) 1);
    final List<CosemTimeDto> actionStartTimes =
        Collections.singletonList(new CosemTimeDto(0, 0, 0, 0));
    final List<String> weekNames = Collections.singletonList("Week1");
    final List<String> seasonNames = Collections.singletonList("Season1");
    final List<CosemDateDto> seasonStarts =
        Collections.singletonList(new CosemDateDto(0xFFFF, 1, 1));

    final ActivityCalendarDto activityCalendar =
        this.createActivityCalendar(seasonNames, seasonStarts, weekNames, dayIds, actionStartTimes);

    // CALL
    final AccessResultCode resultCode =
        this.executor.execute(this.conn, this.DLMS_DEVICE, activityCalendar, this.messageMetadata);

    // VERIFY
    assertThat(resultCode).isEqualTo(AccessResultCode.SUCCESS);
    verify(this.dlmsConnection, times(4)).set(this.setParameterArgumentCaptor.capture());
    this.activityCalendarValidator.verify(
        () -> ActivityCalendarValidator.validate(activityCalendar), times(1));

    final List<SetParameter> setParameters = this.setParameterArgumentCaptor.getAllValues();
    this.verifySetParameters(
        setParameters, dayIds, actionStartTimes, weekNames, seasonNames, seasonStarts);
  }

  @Test
  void testSetActivityCalendarWithMultipleSeasons()
      throws ProtocolAdapterException, IOException, FunctionalException {
    // SETUP
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);
    when(this.activationExecutor.execute(this.conn, this.DLMS_DEVICE, null, this.messageMetadata))
        .thenReturn(MethodResultCode.SUCCESS);

    final List<Short> dayIds = Collections.singletonList((short) 1);
    final List<CosemTimeDto> actionStartTimes =
        Collections.singletonList(new CosemTimeDto(0, 0, 0, 0));
    final List<String> weekNames = Collections.singletonList("Week1");
    final List<String> seasonNames = Arrays.asList("Season1", "Season2");
    final List<CosemDateDto> seasonStarts =
        Arrays.asList(new CosemDateDto(0xFFFF, 1, 1), new CosemDateDto(0xFFFF, 6, 15));

    final ActivityCalendarDto activityCalendar =
        this.createActivityCalendar(seasonNames, seasonStarts, weekNames, dayIds, actionStartTimes);

    // CALL
    final AccessResultCode resultCode =
        this.executor.execute(this.conn, this.DLMS_DEVICE, activityCalendar, this.messageMetadata);

    // VERIFY
    assertThat(resultCode).isEqualTo(AccessResultCode.SUCCESS);
    verify(this.dlmsConnection, times(4)).set(this.setParameterArgumentCaptor.capture());
    this.activityCalendarValidator.verify(
        () -> ActivityCalendarValidator.validate(activityCalendar), times(1));

    final List<SetParameter> setParameters = this.setParameterArgumentCaptor.getAllValues();
    this.verifySetParameters(
        setParameters, dayIds, actionStartTimes, weekNames, seasonNames, seasonStarts);
  }

  @Test
  void testSetActivityCalendarWithMultipleSeasonsWeeksDaysAndActions()
      throws ProtocolAdapterException, IOException, FunctionalException {
    // SETUP
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);
    when(this.activationExecutor.execute(this.conn, this.DLMS_DEVICE, null, this.messageMetadata))
        .thenReturn(MethodResultCode.SUCCESS);

    final List<Short> dayIds = Arrays.asList((short) 1, (short) 2, (short) 3);
    final List<CosemTimeDto> actionStartTimes =
        Arrays.asList(new CosemTimeDto(0, 0, 0, 0), new CosemTimeDto(10, 15, 0, 0));
    final List<String> weekNames = Arrays.asList("Week1", "Week2");
    final List<String> seasonNames = Arrays.asList("Season1", "Season2", "Season3");
    final List<CosemDateDto> seasonStarts =
        Arrays.asList(
            new CosemDateDto(0xFFFF, 1, 1),
            new CosemDateDto(0xFFFF, 6, 15),
            new CosemDateDto(0xFFFF, 12, 25));

    final ActivityCalendarDto activityCalendar =
        this.createActivityCalendar(seasonNames, seasonStarts, weekNames, dayIds, actionStartTimes);

    // CALL
    final AccessResultCode resultCode =
        this.executor.execute(this.conn, this.DLMS_DEVICE, activityCalendar, this.messageMetadata);

    // VERIFY
    assertThat(resultCode).isEqualTo(AccessResultCode.SUCCESS);
    verify(this.dlmsConnection, times(4)).set(this.setParameterArgumentCaptor.capture());
    this.activityCalendarValidator.verify(
        () -> ActivityCalendarValidator.validate(activityCalendar), times(1));

    final List<SetParameter> setParameters = this.setParameterArgumentCaptor.getAllValues();
    this.verifySetParameters(
        setParameters, dayIds, actionStartTimes, weekNames, seasonNames, seasonStarts);
  }

  @Test
  void testSetActivityCalendarWithValidationFailure() throws IOException {
    // SETUP
    this.activityCalendarValidator
        .when(() -> ActivityCalendarValidator.validate(any()))
        .thenThrow(
            new FunctionalException(
                FunctionalExceptionType.VALIDATION_ERROR, ComponentType.PROTOCOL_DLMS));
    final ActivityCalendarDto activityCalendar = this.createDefaultActivityCalendar();

    // CALL
    final Throwable thrown =
        catchThrowable(
            () ->
                this.executor.execute(
                    this.conn, this.DLMS_DEVICE, activityCalendar, this.messageMetadata));

    // VERIFY
    assertThat(thrown).isInstanceOf(FunctionalException.class);
  }

  @Test
  void testSetActivityCalendarWithOneOfTheSetRequestsFailing() throws IOException {
    // SETUP
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class)))
        .thenReturn(AccessResultCode.SUCCESS) // Calendar name
        .thenReturn(AccessResultCode.OTHER_REASON) // Season profiles
        .thenReturn(AccessResultCode.SUCCESS) // Week profiles
        .thenReturn(AccessResultCode.SUCCESS); // Day profiles

    final ActivityCalendarDto activityCalendar = this.createDefaultActivityCalendar();

    // CALL
    final Throwable thrown =
        catchThrowable(
            () ->
                this.executor.execute(
                    this.conn, this.DLMS_DEVICE, activityCalendar, this.messageMetadata));

    // VERIFY
    assertThat(thrown)
        .isInstanceOf(ProtocolAdapterException.class)
        .hasMessageContaining("SEASONS: Result(OTHER_REASON)");
    this.activityCalendarValidator.verify(
        () -> ActivityCalendarValidator.validate(activityCalendar), times(1));
  }

  @Test
  void testSetActivityCalendarWithActivationFailure() throws ProtocolAdapterException, IOException {
    // SETUP
    final String errorMessage = "Activation failure";
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);
    when(this.activationExecutor.execute(this.conn, this.DLMS_DEVICE, null, this.messageMetadata))
        .thenThrow(new ProtocolAdapterException(errorMessage));

    final ActivityCalendarDto activityCalendar = this.createDefaultActivityCalendar();

    // CALL
    final Throwable thrown =
        catchThrowable(
            () ->
                this.executor.execute(
                    this.conn, this.DLMS_DEVICE, activityCalendar, this.messageMetadata));

    // VERIFY
    assertThat(thrown)
        .isInstanceOf(ProtocolAdapterException.class)
        .hasMessageContaining(errorMessage);
    this.activityCalendarValidator.verify(
        () -> ActivityCalendarValidator.validate(activityCalendar), times(1));
  }

  private ActivityCalendarDto createDefaultActivityCalendar() {
    final List<Short> dayIds = Collections.singletonList((short) 1);
    final List<CosemTimeDto> actionStartTimes =
        Collections.singletonList(new CosemTimeDto(0, 0, 0, 0));
    final List<String> weekNames = Collections.singletonList("Week1");
    final List<String> seasonNames = Collections.singletonList("Season1");
    final List<CosemDateDto> seasonStarts =
        Collections.singletonList(new CosemDateDto(0xFFFF, 1, 1));

    return this.createActivityCalendar(
        seasonNames, seasonStarts, weekNames, dayIds, actionStartTimes);
  }

  private ActivityCalendarDto createActivityCalendar(
      final List<String> seasonNames,
      final List<CosemDateDto> seasonStarts,
      final List<String> weekNames,
      final List<Short> dayIds,
      final List<CosemTimeDto> actionStartTimes) {
    final List<DayProfileDto> dayProfiles = this.createDayProfiles(dayIds, actionStartTimes);
    final List<WeekProfileDto> weekProfiles = this.createWeekProfiles(weekNames, dayProfiles);
    final List<SeasonProfileDto> seasonProfiles =
        this.createSeasonProfiles(seasonNames, seasonStarts, weekProfiles);

    return new ActivityCalendarDto(CALENDAR_NAME, ACTIVATE_PASSIVE_CALENDAR_TIME, seasonProfiles);
  }

  private List<DayProfileDto> createDayProfiles(
      final List<Short> dayIds, final List<CosemTimeDto> actionStartTimes) {
    final List<DayProfileActionDto> actions =
        IntStream.range(0, actionStartTimes.size())
            .mapToObj(i -> new DayProfileActionDto(i + 1, actionStartTimes.get(i)))
            .collect(Collectors.toList());
    return dayIds.stream()
        .map(id -> new DayProfileDto(id.intValue(), actions))
        .collect(Collectors.toList());
  }

  private List<WeekProfileDto> createWeekProfiles(
      final List<String> weekNames, final List<DayProfileDto> dayProfiles) {
    final int size = dayProfiles.size();

    return weekNames.stream()
        .map(
            weekName ->
                WeekProfileDto.newBuilder()
                    .withWeekProfileName(weekName)
                    .withMonday(dayProfiles.get(0))
                    .withTuesday(dayProfiles.get(this.getIndexOr0(1, size)))
                    .withWednesday(dayProfiles.get(this.getIndexOr0(2, size)))
                    .withThursday(dayProfiles.get(this.getIndexOr0(3, size)))
                    .withFriday(dayProfiles.get(this.getIndexOr0(4, size)))
                    .withSaturday(dayProfiles.get(this.getIndexOr0(5, size)))
                    .withSunday(dayProfiles.get(this.getIndexOr0(6, size)))
                    .build())
        .collect(Collectors.toList());
  }

  private List<SeasonProfileDto> createSeasonProfiles(
      final List<String> seasonNames,
      final List<CosemDateDto> seasonStarts,
      final List<WeekProfileDto> weekProfiles) {
    return IntStream.range(0, seasonNames.size())
        .mapToObj(
            i ->
                new SeasonProfileDto(
                    seasonNames.get(i),
                    new CosemDateTimeDto(
                        seasonStarts.get(i),
                        new CosemTimeDto(0, 0, 0, 0),
                        0,
                        new ClockStatusDto(0)),
                    weekProfiles.get(this.getIndexOr0(i, weekProfiles.size()))))
        .collect(Collectors.toList());
  }

  private int getIndexOr0(final int index, final int size) {
    if (index < size) {
      return index;
    } else {
      return 0;
    }
  }

  private void verifySetParameters(
      final List<SetParameter> setParameters,
      final List<Short> dayIds,
      final List<CosemTimeDto> actionStartTimes,
      final List<String> weekNames,
      final List<String> seasonNames,
      final List<CosemDateDto> seasonStarts) {
    assertThat(setParameters).hasSize(4);

    this.verifySetParameterCalendarName(setParameters.get(0));
    this.verifySetParameterSeasonProfiles(
        setParameters.get(1), seasonNames, seasonStarts, weekNames);
    this.verifySetParameterWeekProfiles(setParameters.get(2), weekNames, dayIds);
    this.verifySetParameterDayProfiles(setParameters.get(3), dayIds, actionStartTimes);
  }

  private void verifySetParameterCalendarName(final SetParameter setParameter) {
    final AttributeAddress attributeAddress = setParameter.getAttributeAddress();
    assertThat(attributeAddress.getClassId()).isEqualTo(CLASS_ID);
    assertThat(attributeAddress.getInstanceId()).isEqualTo(OBIS_CODE);
    assertThat(attributeAddress.getId()).isEqualTo(ATTRIBUTE_ID_CALENDAR_NAME_PASSIVE);

    final DataObject dataObject = setParameter.getData();
    assertThat(dataObject.getType()).isEqualTo(Type.OCTET_STRING);

    assertThat((byte[]) dataObject.getValue())
        .containsExactly(CALENDAR_NAME.getBytes(StandardCharsets.UTF_8));
  }

  private void verifySetParameterSeasonProfiles(
      final SetParameter setParameter,
      final List<String> seasonNames,
      final List<CosemDateDto> seasonStarts,
      final List<String> weekNames) {
    final AttributeAddress attributeAddress = setParameter.getAttributeAddress();
    assertThat(attributeAddress.getClassId()).isEqualTo(CLASS_ID);
    assertThat(attributeAddress.getInstanceId()).isEqualTo(OBIS_CODE);
    assertThat(attributeAddress.getId()).isEqualTo(ATTRIBUTE_ID_SEASON_PROFILE_PASSIVE);

    final DataObject dataObject = setParameter.getData();
    assertThat(dataObject.getType()).isEqualTo(Type.ARRAY);

    final List<DataObject> seasons = dataObject.getValue();
    assertThat(seasons).hasSize(seasonNames.size());

    IntStream.range(0, seasons.size())
        .forEach(
            i ->
                this.verifyDataObjectSeason(
                    seasons.get(i),
                    seasonNames.get(i),
                    seasonStarts.get(i),
                    weekNames.get(this.getIndexOr0(i, weekNames.size()))));
  }

  private void verifyDataObjectSeason(
      final DataObject season,
      final String seasonName,
      final CosemDateDto seasonStart,
      final String weekName) {
    assertThat(season.getType()).isEqualTo(Type.STRUCTURE);

    final List<DataObject> values = season.getValue();
    assertThat(values).hasSize(3);

    // Season Name
    assertThat(values.get(0).getType()).isEqualTo(Type.OCTET_STRING);
    assertThat((byte[]) values.get(0).getValue())
        .containsExactly(seasonName.getBytes(StandardCharsets.UTF_8));

    // Season start time
    assertThat(values.get(1).getType()).isEqualTo(Type.OCTET_STRING);
    final CosemDateTime cosemDateTime = CosemDateTime.decode(values.get(1).getValue());
    final CosemDateTime expectedTime =
        new CosemDateTime(
            0xFFFF, seasonStart.getMonth(), seasonStart.getDayOfMonth(), 255, 0, 0, 0, 0, 0);
    assertThat(cosemDateTime).usingRecursiveComparison().isEqualTo(expectedTime);

    // Week name
    assertThat(values.get(2).getType()).isEqualTo(Type.OCTET_STRING);
    assertThat((byte[]) values.get(2).getValue())
        .containsExactly(weekName.getBytes(StandardCharsets.UTF_8));
  }

  private void verifySetParameterWeekProfiles(
      final SetParameter setParameter, final List<String> weekNames, final List<Short> dayIds) {
    final AttributeAddress attributeAddress = setParameter.getAttributeAddress();
    assertThat(attributeAddress.getClassId()).isEqualTo(CLASS_ID);
    assertThat(attributeAddress.getInstanceId()).isEqualTo(OBIS_CODE);
    assertThat(attributeAddress.getId()).isEqualTo(ATTRIBUTE_ID_WEEK_PROFILE_TABLE_PASSIVE);

    final DataObject dataObject = setParameter.getData();
    assertThat(dataObject.getType()).isEqualTo(Type.ARRAY);

    final List<DataObject> weeks = dataObject.getValue();
    this.verifyWeeks(weeks, weekNames, dayIds);
  }

  private void verifyWeeks(
      final List<DataObject> weeks, final List<String> weekNames, final List<Short> dayIds) {
    assertThat(weeks).hasSize(weekNames.size());

    final List<String> weekNamesNotFound = new ArrayList<>(weekNames);
    weeks.forEach(
        week -> {
          final List<DataObject> nameAndDays = week.getValue();
          assertThat(nameAndDays).hasSize(8);

          // Week name
          assertThat(nameAndDays.get(0).getType()).isEqualTo(Type.OCTET_STRING);
          final String weekName =
              new String((byte[]) nameAndDays.get(0).getValue(), StandardCharsets.UTF_8);
          assertThat(weekNamesNotFound.remove(weekName)).isTrue();

          // DayIds for Monday - Sunday
          IntStream.range(1, 7)
              .forEach(
                  i -> {
                    assertThat(nameAndDays.get(i).getType()).isEqualTo(Type.UNSIGNED);
                    assertThat((short) nameAndDays.get(i).getValue())
                        .isEqualTo(dayIds.get(this.getIndexOr0(i - 1, dayIds.size())));
                  });
        });

    assertThat(weekNamesNotFound).isEmpty();
  }

  private void verifySetParameterDayProfiles(
      final SetParameter setParameter,
      final List<Short> dayIds,
      final List<CosemTimeDto> actionStartTimes) {
    final AttributeAddress attributeAddress = setParameter.getAttributeAddress();
    assertThat(attributeAddress.getClassId()).isEqualTo(CLASS_ID);
    assertThat(attributeAddress.getInstanceId()).isEqualTo(OBIS_CODE);
    assertThat(attributeAddress.getId()).isEqualTo(ATTRIBUTE_ID_DAY_PROFILE_TABLE_PASSIVE);

    final DataObject dataObject = setParameter.getData();
    assertThat(dataObject.getType()).isEqualTo(Type.ARRAY);

    final List<DataObject> days = dataObject.getValue();
    this.verifyDays(days, dayIds, actionStartTimes);
  }

  private void verifyDays(
      final List<DataObject> days,
      final List<Short> dayIds,
      final List<CosemTimeDto> actionStartTimes) {
    assertThat(days).hasSize(dayIds.size());

    final List<Short> dayIdsNotFound = new ArrayList<>(dayIds);
    days.forEach(
        day -> {
          final List<DataObject> elements = day.getValue();
          assertThat(elements).hasSize(2);

          // DayId
          assertThat(elements.get(0).getType()).isEqualTo(Type.UNSIGNED);
          final Short id = elements.get(0).getValue();
          assertThat(dayIdsNotFound.remove(id)).isTrue();

          // Day profile actions
          assertThat(elements.get(1).getType()).isEqualTo(Type.ARRAY);
          final List<DataObject> actions = elements.get(1).getValue();
          assertThat(actions).hasSize(actionStartTimes.size());

          IntStream.range(0, actions.size())
              .forEach(
                  i -> this.verifyDataObjectAction(actions.get(i), actionStartTimes.get(i), i + 1));
        });

    assertThat(dayIdsNotFound).isEmpty();
  }

  private void verifyDataObjectAction(
      final DataObject action, final CosemTimeDto actionStartTime, final int scriptSelector) {

    assertThat(action.getType()).isEqualTo(Type.STRUCTURE);

    final List<DataObject> actionElements = action.getValue();
    assertThat(actionElements).hasSize(3);

    // start_time
    assertThat(actionElements.get(0).getType()).isEqualTo(Type.OCTET_STRING);
    assertThat((byte[]) actionElements.get(0).getValue())
        .containsExactly(
            new byte[] {
              (byte) actionStartTime.getHour(),
              (byte) actionStartTime.getMinute(),
              (byte) actionStartTime.getSecond(),
              (byte) actionStartTime.getHundredths()
            });

    // script_logical_name
    assertThat(actionElements.get(1).getType()).isEqualTo(Type.OCTET_STRING);
    assertThat((byte[]) actionElements.get(1).getValue()).containsExactly(OBIS_CODE_SCRIPT.bytes());

    // script_selector
    assertThat(actionElements.get(2).getType()).isEqualTo(Type.LONG_UNSIGNED);
    assertThat((int) actionElements.get(2).getValue()).isEqualTo(scriptSelector);
  }
}
