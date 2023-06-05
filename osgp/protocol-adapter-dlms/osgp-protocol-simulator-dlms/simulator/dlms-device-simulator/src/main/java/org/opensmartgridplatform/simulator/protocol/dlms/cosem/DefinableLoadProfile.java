// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAccessMode;
import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.IllegalAttributeAccessException;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CaptureObjectDefinition;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CaptureObjectDefinitionCollection;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CosemDateTimeProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.Integer32DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.UInteger16DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.UInteger32DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.util.DynamicValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@CosemClass(id = 7)
public class DefinableLoadProfile extends ProfileGeneric {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefinableLoadProfile.class);

  private static final int MAX_CAPTURE_OBJECTS = 20;
  private static final int MAX_PROFILE_ENTRIES = 960;

  private static final DataProcessor COSEM_DATE_TIME_PROCESSOR = new CosemDateTimeProcessor();
  private static final DataProcessor LONG_UNSIGNED_PROCESSOR = new UInteger16DataProcessor();
  private static final DataProcessor DOUBLE_LONG_PROCESSOR = new Integer32DataProcessor();
  private static final DataProcessor DOUBLE_LONG_UNSIGNED_PROCESSOR = new UInteger32DataProcessor();
  private static final Map<CaptureObject, DataProcessor> PROCESSORS_BY_CAPTURE_OBJECT =
      new HashMap<>();

  private static final CaptureObject CLOCK_TIME =
      new CaptureObject(
          InterfaceClass.CLOCK.id(), "0.0.1.0.0.255", (byte) ClockAttribute.TIME.attributeId(), 0);

  private static final CaptureObject NUMBER_OF_POWER_FAILURES_IN_ANY_PHASE_VALUE =
      new CaptureObject(
          InterfaceClass.DATA.id(), "0.0.96.7.21.255", (byte) DataAttribute.VALUE.attributeId(), 0);
  private static final CaptureObject NUMBER_OF_LONG_POWER_FAILURES_IN_ANY_PHASE_VALUE =
      new CaptureObject(
          InterfaceClass.DATA.id(), "0.0.96.7.9.255", (byte) DataAttribute.VALUE.attributeId(), 0);
  private static final CaptureObject TIME_THRESHOLD_FOR_LONG_POWER_FAILURE_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "0.0.96.7.20.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject DURATION_OF_LONG_POWER_FAILURES_IN_ANY_PHASE_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "0.0.96.7.19.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);

  private static final CaptureObject INSTANTANEOUS_VOLTAGE_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.32.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_VOLTAGE_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.32.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_CURRENT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.31.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_ACTIVE_POWER_IMPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.21.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_ACTIVE_POWER_EXPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.22.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.21.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.22.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_REACTIVE_POWER_IMPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.23.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_REACTIVE_POWER_EXPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.24.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_CURRENT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.31.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_VOLTAGE_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.52.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_VOLTAGE_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.52.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_CURRENT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.51.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_ACTIVE_POWER_IMPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.41.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_ACTIVE_POWER_EXPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.42.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.41.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.42.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_REACTIVE_POWER_IMPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.43.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_REACTIVE_POWER_EXPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.44.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_CURRENT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.51.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_VOLTAGE_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.72.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_VOLTAGE_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.72.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_CURRENT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.71.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_ACTIVE_POWER_IMPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.61.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_ACTIVE_POWER_EXPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.62.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.61.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.62.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_REACTIVE_POWER_IMPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.63.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_REACTIVE_POWER_EXPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.64.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject AVERAGE_CURRENT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.71.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_IMPORT_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.1.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_EXPORT_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.2.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.16.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject INSTANTANEOUS_CURRENT_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.90.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);

  private static final CaptureObject THRESHOLD_FOR_VOLTAGE_SAG_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.12.31.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject TIME_THRESHOLD_FOR_VOLTAGE_SAG_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.12.43.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L1_VALUE =
      new CaptureObject(
          InterfaceClass.DATA.id(), "1.0.32.32.0.255", (byte) DataAttribute.VALUE.attributeId(), 0);
  private static final CaptureObject NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L2_VALUE =
      new CaptureObject(
          InterfaceClass.DATA.id(), "1.0.52.32.0.255", (byte) DataAttribute.VALUE.attributeId(), 0);
  private static final CaptureObject NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L3_VALUE =
      new CaptureObject(
          InterfaceClass.DATA.id(), "1.0.72.32.0.255", (byte) DataAttribute.VALUE.attributeId(), 0);
  private static final CaptureObject THRESHOLD_FOR_VOLTAGE_SWELL_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.12.35.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject TIME_THRESHOLD_FOR_VOLTAGE_SWELL_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.12.44.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  private static final CaptureObject NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L1_VALUE =
      new CaptureObject(
          InterfaceClass.DATA.id(), "1.0.32.36.0.255", (byte) DataAttribute.VALUE.attributeId(), 0);
  private static final CaptureObject NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L2_VALUE =
      new CaptureObject(
          InterfaceClass.DATA.id(), "1.0.52.36.0.255", (byte) DataAttribute.VALUE.attributeId(), 0);
  private static final CaptureObject NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L3_VALUE =
      new CaptureObject(
          InterfaceClass.DATA.id(), "1.0.72.36.0.255", (byte) DataAttribute.VALUE.attributeId(), 0);

  static {
    PROCESSORS_BY_CAPTURE_OBJECT.put(CLOCK_TIME, COSEM_DATE_TIME_PROCESSOR);

    PROCESSORS_BY_CAPTURE_OBJECT.put(
        NUMBER_OF_POWER_FAILURES_IN_ANY_PHASE_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        NUMBER_OF_LONG_POWER_FAILURES_IN_ANY_PHASE_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        TIME_THRESHOLD_FOR_LONG_POWER_FAILURE_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        DURATION_OF_LONG_POWER_FAILURES_IN_ANY_PHASE_VALUE, DOUBLE_LONG_UNSIGNED_PROCESSOR);

    PROCESSORS_BY_CAPTURE_OBJECT.put(INSTANTANEOUS_VOLTAGE_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_VOLTAGE_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(INSTANTANEOUS_CURRENT_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_ACTIVE_POWER_IMPORT_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_ACTIVE_POWER_EXPORT_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        AVERAGE_REACTIVE_POWER_IMPORT_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        AVERAGE_REACTIVE_POWER_EXPORT_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_CURRENT_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(INSTANTANEOUS_VOLTAGE_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_VOLTAGE_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(INSTANTANEOUS_CURRENT_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_ACTIVE_POWER_IMPORT_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_ACTIVE_POWER_EXPORT_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        AVERAGE_REACTIVE_POWER_IMPORT_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        AVERAGE_REACTIVE_POWER_EXPORT_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_CURRENT_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(INSTANTANEOUS_VOLTAGE_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_VOLTAGE_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(INSTANTANEOUS_CURRENT_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_ACTIVE_POWER_IMPORT_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_ACTIVE_POWER_EXPORT_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        AVERAGE_REACTIVE_POWER_IMPORT_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        AVERAGE_REACTIVE_POWER_EXPORT_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(AVERAGE_CURRENT_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        INSTANTANEOUS_ACTIVE_POWER_IMPORT_VALUE, DOUBLE_LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        INSTANTANEOUS_ACTIVE_POWER_EXPORT_VALUE, DOUBLE_LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(INSTANTANEOUS_ACTIVE_POWER_VALUE, DOUBLE_LONG_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(INSTANTANEOUS_CURRENT_VALUE, LONG_UNSIGNED_PROCESSOR);

    PROCESSORS_BY_CAPTURE_OBJECT.put(THRESHOLD_FOR_VOLTAGE_SAG_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(TIME_THRESHOLD_FOR_VOLTAGE_SAG_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(THRESHOLD_FOR_VOLTAGE_SWELL_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        TIME_THRESHOLD_FOR_VOLTAGE_SWELL_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
  }

  private static final List<CaptureObject> DEFAULT_CAPTURE_OBJECTS =
      Arrays.asList(
          CLOCK_TIME,
          INSTANTANEOUS_VOLTAGE_L1_VALUE,
          INSTANTANEOUS_VOLTAGE_L2_VALUE,
          INSTANTANEOUS_VOLTAGE_L3_VALUE,
          AVERAGE_VOLTAGE_L1_VALUE,
          AVERAGE_VOLTAGE_L2_VALUE,
          AVERAGE_VOLTAGE_L3_VALUE,
          INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1_VALUE,
          INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1_VALUE,
          AVERAGE_CURRENT_L1_VALUE,
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
          AVERAGE_REACTIVE_POWER_EXPORT_L3_VALUE,
          NUMBER_OF_LONG_POWER_FAILURES_IN_ANY_PHASE_VALUE,
          NUMBER_OF_POWER_FAILURES_IN_ANY_PHASE_VALUE,
          NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L1_VALUE,
          NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L1_VALUE,
          NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L2_VALUE,
          NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L2_VALUE,
          NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L3_VALUE,
          NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L3_VALUE,
          INSTANTANEOUS_CURRENT_VALUE);

  @Autowired private DynamicValues dynamicValues;

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

  @CosemAttribute(
      id = 3,
      type = Type.ARRAY,
      accessMode = AttributeAccessMode.READ_AND_WRITE,
      snOffset = 0x10)
  private DataObject captureObjects;

  @CosemAttribute(
      id = 4,
      type = Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_AND_WRITE,
      snOffset = 0x18)
  private DataObject capturePeriod;

  @CosemAttribute(
      id = 5,
      type = Type.ENUMERATE,
      accessMode = AttributeAccessMode.READ_AND_WRITE,
      snOffset = 0x20)
  private final DataObject sortMethod;

  @CosemAttribute(
      id = 6,
      type = Type.STRUCTURE,
      accessMode = AttributeAccessMode.READ_AND_WRITE,
      snOffset = 0x28)
  private final DataObject sortObject;

  @CosemAttribute(
      id = 7,
      type = Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x30)
  private final DataObject entriesInUse;

  @CosemAttribute(
      id = 8,
      type = Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_AND_WRITE,
      snOffset = 0x38)
  private DataObject profileEntries;

  private final Calendar time;

  private CaptureObjectDefinitionCollection captureObjectDefinitionCollection;

  private final Random random = new Random();

  public DefinableLoadProfile(final Calendar time) {
    super("0.1.94.31.6.255");
    this.time = time;

    this.buffer = DataObject.newNullData();
    this.captureObjects = this.defaultCaptureObjects();
    this.captureObjectDefinitionCollection =
        DefinableLoadProfile.initCaptureObjects(DEFAULT_CAPTURE_OBJECTS);
    this.sortMethod = DataObject.newEnumerateData(SortMethod.FIFO.value());
    this.sortObject = DataObject.newNullData();
    this.entriesInUse = DataObject.newNullData();
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

    final long numberOfProfileEntries = this.getProfileEntries().getValue();

    this.bufferData = new CircularFifoQueue<>((int) numberOfProfileEntries);

    final List<CaptureObject> captureObjectDefinitions = this.getCaptureObjectDefinitions();
    final int numberOfCaptureObjects = captureObjectDefinitions.size();
    final long capturePeriodSeconds = this.getCapturePeriod().getValue();

    for (int i = 0; i < numberOfProfileEntries; i++) {
      final Calendar cal = this.getNextDateTime((int) capturePeriodSeconds);
      final List<Object> profileEntryList = new ArrayList<>();
      profileEntryList.add(cal);
      for (int j = 1; j < numberOfCaptureObjects; j++) {
        final CaptureObject captureObject = captureObjectDefinitions.get(j);
        this.addProfileEntry(profileEntryList, captureObject, cal, this.random);
      }
      this.bufferData.add(profileEntryList);
    }
  }

  private void addProfileEntry(
      final List<Object> profileEntryList,
      final CaptureObject captureObject,
      final Calendar profileEntryTime,
      final Random random) {

    final DataProcessor processor = PROCESSORS_BY_CAPTURE_OBJECT.get(captureObject);
    if (COSEM_DATE_TIME_PROCESSOR == processor) {
      profileEntryList.add(profileEntryTime);
    } else if (LONG_UNSIGNED_PROCESSOR == processor) {
      /*
       * Random value in the range of valid long-unsigned values [0 ..
       * 0xFFFF]
       */
      profileEntryList.add(random.nextInt(0xFFFF + 1));
    } else if (DOUBLE_LONG_PROCESSOR == processor) {
      /*
       * Random value in the range of valid double-long values (any int)
       */
      int next = random.nextInt();
      if (random.nextBoolean()) {
        next = -next;
      }
      profileEntryList.add(next);
    } else if (DOUBLE_LONG_UNSIGNED_PROCESSOR == processor) {
      /*
       * Random value in the range of valid double-long-unsigned values [0
       * .. 0xFFFFFFFFL]
       */
      profileEntryList.add(Math.max(0xFFFFFFFFL, random.nextLong()));
    }
  }

  private List<CaptureObject> getCaptureObjectDefinitions() {
    final List<DataObject> captureObjectList = this.captureObjects.getValue();
    final List<CaptureObject> captureObjectDefinitions = new ArrayList<>();
    for (final DataObject captureObject : captureObjectList) {
      captureObjectDefinitions.add(CaptureObject.newCaptureObject(captureObject));
    }
    return captureObjectDefinitions;
  }

  private Calendar getNextDateTime(final int capturePeriodSeconds) {
    final Calendar next = (Calendar) this.time.clone();
    this.time.add(Calendar.SECOND, capturePeriodSeconds);
    return next;
  }

  @Override
  protected CaptureObjectDefinitionCollection getCaptureObjectDefinitionCollection() {
    return this.captureObjectDefinitionCollection;
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
    return this.captureObjects;
  }

  public void setCaptureObjects(final DataObject captureObjects)
      throws IllegalAttributeAccessException {
    final List<DataObject> captureObjectList = captureObjects.getValue();
    final int numberOfCaptureObjects = captureObjectList.size();
    if (numberOfCaptureObjects > MAX_CAPTURE_OBJECTS) {
      LOGGER.error(
          "Number of capture objects larger than supported (max {}): {}",
          MAX_CAPTURE_OBJECTS,
          numberOfCaptureObjects);
      throw new IllegalAttributeAccessException(
          AccessResultCode.OTHER_REASON,
          new IllegalArgumentException(
              "Number of capture objects larger than supported (max "
                  + MAX_CAPTURE_OBJECTS
                  + "): "
                  + numberOfCaptureObjects));
    }
    this.reinitializeCaptureObjects(captureObjectList);
    this.captureObjects = captureObjects;
    /*
     * Setting the capture objects has an effect on the buffer. Make sure
     * the buffer will be reinitialized when getBuffer is called.
     */
    this.bufferData = null;
  }

  private void reinitializeCaptureObjects(final List<DataObject> captureObjectList)
      throws IllegalAttributeAccessException {
    final List<CaptureObject> captureObjectInitList = new ArrayList<>();
    for (final DataObject captureObject : captureObjectList) {
      final CaptureObject initCaptureObject;
      try {
        initCaptureObject = CaptureObject.newCaptureObject(captureObject);
      } catch (final RuntimeException e) {
        throw new IllegalAttributeAccessException(
            AccessResultCode.OTHER_REASON,
            new IllegalArgumentException(
                "Unable to create capture object from: " + captureObject, e));
      }
      if (!PROCESSORS_BY_CAPTURE_OBJECT.containsKey(initCaptureObject)) {
        LOGGER.error("No data processor configured for {}", initCaptureObject);
        throw new IllegalAttributeAccessException(
            AccessResultCode.OTHER_REASON,
            new IllegalArgumentException("No data processor configured for " + initCaptureObject));
      }
      captureObjectInitList.add(initCaptureObject);
    }
    this.captureObjectDefinitionCollection =
        DefinableLoadProfile.initCaptureObjects(captureObjectInitList);
  }

  public DataObject getCapturePeriod() {
    return this.dynamicValues.getDlmsAttributeValue(
        this, ProfileGenericAttribute.CAPTURE_PERIOD.attributeId());
  }

  public void setCapturePeriod(final DataObject capturePeriod) {
    this.dynamicValues.setDlmsAttributeValue(
        this, ProfileGenericAttribute.CAPTURE_PERIOD.attributeId(), capturePeriod);
  }

  public DataObject getProfileEntries() {
    return this.dynamicValues.getDlmsAttributeValue(
        this, ProfileGenericAttribute.PROFILE_ENTRIES.attributeId());
  }

  public void setProfileEntries(final DataObject profileEntries)
      throws IllegalAttributeAccessException {
    final long numberOfProfileEntries = profileEntries.getValue();
    if (numberOfProfileEntries > MAX_PROFILE_ENTRIES) {
      LOGGER.error(
          "Number of profile entries larger than supported (max {}): {}",
          MAX_PROFILE_ENTRIES,
          numberOfProfileEntries);
      throw new IllegalAttributeAccessException(
          AccessResultCode.OTHER_REASON,
          new IllegalArgumentException(
              "Number of profile entries larger than supported (max "
                  + MAX_PROFILE_ENTRIES
                  + "): "
                  + numberOfProfileEntries));
    }
    this.dynamicValues.setDlmsAttributeValue(
        this, ProfileGenericAttribute.PROFILE_ENTRIES.attributeId(), profileEntries);
    /*
     * Setting the number of profile entries has an effect on the buffer.
     * Make sure the buffer will be reinitialized when getBuffer is called.
     */
    this.bufferData = null;
  }
}
