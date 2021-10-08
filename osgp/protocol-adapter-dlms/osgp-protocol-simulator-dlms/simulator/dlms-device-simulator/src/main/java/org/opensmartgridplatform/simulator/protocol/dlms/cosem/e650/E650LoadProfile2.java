/*
 * Copyright 2017 Smart Society Services B.V.
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
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CaptureObjectDefinition;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CaptureObjectDefinitionCollection;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CosemDateTimeProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.Integer32DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.UInteger32DataProcessor;

@CosemClass(id = 7, version = 1)
public class E650LoadProfile2 extends ProfileWithTime {

  /** 10 days = 240 hours = 2880 periods of 5 minutes. */
  private static final int PROFILE_ENTRIES = 2880;

  private static final CaptureObjectDefinitionCollection CAPTURE_OBJECT_DEFINITIONS =
      initCaptureObjects();

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
   * captureObjectDefinitions
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

  public E650LoadProfile2(final Calendar time) {
    super(time, 0x0AA8, "1.0.99.2.0.255");

    this.buffer = DataObject.newNullData();
    this.captureObjects = DataObject.newNullData();
    this.capturePeriod = DataObject.newInteger32Data(CAPTURE_PERIOD);
    this.sortMethod = DataObject.newEnumerateData(SortMethod.FIFO.value());
    this.sortObject = DataObject.newNullData();
    this.entriesInUse = DataObject.newNullData();
    this.profileEntries = DataObject.newInteger32Data(PROFILE_ENTRIES);

    this.initBufferData();
  }

  private static CaptureObjectDefinitionCollection initCaptureObjects() {
    final CaptureObjectDefinitionCollection definitions = new CaptureObjectDefinitionCollection();

    // Clock
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(8, "0.0.1.0.0.255", (byte) 2, 0), new CosemDateTimeProcessor()));

    // Event register, EDIS_Status_LoPr2
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "0.0.96.240.12.255", (byte) 20, 0),
            new UInteger32DataProcessor()));

    // All harmonics instantaneous voltage phase 1
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.0.32.7.126.255", (byte) 2, 0), new Integer32DataProcessor()));
    // All harmonics instantaneous voltage phase 2
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.0.52.7.126.255", (byte) 2, 0), new Integer32DataProcessor()));
    // All harmonics instantaneous voltage phase 3
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.0.72.7.126.255", (byte) 2, 0), new Integer32DataProcessor()));
    // All harmonics instantaneous current phase 1
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.0.31.7.126.255", (byte) 2, 0), new Integer32DataProcessor()));
    // All harmonics instantaneous current phase 2
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.0.51.7.126.255", (byte) 2, 0), new Integer32DataProcessor()));
    // All harmonics instantaneous current phase 3
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.0.71.7.126.255", (byte) 2, 0), new Integer32DataProcessor()));

    // Total instantaneous voltage phase 1
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.1.32.7.0.255", (byte) 11, 0), new Integer32DataProcessor()));
    // Total instantaneous voltage phase 2
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.1.52.7.0.255", (byte) 11, 0), new Integer32DataProcessor()));
    // Total instantaneous voltage phase 3
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.1.72.7.0.255", (byte) 11, 0), new Integer32DataProcessor()));
    // Total instantaneous current phase 1
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.1.31.7.0.255", (byte) 11, 0), new Integer32DataProcessor()));
    // Total instantaneous current phase 2
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.1.51.7.0.255", (byte) 11, 0), new Integer32DataProcessor()));
    // Total instantaneous current phase 3
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.1.71.7.0.255", (byte) 11, 0), new Integer32DataProcessor()));

    // Total instantaneous current neutral
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.1.91.7.0.255", (byte) 11, 0), new Integer32DataProcessor()));
    // Total instantaneous active power phase 1
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.1.36.7.0.255", (byte) 11, 0), new Integer32DataProcessor()));
    // Total instantaneous active power phase 2
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.1.56.7.0.255", (byte) 11, 0), new Integer32DataProcessor()));
    // Total instantaneous active power phase 3
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.1.76.7.0.255", (byte) 11, 0), new Integer32DataProcessor()));

    return definitions;
  }

  /** Initializes buffer with some data. */
  private void initBufferData() {

    this.bufferData = new CircularFifoQueue<>(PROFILE_ENTRIES);

    long edis = 0;

    int ahVoltageL1 = 0;
    int ahVoltageL2 = 0;
    int ahVoltageL3 = 0;
    int ahCurrentL1 = 0;
    int ahCurrentL2 = 0;
    int ahCurrentL3 = 0;

    int totalVoltageL1 = 0;
    int totalVoltageL2 = 0;
    int totalVoltageL3 = 0;
    int totalCurrentL1 = 0;
    int totalCurrentL2 = 0;
    int totalCurrentL3 = 0;

    int totalCurrentNeutral = 0;
    int totalActivePowerL1 = 0;
    int totalActivePowerL2 = 0;
    int totalActivePowerL3 = 0;

    for (int i = 0; i < PROFILE_ENTRIES; i++) {
      final Calendar cal = this.getDateTime();
      this.forwardTime();

      edis += 1;
      ahVoltageL1 += 1;
      ahVoltageL2 += 2;
      ahVoltageL3 += 3;
      ahCurrentL1 += 1;
      ahCurrentL2 += 2;
      ahCurrentL3 += 3;

      totalVoltageL1 += 1;
      totalVoltageL2 += 2;
      totalVoltageL3 += 3;
      totalCurrentL1 += 1;
      totalCurrentL2 += 2;
      totalCurrentL3 += 3;

      totalCurrentNeutral += 1;
      totalActivePowerL1 += 1;
      totalActivePowerL2 += 2;
      totalActivePowerL3 += 3;

      // Use unrealistic but recognizable test values.
      this.bufferData.add(
          Arrays.asList(
              cal,
              edis,
              ahVoltageL1,
              ahVoltageL2,
              ahVoltageL3,
              ahCurrentL1,
              ahCurrentL2,
              ahCurrentL3,
              totalVoltageL1,
              totalVoltageL2,
              totalVoltageL3,
              totalCurrentL1,
              totalCurrentL2,
              totalCurrentL3,
              totalCurrentNeutral,
              totalActivePowerL1,
              totalActivePowerL2,
              totalActivePowerL3));
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
    return CAPTURE_OBJECT_DEFINITIONS;
  }
}
