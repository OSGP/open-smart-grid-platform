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

public class ScheduleMessageDataContainerDto implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 6516779611853805357L;

    private List<ScheduleDto> scheduleList;
    private PageInfoDto pageInfo;

    public ScheduleMessageDataContainerDto(final List<ScheduleDto> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public List<ScheduleDto> getScheduleList() {
        return this.scheduleList;
    }

    public PageInfoDto getPageInfo() {
        return this.pageInfo;
    }

    public void setPageInfo(final PageInfoDto pageInfo) {
        this.pageInfo = pageInfo;
    }
}
