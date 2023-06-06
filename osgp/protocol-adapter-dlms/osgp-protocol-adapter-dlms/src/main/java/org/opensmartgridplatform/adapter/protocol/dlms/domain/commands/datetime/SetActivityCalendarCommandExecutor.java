// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DataObjectAttrExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DataObjectAttrExecutors;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActivityCalendarDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActivityCalendarDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DayProfileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SeasonProfileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.WeekProfileDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class SetActivityCalendarCommandExecutor
    extends AbstractCommandExecutor<ActivityCalendarDto, AccessResultCode> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SetActivityCalendarCommandExecutor.class);

  private static final int CLASS_ID = 20;
  private static final ObisCode OBIS_CODE = new ObisCode("0.0.13.0.0.255");
  private static final int ATTRIBUTE_ID_CALENDAR_NAME_PASSIVE = 6;
  private static final int ATTRIBUTE_ID_SEASON_PROFILE_PASSIVE = 7;
  private static final int ATTRIBUTE_ID_WEEK_PROFILE_TABLE_PASSIVE = 8;
  private static final int ATTRIBUTE_ID_DAY_PROFILE_TABLE_PASSIVE = 9;

  private final ConfigurationMapper configurationMapper;

  private final SetActivityCalendarCommandActivationExecutor
      setActivityCalendarCommandActivationExecutor;

  private final DlmsHelper dlmsHelper;

  @Autowired
  public SetActivityCalendarCommandExecutor(
      final DlmsHelper dlmsHelper,
      final SetActivityCalendarCommandActivationExecutor
          setActivityCalendarCommandActivationExecutor,
      final ConfigurationMapper configurationMapper) {
    super(ActivityCalendarDataDto.class);
    this.dlmsHelper = dlmsHelper;
    this.setActivityCalendarCommandActivationExecutor =
        setActivityCalendarCommandActivationExecutor;
    this.configurationMapper = configurationMapper;
  }

  @Override
  public ActivityCalendarDto fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);
    final ActivityCalendarDataDto activityCalendarDataDto = (ActivityCalendarDataDto) bundleInput;

    return activityCalendarDataDto.getActivityCalendar();
  }

  @Override
  public ActionResponseDto asBundleResponse(final AccessResultCode executionResult)
      throws ProtocolAdapterException {

    this.checkAccessResultCode(executionResult);

    return new ActionResponseDto("Set Activity Calendar Result is OK");
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ActivityCalendarDto activityCalendar,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException, FunctionalException {
    LOGGER.debug(
        "SetActivityCalendarCommandExecutor.execute {} called", activityCalendar.getCalendarName());

    ActivityCalendarValidator.validate(activityCalendar);

    final List<SeasonProfileDto> seasonProfileList = activityCalendar.getSeasonProfileList();
    final Set<WeekProfileDto> weekProfileSet = this.getWeekProfileSet(seasonProfileList);
    final Set<DayProfileDto> dayProfileSet = this.getDayProfileSet(weekProfileSet);

    final DataObjectAttrExecutors dataObjectExecutors =
        new DataObjectAttrExecutors("SetActivityCalendar")
            .addExecutor(this.getCalendarNameExecutor(activityCalendar))
            .addExecutor(this.getSeasonProfileExecutor(seasonProfileList))
            .addExecutor(this.getWeekProfileTableExecutor(weekProfileSet))
            .addExecutor(this.getDayProfileTablePassiveExecutor(dayProfileSet));

    conn.getDlmsMessageListener()
        .setDescription(
            "SetActivityCalendar for calendar "
                + activityCalendar.getCalendarName()
                + ", set attributes: "
                + dataObjectExecutors.describeAttributes());

    dataObjectExecutors.execute(conn);

    LOGGER.debug("Finished setting the passive activity calendar");

    // Now activate the newly set activity calendar
    // In case of an exception include the activity calendar set here above
    // in the exception to throw
    try {
      this.setActivityCalendarCommandActivationExecutor.execute(
          conn, device, null, messageMetadata);
      LOGGER.debug("Finished activating the passive to the active activity calendar");

    } catch (final ProtocolAdapterException e) {

      final StringBuilder message = new StringBuilder();
      for (final DataObjectAttrExecutor executor :
          dataObjectExecutors.getDataObjectAttrExecutorList()) {
        message.append(executor.createRequestAndResultCodeInfo());
      }
      throw new ProtocolAdapterException(e.getMessage() + message, e);
    }

    return AccessResultCode.SUCCESS;
  }

  private DataObjectAttrExecutor getCalendarNameExecutor(
      final ActivityCalendarDto activityCalendar) {
    final AttributeAddress calendarNamePassive =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_CALENDAR_NAME_PASSIVE);
    final DataObject value =
        DataObject.newOctetStringData(activityCalendar.getCalendarName().getBytes());
    return new DataObjectAttrExecutor(
        "CALENDARNAME",
        calendarNamePassive,
        value,
        CLASS_ID,
        OBIS_CODE,
        ATTRIBUTE_ID_CALENDAR_NAME_PASSIVE);
  }

  private DataObjectAttrExecutor getDayProfileTablePassiveExecutor(
      final Set<DayProfileDto> dayProfileSet) {
    final AttributeAddress dayProfileTablePassive =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_DAY_PROFILE_TABLE_PASSIVE);
    final DataObject dayArray =
        DataObject.newArrayData(
            this.configurationMapper.mapAsList(dayProfileSet, DataObject.class));

    LOGGER.debug("DayProfileTablePassive to set is: {}", this.dlmsHelper.getDebugInfo(dayArray));

    return new DataObjectAttrExecutor(
        "DAYS",
        dayProfileTablePassive,
        dayArray,
        CLASS_ID,
        OBIS_CODE,
        ATTRIBUTE_ID_DAY_PROFILE_TABLE_PASSIVE);
  }

  /** get all day profiles from all the week profiles */
  private Set<DayProfileDto> getDayProfileSet(final Set<WeekProfileDto> weekProfileSet) {
    final Set<DayProfileDto> dayProfileHashSet = new HashSet<>();

    for (final WeekProfileDto weekProfile : weekProfileSet) {
      dayProfileHashSet.addAll(weekProfile.getAllDaysAsList());
    }

    return dayProfileHashSet;
  }

  private DataObjectAttrExecutor getWeekProfileTableExecutor(
      final Set<WeekProfileDto> weekProfileSet) {

    final AttributeAddress weekProfileTablePassive =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_WEEK_PROFILE_TABLE_PASSIVE);
    final DataObject weekArray =
        DataObject.newArrayData(
            this.configurationMapper.mapAsList(weekProfileSet, DataObject.class));

    LOGGER.debug("WeekProfileTablePassive to set is: {}", this.dlmsHelper.getDebugInfo(weekArray));

    return new DataObjectAttrExecutor(
        "WEEKS",
        weekProfileTablePassive,
        weekArray,
        CLASS_ID,
        OBIS_CODE,
        ATTRIBUTE_ID_WEEK_PROFILE_TABLE_PASSIVE);
  }

  private Set<WeekProfileDto> getWeekProfileSet(final List<SeasonProfileDto> seasonProfileList) {
    // Use HashSet to ensure that unique WeekProfiles are returned. For
    // there can be duplicates.
    final Set<WeekProfileDto> weekProfileSet = new HashSet<>();

    for (final SeasonProfileDto seasonProfile : seasonProfileList) {
      weekProfileSet.add(seasonProfile.getWeekProfile());
    }
    return weekProfileSet;
  }

  private DataObjectAttrExecutor getSeasonProfileExecutor(
      final List<SeasonProfileDto> seasonProfileList) {

    final AttributeAddress seasonProfilePassive =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID_SEASON_PROFILE_PASSIVE);
    final DataObject seasonsArray =
        DataObject.newArrayData(
            this.configurationMapper.mapAsList(seasonProfileList, DataObject.class));

    LOGGER.debug("SeasonProfilePassive to set is: {}", this.dlmsHelper.getDebugInfo(seasonsArray));

    return new DataObjectAttrExecutor(
        "SEASONS",
        seasonProfilePassive,
        seasonsArray,
        CLASS_ID,
        OBIS_CODE,
        ATTRIBUTE_ID_SEASON_PROFILE_PASSIVE);
  }
}
