// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
  private final DataObject calendarNameActive;

  @CosemAttribute(id = 3, type = Type.ARRAY)
  private final DataObject seasonProfileActive;

  @CosemAttribute(id = 4, type = Type.ARRAY)
  private final DataObject weekProfileTableActive;

  @CosemAttribute(id = 5, type = Type.ARRAY)
  private final DataObject dayProfileTableActive;

  @CosemAttribute(id = 6, type = Type.OCTET_STRING)
  private final DataObject calendarNamePassive;

  @CosemAttribute(id = 7, type = Type.ARRAY)
  private final DataObject seasonProfilePassive;

  @CosemAttribute(id = 8, type = Type.ARRAY)
  private final DataObject weekProfileTablePassive;

  @CosemAttribute(id = 9, type = Type.ARRAY)
  private final DataObject dayProfileTablePassive;

  @CosemAttribute(id = 10, type = Type.OCTET_STRING)
  private final DataObject activatePassiveCalendarTime;

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

  @CosemMethod(id = 1, consumes = Type.INTEGER)
  public void activatePassiveCalendar(final DataObject param) {
    // No simulation of action at this point.
  }
}
