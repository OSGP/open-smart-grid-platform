//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.builder.ScalerUnitBuilder;

@CosemClass(id = 4)
public class OctetStringExtendedRegister extends CosemInterfaceObject {
  @CosemAttribute(id = 2, type = DataObject.Type.OCTET_STRING)
  private final DataObject value;

  @CosemAttribute(id = 3, type = DataObject.Type.STRUCTURE)
  private final DataObject scalerUnit;

  @CosemAttribute(id = 5, type = DataObject.Type.DATE_TIME)
  private final DataObject captureTime;

  public OctetStringExtendedRegister(
      final String logicalName,
      final byte[] value,
      final int scaler,
      final UnitType unit,
      final CosemDateTime dateTime) {
    super(logicalName);
    this.value = DataObject.newOctetStringData(value);
    this.scalerUnit = ScalerUnitBuilder.createScalerUnit(scaler, unit.value());
    this.captureTime = DataObject.newDateTimeData(dateTime);
  }
}
