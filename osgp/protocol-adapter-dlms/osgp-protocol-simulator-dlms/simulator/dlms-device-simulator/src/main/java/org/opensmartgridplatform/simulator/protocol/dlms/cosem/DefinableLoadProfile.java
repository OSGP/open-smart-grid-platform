// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

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
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.GsmDiagnosticAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusClientAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusDiagnosticAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.util.DynamicValues;

@CosemClass(id = 7)
public class DefinableLoadProfile extends DynamicProfile {
  private static final String LOGICAL_NAME_DEFINABLE_LOAD_PROFILE = "0.1.94.31.6.255";
  private static final Map<CaptureObject, DataProcessor> PROCESSORS_BY_CAPTURE_OBJECT =
      new HashMap<>();

  public static final CaptureObject CLOCK_TIME =
      new CaptureObject(
          InterfaceClass.CLOCK.id(), "0.0.1.0.0.255", (byte) ClockAttribute.TIME.attributeId(), 0);

  public static final CaptureObject CDMA_DIAGNOSTIC_SIGNAL_QUALITY =
      new CaptureObject(
          InterfaceClass.GSM_DIAGNOSTIC.id(),
          "0.1.25.6.0.255",
          (byte) GsmDiagnosticAttribute.CELL_INFO.attributeId(),
          3);

  public static final CaptureObject GPRS_DIAGNOSTIC_SIGNAL_QUALITY =
      new CaptureObject(
          InterfaceClass.GSM_DIAGNOSTIC.id(),
          "0.0.25.6.0.255",
          (byte) GsmDiagnosticAttribute.CELL_INFO.attributeId(),
          3);

  public static final CaptureObject CDMA_DIAGNOSTIC_BER =
      new CaptureObject(
          InterfaceClass.GSM_DIAGNOSTIC.id(),
          "0.1.25.6.0.255",
          (byte) GsmDiagnosticAttribute.CELL_INFO.attributeId(),
          4);

  public static final CaptureObject GPRS_DIAGNOSTIC_BER =
      new CaptureObject(
          InterfaceClass.GSM_DIAGNOSTIC.id(),
          "0.0.25.6.0.255",
          (byte) GsmDiagnosticAttribute.CELL_INFO.attributeId(),
          4);

  public static final CaptureObject MBUS_CLIENT_SETUP_CHN1_VALUE =
      new CaptureObject(
          InterfaceClass.MBUS_CLIENT.id(),
          "0.1.24.1.0.255",
          (byte) MbusClientAttribute.STATUS.attributeId(),
          0);
  public static final CaptureObject MBUS_CLIENT_SETUP_CHN2_VALUE =
      new CaptureObject(
          InterfaceClass.MBUS_CLIENT.id(),
          "0.2.24.1.0.255",
          (byte) MbusClientAttribute.STATUS.attributeId(),
          0);

  public static final CaptureObject MBUS_DIAGNOSTIC_RSSI_CHN1_VALUE =
      new CaptureObject(
          InterfaceClass.MBUS_DIAGNOSTIC.id(),
          "0.1.24.9.0.255",
          (byte) MbusDiagnosticAttribute.RECEIVED_SIGNAL_STRENGTH.attributeId(),
          0);
  public static final CaptureObject MBUS_DIAGNOSTIC_RSSI_CHN2_VALUE =
      new CaptureObject(
          InterfaceClass.MBUS_DIAGNOSTIC.id(),
          "0.2.24.9.0.255",
          (byte) MbusDiagnosticAttribute.RECEIVED_SIGNAL_STRENGTH.attributeId(),
          0);
  public static final CaptureObject MBUS_DIAGNOSTIC_FCS_NOK_CHN1_VALUE =
      new CaptureObject(
          InterfaceClass.MBUS_DIAGNOSTIC.id(),
          "0.1.24.9.0.255",
          (byte) MbusDiagnosticAttribute.FCS_NOK_FRAMES_COUNTER.attributeId(),
          0);
  public static final CaptureObject MBUS_DIAGNOSTIC_FCS_NOK_CHN2_VALUE =
      new CaptureObject(
          InterfaceClass.MBUS_DIAGNOSTIC.id(),
          "0.2.24.9.0.255",
          (byte) MbusDiagnosticAttribute.FCS_NOK_FRAMES_COUNTER.attributeId(),
          0);
  public static final CaptureObject NUMBER_OF_POWER_FAILURES_IN_ANY_PHASE_VALUE =
      new CaptureObject(
          InterfaceClass.DATA.id(), "0.0.96.7.21.255", (byte) DataAttribute.VALUE.attributeId(), 0);

  public static final CaptureObject NUMBER_OF_LONG_POWER_FAILURES_IN_ANY_PHASE_VALUE =
      new CaptureObject(
          InterfaceClass.DATA.id(), "0.0.96.7.9.255", (byte) DataAttribute.VALUE.attributeId(), 0);
  public static final CaptureObject TIME_THRESHOLD_FOR_LONG_POWER_FAILURE_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "0.0.96.7.20.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject DURATION_OF_LONG_POWER_FAILURES_IN_ANY_PHASE_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "0.0.96.7.19.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);

  public static final CaptureObject INSTANTANEOUS_VOLTAGE_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.32.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_VOLTAGE_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.32.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_CURRENT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.31.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_ACTIVE_POWER_IMPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.21.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_ACTIVE_POWER_EXPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.22.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.21.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.22.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_REACTIVE_POWER_IMPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.23.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_REACTIVE_POWER_EXPORT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.24.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_CURRENT_L1_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.31.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_VOLTAGE_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.52.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_VOLTAGE_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.52.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_CURRENT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.51.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_ACTIVE_POWER_IMPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.41.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_ACTIVE_POWER_EXPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.42.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.41.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.42.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_REACTIVE_POWER_IMPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.43.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_REACTIVE_POWER_EXPORT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.44.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_CURRENT_L2_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.51.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_VOLTAGE_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.72.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_VOLTAGE_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.72.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_CURRENT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.71.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_ACTIVE_POWER_IMPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.61.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_ACTIVE_POWER_EXPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.62.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.61.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.62.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_REACTIVE_POWER_IMPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.63.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_REACTIVE_POWER_EXPORT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.64.24.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject AVERAGE_CURRENT_L3_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.71.24.0.255",
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
  public static final CaptureObject INSTANTANEOUS_ACTIVE_POWER_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.16.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject INSTANTANEOUS_CURRENT_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.90.7.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);

  public static final CaptureObject THRESHOLD_FOR_VOLTAGE_SAG_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.12.31.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject TIME_THRESHOLD_FOR_VOLTAGE_SAG_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.12.43.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L1_VALUE =
      new CaptureObject(
          InterfaceClass.DATA.id(), "1.0.32.32.0.255", (byte) DataAttribute.VALUE.attributeId(), 0);
  public static final CaptureObject NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L2_VALUE =
      new CaptureObject(
          InterfaceClass.DATA.id(), "1.0.52.32.0.255", (byte) DataAttribute.VALUE.attributeId(), 0);
  public static final CaptureObject NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L3_VALUE =
      new CaptureObject(
          InterfaceClass.DATA.id(), "1.0.72.32.0.255", (byte) DataAttribute.VALUE.attributeId(), 0);
  public static final CaptureObject THRESHOLD_FOR_VOLTAGE_SWELL_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.12.35.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject TIME_THRESHOLD_FOR_VOLTAGE_SWELL_VALUE =
      new CaptureObject(
          InterfaceClass.REGISTER.id(),
          "1.0.12.44.0.255",
          (byte) RegisterAttribute.VALUE.attributeId(),
          0);
  public static final CaptureObject NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L1_VALUE =
      new CaptureObject(
          InterfaceClass.DATA.id(), "1.0.32.36.0.255", (byte) DataAttribute.VALUE.attributeId(), 0);
  public static final CaptureObject NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L2_VALUE =
      new CaptureObject(
          InterfaceClass.DATA.id(), "1.0.52.36.0.255", (byte) DataAttribute.VALUE.attributeId(), 0);
  public static final CaptureObject NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L3_VALUE =
      new CaptureObject(
          InterfaceClass.DATA.id(), "1.0.72.36.0.255", (byte) DataAttribute.VALUE.attributeId(), 0);

  static {
    PROCESSORS_BY_CAPTURE_OBJECT.put(CLOCK_TIME, COSEM_DATE_TIME_PROCESSOR);

    PROCESSORS_BY_CAPTURE_OBJECT.put(
        CDMA_DIAGNOSTIC_SIGNAL_QUALITY, GSM_DIAGNOSTIC_CELL_INFO_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(
        GPRS_DIAGNOSTIC_SIGNAL_QUALITY, GSM_DIAGNOSTIC_CELL_INFO_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(CDMA_DIAGNOSTIC_BER, GSM_DIAGNOSTIC_CELL_INFO_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(GPRS_DIAGNOSTIC_BER, GSM_DIAGNOSTIC_CELL_INFO_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(MBUS_CLIENT_SETUP_CHN1_VALUE, UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(MBUS_CLIENT_SETUP_CHN2_VALUE, UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(MBUS_DIAGNOSTIC_RSSI_CHN1_VALUE, UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(MBUS_DIAGNOSTIC_RSSI_CHN2_VALUE, UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(MBUS_DIAGNOSTIC_FCS_NOK_CHN1_VALUE, UNSIGNED_PROCESSOR);
    PROCESSORS_BY_CAPTURE_OBJECT.put(MBUS_DIAGNOSTIC_FCS_NOK_CHN2_VALUE, UNSIGNED_PROCESSOR);

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

  public DefinableLoadProfile(
      final DynamicValues dynamicValues,
      final Calendar time,
      final Integer maxNumberOfCaptureObjects,
      final List<CaptureObject> captureObjectList) {
    super(
        LOGICAL_NAME_DEFINABLE_LOAD_PROFILE,
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
