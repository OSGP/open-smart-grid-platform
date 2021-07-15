/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper;

/** Contains a list of all Logical nodes of the IEC61850 Device */
public enum LogicalNode {
  /** LLN0, configuration Logical Node zero. */
  LOGICAL_NODE_ZERO("LLN0"),
  /** LPHD1, configuration Physical Device Node one. */
  PHYSICAL_DEVICE_ONE("LPHD1"),
  /** SPGGIO_X, general IO Node. */
  SPGGIO_1("SPGGIO1"),
  SPGGIO_2("SPGGIO2"),
  SPGGIO_3("SPGGIO3"),
  SPGGIO_4("SPGGIO4"),
  SPGGIO_5("SPGGIO5"),
  SPGGIO_6("SPGGIO6"),
  SPGGIO_7("SPGGIO7"),
  SPGGIO_8("SPGGIO8"),
  SPGGIO_9("SPGGIO9"),
  SPGGIO_10("SPGGIO10"),
  SPGGIO_11("SPGGIO11"),
  SPGGIO_12("SPGGIO12"),
  SPGGIO_13("SPGGIO13"),
  SPGGIO_14("SPGGIO14"),
  SPGGIO_15("SPGGIO15"),
  SPGGIO_16("SPGGIO16"),
  /** CSLC, configuration Logical Node. */
  STREET_LIGHT_CONFIGURATION("CSLC"),
  /** XSWC1, relays number 1 Logical Node. */
  SWITCH_COMPONENT_ONE("XSWC1"),
  /** XSWC2, relays number 1 Logical Node. */
  SWITCH_COMPONENT_TWO("XSWC2"),
  /** XSWC3, relays number 1 Logical Node. */
  SWITCH_COMPONENT_THREE("XSWC3"),
  /** XSWC4, relays number 1 Logical Node. */
  SWITCH_COMPONENT_FOUR("XSWC4"),
  /** DGEN1, DER Generator Logical Node. */
  GENERATOR_ONE("DGEN1"),
  /** ZBAT1, Logical Node containing Battery System characteristics */
  BATTERY_ONE("ZBAT1"),
  /** MMXU1, Measurements Logical Node. */
  MEASUREMENT_ONE("MMXU1"),
  /** MMXU2, Measurements Logical Node. */
  MEASUREMENT_TWO("MMXU2"),
  /** MMXU3, Measurements Logical Node. */
  MEASUREMENT_THREE("MMXU3"),
  /** MMXU4, Measurements Logical Node. */
  MEASUREMENT_FOUR("MMXU4"),
  /** MMXU5, Measurements Logical Node. */
  MEASUREMENT_FIVE("MMXU5"),
  /** MMTR1, Logical Node Meter Reading */
  METER_READING_ONE("MMTR1"),
  /** MMTR2, Logical Node Meter Reading */
  METER_READING_TWO("MMTR2"),
  /** MMTR3, Logical Node Meter Reading */
  METER_READING_THREE("MMTR3"),
  /** MMTR4, Logical Node Meter Reading */
  METER_READING_FOUR("MMTR4"),
  /** MMTR5, Logical Node Meter Reading */
  METER_READING_FIVE("MMTR5"),
  /** DRCT, DER Controller characteristics; */
  DER_CONTROLLER_CHARACTERISTICS_ONE("DRCT1"),
  /** DRCC, DER Supervisory Control */
  DER_SUPERVISORY_CONTROL_ONE("DRCC1"),
  /** GGIO, Generic Process I/O */
  GENERIC_PROCESS_I_O("GGIO1"),
  /** DSCH1, DER Schedule */
  DER_SCHEDULE_ONE("DSCH1"),
  /** DSCH2, DER Schedule */
  DER_SCHEDULE_TWO("DSCH2"),
  /** DSCH3, DER Schedule */
  DER_SCHEDULE_THREE("DSCH3"),
  /** DSCH4, DER Schedule */
  DER_SCHEDULE_FOUR("DSCH4"),
  /** KTNK1, Tank Characteristics */
  TANK_CHARACTERISTICS_ONE("KTNK1"),
  /** TTMP1, Temperature at entrance or top */
  TEMPERATURE_ONE("TTMP1"),
  /** TTMP2, Temperature at exit or middle */
  TEMPERATURE_TWO("TTMP2"),
  /** TTMP3, Temperature at exit or bottom */
  TEMPERATURE_THREE("TTMP3"),
  /** TTMP4, Target temperature */
  TEMPERATURE_FOUR("TTMP4"),
  /**
   * MFLW1, Type of material set to value 2 (Water) and MatStat 8State of material) to value 1
   * (Gaseous)
   */
  MAT_FLOW_ONE("MFLW1"),
  /**
   * MFLW2, Type of material set to value 6 (Natural gas) and MatStat 8State of material) to value 2
   * (Liquid)
   */
  MAT_FLOW_TWO("MFLW2"),
  /** QVVR1, Voltage variation */
  QVVR1("QVVR1");

  private final String description;

  private LogicalNode(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return this.description;
  }

  /**
   * Get the name of a relay Logical Node by index/number.
   *
   * @param index The index/number of the relay.
   * @return The name of a relay Logical Node.
   */
  public static LogicalNode getSwitchComponentByIndex(final int index) {
    switch (index) {
      case 1:
        return SWITCH_COMPONENT_ONE;
      case 2:
        return SWITCH_COMPONENT_TWO;
      case 3:
        return SWITCH_COMPONENT_THREE;
      case 4:
        return SWITCH_COMPONENT_FOUR;
      default:
        throw new IllegalArgumentException("Invalid index value for relay: " + index);
    }
  }

  /**
   * Get the name of a SPGGIO Logical Node by index/number.
   *
   * @param index The index/number of the SPGGIO.
   * @return The name of a SPGGIO Logical Node.
   */
  public static LogicalNode getSpggioByIndex(final int index) {

    if (index < 1 || index > 16) {
      throw new IllegalArgumentException("Invalid index value for SPGGIO: " + index);
    }
    final String description = "SPGGIO" + index;
    return LogicalNode.fromString(description);
  }

  public static LogicalNode fromString(final String description) {

    if (description != null) {
      for (final LogicalNode ln : LogicalNode.values()) {
        if (description.equalsIgnoreCase(ln.description)) {
          return ln;
        }
      }
    }
    throw new IllegalArgumentException(
        "No LogicalNode constant with description " + description + " found.");
  }
}
