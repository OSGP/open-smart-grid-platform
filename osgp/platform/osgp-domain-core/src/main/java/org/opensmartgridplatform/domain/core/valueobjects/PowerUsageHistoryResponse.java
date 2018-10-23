/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import java.util.List;

public class PowerUsageHistoryResponse implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -7131479761250193774L;

    private List<PowerUsageData> powerUsageData;

    public PowerUsageHistoryResponse(final List<PowerUsageData> powerUsageData) {

        if (powerUsageData == null) {
            throw new IllegalArgumentException("PowerUsageData is null.");
        } else {
            this.powerUsageData = powerUsageData;
        }
    }

    public List<PowerUsageData> getPowerUsageData() {
        return this.powerUsageData;
    }
}
