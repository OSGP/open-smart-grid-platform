/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem.e650;

import java.util.Arrays;
import java.util.Calendar;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.openmuc.jdlms.AttributeAccessMode;
import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.CaptureObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ProfileWithTime;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.SortMethod;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.BoolDataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CaptureObjectDefinition;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CaptureObjectDefinitionCollection;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CosemDateTimeProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.Integer32DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.UInteger32DataProcessor;

/**
 * Dedicated event log like <em>Under voltage L1 Log</em>, <em>Over voltage L1 Log</em>, <em>Phase
 * failure L1 Log</em>, etcetera,
 */
@CosemClass(id = 7, version = 1)
public class E650DedicatedEventLog extends ProfileWithTime {

  private static final int PROFILE_ENTRIES = 64;

  private static final int DEFAULT_SUBTYPE = 10910;

  private static final short DEFAULT_OWN_CLASS_VERSION = (short) 7;

  private final CaptureObjectDefinitionCollection captureObjectDefinitions;

  /**
   * Only for cosem attribute definition, data remains untouched. Attribute data is gathered from
   * {@link #bufferData}.
   */
  @CosemAttribute(
      id = 2,
      type = Type.ARRAY,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x08,
      selector = {1, 2})
  private final DataObject buffer;

  /**
   * Only for cosem attribute definition, data remains untouched. Attribute data is gathered from
   * {@link #captureObjectDefinitions}
   */
  @CosemAttribute(
      id = 3,
      type = Type.ARRAY,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x10)
  private final DataObject captureObjects;

  @CosemAttribute(
      id = 4,
      type = Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x18)
  public DataObject capturePeriod;

  @CosemAttribute(
      id = 5,
      type = Type.ENUMERATE,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x20)
  public DataObject sortMethod;

  @CosemAttribute(
      id = 6,
      type = Type.STRUCTURE,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x28)
  public DataObject sortObject;

  /**
   * Only for cosem attribute definition, data remains untouched. Attribute data is gathered from
   * size of {@link #bufferData}
   */
  @CosemAttribute(
      id = 7,
      type = Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x30)
  private final DataObject entriesInUse;

  @CosemAttribute(
      id = 8,
      type = Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x38)
  public DataObject profileEntries;

  @CosemAttribute(id = 9, type = Type.NULL_DATA, snOffset = 0x40)
  public DataObject resAttribute1;

  @CosemAttribute(id = 10, type = Type.NULL_DATA, snOffset = 0x48)
  public DataObject resAttribute2;

  @CosemAttribute(id = 11, type = Type.NULL_DATA, snOffset = 0x50)
  public DataObject resAttribute3;

  @CosemAttribute(id = 12, type = Type.NULL_DATA, snOffset = 0x58)
  public DataObject reset;

  @CosemAttribute(id = 13, type = Type.NULL_DATA, snOffset = 0x60)
  public DataObject capture;

  @CosemAttribute(id = 14, type = Type.NULL_DATA, snOffset = 0x68)
  public DataObject resService3;

  @CosemAttribute(id = 15, type = Type.NULL_DATA, snOffset = 0x70)
  public DataObject resService4;

  @CosemAttribute(id = 16, type = Type.LONG_UNSIGNED, snOffset = 0x78)
  public DataObject subtype = DataObject.newUInteger16Data(DEFAULT_SUBTYPE);

  @CosemAttribute(id = 17, type = Type.UNSIGNED, snOffset = 0x80)
  public DataObject ownClassVersion = DataObject.newUInteger8Data(DEFAULT_OWN_CLASS_VERSION);

  @CosemAttribute(id = 18, type = Type.OCTET_STRING, snOffset = 0x88)
  private final DataObject attrVaaAccList =
      DataObject.newOctetStringData("attrVaaAccList".getBytes());

  @CosemAttribute(id = 19, type = Type.OCTET_STRING, snOffset = 0x90)
  private final DataObject idString = DataObject.newOctetStringData("idString".getBytes());

  @CosemAttribute(id = 20, type = Type.BOOLEAN, snOffset = 0x98)
  private final DataObject profileMemoryType = DataObject.newBoolData(true);

  @CosemAttribute(id = 21, type = Type.LONG_UNSIGNED, snOffset = 0xA0)
  public DataObject lcdManualEntries = DataObject.newUInteger16Data(1);

  @CosemAttribute(id = 22, type = Type.LONG_UNSIGNED, snOffset = 0xA8)
  public DataObject lcdServiceEntries = DataObject.newUInteger16Data(1);

  @CosemAttribute(id = 23, type = Type.LONG_UNSIGNED, snOffset = 0xB0)
  public DataObject readOutEntries = DataObject.newUInteger16Data(1);

  @CosemAttribute(id = 24, type = Type.ENUMERATE, snOffset = 0xB8)
  public DataObject identifierType;

  @CosemAttribute(id = 25, type = Type.UNSIGNED, snOffset = 0xC0)
  public DataObject outputFlags = DataObject.newUInteger8Data((short) 0);

  @CosemAttribute(id = 26, type = Type.DOUBLE_LONG_UNSIGNED, snOffset = 0xC8)
  public DataObject bufferStartAddress = DataObject.newUInteger32Data(0L);

  @CosemAttribute(id = 27, type = Type.LONG_UNSIGNED, snOffset = 0xD0)
  public DataObject lcdAutoScrollEntries = DataObject.newUInteger16Data(0);

  @CosemAttribute(id = 28, type = Type.UNSIGNED, snOffset = 0xD8)
  public DataObject eventNo = DataObject.newUInteger8Data((short) 0);

  @CosemAttribute(id = 29, type = Type.BOOLEAN, snOffset = 0xE0)
  private final DataObject status = DataObject.newBoolData(true);

  @CosemAttribute(id = 30, type = Type.DOUBLE_LONG_UNSIGNED, snOffset = 0xE8)
  private final DataObject currentNoOfSeconds = DataObject.newUInteger32Data(0L);

  @CosemAttribute(id = 31, type = Type.DOUBLE_LONG_UNSIGNED, snOffset = 0xF0)
  private final DataObject totalNoOfEvents = DataObject.newUInteger32Data(0L);

  @CosemAttribute(id = 32, type = Type.DOUBLE_LONG_UNSIGNED, snOffset = 0xF8)
  private final DataObject totalNoOfSeconds = DataObject.newUInteger32Data(0L);

  private final String dedicatedEventLogObisCode;
  private final String captureObjectObisCode;

  public E650DedicatedEventLog(
      final Calendar time,
      final int objectName,
      final String dedicatedEventLogObisCode,
      final String captureObjectObisCode) {
    super(time, objectName, dedicatedEventLogObisCode);
    this.dedicatedEventLogObisCode = dedicatedEventLogObisCode;
    this.captureObjectObisCode = captureObjectObisCode;
    this.captureObjectDefinitions = this.initCaptureObjects();
    this.buffer = DataObject.newNullData();
    this.captureObjects = DataObject.newNullData();
    this.capturePeriod = DataObject.newInteger32Data(CAPTURE_PERIOD);
    this.sortMethod = DataObject.newEnumerateData(SortMethod.FIFO.value());
    this.sortObject = DataObject.newNullData();
    this.entriesInUse = DataObject.newUInteger32Data(0);
    this.profileEntries = DataObject.newInteger32Data(PROFILE_ENTRIES);

    this.initBufferData();
  }

  private CaptureObjectDefinitionCollection initCaptureObjects() {
    final CaptureObjectDefinitionCollection definitions = new CaptureObjectDefinitionCollection();

    // Clock
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(8, "0.0.1.0.0.255", (byte) 2, 0), new CosemDateTimeProcessor()));

    // UnderVoltageL1, Status
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(7, this.dedicatedEventLogObisCode, (byte) 29, 0),
            new BoolDataProcessor()));

    // UnderVoltageL1, CurrentNoOfSeconds
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(7, this.dedicatedEventLogObisCode, (byte) 30, 0),
            new UInteger32DataProcessor()));

    if (this.captureObjectObisCode != null) {
      // Voltage
      definitions.add(
          new CaptureObjectDefinition(
              new CaptureObject(4, this.captureObjectObisCode, (byte) 2, 0),
              new Integer32DataProcessor()));
    }
    return definitions;
  }

  private void initBufferData() {

    this.bufferData = new CircularFifoQueue<>(PROFILE_ENTRIES);

    boolean statusValue = false;
    long currentNoOfSecondsValue = 0;
    int value = 205;

    for (int i = 0; i < PROFILE_ENTRIES; i++) {
      final Calendar cal = this.getDateTime();
      this.forwardTime();
      statusValue = !statusValue;
      currentNoOfSecondsValue += CAPTURE_PERIOD;
      value += 10;
      if (this.captureObjectObisCode == null) {
        this.bufferData.add(Arrays.asList(cal, statusValue, currentNoOfSecondsValue));
      } else {
        this.bufferData.add(Arrays.asList(cal, statusValue, currentNoOfSecondsValue, value));
      }
    }
  }

  @Override
  public DataObject getBuffer(final SelectiveAccessDescription selectiveAccessDescription) {
    if (this.bufferData == null) {
      this.initBufferData();
    }
    return super.getBuffer(selectiveAccessDescription);
  }

  @Override
  public DataObject getCaptureObjects() {
    return this.getCaptureObjectDefinitionCollection().captureObjectsAsDataObject();
  }

  @Override
  protected CaptureObjectDefinitionCollection getCaptureObjectDefinitionCollection() {
    return this.captureObjectDefinitions;
  }
}
