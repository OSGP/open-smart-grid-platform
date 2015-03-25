package com.alliander.osgp.domain.core.valueobjects;

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
