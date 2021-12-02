/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * ://www.apache.org/licenses/LICENSE-2.0
 */
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
