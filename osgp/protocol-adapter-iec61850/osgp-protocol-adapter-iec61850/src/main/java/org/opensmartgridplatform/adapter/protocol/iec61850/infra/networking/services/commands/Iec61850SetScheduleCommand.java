/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.Fc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.ScheduleEntry;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.ScheduleWeekday;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.TriggerType;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeWriteException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.Function;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
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

  // The value used to indicate that the time on or time off of a schedule
  // entry is unused.
  private static final int DEFAULT_SCHEDULE_VALUE = -1;
  // The number of schedule entries available for a relay.
  private static final int MAX_NUMBER_OF_SCHEDULE_ENTRIES = 64;

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
      final RelayTypeDto relayType,
      final ScheduleDto scheduleDto,
      final Ssld ssld)
      throws ProtocolAdapterException {

    final String tariffOrLight = relayType.equals(RelayTypeDto.LIGHT) ? "light" : "tariff";
    final List<ScheduleEntryDto> scheduleList = scheduleDto.getScheduleList();
    final Short astronomicalSunriseOffset = scheduleDto.getAstronomicalSunriseOffset();
    final Short astronomicalSunsetOffset = scheduleDto.getAstronomicalSunsetOffset();

    try {
      // Creating a list of all Schedule entries, grouped by relay index.
      final Map<Integer, List<ScheduleEntry>> relaySchedulesEntries =
          this.createScheduleEntries(scheduleList, ssld, relayType);

      final Function<Void> function =
          new Function<Void>() {

            @Override
            public Void apply(final DeviceMessageLog deviceMessageLog)
                throws ProtocolAdapterException {

              this.writeAstronomicalOffsetsForSchedule(deviceMessageLog);

              Iec61850SetScheduleCommand.this.disableScheduleEntries(
                  relayType, deviceConnection, iec61850Client, deviceMessageLog, ssld);

              for (final Integer relayIndex : relaySchedulesEntries.keySet()) {
                final List<ScheduleEntry> scheduleEntries = relaySchedulesEntries.get(relayIndex);
                final int numberOfScheduleEntries = scheduleEntries.size();

                if (numberOfScheduleEntries > MAX_NUMBER_OF_SCHEDULE_ENTRIES) {
                  throw new ProtocolAdapterException(
                      "Received "
                          + numberOfScheduleEntries
                          + " "
                          + tariffOrLight
                          + " schedule entries for relay "
                          + relayIndex
                          + " for device "
                          + ssld.getDeviceIdentification()
                          + ". Setting more than "
                          + MAX_NUMBER_OF_SCHEDULE_ENTRIES
                          + " is not possible.");
                }

                // Get the logical node for the relay index.
                final LogicalNode logicalNode = LogicalNode.getSwitchComponentByIndex(relayIndex);
                // Check if the relay can be operated, if not, enable
                // operation for the current relay. This is needed to
                // ensure that the switch schedule which will be written
                // to the device can be executed.
                Iec61850Commands.enableOperationOfRelay(
                    deviceConnection, iec61850Client, deviceMessageLog, logicalNode, relayIndex);

                // Get the logical node and read all the values for the
                // schedule of the current relay.
                final NodeContainer schedule =
                    deviceConnection.getFcModelNode(
                        LogicalDevice.LIGHTING, logicalNode, DataAttribute.SCHEDULE, Fc.CF);
                iec61850Client.readNodeDataValues(
                    deviceConnection.getConnection().getClientAssociation(),
                    schedule.getFcmodelNode());

                // Write the schedule entries of the switch schedule to
                // the logical node of the schedule for the current
                // relay.
                for (int i = 0; i < numberOfScheduleEntries; i++) {
                  LOGGER.info(
                      "Write {} schedule entry {} for relay {}", tariffOrLight, i + 1, relayIndex);
                  this.writeScheduleEntryForRelay(
                      deviceMessageLog, scheduleEntries, logicalNode, schedule, i);
                }
              }
              Iec61850SetScheduleCommand.this.loggingService.logMessage(
                  deviceMessageLog,
                  deviceConnection.getDeviceIdentification(),
                  deviceConnection.getOrganisationIdentification(),
                  false);
              return null;
            }

            private void writeAstronomicalOffsetsForSchedule(
                final DeviceMessageLog deviceMessageLog) throws NodeException {
              if (relayType.equals(RelayTypeDto.LIGHT)
                  && astronomicalSunriseOffset != null
                  && astronomicalSunsetOffset != null) {
                final NodeContainer softwareConfiguration =
                    deviceConnection.getFcModelNode(
                        LogicalDevice.LIGHTING, LogicalNode.STREET_LIGHT_CONFIGURATION,
                        DataAttribute.SOFTWARE_CONFIGURATION, Fc.CF);

                softwareConfiguration.writeShort(
                    SubDataAttribute.ASTRONOMIC_SUNRISE_OFFSET, astronomicalSunriseOffset);
                deviceMessageLog.addVariable(
                    LogicalNode.STREET_LIGHT_CONFIGURATION,
                    DataAttribute.SOFTWARE_CONFIGURATION,
                    Fc.CF,
                    SubDataAttribute.ASTRONOMIC_SUNRISE_OFFSET,
                    Short.toString(astronomicalSunriseOffset));

                softwareConfiguration.writeShort(
                    SubDataAttribute.ASTRONOMIC_SUNSET_OFFSET, astronomicalSunsetOffset);
                deviceMessageLog.addVariable(
                    LogicalNode.STREET_LIGHT_CONFIGURATION,
                    DataAttribute.SOFTWARE_CONFIGURATION,
                    Fc.CF,
                    SubDataAttribute.ASTRONOMIC_SUNSET_OFFSET,
                    Short.toString(astronomicalSunsetOffset));
              }
            }

            private void writeScheduleEntryForRelay(
                final DeviceMessageLog deviceMessageLog,
                final List<ScheduleEntry> scheduleEntries,
                final LogicalNode logicalNode,
                final NodeContainer schedule,
                final int i)
                throws NodeWriteException {
              final ScheduleEntry scheduleEntry = scheduleEntries.get(i);

              final String scheduleEntryName =
                  SubDataAttribute.SCHEDULE_ENTRY.getDescription() + (i + 1);
              final NodeContainer scheduleNode = schedule.getChild(scheduleEntryName);

              this.setEnabled(
                  deviceMessageLog, logicalNode, scheduleEntry, scheduleEntryName, scheduleNode);

              this.setDay(
                  deviceMessageLog, logicalNode, scheduleEntry, scheduleEntryName, scheduleNode);

              this.setSwitchTimes(
                  deviceMessageLog, logicalNode, scheduleEntry, scheduleEntryName, scheduleNode);

              this.setMinimumTimeOn(
                  deviceMessageLog, logicalNode, scheduleEntry, scheduleEntryName, scheduleNode);

              this.setTriggerWindow(
                  deviceMessageLog, logicalNode, scheduleEntry, scheduleEntryName, scheduleNode);
            }

            private void setEnabled(
                final DeviceMessageLog deviceMessageLog,
                final LogicalNode logicalNode,
                final ScheduleEntry scheduleEntry,
                final String scheduleEntryName,
                final NodeContainer scheduleNode)
                throws NodeWriteException {
              final BdaBoolean enabled = scheduleNode.getBoolean(SubDataAttribute.SCHEDULE_ENABLE);
              if (enabled.getValue() != scheduleEntry.isEnabled()) {
                scheduleNode.writeBoolean(
                    SubDataAttribute.SCHEDULE_ENABLE, scheduleEntry.isEnabled());
              }
              deviceMessageLog.addVariable(
                  logicalNode,
                  DataAttribute.SCHEDULE,
                  Fc.CF,
                  scheduleEntryName,
                  SubDataAttribute.SCHEDULE_ENABLE,
                  Boolean.toString(scheduleEntry.isEnabled()));
            }

            private void setDay(
                final DeviceMessageLog deviceMessageLog,
                final LogicalNode logicalNode,
                final ScheduleEntry scheduleEntry,
                final String scheduleEntryName,
                final NodeContainer scheduleNode)
                throws NodeWriteException {
              final Integer day = scheduleNode.getInteger(SubDataAttribute.SCHEDULE_DAY).getValue();
              if (day != scheduleEntry.getDay()) {
                scheduleNode.writeInteger(SubDataAttribute.SCHEDULE_DAY, scheduleEntry.getDay());
              }
              deviceMessageLog.addVariable(
                  logicalNode,
                  DataAttribute.SCHEDULE,
                  Fc.CF,
                  scheduleEntryName,
                  SubDataAttribute.SCHEDULE_DAY,
                  Integer.toString(scheduleEntry.getDay()));
            }

            private void setSwitchTimes(
                final DeviceMessageLog deviceMessageLog,
                final LogicalNode logicalNode,
                final ScheduleEntry scheduleEntry,
                final String scheduleEntryName,
                final NodeContainer scheduleNode)
                throws NodeWriteException {
              /*
               * A schedule entry on the platform is about switching on a
               * certain time, or on a certain trigger. The schedule
               * entries on the device are about a period with a time on
               * and a time off. To bridge these different approaches,
               * either the on or the off values on the device are set to
               * a certain default to indicate they are not relevant to
               * the schedule entry.
               */
              int timeOnValue = DEFAULT_SCHEDULE_VALUE;
              byte timeOnTypeValue = DEFAULT_SCHEDULE_VALUE;
              int timeOffValue = DEFAULT_SCHEDULE_VALUE;
              byte timeOffTypeValue = DEFAULT_SCHEDULE_VALUE;

              if (scheduleEntry.isOn()) {
                timeOnValue = scheduleEntry.getTime();
                timeOnTypeValue = (byte) scheduleEntry.getTriggerType().getIndex();
              } else {
                timeOffValue = scheduleEntry.getTime();
                timeOffTypeValue = (byte) scheduleEntry.getTriggerType().getIndex();
              }

              final Integer timeOn =
                  scheduleNode.getInteger(SubDataAttribute.SCHEDULE_TIME_ON).getValue();
              if (timeOn != timeOnValue) {
                scheduleNode.writeInteger(SubDataAttribute.SCHEDULE_TIME_ON, timeOnValue);
              }
              deviceMessageLog.addVariable(
                  logicalNode,
                  DataAttribute.SCHEDULE,
                  Fc.CF,
                  scheduleEntryName,
                  SubDataAttribute.SCHEDULE_TIME_ON,
                  Integer.toString(timeOnValue));

              final Byte timeOnActionTime =
                  scheduleNode.getByte(SubDataAttribute.SCHEDULE_TIME_ON_TYPE).getValue();
              if (timeOnActionTime != timeOnTypeValue) {
                scheduleNode.writeByte(SubDataAttribute.SCHEDULE_TIME_ON_TYPE, timeOnTypeValue);
              }
              deviceMessageLog.addVariable(
                  logicalNode,
                  DataAttribute.SCHEDULE,
                  Fc.CF,
                  scheduleEntryName,
                  SubDataAttribute.SCHEDULE_TIME_ON_TYPE,
                  Byte.toString(timeOnTypeValue));

              final Integer timeOff =
                  scheduleNode.getInteger(SubDataAttribute.SCHEDULE_TIME_OFF).getValue();
              if (timeOff != timeOffValue) {
                scheduleNode.writeInteger(SubDataAttribute.SCHEDULE_TIME_OFF, timeOffValue);
              }
              deviceMessageLog.addVariable(
                  logicalNode,
                  DataAttribute.SCHEDULE,
                  Fc.CF,
                  scheduleEntryName,
                  SubDataAttribute.SCHEDULE_TIME_OFF,
                  Integer.toString(timeOffValue));

              final Byte timeOffActionTime =
                  scheduleNode.getByte(SubDataAttribute.SCHEDULE_TIME_OFF_TYPE).getValue();
              if (timeOffActionTime != timeOffTypeValue) {
                scheduleNode.writeByte(SubDataAttribute.SCHEDULE_TIME_OFF_TYPE, timeOffTypeValue);
              }
              deviceMessageLog.addVariable(
                  logicalNode,
                  DataAttribute.SCHEDULE,
                  Fc.CF,
                  scheduleEntryName,
                  SubDataAttribute.SCHEDULE_TIME_OFF_TYPE,
                  Byte.toString(timeOffTypeValue));
            }

            private void setMinimumTimeOn(
                final DeviceMessageLog deviceMessageLog,
                final LogicalNode logicalNode,
                final ScheduleEntry scheduleEntry,
                final String scheduleEntryName,
                final NodeContainer scheduleNode)
                throws NodeWriteException {
              final Integer minimumTimeOn =
                  scheduleNode.getUnsignedShort(SubDataAttribute.MINIMUM_TIME_ON).getValue();
              final Integer newMinimumTimeOn = scheduleEntry.getMinimumLightsOn() / 60;
              if (!Objects.equals(minimumTimeOn, newMinimumTimeOn)) {
                scheduleNode.writeUnsignedShort(SubDataAttribute.MINIMUM_TIME_ON, newMinimumTimeOn);
              }
              deviceMessageLog.addVariable(
                  logicalNode,
                  DataAttribute.SCHEDULE,
                  Fc.CF,
                  scheduleEntryName,
                  SubDataAttribute.MINIMUM_TIME_ON,
                  Integer.toString(newMinimumTimeOn));
            }

            private void setTriggerWindow(
                final DeviceMessageLog deviceMessageLog,
                final LogicalNode logicalNode,
                final ScheduleEntry scheduleEntry,
                final String scheduleEntryName,
                final NodeContainer scheduleNode)
                throws NodeWriteException {
              final Integer triggerMinutesBefore =
                  scheduleNode
                      .getUnsignedShort(SubDataAttribute.SCHEDULE_TRIGGER_MINUTES_BEFORE)
                      .getValue();
              if (triggerMinutesBefore != scheduleEntry.getTriggerWindowMinutesBefore()) {
                scheduleNode.writeUnsignedShort(
                    SubDataAttribute.SCHEDULE_TRIGGER_MINUTES_BEFORE,
                    scheduleEntry.getTriggerWindowMinutesBefore());
              }
              deviceMessageLog.addVariable(
                  logicalNode,
                  DataAttribute.SCHEDULE,
                  Fc.CF,
                  scheduleEntryName,
                  SubDataAttribute.SCHEDULE_TRIGGER_MINUTES_BEFORE,
                  Integer.toString(scheduleEntry.getTriggerWindowMinutesBefore()));

              final Integer triggerMinutesAfter =
                  scheduleNode
                      .getUnsignedShort(SubDataAttribute.SCHEDULE_TRIGGER_MINUTES_AFTER)
                      .getValue();
              if (triggerMinutesAfter != scheduleEntry.getTriggerWindowMinutesAfter()) {
                scheduleNode.writeUnsignedShort(
                    SubDataAttribute.SCHEDULE_TRIGGER_MINUTES_AFTER,
                    scheduleEntry.getTriggerWindowMinutesAfter());
              }
              deviceMessageLog.addVariable(
                  logicalNode,
                  DataAttribute.SCHEDULE,
                  Fc.CF,
                  scheduleEntryName,
                  SubDataAttribute.SCHEDULE_TRIGGER_MINUTES_AFTER,
                  Integer.toString(scheduleEntry.getTriggerWindowMinutesAfter()));
            }
          };

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

    ScheduleEntry scheduleEntry;
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
      builder.enabled(schedule.getIsEnabled() == null ? true : schedule.getIsEnabled());
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
    } catch (IllegalStateException | IllegalArgumentException e) {
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

  /**
   * Disable the schedule entries for all relays of a given {@link RelayTypeDto} using the {@link
   * DeviceOutputSetting}s for a device.
   *
   * @throws NodeException
   */
  private void disableScheduleEntries(
      final RelayTypeDto relayTypeDto,
      final DeviceConnection deviceConnection,
      final Iec61850Client iec61850Client,
      final DeviceMessageLog deviceMessageLog,
      final Ssld ssld)
      throws NodeException {

    final List<DeviceOutputSetting> deviceOutputSettings =
        this.ssldDataService.findByRelayType(ssld, RelayType.valueOf(relayTypeDto.name()));

    for (final DeviceOutputSetting deviceOutputSetting : deviceOutputSettings) {
      final int relayIndex = deviceOutputSetting.getInternalId();

      final LogicalNode logicalNode = LogicalNode.getSwitchComponentByIndex(relayIndex);
      final NodeContainer schedule =
          deviceConnection.getFcModelNode(
              LogicalDevice.LIGHTING, logicalNode, DataAttribute.SCHEDULE, Fc.CF);
      iec61850Client.readNodeDataValues(
          deviceConnection.getConnection().getClientAssociation(), schedule.getFcmodelNode());

      for (int i = 1; i <= MAX_NUMBER_OF_SCHEDULE_ENTRIES; i++) {
        final String scheduleEntryName = SubDataAttribute.SCHEDULE_ENTRY.getDescription() + i;
        final NodeContainer scheduleNode = schedule.getChild(scheduleEntryName);

        final boolean enabled =
            scheduleNode.getBoolean(SubDataAttribute.SCHEDULE_ENABLE).getValue();
        LOGGER.info(
            "Checking if schedule entry {} for relay {} is enabled: {}", i, relayIndex, enabled);
        if (enabled) {
          LOGGER.info(
              "Disabling schedule entry {} of {} for relay {} before setting new {} schedule",
              i,
              MAX_NUMBER_OF_SCHEDULE_ENTRIES,
              relayIndex,
              relayTypeDto.name());
          scheduleNode.writeBoolean(SubDataAttribute.SCHEDULE_ENABLE, false);

          deviceMessageLog.addVariable(
              logicalNode,
              DataAttribute.SCHEDULE,
              Fc.CF,
              scheduleEntryName,
              SubDataAttribute.SCHEDULE_ENABLE,
              Boolean.toString(false));
        }
      }
    }
  }
}
