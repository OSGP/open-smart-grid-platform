// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
public class PowerQualityProfile1 extends ProfileGeneric {
  /** Every 15 minutes. */
  private static final int CAPTURE_PERIOD = 900;

  /** 10 days = 240 hours = 960 periods of 15 minutes. */
  private static final int PROFILE_ENTRIES = 960;

  private static final String LOGICAL_NAME_POWER_QUALITY_PROFILE_1 = "1.0.99.1.1.255";
  public static final String AVERAGE_ACTIVE_POWER_IMPORT_L1_LOGICAL_NAME = "1.0.21.4.0.255";
  public static final String AVERAGE_ACTIVE_POWER_IMPORT_L2_LOGICAL_NAME = "1.0.41.4.0.255";
  public static final String AVERAGE_ACTIVE_POWER_IMPORT_L3_LOGICAL_NAME = "1.0.61.4.0.255";
  public static final String AVERAGE_ACTIVE_POWER_EXPORT_L1_LOGICAL_NAME = "1.0.22.4.0.255";
  public static final String AVERAGE_ACTIVE_POWER_EXPORT_L2_LOGICAL_NAME = "1.0.42.4.0.255";
  public static final String AVERAGE_ACTIVE_POWER_EXPORT_L3_LOGICAL_NAME = "1.0.62.4.0.255";
  public static final String AVERAGE_REACTIVE_POWER_IMPORT_L1_LOGICAL_NAME = "1.0.23.4.0.255";
  public static final String AVERAGE_REACTIVE_POWER_IMPORT_L2_LOGICAL_NAME = "1.0.43.4.0.255";
  public static final String AVERAGE_REACTIVE_POWER_IMPORT_L3_LOGICAL_NAME = "1.0.63.4.0.255";
  public static final String AVERAGE_REACTIVE_POWER_EXPORT_L1_LOGICAL_NAME = "1.0.24.4.0.255";
  public static final String AVERAGE_REACTIVE_POWER_EXPORT_L2_LOGICAL_NAME = "1.0.44.4.0.255";
  public static final String AVERAGE_REACTIVE_POWER_EXPORT_L3_LOGICAL_NAME = "1.0.64.4.0.255";

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

  private static final CaptureObject AVERAGE_ACTIVE_POWER_IMPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_ACTIVE_POWER_IMPORT_L1_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_ACTIVE_POWER_IMPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_ACTIVE_POWER_IMPORT_L2_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_ACTIVE_POWER_IMPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_ACTIVE_POWER_IMPORT_L3_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_ACTIVE_POWER_EXPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          PowerQualityProfile1.AVERAGE_ACTIVE_POWER_EXPORT_L1_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_ACTIVE_POWER_EXPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_ACTIVE_POWER_EXPORT_L2_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_ACTIVE_POWER_EXPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_ACTIVE_POWER_EXPORT_L3_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_REACTIVE_POWER_IMPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_REACTIVE_POWER_IMPORT_L1_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_REACTIVE_POWER_IMPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_REACTIVE_POWER_IMPORT_L2_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_REACTIVE_POWER_IMPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_REACTIVE_POWER_IMPORT_L3_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_REACTIVE_POWER_EXPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_REACTIVE_POWER_EXPORT_L1_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_REACTIVE_POWER_EXPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_REACTIVE_POWER_EXPORT_L2_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_REACTIVE_POWER_EXPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_REACTIVE_POWER_EXPORT_L3_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);

  static {
    PROCESSORS_BY_CAPTURE_OBJECT.put(CLOCK_TIME, COSEM_DATE_TIME_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_ACTIVE_POWER_IMPORT_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_ACTIVE_POWER_IMPORT_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_ACTIVE_POWER_IMPORT_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_ACTIVE_POWER_EXPORT_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_ACTIVE_POWER_EXPORT_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_ACTIVE_POWER_EXPORT_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        AVERAGE_REACTIVE_POWER_IMPORT_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        AVERAGE_REACTIVE_POWER_IMPORT_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        AVERAGE_REACTIVE_POWER_IMPORT_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        AVERAGE_REACTIVE_POWER_EXPORT_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        AVERAGE_REACTIVE_POWER_EXPORT_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        AVERAGE_REACTIVE_POWER_EXPORT_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
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
          AVERAGE_ACTIVE_POWER_IMPORT_L1_VALUE,
          AVERAGE_ACTIVE_POWER_IMPORT_L2_VALUE,
          AVERAGE_ACTIVE_POWER_IMPORT_L3_VALUE,
          AVERAGE_ACTIVE_POWER_EXPORT_L1_VALUE,
          AVERAGE_ACTIVE_POWER_EXPORT_L2_VALUE,
          AVERAGE_ACTIVE_POWER_EXPORT_L3_VALUE,
          AVERAGE_REACTIVE_POWER_IMPORT_L1_VALUE,
          AVERAGE_REACTIVE_POWER_IMPORT_L2_VALUE,
          AVERAGE_REACTIVE_POWER_IMPORT_L3_VALUE,
          AVERAGE_REACTIVE_POWER_EXPORT_L1_VALUE,
          AVERAGE_REACTIVE_POWER_EXPORT_L2_VALUE,
          AVERAGE_REACTIVE_POWER_EXPORT_L3_VALUE);

  public PowerQualityProfile1(final Calendar time) {
    super(LOGICAL_NAME_POWER_QUALITY_PROFILE_1);
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
    final int averageActivePowerImportL1Value = 10;
    final int averageActivePowerImportL2Value = 10;
    final int averageActivePowerImportL3Value = 10;
    final int averageActivePowerExportL1Value = 10;
    final int averageActivePowerExportL2Value = 10;
    final int averageActivePowerExportL3Value = 10;
    final int averageReactivePowerImportL1Value = 15;
    final int averageReactivePowerImportL2Value = 15;
    final int averageReactivePowerImportL3Value = 15;
    final int averageReactivePowerExportL1Value = 15;
    final int averageReactivePowerExportL2Value = 15;
    final int averageReactivePowerExportL3Value = 15;

    for (int i = 0; i < PROFILE_ENTRIES; i++) {
      final Calendar cal = this.getNextDateTime();
      this.bufferData.add(
          Arrays.asList(
              cal,
              averageActivePowerImportL1Value,
              averageActivePowerImportL2Value,
              averageActivePowerImportL3Value,
              averageActivePowerExportL1Value,
              averageActivePowerExportL2Value,
              averageActivePowerExportL3Value,
              averageReactivePowerImportL1Value,
              averageReactivePowerImportL2Value,
              averageReactivePowerImportL3Value,
              averageReactivePowerExportL1Value,
              averageReactivePowerExportL2Value,
              averageReactivePowerExportL3Value));
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
