/*
 * Copyright 2016 Smart Society Services B.V.
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
public class AlarmFilter extends CosemInterfaceObject {

  @CosemAttribute(id = 2, type = Type.DOUBLE_LONG_UNSIGNED)
  private final DataObject value;

  public AlarmFilter(final int value) {
    super("0.0.97.98.10.255");
    this.value = DataObject.newUInteger32Data(value);
  }
}
