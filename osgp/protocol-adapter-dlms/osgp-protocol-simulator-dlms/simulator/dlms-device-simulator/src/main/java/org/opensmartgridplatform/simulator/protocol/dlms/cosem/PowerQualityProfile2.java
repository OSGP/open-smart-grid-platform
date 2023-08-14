// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.Clock.LOGICAL_NAME_CLOCK;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openmuc.jdlms.AttributeAccessMode;
import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.IllegalAttributeAccessException;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.util.DynamicValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CosemClass(id = 7)
public class PowerQualityProfile2 extends DynamicProfile {
  private static final Logger LOGGER = LoggerFactory.getLogger(PowerQualityProfile2.class);
  /** Every 10 minutes. */
  public static final int CAPTURE_PERIOD = 600;

  /** 10 days = 240 hours = 1440 periods of 10 minutes. */
  public static final int PROFILE_ENTRIES = 1440;

  public static final String LOGICAL_NAME = "1.0.99.1.2.255";

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
  public static final String INSTANTANEOUS_ACTIVE_POWER_IMPORT_LOGICAL_NAME_DSMR22 =
      "1.0.15.7.0.255";
  public static final String INSTANTANEOUS_ACTIVE_POWER_EXPORT_LOGICAL_NAME = "1.0.2.7.0.255";

  public static final String INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1_LOGICAL_NAME = "1.0.22.7.0.255";
  public static final String INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2_LOGICAL_NAME = "1.0.42.7.0.255";
  public static final String INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3_LOGICAL_NAME = "1.0.62.7.0.255";

  public static final String INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1_LOGICAL_NAME = "1.0.21.7.0.255";
  public static final String INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2_LOGICAL_NAME = "1.0.41.7.0.255";
  public static final String INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3_LOGICAL_NAME = "1.0.61.7.0.255";

  private static final Map<CaptureObject, DataProcessor> PROCESSORS_BY_CAPTURE_OBJECT =
      new HashMap<>();

  public static final CaptureObject CLOCK_TIME =
      new CaptureObject(
          InterfaceClass.CLOCK.id(),
          LOGICAL_NAME_CLOCK,
          (byte) ClockAttribute.TIME.attributeId(),
          0);

  public static final CaptureObject AVERAGE_VOLTAGE_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_VOLTAGE_L1_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_VOLTAGE_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_VOLTAGE_L2_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_VOLTAGE_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_VOLTAGE_L3_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_VOLTAGE_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          INSTANTANEOUS_VOLTAGE_L1_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_VOLTAGE_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          INSTANTANEOUS_VOLTAGE_L2_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_VOLTAGE_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          INSTANTANEOUS_VOLTAGE_L3_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);

  public static final CaptureObject AVERAGE_CURRENT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_CURRENT_L1_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_CURRENT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_CURRENT_L2_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_CURRENT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_CURRENT_L3_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_CURRENT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          INSTANTANEOUS_CURRENT_L1_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_IMPORT_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.1.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_EXPORT_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.2.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);

  public static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.21.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.41.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.61.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.22.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.42.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.62.7.0.255",
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
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        INSTANTANEOUS_ACTIVE_POWER_IMPORT_VALUE, DOUBLE_LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        INSTANTANEOUS_ACTIVE_POWER_EXPORT_VALUE, DOUBLE_LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2_VALUE, LONG_UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3_VALUE, LONG_UNSIGNED_PROCESSOR);
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
  private DataObject captureObjects;

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

  public PowerQualityProfile2(
      final DynamicValues dynamicValues,
      final Calendar time,
      final Integer maxNumberOfCaptureObjects,
      final List<CaptureObject> captureObjectList) {
    super(
        LOGICAL_NAME,
        dynamicValues,
        time,
        maxNumberOfCaptureObjects,
        captureObjectList,
        PROCESSORS_BY_CAPTURE_OBJECT);
    LOGGER.info("---------------------PowerQualityProfile2");

    this.buffer = DataObject.newNullData();
    this.sortMethod = DataObject.newEnumerateData(SortMethod.FIFO.value());
    this.sortObject = DataObject.newNullData();
    this.entriesInUse = DataObject.newNullData();
  }

  @Override
  public DataObject getBuffer(final SelectiveAccessDescription selectiveAccessDescription) {
    LOGGER.info("-PQ2- getBuffer");
    return super.getBuffer(selectiveAccessDescription);
  }

  @Override
  public DataObject getCaptureObjects() {
    return super.getCaptureObjects();
  }

  @Override
  public void setCaptureObjects(final DataObject captureObjects)
      throws IllegalAttributeAccessException {
    super.setCaptureObjects(captureObjects);
  }

  @Override
  public DataObject getCapturePeriod() {
    return super.getCapturePeriod();
  }

  @Override
  public void setCapturePeriod(final DataObject capturePeriod) {
    super.setCapturePeriod(capturePeriod);
  }

  @Override
  public DataObject getProfileEntries() {
    return super.getProfileEntries();
  }

  @Override
  public void setProfileEntries(final DataObject profileEntries)
      throws IllegalAttributeAccessException {
    super.setProfileEntries(profileEntries);
  }
}
