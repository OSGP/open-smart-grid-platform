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
public class MBusClearStatusMask extends CosemInterfaceObject {

  @CosemAttribute(
      id = 2,
      type = Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_AND_WRITE)
  public DataObject value;

  public MBusClearStatusMask(final int channel, final long value) {
    super(String.format("0.%1$d.94.31.10.255", channel));
    this.value = DataObject.newUInteger32Data(value);
  }
}
