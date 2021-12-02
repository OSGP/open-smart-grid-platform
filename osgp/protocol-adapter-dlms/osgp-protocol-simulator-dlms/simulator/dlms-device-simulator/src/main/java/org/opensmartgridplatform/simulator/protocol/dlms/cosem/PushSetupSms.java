/*
 * Copyright 2017 Smart Society Services B.V.
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

@CosemClass(id = 40)
public class PushSetupSms extends CosemInterfaceObject {

  @CosemAttribute(id = 2, type = Type.ARRAY)
  public final DataObject value;

  @CosemAttribute(id = 3, type = Type.STRUCTURE)
  public final DataObject sendDestAndMethod;

  @CosemAttribute(id = 4, type = Type.ARRAY)
  public final DataObject communicationWindow;

  @CosemAttribute(id = 5, type = Type.LONG_UNSIGNED)
  public final DataObject randomisationStartInterval;

  @CosemAttribute(id = 6, type = Type.UNSIGNED)
  public final DataObject numberOfRetries;

  @CosemAttribute(id = 7, type = Type.LONG_UNSIGNED)
  public final DataObject repetitionDelay;

  public PushSetupSms() {
    super("0.2.25.9.0.255");
    this.value = this.createPushObjectList();
    this.sendDestAndMethod = this.createSendDestAndMethod();
    this.communicationWindow = this.createCommunicationWindow();
    this.randomisationStartInterval = this.createRandomisationStartInterval();
    this.numberOfRetries = this.createNumberOfRetries();
    this.repetitionDelay = this.createRepetitionDelay();
  }

  // just fill with empty data.
  public DataObject createPushObjectList() {
    return DataObject.newStructureData();
  }

  public DataObject createSendDestAndMethod() {
    return DataObject.newStructureData();
  }

  public DataObject createRandomisationStartInterval() {
    return DataObject.newStructureData();
  }

  public DataObject createCommunicationWindow() {
    return DataObject.newStructureData();
  }

  public DataObject createNumberOfRetries() {
    return DataObject.newStructureData();
  }

  public DataObject createRepetitionDelay() {
    return DataObject.newStructureData();
  }
}
