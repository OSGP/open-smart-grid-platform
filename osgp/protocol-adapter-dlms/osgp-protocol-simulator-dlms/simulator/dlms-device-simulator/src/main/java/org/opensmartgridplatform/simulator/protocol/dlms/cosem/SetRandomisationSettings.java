//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;

@CosemClass(id = 1)
public class SetRandomisationSettings extends CosemInterfaceObject {

  @CosemAttribute(id = 2, type = Type.ARRAY)
  public DataObject entries;

  public SetRandomisationSettings() {
    super("0.1.94.31.12.255");
    this.entries = DataObject.newNullData();
  }
}
