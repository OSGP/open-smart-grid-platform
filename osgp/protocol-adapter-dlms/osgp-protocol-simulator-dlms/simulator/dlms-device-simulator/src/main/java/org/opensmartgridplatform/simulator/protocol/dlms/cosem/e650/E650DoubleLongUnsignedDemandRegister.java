// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem.e650;

import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemSnInterfaceObject;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.UnitType;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.builder.ScalerUnitBuilder;

@CosemClass(id = 5)
public class E650DoubleLongUnsignedDemandRegister extends CosemSnInterfaceObject {

  @CosemAttribute(id = 2, type = Type.DOUBLE_LONG_UNSIGNED, snOffset = 0x08)
  private final DataObject currentAverageValue;

  @CosemAttribute(id = 3, type = Type.DOUBLE_LONG_UNSIGNED, snOffset = 0x10)
  private final DataObject lastAverageValue;

  /** the value of scaler and unit */
  @CosemAttribute(id = 4, type = Type.STRUCTURE, snOffset = 0x18)
  private final DataObject scalerUnit;

  public E650DoubleLongUnsignedDemandRegister(
      final int objectName,
      final String logicalName,
      final long value,
      final int scaler,
      final UnitType unit) {
    super(objectName, logicalName);
    this.currentAverageValue = DataObject.newUInteger32Data(value);
    this.lastAverageValue = DataObject.newUInteger32Data(value);
    this.scalerUnit = ScalerUnitBuilder.createScalerUnit(scaler, unit.value());
  }
}
