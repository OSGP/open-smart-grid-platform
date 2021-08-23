/*
 * Copyright 2021 Alliander N.V.
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.addSetting;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EventLogCategory;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EventType;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;

public class FindAuxillaryEventsReads extends AbstractFindEventsReads {
  private static final List<EventType> allowed =
      Collections.unmodifiableList(
          Arrays.asList(
              EventType.AUXILIARY_EVENTLOG_CLEARED,
              EventType.MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_1,
              EventType.MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_1,
              EventType.MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_1,
              EventType.MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_1,
              EventType.MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_1,
              EventType.MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_1,
              EventType.MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_1,
              EventType.MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_1,
              EventType.MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_2,
              EventType.MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_2,
              EventType.MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_2,
              EventType.MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_2,
              EventType.MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_2,
              EventType.MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_2,
              EventType.MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_2,
              EventType.MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_2,
              EventType.MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_3,
              EventType.MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_3,
              EventType.MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_3,
              EventType.MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_3,
              EventType.MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_3,
              EventType.MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_3,
              EventType.MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_3,
              EventType.MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_3,
              EventType.MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_4,
              EventType.MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_4,
              EventType.MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_4,
              EventType.MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_4,
              EventType.MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_4,
              EventType.MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_4,
              EventType.MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_4,
              EventType.MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_15_RESERVED_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_18_RESERVED_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_1,
              EventType.KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_1,
              EventType.KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_1,
              EventType.MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_15_RESERVED_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_18_RESERVED_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_2,
              EventType.KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_2,
              EventType.KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_2,
              EventType.MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_15_RESERVED_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_18_RESERVED_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_3,
              EventType.KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_3,
              EventType.KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_3,
              EventType.MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_15_RESERVED_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_18_RESERVED_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_4,
              EventType.MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_4,
              EventType.KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_4,
              EventType.KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_4));

  @Override
  protected String getEventLogCategory() {
    return EventLogCategory.AUXILIARY_EVENT_LOG.name();
  }

  @When("^receiving a find auxiliary events request$")
  @Override
  public void receivingAFindEventsRequest(final Map<String, String> requestData) throws Throwable {
    super.receivingAFindEventsRequest(requestData);
  }

  @Then("^auxiliary events should be returned$")
  @Override
  public void eventsShouldBeReturned(final Map<String, String> settings) throws Throwable {
    super.eventsShouldBeReturned(
        addSetting(settings, PlatformKeys.KEY_EVENTS_NODELIST_EXPECTED, "true"));
  }

  @Then("^auxiliary events for all types should be returned$")
  public void auxiliaryEventsForAllTypesShouldBeReturned(final Map<String, String> settings)
      throws Throwable {
    super.eventsForAllTypesShouldBeReturned(settings);
  }

  @Then("^(\\d++) auxiliary events should be returned$")
  public void numberOfEventsShouldBeReturned(
      final int numberOfEvents, final Map<String, String> settings) throws Throwable {
    super.eventsShouldBeReturned(numberOfEvents, settings);
  }

  @Override
  protected List<EventType> getAllowedEventTypes() {
    return allowed;
  }
}
