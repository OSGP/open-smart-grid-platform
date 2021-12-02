/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem.e650;

import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemSnInterfaceObject;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.UnitType;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.builder.ScalerUnitBuilder;

@CosemClass(id = 4)
public class E650DoubleLongExtendedRegister extends CosemSnInterfaceObject {

  /** the value of the register */
  @CosemAttribute(id = 2, type = Type.DOUBLE_LONG, snOffset = 0x08)
  private final DataObject value;

  /** the value of scaler and unit */
  @CosemAttribute(id = 3, type = Type.STRUCTURE, snOffset = 0x10)
  private final DataObject scalerUnit;

  public E650DoubleLongExtendedRegister(
      final int objectName,
      final String logicalName,
      final int value,
      final int scaler,
      final UnitType unit) {
    super(objectName, logicalName);
    this.value = DataObject.newInteger32Data(value);
    this.scalerUnit = ScalerUnitBuilder.createScalerUnit(scaler, unit.value());
  }
}
