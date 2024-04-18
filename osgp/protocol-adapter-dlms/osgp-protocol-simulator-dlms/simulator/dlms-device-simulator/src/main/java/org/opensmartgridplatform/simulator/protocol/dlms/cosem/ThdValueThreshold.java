// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.builder.ScalerUnitBuilder;

@CosemClass(id = 3)
public class ThdValueThreshold extends CosemInterfaceObject {

  @CosemAttribute(id = 2, type = Type.LONG_UNSIGNED)
  private final DataObject value;

  @CosemAttribute(id = 3, type = Type.STRUCTURE)
  private final DataObject scalerUnit;

  public ThdValueThreshold(final int value) {
    super("1.0.11.35.124.255");
    this.value = DataObject.newUInteger16Data(value);
    this.scalerUnit = ScalerUnitBuilder.createScalerUnit(0, UnitType.PERCENTAGE.value());
  }
}
