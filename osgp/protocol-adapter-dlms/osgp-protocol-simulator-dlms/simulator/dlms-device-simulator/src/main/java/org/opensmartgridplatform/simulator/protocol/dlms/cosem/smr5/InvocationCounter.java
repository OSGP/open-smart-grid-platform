// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem.smr5;

import org.openmuc.jdlms.AttributeAccessMode;
import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.datatypes.DataObject;

@CosemClass(id = 1)
public class InvocationCounter extends CosemInterfaceObject {
  @CosemAttribute(
      id = 2,
      type = DataObject.Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_ONLY)
  public DataObject value;

  public InvocationCounter(final long value) {
    super("0.0.43.1.0.255");
    this.value = DataObject.newUInteger32Data(value);
  }
}
