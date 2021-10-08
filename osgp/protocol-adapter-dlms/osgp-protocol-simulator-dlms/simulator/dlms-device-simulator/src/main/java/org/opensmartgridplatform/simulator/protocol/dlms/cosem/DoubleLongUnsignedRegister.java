/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.builder.ScalerUnitBuilder;

@CosemClass(id = 3)
public class DoubleLongUnsignedRegister extends CosemInterfaceObject {

  /** the value of the register */
  @CosemAttribute(id = 2, type = Type.DOUBLE_LONG_UNSIGNED)
  private final DataObject value;

  /** the value of scaler and unit */
  @CosemAttribute(id = 3, type = Type.STRUCTURE)
  private final DataObject scalerUnit;

  public DoubleLongUnsignedRegister(
      final String logicalName, final long value, final int scaler, final UnitType unit) {
    super(logicalName);
    this.value = DataObject.newUInteger32Data(value);
    this.scalerUnit = ScalerUnitBuilder.createScalerUnit(scaler, unit.value());
  }
}
