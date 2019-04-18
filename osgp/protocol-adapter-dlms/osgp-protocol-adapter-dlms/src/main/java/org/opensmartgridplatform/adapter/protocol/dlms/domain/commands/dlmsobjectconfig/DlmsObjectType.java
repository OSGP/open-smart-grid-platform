/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

// @formatter:off
public enum DlmsObjectType {
    AMR_STATUS,
    AMR_STATUS_MBUS,
    CLOCK,
    ACTIVE_ENERGY_IMPORT,
    ACTIVE_ENERGY_IMPORT_RATE_1,
    ACTIVE_ENERGY_IMPORT_RATE_2,
    ACTIVE_ENERGY_EXPORT,
    ACTIVE_ENERGY_EXPORT_RATE_1,
    ACTIVE_ENERGY_EXPORT_RATE_2,
    MBUS_MASTER_VALUE,
    INTERVAL_VALUES,
    MONTHLY_BILLING_VALUES,
    DAILY_LOAD_PROFILE
}