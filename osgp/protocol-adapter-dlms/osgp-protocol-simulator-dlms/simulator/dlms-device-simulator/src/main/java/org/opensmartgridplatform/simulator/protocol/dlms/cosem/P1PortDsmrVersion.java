//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import org.openmuc.jdlms.AttributeAccessMode;
import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;

@CosemClass(id = 1)
public class P1PortDsmrVersion extends CosemInterfaceObject {

  @CosemAttribute(id = 2, type = Type.OCTET_STRING, accessMode = AttributeAccessMode.READ_ONLY)
  public DataObject value;

  public P1PortDsmrVersion(final byte[] value) {
    super("1.3.0.2.8.255");
    this.value = DataObject.newOctetStringData(value);
  }
}
