/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.publiclighting.application.mapping;

import com.alliander.osgp.domain.core.valueobjects.ActionTimeType;
import com.alliander.osgp.domain.core.valueobjects.LightValue;
import com.alliander.osgp.domain.core.valueobjects.ScheduleEntry;
import com.alliander.osgp.domain.core.valueobjects.TriggerType;
import com.alliander.osgp.domain.core.valueobjects.WeekDayType;
import com.alliander.osgp.domain.core.valueobjects.WindowType;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class ScheduleEntryConverter
        extends BidirectionalConverter<com.alliander.osgp.dto.valueobjects.ScheduleEntryDto, ScheduleEntry> {

    @Override
    public ScheduleEntry convertTo(final com.alliander.osgp.dto.valueobjects.ScheduleEntryDto source,
            final Type<ScheduleEntry> destinationType, final MappingContext context) {

        final ScheduleEntry schedule = new ScheduleEntry();
        schedule.setActionTime(this.mapperFacade.map(source.getActionTime(), ActionTimeType.class));
        schedule.setEndDay(source.getEndDay());
        schedule.setLightValue(this.mapperFacade.mapAsList(source.getLightValue(), LightValue.class));
        schedule.setStartDay(source.getStartDay());
        schedule.setTime(source.getTime());
        schedule.setTriggerType(this.mapperFacade.map(source.getTriggerType(), TriggerType.class));
        schedule.setTriggerWindow(this.mapperFacade.map(source.getTriggerWindow(), WindowType.class));
        schedule.setWeekDay(this.mapperFacade.map(source.getWeekDay(), WeekDayType.class));
        schedule.setIndex(source.getIndex());
        schedule.setIsEnabled(source.getIsEnabled());
        schedule.setMinimumLightsOn(source.getMinimumLightsOn());

        return schedule;
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.ScheduleEntryDto convertFrom(final ScheduleEntry source,
            final Type<com.alliander.osgp.dto.valueobjects.ScheduleEntryDto> destinationType, final MappingContext context) {

        final com.alliander.osgp.dto.valueobjects.ScheduleEntryDto schedule = new com.alliander.osgp.dto.valueobjects.ScheduleEntryDto();
        schedule.setActionTime(this.mapperFacade.map(source.getActionTime(),
                com.alliander.osgp.dto.valueobjects.ActionTimeTypeDto.class));
        schedule.setEndDay(source.getEndDay());
        schedule.setLightValue(this.mapperFacade.mapAsList(source.getLightValue(),
                com.alliander.osgp.dto.valueobjects.LightValueDto.class));
        schedule.setStartDay(source.getStartDay());
        schedule.setTime(source.getTime());
        schedule.setTriggerType(this.mapperFacade.map(source.getTriggerType(),
                com.alliander.osgp.dto.valueobjects.TriggerTypeDto.class));
        schedule.setTriggerWindow(this.mapperFacade.map(source.getTriggerWindow(),
                com.alliander.osgp.dto.valueobjects.WindowTypeDto.class));
        schedule.setWeekDay(
                this.mapperFacade.map(source.getWeekDay(), com.alliander.osgp.dto.valueobjects.WeekDayTypeDto.class));
        schedule.setIndex(source.getIndex());
        schedule.setIsEnabled(source.getIsEnabled());
        schedule.setMinimumLightsOn(source.getMinimumLightsOn());

        return schedule;
    }

}
