//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.cosem.e650;

import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemSnInterfaceObject;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.UnitType;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.builder.ScalerUnitBuilder;

/**
 * Diagnostic register like <em>DiagnosticRegister1</em>, <em>DiagnosticRegister2</em>, etcetera.
 */
@CosemClass(id = 3)
public class E650DiagnosticRegister extends CosemSnInterfaceObject {

  /** the value of the register */
  @CosemAttribute(id = 2, type = Type.DOUBLE_LONG_UNSIGNED, snOffset = 0x08)
  private final DataObject value;

  /** the value of scaler and unit */
  @CosemAttribute(id = 3, type = Type.STRUCTURE, snOffset = 0x10)
  private final DataObject scalerUnit;

  @CosemAttribute(id = 4, type = Type.NULL_DATA, snOffset = 0x18)
  private final DataObject resAttribute1 = DataObject.newNullData();

  @CosemAttribute(id = 5, type = Type.NULL_DATA, snOffset = 0x20)
  private final DataObject resAttribute2 = DataObject.newNullData();

  @CosemAttribute(id = 6, type = Type.NULL_DATA, snOffset = 0x28)
  private final DataObject reset = DataObject.newNullData();

  @CosemAttribute(id = 7, type = Type.NULL_DATA, snOffset = 0x30)
  private final DataObject resService = DataObject.newNullData();

  @CosemAttribute(id = 8, type = Type.LONG_UNSIGNED, snOffset = 0x38)
  private final DataObject subType = DataObject.newUInteger16Data(20009);

  @CosemAttribute(id = 9, type = Type.UNSIGNED, snOffset = 0x40)
  private final DataObject ownClassVersion = DataObject.newUInteger8Data((short) 8);

  @CosemAttribute(id = 10, type = Type.OCTET_STRING, snOffset = 0x48)
  private final DataObject idString = DataObject.newOctetStringData("1234".getBytes());

  @CosemAttribute(id = 11, type = Type.DOUBLE_LONG, snOffset = 0x50)
  private final DataObject filterValue = DataObject.newInteger32Data(1);

  public E650DiagnosticRegister(
      final int objectName,
      final String logicalName,
      final long value,
      final int scaler,
      final UnitType unit) {
    super(objectName, logicalName);
    this.value = DataObject.newUInteger32Data(value);
    this.scalerUnit = ScalerUnitBuilder.createScalerUnit(scaler, unit.value());
  }
}
