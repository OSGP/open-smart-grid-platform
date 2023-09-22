// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.Event;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventDetail;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventLogCategory;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventType;

class ListEventMappingTest {

  private static final String NUMBER_OF_EVENTS = "number of events";
  private static final String EVENT_CODE_WITH_MAPPING_OF = "eventCode with mapping of ";
  private static final String EVENT_COUNTER_WITH_MAPPING_OF = "eventCounter with mapping of ";
  private static final String TIMESTAMP_WITH_MAPPING_OF = "timestamp with mapping of ";

  private static final EventType EVENT_TYPE = EventType.ERROR_REGISTER_CLEARED;
  private static final Integer EVENT_COUNTER = 1;
  private static final List<Event> STANDARD_EVENTS =
      Arrays.asList(
          newEvent(
              ZonedDateTime.now(),
              EventType.EVENTLOG_CLEARED,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.POWER_FAILURE,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.POWER_RETURNED,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.CLOCK_UPDATE,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.CLOCK_ADJUSTED_OLD_TIME,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.CLOCK_ADJUSTED_NEW_TIME,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.CLOCK_INVALID,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.REPLACE_BATTERY,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.BATTERY_VOLTAGE_LOW,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.TARIFF_ACTIVATED,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.ERROR_REGISTER_CLEARED,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.ALARM_REGISTER_CLEARED,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.HARDWARE_ERROR_PROGRAM_MEMORY,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.HARDWARE_ERROR_RAM,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.HARDWARE_ERROR_NV_MEMORY,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.WATCHDOG_ERROR,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.HARDWARE_ERROR_MEASUREMENT_SYSTEM,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.FIRMWARE_READY_FOR_ACTIVATION,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.FIRMWARE_ACTIVATED,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.PASSIVE_TARIFF_UPDATED,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.SUCCESSFUL_SELFCHECK_AFTER_FIRMWARE_UPDATE,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.COMMUNICATION_MODULE_REMOVED,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.COMMUNICATION_MODULE_INSERTED,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.ERROR_REGISTER_2_CLEARED,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.ALARM_REGISTER_2_CLEARED,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_231,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_232,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_233,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_234,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_235,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_236,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_237,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_238,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_239,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_240,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_241,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_242,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_243,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_244,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_245,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_246,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_247,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_248,
              null,
              EventLogCategory.STANDARD_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MANUFACTURER_SPECIFIC_249,
              null,
              EventLogCategory.STANDARD_EVENT_LOG));

  private static final List<Event> FRAUD_DETECTION_EVENTS =
      Arrays.asList(
          newEvent(
              ZonedDateTime.now(),
              EventType.TERMINAL_COVER_REMOVED,
              null,
              EventLogCategory.FRAUD_DETECTION_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.TERMINAL_COVER_CLOSED,
              null,
              EventLogCategory.FRAUD_DETECTION_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.STRONG_DC_FIELD_DETECTED,
              null,
              EventLogCategory.FRAUD_DETECTION_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.NO_STRONG_DC_FIELD_ANYMORE,
              null,
              EventLogCategory.FRAUD_DETECTION_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.METER_COVER_REMOVED,
              null,
              EventLogCategory.FRAUD_DETECTION_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.METER_COVER_CLOSED,
              null,
              EventLogCategory.FRAUD_DETECTION_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.FAILED_LOGIN_ATTEMPT,
              null,
              EventLogCategory.FRAUD_DETECTION_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.CONFIGURATION_CHANGE,
              null,
              EventLogCategory.FRAUD_DETECTION_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MODULE_COVER_OPENED,
              null,
              EventLogCategory.FRAUD_DETECTION_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MODULE_COVER_CLOSED,
              null,
              EventLogCategory.FRAUD_DETECTION_LOG));

  private static final List<Event> COMMUNICATION_SESSIONS_EVENTS =
      Arrays.asList(
          newEvent(
              ZonedDateTime.now(),
              EventType.METROLOGICAL_MAINTENANCE,
              0,
              EventLogCategory.COMMUNICATION_SESSION_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.TECHNICAL_MAINTENANCE,
              0,
              EventLogCategory.COMMUNICATION_SESSION_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.RETRIEVE_METER_READINGS_E,
              0,
              EventLogCategory.COMMUNICATION_SESSION_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.RETRIEVE_METER_READINGS_G,
              1,
              EventLogCategory.COMMUNICATION_SESSION_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.RETRIEVE_INTERVAL_DATA_E,
              3754,
              EventLogCategory.COMMUNICATION_SESSION_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.RETRIEVE_INTERVAL_DATA_G,
              65535,
              EventLogCategory.COMMUNICATION_SESSION_LOG));

  private static final List<Event> M_BUS_EVENTS =
      Arrays.asList(
          newEvent(
              ZonedDateTime.now(),
              EventType.COMMUNICATION_ERROR_M_BUS_CHANNEL_1,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.COMMUNICATION_OK_M_BUS_CHANNEL_1,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.REPLACE_BATTERY_M_BUS_CHANNEL_1,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.FRAUD_ATTEMPT_M_BUS_CHANNEL_1,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.CLOCK_ADJUSTED_M_BUS_CHANNEL_1,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_1,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.DEAD_BATTERY_ERROR_M_BUS_DEVICE_CHANNEL_1,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.COMMUNICATION_ERROR_M_BUS_CHANNEL_2,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.COMMUNICATION_OK_M_BUS_CHANNEL_2,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.REPLACE_BATTERY_M_BUS_CHANNEL_2,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.FRAUD_ATTEMPT_M_BUS_CHANNEL_2,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.CLOCK_ADJUSTED_M_BUS_CHANNEL_2,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_2,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.DEAD_BATTERY_ERROR_M_BUS_DEVICE_CHANNEL_2,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.COMMUNICATION_ERROR_M_BUS_CHANNEL_3,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.COMMUNICATION_OK_M_BUS_CHANNEL_3,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.REPLACE_BATTERY_M_BUS_CHANNEL_3,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.FRAUD_ATTEMPT_M_BUS_CHANNEL_3,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.CLOCK_ADJUSTED_M_BUS_CHANNEL_3,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_3,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.DEAD_BATTERY_ERROR_M_BUS_DEVICE_CHANNEL_3,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.COMMUNICATION_ERROR_M_BUS_CHANNEL_4,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.COMMUNICATION_OK_M_BUS_CHANNEL_4,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.REPLACE_BATTERY_M_BUS_CHANNEL_4,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.FRAUD_ATTEMPT_M_BUS_CHANNEL_4,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.CLOCK_ADJUSTED_M_BUS_CHANNEL_4,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_4,
              null,
              EventLogCategory.M_BUS_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.DEAD_BATTERY_ERROR_M_BUS_DEVICE_CHANNEL_4,
              null,
              EventLogCategory.M_BUS_EVENT_LOG));

  private static final List<Event> POWER_QUALITY_EXTENDED_EVENTS =
      Arrays.asList(
          newEvent(
              ZonedDateTime.now(),
              EventType.VOLTAGE_SAG_IN_PHASE_L1,
              null,
              EventLogCategory.POWER_QUALITY_EXTENDED_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.VOLTAGE_SAG_IN_PHASE_L2,
              null,
              EventLogCategory.POWER_QUALITY_EXTENDED_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.VOLTAGE_SAG_IN_PHASE_L3,
              null,
              EventLogCategory.POWER_QUALITY_EXTENDED_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.VOLTAGE_SWELL_IN_PHASE_L1,
              null,
              EventLogCategory.POWER_QUALITY_EXTENDED_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.VOLTAGE_SWELL_IN_PHASE_L2,
              null,
              EventLogCategory.POWER_QUALITY_EXTENDED_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.VOLTAGE_SWELL_IN_PHASE_L3,
              null,
              EventLogCategory.POWER_QUALITY_EXTENDED_EVENT_LOG));

  private static final List<Event> AUXILIARY_EVENTS =
      Arrays.asList(
          newEvent(
              ZonedDateTime.now(),
              EventType.AUXILIARY_EVENTLOG_CLEARED,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_15_RESERVED_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_19_RESERVED_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_1,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_15_RESERVED_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_19_RESERVED_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_2,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_15_RESERVED_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_19_RESERVED_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_3,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_15_RESERVED_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_19_RESERVED_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG),
          newEvent(
              ZonedDateTime.now(),
              EventType.KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_4,
              null,
              EventLogCategory.AUXILIARY_EVENT_LOG));

  private final ManagementMapper managementMapper = new ManagementMapper();

  private void checkEventsMappedFromWsSchema(
      final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event>
          originalEvents,
      final List<Event> mappedEvents) {

    assertThat(mappedEvents.size()).as(NUMBER_OF_EVENTS).isEqualTo(originalEvents.size());

    for (int i = 0; i < originalEvents.size(); i++) {
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event
          originalEvent = originalEvents.get(i);
      final Event mappedEvent = mappedEvents.get(i);

      assertThat(mappedEvent.getEventCode().intValue())
          .as(EVENT_CODE_WITH_MAPPING_OF + originalEvent.getEventType())
          .isEqualTo(EventType.valueOf(originalEvent.getEventType().name()).getEventCode());

      assertThat(mappedEvent.getEventCounter())
          .as(EVENT_COUNTER_WITH_MAPPING_OF + originalEvent.getEventType())
          .isEqualTo(originalEvent.getEventCounter());

      assertThat(mappedEvent.getTimestamp())
          .as(TIMESTAMP_WITH_MAPPING_OF + originalEvent.getEventType())
          .isEqualTo(originalEvent.getTimestamp().toGregorianCalendar().toZonedDateTime());
    }
  }

  private void checkEventsMappedToWsSchema(
      final List<Event> originalEvents,
      final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event>
          mappedEvents) {

    assertThat(mappedEvents.size()).as(NUMBER_OF_EVENTS).isEqualTo(originalEvents.size());

    for (int i = 0; i < originalEvents.size(); i++) {
      final Event originalEvent = originalEvents.get(i);
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event mappedEvent =
          mappedEvents.get(i);

      assertThat(EventType.valueOf(mappedEvent.getEventType().name()).getEventCode())
          .as(EVENT_CODE_WITH_MAPPING_OF + originalEvent)
          .isEqualTo(originalEvent.getEventCode().intValue());

      assertThat(mappedEvent.getEventCounter())
          .as(EVENT_COUNTER_WITH_MAPPING_OF + originalEvent)
          .isEqualTo(originalEvent.getEventCounter());

      assertThat(mappedEvent.getTimestamp().toGregorianCalendar().toZonedDateTime())
          .as(TIMESTAMP_WITH_MAPPING_OF + originalEvent)
          .isEqualTo(originalEvent.getTimestamp());
    }
  }

  /** Tests if mapping a List, typed to Event, succeeds if the List is empty. */
  @Test
  void testEmptyListEventMapping() {

    // build test data
    final List<Event> listOriginal = new ArrayList<>();

    // actual mapping
    final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event>
        listMapped =
            this.managementMapper.mapAsList(
                listOriginal,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event.class);

    // check mapping
    assertThat(listMapped).isNotNull();
    assertThat(listMapped.isEmpty()).isTrue();
  }

  /** Tests if mapping a List, typed to Event, succeeds if the List is filled. */
  @Test
  void testFilledListEventMapping() {

    // build test data
    final ZonedDateTime timestamp = ZonedDateTime.now();
    final List<EventDetail> eventDetails = Collections.singletonList(new EventDetail("A", "B"));
    final Event event =
        new Event(
            timestamp,
            EVENT_TYPE,
            EVENT_COUNTER,
            EventLogCategory.STANDARD_EVENT_LOG,
            eventDetails);
    final List<Event> listOriginal = new ArrayList<>();
    listOriginal.add(event);

    // actual mapping
    final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event>
        listMapped =
            this.managementMapper.mapAsList(
                listOriginal,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event.class);

    // check mapping
    assertThat(listMapped).isNotNull();
    assertThat(listMapped.get(0)).isNotNull();
    assertThat(listMapped.get(0).getEventCounter()).isNotNull();
    assertThat(listMapped.get(0).getEventType()).isNotNull();
    assertThat(listMapped.get(0).getTimestamp()).isNotNull();
    assertThat(listMapped.get(0).getEventLogCategory()).isNotNull();

    assertThat(listMapped.get(0).getTimestamp().getYear()).isEqualTo(timestamp.getYear());
    assertThat(listMapped.get(0).getTimestamp().getMonth()).isEqualTo(timestamp.getMonthValue());
    assertThat(listMapped.get(0).getTimestamp().getDay()).isEqualTo(timestamp.getDayOfMonth());

    assertThat(listMapped.get(0).getTimestamp().getHour()).isEqualTo(timestamp.getHour());
    assertThat(listMapped.get(0).getTimestamp().getMinute()).isEqualTo(timestamp.getMinute());
    assertThat(listMapped.get(0).getTimestamp().getSecond()).isEqualTo(timestamp.getSecond());

    assertThat(listMapped.get(0).getEventType().name()).isEqualTo(EVENT_TYPE.name());
    assertThat(listMapped.get(0).getEventCode()).isEqualTo(EVENT_TYPE.getEventCode());
    assertThat(listMapped.get(0).getEventCounter()).isEqualTo(EVENT_COUNTER);
    assertThat(listMapped.get(0).getEventLogCategory())
        .isEqualTo(
            org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EventLogCategory
                .STANDARD_EVENT_LOG);

    assertThat(listMapped.get(0).getEventDetails().size()).isOne();
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EventDetail
        eventDetail = listMapped.get(0).getEventDetails().get(0);
    assertThat(eventDetail.getName()).isEqualTo("A");
    assertThat(eventDetail.getValue()).isEqualTo("B");
  }

  @Test
  void testMappingForListOfCommunicationSessionEvents() {

    final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event>
        mappedCommunicationSessionEvents =
            this.managementMapper.mapAsList(
                COMMUNICATION_SESSIONS_EVENTS,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event.class);

    this.checkEventsMappedToWsSchema(
        COMMUNICATION_SESSIONS_EVENTS, mappedCommunicationSessionEvents);

    final List<Event> communicationSessionEvents =
        this.managementMapper.mapAsList(mappedCommunicationSessionEvents, Event.class);

    this.checkEventsMappedFromWsSchema(
        mappedCommunicationSessionEvents, communicationSessionEvents);
  }

  @Test
  void testMappingForListOfFraudDetectionEvents() {

    final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event>
        mappedFraudDetectionEvents =
            this.managementMapper.mapAsList(
                FRAUD_DETECTION_EVENTS,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event.class);

    this.checkEventsMappedToWsSchema(FRAUD_DETECTION_EVENTS, mappedFraudDetectionEvents);

    final List<Event> fraudDetectionEvents =
        this.managementMapper.mapAsList(mappedFraudDetectionEvents, Event.class);

    this.checkEventsMappedFromWsSchema(mappedFraudDetectionEvents, fraudDetectionEvents);
  }

  @Test
  void testMappingForListOfMBusEvents() {

    final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event>
        mappedMBusEvents =
            this.managementMapper.mapAsList(
                M_BUS_EVENTS,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event.class);

    this.checkEventsMappedToWsSchema(M_BUS_EVENTS, mappedMBusEvents);

    final List<Event> mBusEvents = this.managementMapper.mapAsList(mappedMBusEvents, Event.class);

    this.checkEventsMappedFromWsSchema(mappedMBusEvents, mBusEvents);
  }

  @Test
  void testMappingForListOfPowerQualityExtendedEvents() {

    final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event>
        mappedPowerQualityExtendedEvents =
            this.managementMapper.mapAsList(
                POWER_QUALITY_EXTENDED_EVENTS,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event.class);

    this.checkEventsMappedToWsSchema(
        POWER_QUALITY_EXTENDED_EVENTS, mappedPowerQualityExtendedEvents);

    final List<Event> powerQualityExtendedEvents =
        this.managementMapper.mapAsList(mappedPowerQualityExtendedEvents, Event.class);

    this.checkEventsMappedFromWsSchema(
        mappedPowerQualityExtendedEvents, powerQualityExtendedEvents);
  }

  @Test
  void testMappingForListOfStandardEvents() {

    final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event>
        mappedStandardEvents =
            this.managementMapper.mapAsList(
                STANDARD_EVENTS,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event.class);

    this.checkEventsMappedToWsSchema(STANDARD_EVENTS, mappedStandardEvents);

    final List<Event> standardEvents =
        this.managementMapper.mapAsList(mappedStandardEvents, Event.class);

    this.checkEventsMappedFromWsSchema(mappedStandardEvents, standardEvents);
  }

  @Test
  void testMappingForListOfAuxiliaryEvents() {

    final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event>
        mappedAuxiliaryEvents =
            this.managementMapper.mapAsList(
                AUXILIARY_EVENTS,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event.class);

    this.checkEventsMappedToWsSchema(AUXILIARY_EVENTS, mappedAuxiliaryEvents);

    final List<Event> auxiliaryEvents =
        this.managementMapper.mapAsList(mappedAuxiliaryEvents, Event.class);

    this.checkEventsMappedFromWsSchema(mappedAuxiliaryEvents, auxiliaryEvents);
  }

  /** Tests if mapping a List, typed to Event, succeeds if the List is null. */
  @Test
  void testNullListEventMapping() {
    // build test data
    final List<Event> listOriginal = null;

    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(
            () -> {
              // actual mapping
              this.managementMapper.mapAsList(
                  listOriginal,
                  org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event.class);
            });
  }

  private static Event newEvent(
      final ZonedDateTime timestamp,
      final EventType eventType,
      final Integer eventCounter,
      final EventLogCategory eventLogCategory) {
    return new Event(timestamp, eventType, eventCounter, eventLogCategory, Collections.emptyList());
  }
}
