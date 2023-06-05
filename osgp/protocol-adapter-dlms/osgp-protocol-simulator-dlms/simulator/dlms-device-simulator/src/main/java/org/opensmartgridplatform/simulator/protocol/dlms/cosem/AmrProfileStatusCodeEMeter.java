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
public class AmrProfileStatusCodeEMeter extends CosemInterfaceObject {

  /** The profile status code */
  @CosemAttribute(id = 2, type = Type.UNSIGNED)
  private final DataObject value;

  public AmrProfileStatusCodeEMeter(final short value) {
    super("0.0.96.10.2.255");
    this.value = DataObject.newUInteger8Data(value);
  }
}
