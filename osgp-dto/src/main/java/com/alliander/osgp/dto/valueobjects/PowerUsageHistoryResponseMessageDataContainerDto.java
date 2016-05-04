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

public class PowerUsageHistoryResponseMessageDataContainerDto implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 105839711150545288L;

    private List<PowerUsageDataDto> powerUsageData;
    private PageInfoDto pageInfo;
    private DateTime startTime;
    private DateTime endTime;
    private HistoryTermTypeDto historyTermType;
    private PowerUsageHistoryMessageDataContainerDto requestContainer;

    public PowerUsageHistoryResponseMessageDataContainerDto(final List<PowerUsageDataDto> powerUsageData) {

        if (powerUsageData == null) {
            throw new IllegalArgumentException("PowerUsageData is null.");
        } else {
            this.powerUsageData = powerUsageData;
        }
    }

    public List<PowerUsageDataDto> getPowerUsageData() {
        return this.powerUsageData;
    }

    public PageInfoDto getPageInfo() {
        return this.pageInfo;
    }

    public void setPageInfo(final PageInfoDto pageInfo) {
        this.pageInfo = pageInfo;
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

    public HistoryTermTypeDto getHistoryTermType() {
        return this.historyTermType;
    }

    public void setHistoryTermType(final HistoryTermTypeDto historyTermType) {
        this.historyTermType = historyTermType;
    }

    public PowerUsageHistoryMessageDataContainerDto getRequestContainer() {
        return this.requestContainer;
    }

    public void setRequestContainer(final PowerUsageHistoryMessageDataContainerDto requestContainer) {
        this.requestContainer = requestContainer;
    }
}
