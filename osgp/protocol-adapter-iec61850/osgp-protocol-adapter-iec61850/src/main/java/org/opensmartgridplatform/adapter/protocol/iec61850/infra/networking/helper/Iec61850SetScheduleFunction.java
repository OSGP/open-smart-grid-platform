// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper;

import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.Fc;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.SetScheduleDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.ScheduleEntry;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeWriteException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850Commands;
import org.opensmartgridplatform.adapter.protocol.iec61850.services.DeviceMessageLoggingService;
import org.opensmartgridplatform.core.db.api.iec61850.application.services.SsldDataService;
import org.opensmartgridplatform.core.db.api.iec61850.entities.DeviceOutputSetting;
import org.opensmartgridplatform.core.db.api.iec61850.entities.Ssld;
import org.opensmartgridplatform.core.db.api.iec61850valueobjects.RelayType;
import org.opensmartgridplatform.dto.valueobjects.RelayTypeDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec61850SetScheduleFunction implements Function<Void> {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850SetScheduleFunction.class);
  // The value used to indicate that the time on or time off of a schedule
  // entry is unused.
  private static final int DEFAULT_SCHEDULE_VALUE = -1;
  // The number of schedule entries available for a relay.
  private static final int MAX_NUMBER_OF_SCHEDULE_ENTRIES = 64;

  private final Iec61850Client iec61850Client;
  private final DeviceConnection deviceConnection;
  private final RelayTypeDto relayType;
  private final Ssld ssld;

  private final ScheduleDto scheduleDto;
  private final Map<Integer, List<ScheduleEntry>> relaySchedulesEntries;

  private final DeviceMessageLoggingService loggingService;
  private final SsldDataService ssldDataService;

  public Iec61850SetScheduleFunction(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final SetScheduleDeviceRequest deviceRequest,
      final Ssld ssld,
      final Map<Integer, List<ScheduleEntry>> relaySchedulesEntries,
      final DeviceMessageLoggingService loggingService,
      final SsldDataService ssldDataService) {
    this.iec61850Client = iec61850Client;
    this.deviceConnection = deviceConnection;
    this.ssld = ssld;
    this.relaySchedulesEntries = relaySchedulesEntries;
    this.loggingService = loggingService;
    this.ssldDataService = ssldDataService;
    this.relayType = deviceRequest.getRelayType();
    this.scheduleDto = deviceRequest.getSchedule();
  }

  @Override
  public Void apply(final DeviceMessageLog deviceMessageLog) throws ProtocolAdapterException {

    this.writeAstronomicalOffsetsForSchedule(deviceMessageLog);

    this.disableScheduleEntries(
        this.relayType, this.deviceConnection, this.iec61850Client, deviceMessageLog, this.ssld);

    for (final Map.Entry<Integer, List<ScheduleEntry>> scheduleEntriesByRelayIndex :
        this.relaySchedulesEntries.entrySet()) {
      final Integer relayIndex = scheduleEntriesByRelayIndex.getKey();
      final List<ScheduleEntry> scheduleEntries = scheduleEntriesByRelayIndex.getValue();
      final int numberOfScheduleEntries = scheduleEntries.size();
      final String tariffOrLight = this.relayType.equals(RelayTypeDto.LIGHT) ? "light" : "tariff";

      if (numberOfScheduleEntries > MAX_NUMBER_OF_SCHEDULE_ENTRIES) {
        throw new ProtocolAdapterException(
            "Received "
                + numberOfScheduleEntries
                + " "
                + tariffOrLight
                + " schedule entries for relay "
                + relayIndex
                + " for device "
                + this.ssld.getDeviceIdentification()
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
          this.deviceConnection, this.iec61850Client, deviceMessageLog, logicalNode, relayIndex);

      // Get the logical node and read all the values for the
      // schedule of the current relay.
      final NodeContainer schedule =
          this.deviceConnection.getFcModelNode(
              LogicalDevice.LIGHTING, logicalNode, DataAttribute.SCHEDULE, Fc.CF);
      this.iec61850Client.readNodeDataValues(
          this.deviceConnection.getConnection().getClientAssociation(), schedule.getFcmodelNode());

      // Write the schedule entries of the switch schedule to
      // the logical node of the schedule for the current
      // relay.
      for (int i = 0; i < numberOfScheduleEntries; i++) {
        LOGGER.info("Write {} schedule entry {} for relay {}", tariffOrLight, i + 1, relayIndex);
        this.writeScheduleEntryForRelay(
            deviceMessageLog, scheduleEntries, logicalNode, schedule, i);
      }
    }
    this.loggingService.logMessage(
        deviceMessageLog,
        this.deviceConnection.getDeviceIdentification(),
        this.deviceConnection.getOrganisationIdentification(),
        false);
    return null;
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
          if (LOGGER.isInfoEnabled()) {
            LOGGER.info(
                "Disabling schedule entry {} of {} for relay {} before setting new {} schedule",
                i,
                MAX_NUMBER_OF_SCHEDULE_ENTRIES,
                relayIndex,
                relayTypeDto.name());
          }
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

  private void writeAstronomicalOffsetsForSchedule(final DeviceMessageLog deviceMessageLog)
      throws NodeException {
    final Short astronomicalSunriseOffset = this.scheduleDto.getAstronomicalSunriseOffset();
    final Short astronomicalSunsetOffset = this.scheduleDto.getAstronomicalSunsetOffset();

    if (this.relayType.equals(RelayTypeDto.LIGHT)
        && astronomicalSunriseOffset != null
        && astronomicalSunsetOffset != null) {
      final NodeContainer softwareConfiguration =
          this.deviceConnection.getFcModelNode(
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

    final String scheduleEntryName = SubDataAttribute.SCHEDULE_ENTRY.getDescription() + (i + 1);
    final NodeContainer scheduleNode = schedule.getChild(scheduleEntryName);

    this.setEnabled(deviceMessageLog, logicalNode, scheduleEntry, scheduleEntryName, scheduleNode);

    this.setDay(deviceMessageLog, logicalNode, scheduleEntry, scheduleEntryName, scheduleNode);

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
      scheduleNode.writeBoolean(SubDataAttribute.SCHEDULE_ENABLE, scheduleEntry.isEnabled());
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
    final int day = scheduleNode.getInteger(SubDataAttribute.SCHEDULE_DAY).getValue();
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

    final int timeOn = scheduleNode.getInteger(SubDataAttribute.SCHEDULE_TIME_ON).getValue();
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

    final byte timeOnActionTime =
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

    final int timeOff = scheduleNode.getInteger(SubDataAttribute.SCHEDULE_TIME_OFF).getValue();
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

    final byte timeOffActionTime =
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
    final int triggerMinutesBefore =
        scheduleNode.getUnsignedShort(SubDataAttribute.SCHEDULE_TRIGGER_MINUTES_BEFORE).getValue();
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

    final int triggerMinutesAfter =
        scheduleNode.getUnsignedShort(SubDataAttribute.SCHEDULE_TRIGGER_MINUTES_AFTER).getValue();
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
}
