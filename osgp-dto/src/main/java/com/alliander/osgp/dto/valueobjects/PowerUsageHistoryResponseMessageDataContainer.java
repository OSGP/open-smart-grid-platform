/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;
import java.util.List;

import org.joda.time.DateTime;

public class PowerUsageHistoryResponseMessageDataContainer implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 105839711150545288L;

    private List<PowerUsageData> powerUsageData;
    private int itemCount;
    private int numberOfPages;
    private int currentPage;
    private DateTime startTime;
    private DateTime endTime;
    private HistoryTermType historyTermType;

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

    public int getItemCount() {
        return this.itemCount;
    }

    public void setItemCount(final int itemCount) {
        this.itemCount = itemCount;
    }

    public int getNumberOfPages() {
        return this.numberOfPages;
    }

    public void setNumberOfPages(final int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(final int currentPage) {
        this.currentPage = currentPage;
    }

    public DateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(final DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(final DateTime endTime) {
        this.endTime = endTime;
    }

    public HistoryTermType getHistoryTermType() {
        return this.historyTermType;
    }

    public void setHistoryTermType(final HistoryTermType historyTermType) {
        this.historyTermType = historyTermType;
    }
}
