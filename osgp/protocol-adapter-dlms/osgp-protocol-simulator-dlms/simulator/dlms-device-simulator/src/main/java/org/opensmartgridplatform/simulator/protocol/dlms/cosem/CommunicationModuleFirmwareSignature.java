/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import org.openmuc.jdlms.AttributeAccessMode;
import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;

@CosemClass(id = 1)
public class CommunicationModuleFirmwareSignature extends CosemInterfaceObject {

  @CosemAttribute(id = 2, type = Type.OCTET_STRING, accessMode = AttributeAccessMode.READ_ONLY)
  public DataObject value;

  public CommunicationModuleFirmwareSignature(final byte[] value) {
    super("1.2.0.2.8.255");
    this.value = DataObject.newOctetStringData(value);
  }
}
