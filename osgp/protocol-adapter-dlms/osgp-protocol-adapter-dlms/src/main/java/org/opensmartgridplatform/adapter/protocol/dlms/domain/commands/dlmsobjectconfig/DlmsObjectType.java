/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import java.util.Arrays;

import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;

// @formatter:off
public enum DlmsObjectType {
    AMR_STATUS (null),
    AMR_STATUS_MBUS (null),
    ALARM_FILTER(null),
    CLOCK (null),
    PUSH_SCHEDULER (null),
    PUSH_SETUP_SCHEDULER (null),
    PUSH_SETUP_ALARM (null),
    EXTERNAL_TRIGGER (null),
    EXTERNAL_TRIGGER_SMS (null),
    EXTERNAL_TRIGGER_CSD (null),
    INTERNAL_TRIGGER_ALARM (null),
    ACTIVE_ENERGY_IMPORT (null),
    ACTIVE_ENERGY_IMPORT_RATE_1 (null),
    ACTIVE_ENERGY_IMPORT_RATE_2 (null),
    ACTIVE_ENERGY_EXPORT (null),
    ACTIVE_ENERGY_EXPORT_RATE_1 (null),
    ACTIVE_ENERGY_EXPORT_RATE_2 (null),
    MBUS_MASTER_VALUE (null),
    INTERVAL_VALUES (PeriodTypeDto.INTERVAL),
    MONTHLY_BILLING_VALUES (PeriodTypeDto.MONTHLY),
    DAILY_LOAD_PROFILE (PeriodTypeDto.DAILY),
    DIRECT_ATTACH(null),
    RANDOMISATION_SETTINGS(null);

    private PeriodTypeDto relatedPeriodType;

    DlmsObjectType(PeriodTypeDto relatedPeriodType) {
        this.relatedPeriodType = relatedPeriodType;
    }

    public static DlmsObjectType getTypeForPeriodType(PeriodTypeDto periodType) throws ProtocolAdapterException {
        return Arrays.stream(values())
                .filter(t -> t.relatedPeriodType == periodType)
                .findFirst()
                .orElseThrow(() -> new ProtocolAdapterException(String.format("periodtype %s not supported", periodType)));
    }
}