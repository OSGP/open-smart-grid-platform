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
public class E650LoadProfile1 extends ProfileWithTime {

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

  public E650LoadProfile1(final Calendar time) {
    super(time, 0x6270, "1.0.99.1.0.255");

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

    // Event register, EDIS_Status_LoPr
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "0.0.96.240.12.255", (byte) 18, 0),
            new UInteger32DataProcessor()));

    // Current average active power +
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(5, "1.1.1.4.0.255", (byte) 3, 0), new Integer32DataProcessor()));
    // Current average active power -
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(5, "1.1.2.4.0.255", (byte) 3, 0), new Integer32DataProcessor()));
    // Current average reactive power +
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(5, "1.1.3.4.0.255", (byte) 3, 0), new Integer32DataProcessor()));
    // Current average reactive power -
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(5, "1.1.4.4.0.255", (byte) 3, 0), new Integer32DataProcessor()));

    // Instantaneous power factor phase 1
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.1.33.7.0.255", (byte) 11, 0), new Integer32DataProcessor()));
    // Instantaneous power factor phase 2
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.1.53.7.0.255", (byte) 11, 0), new Integer32DataProcessor()));
    // Instantaneous power factor phase 3
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.1.73.7.0.255", (byte) 11, 0), new Integer32DataProcessor()));

    return definitions;
  }

  /** Initializes buffer with some data. */
  private void initBufferData() {

    this.bufferData = new CircularFifoQueue<>(PROFILE_ENTRIES);

    long edis = 0;
    int currentAverageActivePowerPositive = 0;
    int currentAverageActivePowerNegative = 0;
    int currentAverageReactivePowerPositive = 0;
    int currentAverageReactivePowerNegative = 0;
    int phase1 = 0;
    int phase2 = 0;
    int phase3 = 0;

    for (int i = 0; i < PROFILE_ENTRIES; i++) {
      final Calendar cal = this.getDateTime();
      this.forwardTime();

      edis += 1;
      currentAverageActivePowerPositive += 1;
      currentAverageActivePowerNegative += 2;
      currentAverageReactivePowerPositive += 3;
      currentAverageReactivePowerNegative += 4;

      phase1 += 1;
      phase2 += 2;
      phase3 += 3;

      // Use unrealistic but recognizable test values.
      this.bufferData.add(
          Arrays.asList(
              cal,
              edis,
              currentAverageActivePowerPositive,
              currentAverageActivePowerNegative,
              currentAverageReactivePowerPositive,
              currentAverageReactivePowerNegative,
              phase1,
              phase2,
              phase3));
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
