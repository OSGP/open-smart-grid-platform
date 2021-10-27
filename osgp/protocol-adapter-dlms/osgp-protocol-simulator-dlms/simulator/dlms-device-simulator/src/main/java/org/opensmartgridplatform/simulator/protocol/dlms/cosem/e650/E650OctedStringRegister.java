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

@CosemClass(id = 3)
public class E650OctedStringRegister extends CosemSnInterfaceObject {

  @CosemAttribute(id = 2, type = Type.LONG_UNSIGNED, snOffset = 0x08)
  private final DataObject currentValue;

  @CosemAttribute(id = 3, type = Type.STRUCTURE, snOffset = 0x10)
  private DataObject unitScale = ScalerUnitBuilder.createScalerUnit(0, 100);

  public E650OctedStringRegister(
      final int objectName,
      final String instanceId,
      final int value,
      final int scaler,
      final UnitType unit) {
    super(objectName, instanceId);
    this.currentValue = DataObject.newOctetStringData(new byte[value]);
    this.unitScale = ScalerUnitBuilder.createScalerUnit(scaler, unit.value());
  }
}
