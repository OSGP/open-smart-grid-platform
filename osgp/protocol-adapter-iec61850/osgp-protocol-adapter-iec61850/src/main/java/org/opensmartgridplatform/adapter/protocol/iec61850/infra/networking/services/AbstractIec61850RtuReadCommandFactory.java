/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.IntFunction;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommandFactory;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ActivePowerCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ActualPowerCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ActualPowerLimitCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850AlarmCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850AlarmOtherCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850AveragePowerFactorCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850BehaviourCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850FrequencyCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850HealthCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ImpedanceCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850LoadActualPowerCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850LoadMaximumActualPowerCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850LoadMinimumActualPowerCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850LoadTotalEnergyCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850MaterialFlowCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850MaterialStatusCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850MaterialTypeCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850MaximumActualPowerCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850MinimumActualPowerCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ModeCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850OperationalHoursCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850PhaseToNeutralVoltageCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850PowerFactorCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleCatCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleIdCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleTypeCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850StateCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850TemperatureCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850TotalEnergyCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850VlmCapCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850VoltageDipsCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850WarningCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850WarningOtherCommand;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementFilterDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractIec61850RtuReadCommandFactory
    implements RtuReadCommandFactory<MeasurementDto, MeasurementFilterDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AbstractIec61850RtuReadCommandFactory.class);

  private static final int ONE = 1;
  private static final int TWO = 2;
  private static final int THREE = 3;
  private static final int FOUR = 4;

  private final Map<String, RtuReadCommand<MeasurementDto>> rtuCommandMap;
  private final Set<DataAttribute> dataAttributesUsingFilterId =
      EnumSet.noneOf(DataAttribute.class);

  protected AbstractIec61850RtuReadCommandFactory(
      final Map<String, RtuReadCommand<MeasurementDto>> rtuCommandMap,
      final Set<DataAttribute> dataAttributesUsingFilterId) {

    if (rtuCommandMap == null) {
      this.rtuCommandMap = Collections.emptyMap();
    } else {
      this.rtuCommandMap = new TreeMap<>(rtuCommandMap);
    }
    if (dataAttributesUsingFilterId != null) {
      this.dataAttributesUsingFilterId.addAll(dataAttributesUsingFilterId);
    }
  }

  @Override
  public RtuReadCommand<MeasurementDto> getCommand(final MeasurementFilterDto filter) {
    final DataAttribute dataAttribute = DataAttribute.fromString(filter.getNode());
    if (this.useFilterId(dataAttribute)) {
      return this.getCommand(filter.getNode() + filter.getId());
    } else {
      return this.getCommand(filter.getNode());
    }
  }

  @Override
  public RtuReadCommand<MeasurementDto> getCommand(final String node) {
    final RtuReadCommand<MeasurementDto> command = this.rtuCommandMap.get(node);
    if (command == null) {
      LOGGER.warn("No command found for node {}", node);
    }
    return command;
  }

  private boolean useFilterId(final DataAttribute dataAttribute) {
    return this.dataAttributesUsingFilterId.contains(dataAttribute);
  }

  protected static final class CommandsByAttributeBuilder {

    private static final Map<DataAttribute, RtuReadCommand<MeasurementDto>>
        READ_COMMANDS_BY_ATTRIBUTE = new EnumMap<>(DataAttribute.class);

    private static final Map<DataAttribute, IntFunction<RtuReadCommand<MeasurementDto>>>
        READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE = new EnumMap<>(DataAttribute.class);

    private static final Map<DataAttribute, Map<Integer, RtuReadCommand<MeasurementDto>>>
        readCommandsByAttributeByIndex = new EnumMap<>(DataAttribute.class);

    static {
      /*
       * Prepare a command or command factory for each DataAttribute.
       *
       * If its command takes a no-argument constructor, put a new command
       * in READ_COMMANDS_BY_ATTRIBUTE (commands are stateless services
       * and can be re-used).
       *
       * If the command is for an attribute with an index, put a reference
       * to the constructor taking an int-value in
       * READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE. The actual commands
       * for the indexes that are used will be stored in
       * readCommandsByAttributeByIndex after creation using the factory
       * methods stored here.
       */
      for (final DataAttribute dataAttribute : DataAttribute.values()) {
        switch (dataAttribute) {
          case ACTIVE_POWER:
            /*
             * No command for ACTIVE_POWER. Iec61850ActivePowerCommand
             * is created with an index for ACTIVE_POWER_PHASE_A,
             * ACTIVE_POWER_PHASE_B and ACTIVE_POWER_PHASE_C.
             */
            break;
          case ACTIVE_POWER_PHASE_A:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute,
                i -> new Iec61850ActivePowerCommand(i, DataAttribute.ACTIVE_POWER_PHASE_A));
            break;
          case ACTIVE_POWER_PHASE_B:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute,
                i -> new Iec61850ActivePowerCommand(i, DataAttribute.ACTIVE_POWER_PHASE_B));
            break;
          case ACTIVE_POWER_PHASE_C:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute,
                i -> new Iec61850ActivePowerCommand(i, DataAttribute.ACTIVE_POWER_PHASE_C));
            break;
          case ACTUAL_POWER:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850ActualPowerCommand());
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute, Iec61850LoadActualPowerCommand::new);
            break;
          case ACTUAL_POWER_LIMIT:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850ActualPowerLimitCommand());
            break;
          case ALARM_FOUR:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850AlarmCommand(FOUR));
            break;
          case ALARM_ONE:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850AlarmCommand(ONE));
            break;
          case ALARM_OTHER:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850AlarmOtherCommand());
            break;
          case ALARM_THREE:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850AlarmCommand(THREE));
            break;
          case ALARM_TWO:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850AlarmCommand(TWO));
            break;
          case ASTRONOMICAL:
            break;
          case AVERAGE_POWER_FACTOR:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850AveragePowerFactorCommand());
            break;
          case BEHAVIOR:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850BehaviourCommand());
            break;
          case CERTIFICATE_AUTHORITY_REPLACE:
            break;
          case CLOCK:
            break;
          case DEMAND_POWER:
            break;
          case EVENT_BUFFER:
            break;
          case EVENT_RPN:
            break;
          case FREQUENCY:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute, i -> new Iec61850FrequencyCommand(i, DataAttribute.FREQUENCY));
            break;
          case FUNCTIONAL_FIRMWARE:
            break;
          case GENERATOR_SPEED:
            break;
          case HEALTH:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850HealthCommand());
            break;
          case IMPEDANCE:
            /*
             * No command for IMPEDANCE. Iec61850ImpedanceCommand is
             * created with an index for IMPEDANCE_PHASE_A,
             * IMPEDANCE_PHASE_B and IMPEDANCE_PHASE_C.
             */
            break;
          case IMPEDANCE_PHASE_A:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute,
                i -> new Iec61850ImpedanceCommand(i, DataAttribute.IMPEDANCE_PHASE_A));
            break;
          case IMPEDANCE_PHASE_B:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute,
                i -> new Iec61850ImpedanceCommand(i, DataAttribute.IMPEDANCE_PHASE_B));
            break;
          case IMPEDANCE_PHASE_C:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute,
                i -> new Iec61850ImpedanceCommand(i, DataAttribute.IMPEDANCE_PHASE_C));
            break;
          case IND:
            break;
          case IP_CONFIGURATION:
            break;
          case MASTER_CONTROL:
            break;
          case MATERIAL_FLOW:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute, Iec61850MaterialFlowCommand::new);
            break;
          case MATERIAL_STATUS:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute, Iec61850MaterialStatusCommand::new);
            break;
          case MATERIAL_TYPE:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute, Iec61850MaterialTypeCommand::new);
            break;
          case MAXIMUM_POWER_LIMIT:
            break;
          case MAX_ACTUAL_POWER:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850MaximumActualPowerCommand());
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute, Iec61850LoadMaximumActualPowerCommand::new);
            break;
          case MIN_ACTUAL_POWER:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850MinimumActualPowerCommand());
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute, Iec61850LoadMinimumActualPowerCommand::new);
            break;
          case MODE:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850ModeCommand());
            break;
          case NAME_PLATE:
            break;
          case OPERATIONAL_HOURS:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850OperationalHoursCommand());
            break;
          case OPERATION_TIME:
            break;
          case PHASE_TO_NEUTRAL_VOLTAGE:
            /*
             * No command for PHASE_TO_NEUTRAL_VOLTAGE.
             * Iec61850PhaseToNeutralVoltageCommand is created with an
             * index for PHASE_TO_NEUTRAL_VOLTAGE_PHASE_A,
             * PHASE_TO_NEUTRAL_VOLTAGE_PHASE_B and
             * PHASE_TO_NEUTRAL_VOLTAGE_PHASE_C.
             */
            break;
          case PHASE_TO_NEUTRAL_VOLTAGE_PHASE_A:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute,
                i ->
                    new Iec61850PhaseToNeutralVoltageCommand(
                        i, DataAttribute.PHASE_TO_NEUTRAL_VOLTAGE_PHASE_A));
            break;
          case PHASE_TO_NEUTRAL_VOLTAGE_PHASE_B:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute,
                i ->
                    new Iec61850PhaseToNeutralVoltageCommand(
                        i, DataAttribute.PHASE_TO_NEUTRAL_VOLTAGE_PHASE_B));
            break;
          case PHASE_TO_NEUTRAL_VOLTAGE_PHASE_C:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute,
                i ->
                    new Iec61850PhaseToNeutralVoltageCommand(
                        i, DataAttribute.PHASE_TO_NEUTRAL_VOLTAGE_PHASE_C));
            break;
          case PHYSICAL_HEALTH:
            break;
          case PHYSICAL_NAME:
            break;
          case POSITION:
            break;
          case POWER_FACTOR:
            /*
             * No command for POWER_FACTOR. Iec61850PowerFactorCommand
             * is created with an index for POWER_FACTOR_PHASE_A,
             * POWER_FACTOR_PHASE_B and POWER_FACTOR_PHASE_C.
             */
            break;
          case POWER_FACTOR_PHASE_A:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute,
                i -> new Iec61850PowerFactorCommand(i, DataAttribute.POWER_FACTOR_PHASE_A));
            break;
          case POWER_FACTOR_PHASE_B:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute,
                i -> new Iec61850PowerFactorCommand(i, DataAttribute.POWER_FACTOR_PHASE_B));
            break;
          case POWER_FACTOR_PHASE_C:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute,
                i -> new Iec61850PowerFactorCommand(i, DataAttribute.POWER_FACTOR_PHASE_C));
            break;
          case POWER_RATING:
            break;
          case RCB_A:
            break;
          case RCB_B:
            break;
          case REBOOT_OPERATION:
            break;
          case REGISTRATION:
            break;
          case REPORTING:
            break;
          case REPORT_HEARTBEAT_ONE:
            break;
          case REPORT_MEASUREMENTS_ONE:
            break;
          case REPORT_STATUS_ONE:
            break;
          case SCHEDULE:
            break;
          case SCHEDULE_ABS_TIME:
            /*
             * Iec61850ScheduleAbsTimeCommand is incompatible with
             * return type: RtuReadCommand<MeasurementDto>, it
             * implements RtuReadCommand<ProfileDto>,
             * RtuWriteCommand<ProfileDto>
             */
            break;
          case SCHEDULE_CAT:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute, Iec61850ScheduleCatCommand::new);
            break;
          case SCHEDULE_CAT_RTU:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute, Iec61850ScheduleCatCommand::new);
            break;
          case SCHEDULE_ID:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute, Iec61850ScheduleIdCommand::new);
            break;
          case SCHEDULE_TYPE:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute, Iec61850ScheduleTypeCommand::new);
            break;
          case SECURITY_FIRMWARE:
            break;
          case SENSOR:
            break;
          case SOFTWARE_CONFIGURATION:
            break;
          case STATE:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850StateCommand());
            break;
          case SWITCH_ON_INTERVAL_BUFFER:
            break;
          case SWITCH_TYPE:
            break;
          case TEMPERATURE:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute, Iec61850TemperatureCommand::new);
            break;
          case TLS_CONFIGURATION:
            break;
          case TOTAL_ENERGY:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850TotalEnergyCommand());
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute, Iec61850LoadTotalEnergyCommand::new);
            break;
          case VLMCAP:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850VlmCapCommand());
            break;
          case VOLTAGE_DIPS:
            READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.put(
                dataAttribute, i -> new Iec61850VoltageDipsCommand(i, dataAttribute));
            break;
          case WARNING_FOUR:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850WarningCommand(FOUR));
            break;
          case WARNING_ONE:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850WarningCommand(ONE));
            break;
          case WARNING_OTHER:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850WarningOtherCommand());
            break;
          case WARNING_THREE:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850WarningCommand(THREE));
            break;
          case WARNING_TWO:
            READ_COMMANDS_BY_ATTRIBUTE.put(dataAttribute, new Iec61850WarningCommand(TWO));
            break;
          default:
            throw new AssertionError(
                "No RtuReadCommand configured for DataAttribute " + dataAttribute);
        }
      }
    }

    private final Map<String, RtuReadCommand<MeasurementDto>> map = new HashMap<>();

    public Map<String, RtuReadCommand<MeasurementDto>> build() {
      return this.map;
    }

    public CommandsByAttributeBuilder withSimpleCommandsFor(final Set<DataAttribute> attributes) {
      final Map<String, RtuReadCommand<MeasurementDto>> commandsByAttributeName =
          attributes.stream()
              .filter(READ_COMMANDS_BY_ATTRIBUTE::containsKey)
              .collect(toMap(DataAttribute::getDescription, READ_COMMANDS_BY_ATTRIBUTE::get));
      this.map.putAll(commandsByAttributeName);
      return this;
    }

    public CommandsByAttributeBuilder withIndexedCommandsFor(
        final Set<DataAttribute> indexedCommandAttributes,
        final int indexStart,
        final int indexEnd) {

      final Map<String, RtuReadCommand<MeasurementDto>> commandsByAttributeName = new HashMap<>();
      for (final DataAttribute dataAttribute : indexedCommandAttributes) {
        if (!READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.containsKey(dataAttribute)) {
          continue;
        }
        final Map<Integer, RtuReadCommand<MeasurementDto>> attributeCommandsByIndex;
        synchronized (readCommandsByAttributeByIndex) {
          readCommandsByAttributeByIndex.putIfAbsent(dataAttribute, new TreeMap<>());
          attributeCommandsByIndex = readCommandsByAttributeByIndex.get(dataAttribute);
        }
        synchronized (attributeCommandsByIndex) {
          for (int i = indexStart; i <= indexEnd; i++) {
            if (!attributeCommandsByIndex.containsKey(i)) {
              attributeCommandsByIndex.put(
                  i, READ_COMMAND_FACTORY_INDEXED_BY_ATTRIBUTE.get(dataAttribute).apply(i));
            }
            commandsByAttributeName.put(
                dataAttribute.getDescription() + i, attributeCommandsByIndex.get(i));
          }
        }
      }
      this.map.putAll(commandsByAttributeName);
      return this;
    }
  }
}
