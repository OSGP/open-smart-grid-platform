// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;

@CosemClass(id = 1)
public class CurrentlyActiveTariff extends CosemInterfaceObject {

  @CosemAttribute(id = 2, type = Type.OCTET_STRING)
  public DataObject value;

  public CurrentlyActiveTariff() {
    super("0.0.96.14.0.255");
    // Octet-string[2] as defined in DSMR
    this.value = DataObject.newOctetStringData("AB".getBytes());
  }
}
