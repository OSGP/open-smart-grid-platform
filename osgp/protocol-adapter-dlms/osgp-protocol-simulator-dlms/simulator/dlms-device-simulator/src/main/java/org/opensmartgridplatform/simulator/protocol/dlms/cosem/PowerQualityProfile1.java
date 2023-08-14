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
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CosemDateTimeProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.UInteger16DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.util.DynamicValues;

@CosemClass(id = 7)
public class PowerQualityProfile1 extends DynamicProfile {
  public static final String LOGICAL_NAME = "1.0.99.1.1.255";
  public static final int CAPTURE_PERIOD = 900;

  /** 10 days = 240 hours = 960 periods of 15 minutes. */
  public static final int PROFILE_ENTRIES = 960;

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

  private static final Map<CaptureObject, DataProcessor> PROCESSORS_BY_CAPTURE_OBJECT =
      new HashMap<>();
  private static final DataProcessor COSEM_DATE_TIME_PROCESSOR = new CosemDateTimeProcessor();
  private static final DataProcessor LONG_UNSIGNED_PROCESSOR = new UInteger16DataProcessor();

  public static final CaptureObject CLOCK_TIME =
      new CaptureObject(
          InterfaceClass.CLOCK.id(),
          LOGICAL_NAME_CLOCK,
          (byte) ClockAttribute.TIME.attributeId(),
          0);

  public static final CaptureObject AVERAGE_ACTIVE_POWER_IMPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_ACTIVE_POWER_IMPORT_L1_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_ACTIVE_POWER_IMPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_ACTIVE_POWER_IMPORT_L2_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_ACTIVE_POWER_IMPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_ACTIVE_POWER_IMPORT_L3_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_ACTIVE_POWER_EXPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          PowerQualityProfile1.AVERAGE_ACTIVE_POWER_EXPORT_L1_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_ACTIVE_POWER_EXPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_ACTIVE_POWER_EXPORT_L2_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_ACTIVE_POWER_EXPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_ACTIVE_POWER_EXPORT_L3_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_REACTIVE_POWER_IMPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_REACTIVE_POWER_IMPORT_L1_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_REACTIVE_POWER_IMPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_REACTIVE_POWER_IMPORT_L2_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_REACTIVE_POWER_IMPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_REACTIVE_POWER_IMPORT_L3_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_REACTIVE_POWER_EXPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_REACTIVE_POWER_EXPORT_L1_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_REACTIVE_POWER_EXPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          AVERAGE_REACTIVE_POWER_EXPORT_L2_LOGICAL_NAME,
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_REACTIVE_POWER_EXPORT_L3_VALUE =
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

  public PowerQualityProfile1(
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

    this.buffer = DataObject.newNullData();
    this.sortMethod = DataObject.newEnumerateData(SortMethod.FIFO.value());
    this.sortObject = DataObject.newNullData();
    this.entriesInUse = DataObject.newNullData();
  }

  @Override
  public DataObject getBuffer(final SelectiveAccessDescription selectiveAccessDescription) {
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
