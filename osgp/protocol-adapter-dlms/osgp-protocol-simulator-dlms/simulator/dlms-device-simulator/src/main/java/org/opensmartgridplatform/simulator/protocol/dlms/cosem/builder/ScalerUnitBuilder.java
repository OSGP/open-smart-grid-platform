// Copyright 2016 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem.builder;

import org.openmuc.jdlms.datatypes.DataObject;

public class ScalerUnitBuilder {

  public static final int MINIMUM_SCALER = -128;
  public static final int MAXIMUM_SCALER = 127;
  public static final int MINIMUM_UNIT = 0;
  public static final int MAXIMUM_UNIT = 255;

  private ScalerUnitBuilder() {
    // Private constructor, utility class.
  }

  /**
   * Creates a DataObject that can be used as a scaler_unit value.
   *
   * @param scaler the exponent (to the base of 10) of the multiplication factor ({@value
   *     #MINIMUM_SCALER} .. {@value #MAXIMUM_SCALER}).
   * @param unit enumeration value defining the physical unit ({@value #MINIMUM_UNIT} .. {@value
   *     #MAXIMUM_UNIT}).
   * @return a scaler_unit DataObject for the given scaler and unit.
   */
  public static DataObject createScalerUnit(final int scaler, final int unit) {
    if (scaler < MINIMUM_SCALER || scaler > MAXIMUM_SCALER) {
      throw new IllegalArgumentException(
          "scaler must be in [" + MINIMUM_SCALER + ".." + MAXIMUM_SCALER + "]: " + scaler);
    }
    if (unit < MINIMUM_UNIT || unit > MAXIMUM_UNIT) {
      throw new IllegalArgumentException(
          "unit must be a valid enumeration value ["
              + MINIMUM_UNIT
              + ".."
              + MAXIMUM_UNIT
              + "]: "
              + unit);
    }
    return DataObject.newStructureData(
        DataObject.newInteger8Data((byte) scaler), DataObject.newEnumerateData(unit));
  }
}
