/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import java.util.Arrays;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;

public enum DlmsObjectType {
  AMR_STATUS(null),
  AMR_STATUS_MBUS(null),
  ALARM_FILTER(null),
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
  INTERVAL_VALUES(PeriodTypeDto.INTERVAL),
  MONTHLY_BILLING_VALUES(PeriodTypeDto.MONTHLY),
  DAILY_LOAD_PROFILE(PeriodTypeDto.DAILY),
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

  STANDARD_EVENT_LOG(null),
  FRAUD_DETECTION_EVENT_LOG(null),
  COMMUNICATION_SESSIONS_EVENT_LOG(null),
  MBUS_EVENT_LOG(null),
  POWER_QUALITY_EVENT_LOG(null),
  AUXILIARY_EVENT_LOG(null);

  private final PeriodTypeDto relatedPeriodType;

  DlmsObjectType(final PeriodTypeDto relatedPeriodType) {
    this.relatedPeriodType = relatedPeriodType;
  }

  public static DlmsObjectType getTypeForPeriodType(final PeriodTypeDto periodType)
      throws ProtocolAdapterException {
    return Arrays.stream(values())
        .filter(t -> t.relatedPeriodType == periodType)
        .findFirst()
        .orElseThrow(
            () ->
                new ProtocolAdapterException(
                    String.format("periodType %s not supported", periodType)));
  }
}
