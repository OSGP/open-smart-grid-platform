/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.ArrayList;
import java.util.List;

public class ProfileGenericDataResponseDto extends ActionResponseDto {

    private static final long serialVersionUID = -156966569210717654L;

    private final List<ProfileGenericDataResponseItemDto> items;
    private final PeriodTypeDto periodType;

    public ProfileGenericDataResponseDto(final PeriodTypeDto periodType,
            final List<ProfileGenericDataResponseItemDto> responseItems) {
        this.items = new ArrayList<ProfileGenericDataResponseItemDto>(responseItems);
        this.periodType = periodType;
    }

    public List<ProfileGenericDataResponseItemDto> getItems() {
        return new ArrayList<ProfileGenericDataResponseItemDto>(this.items);
    }

    public PeriodTypeDto getPeriodType() {
        return this.periodType;
    }

}
