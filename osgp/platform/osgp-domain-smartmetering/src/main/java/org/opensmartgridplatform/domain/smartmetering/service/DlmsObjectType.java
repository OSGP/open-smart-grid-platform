/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.smartmetering.service;

import java.util.Arrays;

public enum DlmsObjectType {
  AMR_STATUS(null),
  AMR_STATUS_MBUS(null),
  ALARM_FILTER_1(null),
  ALARM_FILTER_2(null),
  CLOCK(null),
  PUSH_SCHEDULER(null),
  PUSH_SETUP_SCHEDULER(null),
  PUSH_SETUP_ALARM(null),
  EXTERNAL_TRIGGER(null),
  EXTERNAL_TRIGGER_SMS(null),
  EXTERNAL_TRIGGER_CSD(null),
  INTERNAL_TRIGGER_ALARM(null),
  ACTIVE_ENERGY_IMPORT(null),
  ACTIVE_ENERGY_IMPORT_RATE_1(null),
  ACTIVE_ENERGY_IMPORT_RATE_2(null),
  ACTIVE_ENERGY_EXPORT(null),
  ACTIVE_ENERGY_EXPORT_RATE_1(null),
  ACTIVE_ENERGY_EXPORT_RATE_2(null),
  MBUS_MASTER_VALUE(null),
  MBUS_CLIENT_SETUP(null),
  INTERVAL_VALUES(PeriodType.INTERVAL),
  MONTHLY_BILLING_VALUES(PeriodType.MONTHLY),
  DAILY_LOAD_PROFILE(PeriodType.DAILY),
  DIRECT_ATTACH(null),
  RANDOMISATION_SETTINGS(null),
  GSM_DIAGNOSTIC(null),
  READ_MBUS_STATUS(null),
  CLEAR_MBUS_STATUS(null),
  CLIENT_SETUP_MBUS(null),
  ALARM_REGISTER_1(null),
  ALARM_REGISTER_2(null),

  STANDARD_EVENT_CODE(null),
  FRAUD_DETECTION_EVENT_CODE(null),
  MBUS_EVENT_CODE(null),
  COMMUNICATION_SESSIONS_EVENT_CODE(null),
  POWER_QUALITY_EVENT_CODE(null),
  AUXILIARY_EVENT_CODE(null),
  POWER_QUALITY_EXTENDED_EVENT_CODE(null),
  POWER_QUALITY_EXTENDED_EVENT_CODE_MAGNITUDE(null),
  POWER_QUALITY_EXTENDED_EVENT_CODE_DURATION(null),

  STANDARD_EVENT_LOG(null),
  FRAUD_DETECTION_EVENT_LOG(null),
  COMMUNICATION_SESSIONS_EVENT_LOG(null),
  MBUS_EVENT_LOG(null),
  POWER_QUALITY_EVENT_LOG(null),
  AUXILIARY_EVENT_LOG(null),
  POWER_QUALITY_EXTENDED_EVENT_LOG(null);

  private final PeriodType relatedPeriodType;

  public String value() {
    return this.name();
  }

  public static DlmsObjectType fromValue(final String v) {
    return valueOf(v);
  }

  DlmsObjectType(final PeriodType relatedPeriodType) {
    this.relatedPeriodType = relatedPeriodType;
  }

  public static DlmsObjectType getTypeForPeriodType(final PeriodType periodType)
      throws IllegalArgumentException {
    return Arrays.stream(values())
        .filter(t -> t.relatedPeriodType == periodType)
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    String.format("periodType %s not supported", periodType)));
  }
}
