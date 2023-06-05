// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.SetScheduleDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.ScheduleEntry;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.ScheduleWeekday;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.TriggerType;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.Function;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.Iec61850SetScheduleFunction;
import org.opensmartgridplatform.adapter.protocol.iec61850.services.DeviceMessageLoggingService;
import org.opensmartgridplatform.core.db.api.iec61850.application.services.SsldDataService;
import org.opensmartgridplatform.core.db.api.iec61850.entities.DeviceOutputSetting;
import org.opensmartgridplatform.core.db.api.iec61850.entities.Ssld;
import org.opensmartgridplatform.core.db.api.iec61850valueobjects.RelayType;
import org.opensmartgridplatform.dto.valueobjects.ActionTimeTypeDto;
import org.opensmartgridplatform.dto.valueobjects.LightValueDto;
import org.opensmartgridplatform.dto.valueobjects.RelayTypeDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleEntryDto;
import org.opensmartgridplatform.dto.valueobjects.WeekDayTypeDto;
import org.opensmartgridplatform.dto.valueobjects.WindowTypeDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec61850SetScheduleCommand {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850SetScheduleCommand.class);

  private final DeviceMessageLoggingService loggingService;

  private final SsldDataService ssldDataService;

  public Iec61850SetScheduleCommand(
      final DeviceMessageLoggingService loggingService, final SsldDataService ssldDataService) {
    this.loggingService = loggingService;
    this.ssldDataService = ssldDataService;
  }

  public void setScheduleOnDevice(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final SetScheduleDeviceRequest deviceRequest,
      final Ssld ssld)
      throws ProtocolAdapterException {
    final ScheduleDto scheduleDto = deviceRequest.getSchedule();
    final RelayTypeDto relayType = deviceRequest.getRelayType();
    final List<ScheduleEntryDto> scheduleList = scheduleDto.getScheduleList();

    try {
      // Creating a list of all Schedule entries, grouped by relay index.
      final Map<Integer, List<ScheduleEntry>> relaySchedulesEntries =
          this.createScheduleEntries(scheduleList, ssld, relayType);

      final Function<Void> function =
          new Iec61850SetScheduleFunction(
              iec61850Client,
              deviceConnection,
              deviceRequest,
              ssld,
              relaySchedulesEntries,
              this.loggingService,
              this.ssldDataService);

      iec61850Client.sendCommandWithRetry(
          function, "SetSchedule", deviceConnection.getDeviceIdentification());

    } catch (final FunctionalException e) {
      throw new ProtocolAdapterException(e.getMessage(), e);
    }
  }

  /** Returns a map of schedule entries, grouped by the internal index. */
  private Map<Integer, List<ScheduleEntry>> createScheduleEntries(
      final List<ScheduleEntryDto> scheduleList, final Ssld ssld, final RelayTypeDto relayTypeDto)
      throws FunctionalException {

    final Map<Integer, List<ScheduleEntry>> relaySchedulesEntries = new HashMap<>();

    final RelayType relayType = RelayType.valueOf(relayTypeDto.name());

    final List<DeviceOutputSetting> settings =
        this.ssldDataService.findByRelayType(ssld, relayType);

    for (final ScheduleEntryDto schedule : scheduleList) {
      for (final LightValueDto lightValue : schedule.getLightValue()) {

        this.setScheduleEntry(
            ssld, relaySchedulesEntries, relayType, settings, schedule, lightValue);
      }
    }

    return relaySchedulesEntries;
  }

  private ScheduleEntry setScheduleEntry(
      final Ssld ssld,
      final Map<Integer, List<ScheduleEntry>> relaySchedulesEntries,
      final RelayType relayType,
      final List<DeviceOutputSetting> settings,
      final ScheduleEntryDto schedule,
      final LightValueDto lightValue)
      throws FunctionalException {
    final List<Integer> indexes = new ArrayList<>();

    if (lightValue.getIndex() == 0
        && (RelayType.TARIFF.equals(relayType) || RelayType.TARIFF_REVERSED.equals(relayType))) {

      // Index 0 is not allowed for tariff switching.
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR, ComponentType.PROTOCOL_IEC61850);

    } else if (lightValue.getIndex() == 0 && RelayType.LIGHT.equals(relayType)) {

      // Index == 0, getting all light relays and adding their
      // internal indexes to the indexes list.
      for (final DeviceOutputSetting deviceOutputSetting : settings) {
        indexes.add(deviceOutputSetting.getInternalId());
      }
    } else {
      // Index != 0, adding just the one index to the list.
      indexes.add(this.ssldDataService.convertToInternalIndex(ssld, lightValue.getIndex()));
    }

    final ScheduleEntry scheduleEntry;
    try {
      scheduleEntry = this.convertToScheduleEntry(schedule, lightValue);
    } catch (final ProtocolAdapterException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR, ComponentType.PROTOCOL_IEC61850, e);
    }

    for (final Integer internalIndex : indexes) {

      if (relaySchedulesEntries.containsKey(internalIndex)) {
        // Internal index already in the Map, adding to the List
        relaySchedulesEntries.get(internalIndex).add(scheduleEntry);
      } else {

        // First time we come across this relay, checking its
        // type.
        this.checkRelayForSchedules(
            this.ssldDataService
                .getDeviceOutputSettingForInternalIndex(ssld, internalIndex)
                .getRelayType(),
            relayType,
            internalIndex);

        // Adding it to scheduleEntries.
        final List<ScheduleEntry> scheduleEntries = new ArrayList<>();
        scheduleEntries.add(scheduleEntry);

        relaySchedulesEntries.put(internalIndex, scheduleEntries);
      }
    }

    return scheduleEntry;
  }

  private ScheduleEntry convertToScheduleEntry(
      final ScheduleEntryDto schedule, final LightValueDto lightValue)
      throws ProtocolAdapterException {
    final ScheduleEntry.Builder builder = new ScheduleEntry.Builder();
    try {
      if (schedule.getTime() != null) {
        builder.time(this.convertTime(schedule.getTime()));
      }
      final WindowTypeDto triggerWindow = schedule.getTriggerWindow();
      if (triggerWindow != null) {
        if (triggerWindow.getMinutesBefore() > Integer.MAX_VALUE) {
          throw new IllegalArgumentException(
              "Schedule TriggerWindow minutesBefore must not be greater than " + Integer.MAX_VALUE);
        }
        if (triggerWindow.getMinutesAfter() > Integer.MAX_VALUE) {
          throw new IllegalArgumentException(
              "Schedule TriggerWindow minutesAfter must not be greater than " + Integer.MAX_VALUE);
        }
        builder.triggerWindowMinutesBefore((int) triggerWindow.getMinutesBefore());
        builder.triggerWindowMinutesAfter((int) triggerWindow.getMinutesAfter());
      }
      builder.triggerType(this.extractTriggerType(schedule));
      builder.enabled(schedule.getIsEnabled() == null || schedule.getIsEnabled());
      final WeekDayTypeDto weekDay = schedule.getWeekDay();
      if (WeekDayTypeDto.ABSOLUTEDAY.equals(weekDay)) {
        final DateTime specialDay = schedule.getStartDay();
        if (specialDay == null) {
          throw new IllegalArgumentException(
              "Schedule startDay must not be null when weekDay equals ABSOLUTEDAY");
        }
        builder.specialDay(specialDay);
      } else {
        builder.weekday(ScheduleWeekday.valueOf(schedule.getWeekDay().name()));
      }
      builder.on(lightValue.isOn());
      if (schedule.getMinimumLightsOn() != null) {
        builder.minimumLightsOn(schedule.getMinimumLightsOn());
      }
      return builder.build();
    } catch (final IllegalStateException | IllegalArgumentException e) {
      throw new ProtocolAdapterException(
          "Error converting ScheduleDto and LightValueDto into a ScheduleEntry: " + e.getMessage(),
          e);
    }
  }

  /** Check specific for schedule setting. */
  private void checkRelayForSchedules(
      final RelayType actual, final RelayType expected, final Integer internalAddress)
      throws FunctionalException {
    // First check the special case.
    if (expected.equals(RelayType.TARIFF) && actual.equals(RelayType.TARIFF_REVERSED)) {
      return;
    }
    this.checkRelay(actual, expected, internalAddress);
  }

  private TriggerType extractTriggerType(final ScheduleEntryDto schedule) {
    final TriggerType triggerType;
    if (ActionTimeTypeDto.ABSOLUTETIME.equals(schedule.getActionTime())) {
      triggerType = TriggerType.FIX;
    } else if (org.opensmartgridplatform.dto.valueobjects.TriggerTypeDto.ASTRONOMICAL.equals(
        schedule.getTriggerType())) {
      triggerType = TriggerType.AUTONOME;
    } else {
      triggerType = TriggerType.SENSOR;
    }
    return triggerType;
  }

  /**
   * Convert a time String to a short value.
   *
   * @param time a time String in the format hh:mm:ss.SSS, hh:mm:ss or hh:mm.
   * @return the short value formed by parsing the digits of hhmm from the given time.
   * @throws ProtocolAdapterException if time is {@code null} or not of the format specified.
   */
  private short convertTime(final String time) throws ProtocolAdapterException {
    if (time == null || !time.matches("\\d\\d:\\d\\d(:\\d\\d)?\\.?\\d*")) {
      throw new ProtocolAdapterException(
          "Schedule time (" + time + ") is not formatted as hh:mm, hh:mm:ss or hh:mm:ss.SSS");
    }
    return Short.parseShort(time.replace(":", "").substring(0, 4));
  }

  /**
   * Checks to see if the relay has the correct type, throws an exception when that't not the case.
   */
  private void checkRelay(
      final RelayType actual, final RelayType expected, final Integer internalAddress)
      throws FunctionalException {
    if (!actual.equals(expected)) {
      if (RelayType.LIGHT.equals(expected)) {
        LOGGER.error(
            "Relay with internal address: {} is not configured as light relay", internalAddress);
        throw new FunctionalException(
            FunctionalExceptionType.ACTION_NOT_ALLOWED_FOR_LIGHT_RELAY,
            ComponentType.PROTOCOL_IEC61850);
      } else {
        LOGGER.error(
            "Relay with internal address: {} is not configured as tariff relay", internalAddress);
        throw new FunctionalException(
            FunctionalExceptionType.ACTION_NOT_ALLOWED_FOR_TARIFF_RELAY,
            ComponentType.PROTOCOL_IEC61850);
      }
    }
  }
}
