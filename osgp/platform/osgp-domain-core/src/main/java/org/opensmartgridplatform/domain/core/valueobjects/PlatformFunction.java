/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

public enum PlatformFunction {
  CHANGE_DEVICE_MODEL,
  CHANGE_FIRMWARE,
  CHANGE_MANUFACTURER,
  CHANGE_ORGANISATION,
  CREATE_DEVICE_MODEL,
  CREATE_FIRMWARE,
  CREATE_MANUFACTURER,
  CREATE_ORGANISATION,
  FIND_DEVICES,
  FIND_SCHEDULED_TASKS,
  GET_DEVICE_MODELS,
  GET_DEVICE_NO_OWNER,
  GET_FIRMWARE,
  GET_MANUFACTURERS,
  GET_MESSAGES,
  GET_ORGANISATIONS,
  GET_PROTOCOL_INFOS,
  REMOVE_DEVICE_MODEL,
  REMOVE_FIRMWARE,
  REMOVE_MANUFACTURER,
  REMOVE_ORGANISATION,
  REVOKE_KEY,
  SCHEDULE_TEST_ALARM,
  SET_OWNER,
  SET_RANDOMISATION_SETTINGS,
  UPDATE_DEVICE_PROTOCOL,
  UPDATE_KEY
  // add new PlatformFunction in alphabetical order.
}
