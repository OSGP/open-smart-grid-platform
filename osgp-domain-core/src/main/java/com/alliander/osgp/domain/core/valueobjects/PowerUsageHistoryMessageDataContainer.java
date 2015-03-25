package com.alliander.osgp.domain.core.valueobjects;

import java.io.Serializable;

public class PowerUsageHistoryMessageDataContainer implements Serializable {

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
