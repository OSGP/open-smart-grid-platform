/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.Clock.LOGICAL_NAME_CLOCK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.openmuc.jdlms.AttributeAccessMode;
import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CaptureObjectDefinition;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CaptureObjectDefinitionCollection;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CosemDateTimeProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.UInteger16DataProcessor;

@CosemClass(id = 7)
public class PowerQualityProfile2 extends ProfileGeneric {
  /** Every 10 minutes. */
  private static final int CAPTURE_PERIOD = 600;

  /** 10 days = 240 hours = 1440 periods of 10 minutes. */
  private static final int PROFILE_ENTRIES = 1440;

  private static final String LOGICAL_NAME_POWER_QUALITY_PROFILE_2 = "1.0.99.1.2.255";

  public static final String AVERAGE_VOLTAGE_L1_LOGICAL_NAME = "1.0.32.24.0.255";
  public static final String AVERAGE_VOLTAGE_L2_LOGICAL_NAME = "1.0.52.24.0.255";
  public static final String AVERAGE_VOLTAGE_L3_LOGICAL_NAME = "1.0.72.24.0.255";

  public static final String INSTANTANEOUS_VOLTAGE_L1_LOGICAL_NAME = "1.0.32.7.0.255";
  public static final String INSTANTANEOUS_VOLTAGE_L2_LOGICAL_NAME = "1.0.52.7.0.255";
  public static final String INSTANTANEOUS_VOLTAGE_L3_LOGICAL_NAME = "1.0.72.7.0.255";

  public static final String AVERAGE_CURRENT_L1_LOGICAL_NAME = "1.0.31.24.0.255";
  public static final String AVERAGE_CURRENT_L2_LOGICAL_NAME = "1.0.51.24.0.255";
  public static final String AVERAGE_CURRENT_L3_LOGICAL_NAME = "1.0.71.24.0.255";

  public static final String INSTANTANEOUS_CURRENT_L1_LOGICAL_NAME = "1.0.31.7.0.255";
  public static final String INSTANTANEOUS_CURRENT_L2_LOGICAL_NAME = "1.0.51.7.0.255";
  public static final String INSTANTANEOUS_CURRENT_L3_LOGICAL_NAME = "1.0.71.7.0.255";

  public static final String INSTANTANEOUS_ACTIVE_POWER_IMPORT_LOGICAL_NAME = "1.0.1.7.0.255";
  public static final String INSTANTANEOUS_ACTIVE_POWER_EXPORT_LOGICAL_NAME = "1.0.2.7.0.255";

  public static final String INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1_LOGICAL_NAME = "1.0.22.7.0.255";
  public static final String INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2_LOGICAL_NAME = "1.0.42.7.0.255";
  public static final String INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3_LOGICAL_NAME = "1.0.62.7.0.255";

  public static final String INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1_LOGICAL_NAME = "1.0.21.7.0.255";
  public static final String INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2_LOGICAL_NAME = "1.0.41.7.0.255";
  public static final String INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3_LOGICAL_NAME = "1.0.61.7.0.255";

  private CaptureObjectDefinitionCollection captureObjectDefinitionCollection;
  private static final Map<CaptureObject, DataProcessor> PROCESSORS_BY_CAPTURE_OBJECT =
      new HashMap<>();
  private static final DataProcessor COSEM_DATE_TIME_PROCESSOR = new CosemDateTimeProcessor();
  private static final DataProcessor LONG_UNSIGNED_PROCESSOR = new UInteger16DataProcessor();

  private static final CaptureObject CLOCK_TIME =
      new CaptureObject(
          InterfaceClass.CLOCK.id(),
          LOGICAL_NAME_CLOCK,
          (byte) ClockAttribute.TIME.attributeId(),
          0);

  private static final CaptureObject AVERAGE_VOLTAGE_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_VOLTAGE_L1_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_VOLTAGE_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_VOLTAGE_L2_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_VOLTAGE_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_VOLTAGE_L3_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_VOLTAGE_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          INSTANTANEOUS_VOLTAGE_L1_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_VOLTAGE_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          INSTANTANEOUS_VOLTAGE_L2_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_VOLTAGE_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          INSTANTANEOUS_VOLTAGE_L3_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);

  private static final CaptureObject AVERAGE_CURRENT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_CURRENT_L1_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_CURRENT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_CURRENT_L2_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_CURRENT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_CURRENT_L3_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_CURRENT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          INSTANTANEOUS_CURRENT_L1_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);

  static {
    PROCESSORS_BY_CAPTURE_OBJECT.put(CLOCK_TIME, COSEM_DATE_TIME_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_VOLTAGE_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_VOLTAGE_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_VOLTAGE_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(INSTANTANEOUS_VOLTAGE_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(INSTANTANEOUS_VOLTAGE_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(INSTANTANEOUS_VOLTAGE_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_CURRENT_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_CURRENT_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_CURRENT_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(INSTANTANEOUS_CURRENT_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
  }

  /**
   * Only for cosem attribute definition, data remains untouched. Attribute data is gathered from
   * {@link #bufferData}.
   */
  @CosemAttribute(
      id = 2,
      type = DataObject.Type.ARRAY,
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
      type = DataObject.Type.ARRAY,
      accessMode = AttributeAccessMode.READ_AND_WRITE,
      snOffset = 0x10)
  private final DataObject captureObjects;

  @CosemAttribute(
      id = 4,
      type = DataObject.Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x18)
  public DataObject capturePeriod;

  @CosemAttribute(
      id = 5,
      type = DataObject.Type.ENUMERATE,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x20)
  public DataObject sortMethod;

  @CosemAttribute(
      id = 6,
      type = DataObject.Type.STRUCTURE,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x28)
  public DataObject sortObject;

  /**
   * Only for cosem attribute definition, data remains untouched. Attribute data is gathered from
   * size of {@link #bufferData}
   */
  @CosemAttribute(
      id = 7,
      type = DataObject.Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x30)
  private final DataObject entriesInUse;

  @CosemAttribute(
      id = 8,
      type = DataObject.Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x38)
  public DataObject profileEntries;

  private final Calendar time;

  private static final List<CaptureObject> DEFAULT_CAPTURE_OBJECTS =
      Arrays.asList(
          CLOCK_TIME,
          AVERAGE_VOLTAGE_L1_VALUE,
          AVERAGE_VOLTAGE_L2_VALUE,
          AVERAGE_VOLTAGE_L3_VALUE,
          INSTANTANEOUS_VOLTAGE_L1_VALUE,
          INSTANTANEOUS_VOLTAGE_L2_VALUE,
          INSTANTANEOUS_VOLTAGE_L3_VALUE,
          AVERAGE_CURRENT_L1_VALUE,
          AVERAGE_CURRENT_L2_VALUE,
          AVERAGE_CURRENT_L3_VALUE,
          INSTANTANEOUS_CURRENT_L1_VALUE);

  public PowerQualityProfile2(final Calendar time) {
    super(LOGICAL_NAME_POWER_QUALITY_PROFILE_2);
    this.time = time;

    this.buffer = DataObject.newNullData();
    this.captureObjects = this.defaultCaptureObjects();
    this.captureObjectDefinitionCollection = initCaptureObjects(DEFAULT_CAPTURE_OBJECTS);
    this.capturePeriod = DataObject.newUInteger32Data(CAPTURE_PERIOD);
    this.sortMethod = DataObject.newEnumerateData(SortMethod.FIFO.value());
    this.sortObject = DataObject.newNullData();
    this.entriesInUse = DataObject.newNullData();
    this.profileEntries = DataObject.newUInteger32Data(PROFILE_ENTRIES);

    this.initBufferData();
  }

  private static CaptureObjectDefinitionCollection initCaptureObjects(
      final List<CaptureObject> captureObjects) {
    final CaptureObjectDefinitionCollection definitions = new CaptureObjectDefinitionCollection();

    for (final CaptureObject captureObject : captureObjects) {
      definitions.add(
          new CaptureObjectDefinition(
              captureObject, PROCESSORS_BY_CAPTURE_OBJECT.get(captureObject)));
    }
    return definitions;
  }

  private DataObject defaultCaptureObjects() {
    final List<DataObject> captureObjectList = new ArrayList<>();
    for (final CaptureObject captureObject : DEFAULT_CAPTURE_OBJECTS) {
      captureObjectList.add(captureObject.asDataObject());
    }
    return DataObject.newArrayData(captureObjectList);
  }

  /** Initializes buffer with some data. */
  private void initBufferData() {
    this.bufferData = new CircularFifoQueue<>(PROFILE_ENTRIES);
    final long averageCurrentL1Value = 1;
    final long averageCurrentL2Value = 1;
    final long averageCurrentL3Value = 1;
    final long averageVoltageL1Value = 1;
    final long averageVoltageL2Value = 1;
    final long averageVoltageL3Value = 1;
    final long instantaneousVoltageL1Value = 230;
    final long instantaneousVoltageL2Value = 230;
    final long instantaneousVoltageL3Value = 230;
    final long instantaneousCurrentL1Value = 1;

    for (int i = 0; i < PROFILE_ENTRIES; i++) {
      final Calendar cal = this.getNextDateTime();
      this.bufferData.add(
          Arrays.asList(
              cal,
              averageCurrentL1Value,
              averageCurrentL2Value,
              averageCurrentL3Value,
              averageVoltageL1Value,
              averageVoltageL2Value,
              averageVoltageL3Value,
              instantaneousVoltageL1Value,
              instantaneousVoltageL2Value,
              instantaneousVoltageL3Value,
              instantaneousCurrentL1Value));
    }
  }

  private Calendar getNextDateTime() {
    final Calendar next = (Calendar) this.time.clone();
    this.time.add(Calendar.MINUTE, 10);
    return next;
  }

  @Override
  protected CaptureObjectDefinitionCollection getCaptureObjectDefinitionCollection() {
    return this.captureObjectDefinitionCollection;
  }
}
