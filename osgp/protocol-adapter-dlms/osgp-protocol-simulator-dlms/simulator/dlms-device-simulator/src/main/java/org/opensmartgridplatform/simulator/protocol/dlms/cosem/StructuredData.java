/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
