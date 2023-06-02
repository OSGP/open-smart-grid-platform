//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;

@CosemClass(id = 1)
public class BitStringData extends CosemInterfaceObject {
  @CosemAttribute(id = 2, type = Type.BIT_STRING)
  private final DataObject value;

  public BitStringData(final String logicalName, final BitString value) {
    super(logicalName);
    this.value = DataObject.newBitStringData(value);
  }
}
