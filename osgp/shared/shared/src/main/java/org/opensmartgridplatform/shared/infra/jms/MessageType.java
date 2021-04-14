/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.jms;

public enum MessageType {
  // keep MessageType in alphabetical order.
  ADD_DEVICE,
  ADD_METER,
  CLEAR_ALARM_REGISTER,
  CONFIGURE_DEFINABLE_LOAD_PROFILE,
  CONNECT,
  COUPLE_MBUS_DEVICE,
  COUPLE_MBUS_DEVICE_BY_CHANNEL,
  DECOUPLE_MBUS_DEVICE,
  DECOUPLE_MBUS_DEVICE_BY_CHANNEL,
  DEVICE_REGISTRATION_COMPLETED,
  DISABLE_DEBUGGING,
  ENABLE_DEBUGGING,
  EVENT_NOTIFICATION,
  FIND_EVENTS,
  FIND_SCHEDULED_TASKS,
  GENERATE_AND_REPLACE_KEYS,
  GET_ACTUAL_POWER_QUALITY,
  GET_ADMINISTRATIVE_STATUS,
  GET_ALL_ATTRIBUTE_VALUES,
  GET_ASSOCIATION_LN_OBJECTS,
  GET_CONFIGURATION,
  GET_CONFIGURATION_OBJECT,
  GET_DATA,
  GET_DEVICE_AUTHORIZATION,
  GET_DEVICE_MODEL,
  GET_EVENT_NOTIFICATIONS,
  GET_FIRMWARE_FILE,
  GET_FIRMWARE_VERSION,
  GET_HEALTH_STATUS,
  GET_LIGHT_SENSOR_STATUS,
  GET_LIGHT_STATUS,
  GET_MBUS_ENCRYPTION_KEY_STATUS,
  GET_MBUS_ENCRYPTION_KEY_STATUS_BY_CHANNEL,
  GET_MEASUREMENT_REPORT,
  GET_MESSAGES,
  GET_OUTAGES,
  GET_POWER_QUALITY_VALUES,
  GET_POWER_QUALITY_VALUES_PERIODIC,
  GET_PROFILE_GENERIC_DATA,
  GET_SPECIFIC_ATTRIBUTE_VALUE,
  GET_STATUS,
  GET_SUBSCRIPTION_INFORMATION,
  GET_TARIFF_STATUS,
  HANDLE_BUNDLED_ACTIONS,
  PUSH_NOTIFICATION_ALARM,
  PUSH_NOTIFICATION_SMS,
  READ_ALARM_REGISTER,
  REGISTER_DEVICE,
  RELAY_STATUS_UPDATED_EVENTS,
  REMOVE_DEVICE,
  REPLACE_KEYS,
  REQUEST_ACTUAL_METER_DATA,
  REQUEST_PERIODIC_METER_DATA,
  RESUME_SCHEDULE,
  REVOKE_KEY,
  SCAN_MBUS_CHANNELS,
  SET_ACTIVITY_CALENDAR,
  SET_ADMINISTRATIVE_STATUS,
  SET_ALARM_NOTIFICATIONS,
  SET_CLOCK_CONFIGURATION,
  SET_CONFIGURATION,
  SET_CONFIGURATION_OBJECT,
  SET_DATA,
  SET_DEVICE_ALIASES,
  SET_DEVICE_AUTHORIZATION,
  SET_DEVICE_COMMUNICATION_SETTINGS,
  SET_DEVICE_LIFECYCLE_STATUS,
  SET_DEVICE_LIFECYCLE_STATUS_BY_CHANNEL,
  SET_DEVICE_VERIFICATION_KEY,
  SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER,
  SET_EVENT_NOTIFICATIONS,
  SET_LIGHT,
  SET_LIGHT_MEASUREMENT_DEVICE,
  SET_LIGHT_SCHEDULE,
  SET_MAINTENANCE_STATE,
  SET_MBUS_USER_KEY_BY_CHANNEL,
  SET_PUSH_SETUP_ALARM,
  SET_PUSH_SETUP_SMS,
  SET_RANDOMISATION_SETTINGS,
  SET_REBOOT,
  SET_SPECIAL_DAYS,
  SET_TARIFF_SCHEDULE,
  SET_TRANSITION,
  START_SELF_TEST,
  STOP_SELF_TEST,
  SWITCH_CONFIGURATION_BANK,
  SWITCH_FIRMWARE,
  SYNCHRONIZE_TIME,
  UPDATE_DEVICE_CDMA_SETTINGS,
  UPDATE_DEVICE_SSL_CERTIFICATION,
  UPDATE_FIRMWARE,
  UPDATE_KEY
}
