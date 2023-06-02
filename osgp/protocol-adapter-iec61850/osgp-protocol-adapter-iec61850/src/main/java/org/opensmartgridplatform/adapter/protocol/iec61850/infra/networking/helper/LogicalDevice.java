//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper;

/** Contains the name of the Logical Device. */
public enum LogicalDevice {
  /** The name of the Logical Device. */
  LIGHTING("IO"),
  /** The name of the ABB light measurement Logical Device. */
  LD0("LD0"),
  /** Logical Device RTU */
  RTU("RTU"),
  /** Logical Device Photovoltaic */
  PV("PV"),
  /** Logical Device Battery */
  BATTERY("BATTERY"),
  /** Logical Device Engine */
  ENGINE("ENGINE"),
  /** Logical Device Load */
  LOAD("LOAD"),
  /** Logical Device Heat Buffer */
  HEAT_BUFFER("HEAT_BUFFER"),
  /** Logical Device CHP */
  CHP("CHP"),
  /** Logical Device Gas Furnace */
  GAS_FURNACE("GAS_FURNACE"),
  /** Logical Device Heat Pump */
  HEAT_PUMP("HEAT_PUMP"),
  /** Logical Device Boiler */
  BOILER("BOILER"),
  /** Logical Device Wind */
  WIND("WIND"),
  /** Logical Device Pq */
  PQ("PQ");

  private String description;

  private LogicalDevice(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return this.description;
  }

  public static LogicalDevice fromString(final String description) {

    if (description != null) {
      for (final LogicalDevice ld : LogicalDevice.values()) {
        if (description.equalsIgnoreCase(ld.description)) {
          return ld;
        }
      }
    }
    throw new IllegalArgumentException(
        "No LogicalDevice constant with description " + description + " found.");
  }
}
