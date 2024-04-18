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
public class ThdTimeThreshold extends CosemInterfaceObject {

  @CosemAttribute(id = 2, type = Type.DOUBLE_LONG_UNSIGNED)
  private final DataObject value;

  @CosemAttribute(id = 3, type = Type.STRUCTURE)
  private final DataObject scalerUnit;

  public ThdTimeThreshold(final long value) {
    super("1.0.11.44.124.255");
    this.value = DataObject.newUInteger32Data(value);
    this.scalerUnit = ScalerUnitBuilder.createScalerUnit(0, UnitType.SECONDS.value());
  }
}
