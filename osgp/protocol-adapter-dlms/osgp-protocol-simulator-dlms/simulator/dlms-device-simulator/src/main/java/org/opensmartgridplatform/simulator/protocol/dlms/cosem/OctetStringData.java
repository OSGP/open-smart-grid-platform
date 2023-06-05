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
import org.opensmartgridplatform.simulator.protocol.dlms.util.DynamicValues;
import org.springframework.beans.factory.annotation.Autowired;

@CosemClass(id = 1)
public class OctetStringData extends CosemInterfaceObject {

  public static final int ATTRIBUTE_ID_VALUE = 2;

  @Autowired private DynamicValues dynamicValues;

  @CosemAttribute(id = ATTRIBUTE_ID_VALUE, type = Type.OCTET_STRING)
  private DataObject value;

  public OctetStringData(final String logicalName) {
    super(logicalName);
  }

  public DataObject getValue() {
    return this.dynamicValues.getDlmsAttributeValue(this, ATTRIBUTE_ID_VALUE);
  }

  public void setValue(final DataObject octetStringValue) {
    this.dynamicValues.setDlmsAttributeValue(this, ATTRIBUTE_ID_VALUE, octetStringValue);
  }
}
