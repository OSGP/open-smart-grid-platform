// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import org.openmuc.jdlms.AttributeAccessMode;
import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;

@CosemClass(id = 1)
public class AdministrativeInOut extends CosemInterfaceObject {

  @CosemAttribute(id = 2, type = Type.ENUMERATE, accessMode = AttributeAccessMode.READ_AND_WRITE)
  public DataObject value;

  public AdministrativeInOut(final AdministrativeStatusType administrativeStatusType) {
    super("0.1.94.31.0.255");
    this.value = DataObject.newEnumerateData(administrativeStatusType.value());
  }
}
