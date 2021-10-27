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
import org.openmuc.jdlms.CosemMethod;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;

@CosemClass(id = 20)
public class ActivityCalendar extends CosemInterfaceObject {

  @CosemAttribute(id = 2, type = Type.OCTET_STRING)
  private DataObject calendarNameActive;

  @CosemAttribute(id = 3, type = Type.ARRAY)
  private DataObject seasonProfileActive;

  @CosemAttribute(id = 4, type = Type.ARRAY)
  private DataObject weekProfileTableActive;

  @CosemAttribute(id = 5, type = Type.ARRAY)
  private DataObject dayProfileTableActive;

  @CosemAttribute(id = 6, type = Type.OCTET_STRING)
  private DataObject calendarNamePassive;

  @CosemAttribute(id = 7, type = Type.ARRAY)
  private DataObject seasonProfilePassive;

  @CosemAttribute(id = 8, type = Type.ARRAY)
  private DataObject weekProfileTablePassive;

  @CosemAttribute(id = 9, type = Type.ARRAY)
  private DataObject dayProfileTablePassive;

  @CosemAttribute(id = 10, type = Type.OCTET_STRING)
  private DataObject activatePassiveCalendarTime;

  public ActivityCalendar() {
    super("0.0.13.0.0.255");
    this.calendarNameActive = DataObject.newNullData();
    this.seasonProfileActive = DataObject.newNullData();
    this.weekProfileTableActive = DataObject.newNullData();
    this.dayProfileTableActive = DataObject.newNullData();
    this.calendarNamePassive = DataObject.newNullData();
    this.seasonProfilePassive = DataObject.newNullData();
    this.weekProfileTablePassive = DataObject.newNullData();
    this.dayProfileTablePassive = DataObject.newNullData();
    this.activatePassiveCalendarTime = DataObject.newNullData();
  }

  @CosemMethod(id = 1, consumes = Type.DOUBLE_LONG)
  public void activatePassiveCalendar(final DataObject param) {
    // No simulation of action at this point.
  }
}
