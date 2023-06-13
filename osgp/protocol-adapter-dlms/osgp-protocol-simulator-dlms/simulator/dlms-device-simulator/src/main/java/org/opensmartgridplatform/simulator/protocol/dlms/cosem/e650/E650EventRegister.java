// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem.e650;

import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemSnInterfaceObject;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.builder.ScalerUnitBuilder;

@CosemClass(id = 3)
public class E650EventRegister extends CosemSnInterfaceObject {

  @CosemAttribute(id = 2, type = Type.LONG_UNSIGNED, snOffset = 0x08)
  private final DataObject currentValue = DataObject.newUInteger8Data((short) 0);

  @CosemAttribute(id = 3, type = Type.STRUCTURE, snOffset = 0x10)
  private final DataObject unitScale = ScalerUnitBuilder.createScalerUnit(0, 100);

  @CosemAttribute(id = 4, type = Type.NULL_DATA, snOffset = 0x18)
  private final DataObject resAttribute1 = DataObject.newNullData();

  @CosemAttribute(id = 5, type = Type.NULL_DATA, snOffset = 0x20)
  private final DataObject resAttribute2 = DataObject.newNullData();

  @CosemAttribute(id = 6, type = Type.NULL_DATA, snOffset = 0x28)
  private final DataObject reset = DataObject.newNullData();

  @CosemAttribute(id = 7, type = Type.NULL_DATA, snOffset = 0x30)
  private final DataObject resService1 = DataObject.newNullData();

  @CosemAttribute(id = 8, type = Type.LONG_UNSIGNED, snOffset = 0x38)
  private final DataObject subtype = DataObject.newUInteger16Data(20018);

  @CosemAttribute(id = 9, type = Type.UNSIGNED, snOffset = 0x40)
  private final DataObject ownClassVersion = DataObject.newUInteger8Data((short) 9);

  @CosemAttribute(id = 10, type = Type.OCTET_STRING, snOffset = 0x48)
  private final DataObject idString = DataObject.newOctetStringData("idString".getBytes());

  @CosemAttribute(id = 11, type = Type.OCTET_STRING, snOffset = 0x50)
  private final DataObject attrVaaAccList =
      DataObject.newOctetStringData("attrVaaAccList".getBytes());

  @CosemAttribute(id = 12, type = Type.OCTET_STRING, snOffset = 0x58)
  private final DataObject eventStatus = DataObject.newOctetStringData("eventStatus".getBytes());

  @CosemAttribute(id = 13, type = Type.OCTET_STRING, snOffset = 0x60)
  private final DataObject eventFilterSet =
      DataObject.newOctetStringData("eventFilterSet".getBytes());

  @CosemAttribute(id = 14, type = Type.OCTET_STRING, snOffset = 0x68)
  private final DataObject eventFilterClear =
      DataObject.newOctetStringData("eventFilterClear".getBytes());

  @CosemAttribute(id = 15, type = Type.OCTET_STRING, snOffset = 0x70)
  private final DataObject timer = DataObject.newOctetStringData("timer".getBytes());

  @CosemAttribute(id = 16, type = Type.OCTET_STRING, snOffset = 0x78)
  private final DataObject timerControl = DataObject.newOctetStringData("timerControl".getBytes());

  @CosemAttribute(id = 17, type = Type.OCTET_STRING, snOffset = 0x80)
  private final DataObject timerOut = DataObject.newOctetStringData("timerOut".getBytes());

  @CosemAttribute(id = 18, type = Type.DOUBLE_LONG_UNSIGNED, snOffset = 0x88)
  private final DataObject edisStatusLoPr = DataObject.newInteger32Data(4);

  @CosemAttribute(id = 19, type = Type.DOUBLE_LONG_UNSIGNED, snOffset = 0x90)
  private final DataObject edisStatusEvLo = DataObject.newInteger32Data(4);

  @CosemAttribute(id = 20, type = Type.DOUBLE_LONG_UNSIGNED, snOffset = 0x98)
  private final DataObject edisStatusLoPr2 = DataObject.newInteger32Data(4);

  @CosemAttribute(id = 21, type = Type.BOOLEAN, snOffset = 0xA0)
  private final DataObject enableWithoutUinEdisStatus = DataObject.newBoolData(true);

  @CosemAttribute(id = 22, type = Type.OCTET_STRING, snOffset = 0xA8)
  private final DataObject alertMask = DataObject.newOctetStringData("1234".getBytes());

  @CosemAttribute(id = 23, type = Type.OCTET_STRING, snOffset = 0xB0)
  private final DataObject alertMask2 = DataObject.newOctetStringData("1234".getBytes());

  @CosemAttribute(id = 24, type = Type.OCTET_STRING, snOffset = 0xB8)
  private final DataObject operationalStatusMask = DataObject.newOctetStringData("1234".getBytes());

  @CosemAttribute(id = 25, type = Type.DATE_TIME, snOffset = 0xC0)
  private final DataObject eventTimestamp = DataObject.newNullData();

  @CosemAttribute(id = 26, type = Type.OCTET_STRING, snOffset = 0xC8)
  private final DataObject alertTimerControl = DataObject.newOctetStringData("123456".getBytes());

  @CosemAttribute(id = 27, type = Type.OCTET_STRING, snOffset = 0xD0)
  private final DataObject alertTimeout = DataObject.newOctetStringData("12345678".getBytes());

  public E650EventRegister() {
    super(0x48A0, "0.0.96.240.12.255");
  }
}
