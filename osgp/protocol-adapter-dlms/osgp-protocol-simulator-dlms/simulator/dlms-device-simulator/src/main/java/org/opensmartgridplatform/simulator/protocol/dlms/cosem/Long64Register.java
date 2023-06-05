// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemSnInterfaceObject;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.builder.ScalerUnitBuilder;

@CosemClass(id = 3)
public class Long64Register extends CosemSnInterfaceObject {

  /** the value of the register */
  @CosemAttribute(id = 2, type = Type.LONG64, snOffset = 0x08)
  private final DataObject value;

  /** the value of scaler and unit */
  @CosemAttribute(id = 3, type = Type.STRUCTURE, snOffset = 0x10)
  private final DataObject scalerUnit;

  public Long64Register(
      final int objectName,
      final String logicalName,
      final long value,
      final int scaler,
      final UnitType unit) {
    super(objectName, logicalName);
    this.value = DataObject.newInteger64Data(value);
    this.scalerUnit = ScalerUnitBuilder.createScalerUnit(scaler, unit.value());
  }
}
