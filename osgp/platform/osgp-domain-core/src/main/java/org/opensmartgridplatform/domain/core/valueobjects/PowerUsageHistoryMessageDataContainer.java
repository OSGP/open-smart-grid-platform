/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;

public class PowerUsageHistoryMessageDataContainer implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -7037893048316285620L;
    private TimePeriod timePeriod;
    private HistoryTermType historyTermType;

    public TimePeriod getTimePeriod() {
        return this.timePeriod;
    }

    public void setTimePeriod(final TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

    public HistoryTermType getHistoryTermType() {
        return this.historyTermType;
    }

    public void setHistoryTermType(final HistoryTermType historyTermType) {
        this.historyTermType = historyTermType;
    }
}
