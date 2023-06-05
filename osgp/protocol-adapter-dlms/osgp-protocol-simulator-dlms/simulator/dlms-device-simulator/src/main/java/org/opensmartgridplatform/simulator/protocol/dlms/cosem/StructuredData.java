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
public class StructuredData extends CosemInterfaceObject {
  @CosemAttribute(id = 2, type = Type.STRUCTURE)
  private final DataObject value;

  public StructuredData(final String logicalName, final DataObject... values) {
    super(logicalName);
    this.value = DataObject.newStructureData(values);
  }
}
