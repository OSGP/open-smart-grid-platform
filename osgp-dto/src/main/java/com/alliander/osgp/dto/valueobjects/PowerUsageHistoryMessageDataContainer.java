package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

public class PowerUsageHistoryMessageDataContainer implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 3937032014697574838L;

    private TimePeriod timePeriod;
    private HistoryTermType historyTermType;

    public PowerUsageHistoryMessageDataContainer(final TimePeriod timePeriod, final HistoryTermType historyTermType) {
        this.timePeriod = timePeriod;
        this.historyTermType = historyTermType;
    }

    public TimePeriod getTimePeriod() {
        return this.timePeriod;
    }

    public HistoryTermType getHistoryTermType() {
        return this.historyTermType;
    }
}
