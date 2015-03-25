package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;
import java.util.List;

public class PowerUsageHistoryResponseMessageDataContainer implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 105839711150545288L;

    private List<PowerUsageData> powerUsageData;

    public PowerUsageHistoryResponseMessageDataContainer(final List<PowerUsageData> powerUsageData) {

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
