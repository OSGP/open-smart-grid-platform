/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Arrays;
import java.util.Optional;

public enum ConfigurationFlagTypeDto {

    DISCOVER_ON_OPEN_COVER(15, null, false),
    DISCOVER_ON_POWER_ON(14, null, false),
    DYNAMIC_MBUS_ADDRESS(13, null, false),
    PO_ENABLE(12, 12, true),
    HLS_3_ON_P_3_ENABLE(11, null, true),
    HLS_4_ON_P_3_ENABLE(10, null, true),
    HLS_5_ON_P_3_ENABLE(9, 9, true),
    HLS_3_ON_PO_ENABLE(8, null, true),
    HLS_4_ON_PO_ENABLE(7, null, true),
    HLS_5_ON_PO_ENABLE(6, 6, true),
    DIRECT_ATTACH_AT_POWER_ON(null, 5, false),
    HLS_6_ON_P3_ENABLE(null, 4, false),
    HLS_7_ON_P3_ENABLE(null, 3, false),
    HLS_6_ON_P0_ENABLE(null, 2, false),
    HLS_7_ON_P0_ENABLE(null, 1, false);

    private final Integer bitPositionDsmr4;
    private final Integer bitPositionSmr5;
    private final boolean readOnly;

    ConfigurationFlagTypeDto(final Integer bitPositionDsmr4, final Integer bitPositionSmr5, final boolean readOnly) {
        this.bitPositionDsmr4 = bitPositionDsmr4;
        this.bitPositionSmr5 = bitPositionSmr5;
        this.readOnly = readOnly;
    }

    public Integer getBitPositionSmr5() {
        return this.bitPositionSmr5;
    }

    public Integer getBitPositionDsmr4() {
        return this.bitPositionDsmr4;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public static Optional<ConfigurationFlagTypeDto> getSmr5FlagType(final int bitPosition) {
        return Arrays.stream(values()).filter(v -> v.bitPositionSmr5 == bitPosition).findAny();
    }

    public static Optional<ConfigurationFlagTypeDto> getDsmr4FlagType(final int bitPosition) {
        return Arrays.stream(values()).filter(v -> v.bitPositionDsmr4 == bitPosition).findAny();
    }
}