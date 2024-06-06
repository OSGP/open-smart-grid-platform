// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;

@CosemClass(id = 40)
public class PushSetupObject extends CosemInterfaceObject {

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

  public PushSetupObject(final String obisCode) {
    super(obisCode);
    this.value = this.createPushObjectList();
    this.sendDestAndMethod = this.createSendDestAndMethod();
    this.communicationWindow = this.createCommunicationWindow();
    this.randomisationStartInterval = this.createRandomisationStartInterval();
    this.numberOfRetries = this.createNumberOfRetries();
    this.repetitionDelay = this.createRepetitionDelay();
  }

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
