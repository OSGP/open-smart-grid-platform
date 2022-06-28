/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import java.util.Arrays;
import java.util.Calendar;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.openmuc.jdlms.AttributeAccessMode;
import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CaptureObjectDefinition;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CaptureObjectDefinitionCollection;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CosemDateTimeProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.UInteger32DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.UInteger8DataProcessor;

@CosemClass(id = 7)
public class LoadProfileWithPeriod2 extends ProfileGeneric {

  /** Daily. */
  private static final int CAPTURE_PERIOD = 86400;

  /** 40 days. */
  private static final int PROFILE_ENTRIES = 40;

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

  private final Calendar time;

  public LoadProfileWithPeriod2(final Calendar time) {
    super("1.0.99.2.0.255");
    this.time = time;

    this.buffer = DataObject.newNullData();
    this.captureObjects = DataObject.newNullData();
    this.capturePeriod = DataObject.newUInteger32Data(CAPTURE_PERIOD);
    this.sortMethod = DataObject.newEnumerateData(SortMethod.FIFO.value());
    this.sortObject = DataObject.newNullData();
    this.entriesInUse = DataObject.newNullData();
    this.profileEntries = DataObject.newUInteger32Data(PROFILE_ENTRIES);

    this.initBufferData();
  }

  private static CaptureObjectDefinitionCollection initCaptureObjects() {
    final CaptureObjectDefinitionCollection definitions = new CaptureObjectDefinitionCollection();

    // Clock
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(8, "0.0.1.0.0.255", (byte) 2, 0), new CosemDateTimeProcessor()));
    // AmrProfileStatusCode
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(1, "0.0.96.10.2.255", (byte) 2, 0), new UInteger8DataProcessor()));
    // Import Rate 1
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.0.1.8.1.255", (byte) 2, 0), new UInteger32DataProcessor()));
    // Import Rate 1
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.0.1.8.2.255", (byte) 2, 0), new UInteger32DataProcessor()));
    // Import Rate 1
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.0.2.8.1.255", (byte) 2, 0), new UInteger32DataProcessor()));
    // Import Rate 1
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.0.2.8.2.255", (byte) 2, 0), new UInteger32DataProcessor()));

    // Measurement value M-Bus Channel 1
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(4, "0.1.24.2.1.255", (byte) 2, 0), new UInteger32DataProcessor()));
    // Measurement capture time M-Bus Channel 1
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(4, "0.1.24.2.1.255", (byte) 5, 0), new CosemDateTimeProcessor()));
    // Measurement value M-Bus Channel 2
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(4, "0.2.24.2.1.255", (byte) 2, 0), new UInteger32DataProcessor()));
    // Measurement capture time M-Bus Channel 2
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(4, "0.2.24.2.1.255", (byte) 5, 0), new CosemDateTimeProcessor()));
    // Measurement value M-Bus Channel 3
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(4, "0.3.24.2.1.255", (byte) 2, 0), new UInteger32DataProcessor()));
    // Measurement capture time M-Bus Channel 3
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(4, "0.3.24.2.1.255", (byte) 5, 0), new CosemDateTimeProcessor()));
    // Measurement value M-Bus Channel 4
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(4, "0.4.24.2.1.255", (byte) 2, 0), new UInteger32DataProcessor()));
    // Measurement capture time M-Bus Channel 4
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(4, "0.4.24.2.1.255", (byte) 5, 0), new CosemDateTimeProcessor()));

    return definitions;
  }

  /** Initializes buffer with some data. */
  private void initBufferData() {

    this.bufferData = new CircularFifoQueue<>(PROFILE_ENTRIES);

    final short amrProfileStatusCode = 4;
    long importRate1 = 0;
    long importRate2 = 0;
    long exportRate1 = 0;
    long exportRate2 = 0;
    long mBusValue1 = 0;
    long mBusValue2 = 0;
    long mBusValue3 = 0;
    long mBusValue4 = 0;

    for (int i = 0; i < PROFILE_ENTRIES; i++) {
      importRate1 += 1;
      importRate2 += 2;
      exportRate1 += 3;
      exportRate2 += 4;
      mBusValue1 += 5;
      mBusValue2 += 6;
      mBusValue3 += 7;
      mBusValue4 += 8;
      final Calendar cal = this.getNextDateTime();

      this.bufferData.add(
          Arrays.asList(
              cal,
              amrProfileStatusCode,
              importRate1,
              importRate2,
              exportRate1,
              exportRate2,
              mBusValue1,
              cal,
              mBusValue2,
              cal,
              mBusValue3,
              cal,
              mBusValue4,
              cal));
    }
  }

  private Calendar getNextDateTime() {
    final Calendar next = (Calendar) this.time.clone();
    this.time.add(Calendar.DAY_OF_MONTH, 1);
    return next;
  }

  @Override
  protected CaptureObjectDefinitionCollection getCaptureObjectDefinitionCollection() {
    return CAPTURE_OBJECT_DEFINITIONS;
  }
}
