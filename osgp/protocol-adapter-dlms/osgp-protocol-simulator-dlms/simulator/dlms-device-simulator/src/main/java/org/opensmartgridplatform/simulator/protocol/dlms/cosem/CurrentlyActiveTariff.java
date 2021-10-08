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
public class CurrentlyActiveTariff extends CosemInterfaceObject {

  @CosemAttribute(id = 2, type = Type.OCTET_STRING)
  public DataObject value;

  public CurrentlyActiveTariff() {
    super("0.0.96.14.0.255");
    // Octet-string[2] as defined in DSMR
    this.value = DataObject.newOctetStringData("AB".getBytes());
  }
}
