// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import java.util.List;
import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;

@CosemClass(id = 1)
public class ThdInstantaneousCurrentFingerprint extends CosemInterfaceObject {

  @CosemAttribute(id = 2, type = Type.ARRAY)
  private final DataObject values;

  public ThdInstantaneousCurrentFingerprint(final List<DataObject> values, final String obisCode) {
    super(obisCode);
    this.values = DataObject.newArrayData(values);
  }
}
