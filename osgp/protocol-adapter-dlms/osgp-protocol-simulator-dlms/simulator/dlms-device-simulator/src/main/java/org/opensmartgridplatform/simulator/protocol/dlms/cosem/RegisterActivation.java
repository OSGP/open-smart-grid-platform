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

@CosemClass(id = 6)
public class RegisterActivation extends CosemInterfaceObject {

  @CosemAttribute(id = 2, type = Type.ARRAY)
  public DataObject registerAssignment;

  @CosemAttribute(id = 3, type = Type.ARRAY)
  public DataObject maskList;

  @CosemAttribute(id = 4, type = Type.OCTET_STRING)
  public DataObject activeMask;

  public RegisterActivation() {
    super("0.0.14.0.0.255");
    this.registerAssignment = DataObject.newNullData();
    this.maskList = DataObject.newNullData();
    this.activeMask = DataObject.newNullData();
  }
}
