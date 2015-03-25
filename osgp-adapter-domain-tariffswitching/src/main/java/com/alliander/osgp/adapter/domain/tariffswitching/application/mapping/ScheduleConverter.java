package com.alliander.osgp.adapter.domain.tariffswitching.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.ActionTimeType;
import com.alliander.osgp.domain.core.valueobjects.LightValue;
import com.alliander.osgp.domain.core.valueobjects.Schedule;
import com.alliander.osgp.domain.core.valueobjects.TriggerType;
import com.alliander.osgp.domain.core.valueobjects.WeekDayType;
import com.alliander.osgp.domain.core.valueobjects.WindowType;

public class ScheduleConverter extends BidirectionalConverter<com.alliander.osgp.dto.valueobjects.Schedule, Schedule> {

    @Override
    public Schedule convertTo(final com.alliander.osgp.dto.valueobjects.Schedule source,
            final Type<Schedule> destinationType) {

        final Schedule schedule = new Schedule();
        schedule.setActionTime(this.mapperFacade.map(source.getActionTime(), ActionTimeType.class));
        schedule.setEndDay(source.getEndDay());
        schedule.setLightValue(this.mapperFacade.mapAsList(source.getLightValue(), LightValue.class));
        schedule.setStartDay(source.getStartDay());
        schedule.setTime(source.getTime());
        schedule.setTriggerType(this.mapperFacade.map(source.getTriggerType(), TriggerType.class));
        schedule.setTriggerWindow(this.mapperFacade.map(source.getTriggerWindow(), WindowType.class));
        schedule.setWeekDay(this.mapperFacade.map(source.getWeekDay(), WeekDayType.class));
        return schedule;
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.Schedule convertFrom(final Schedule source,
            final Type<com.alliander.osgp.dto.valueobjects.Schedule> destinationType) {

        final com.alliander.osgp.dto.valueobjects.Schedule schedule = new com.alliander.osgp.dto.valueobjects.Schedule();
        schedule.setActionTime(this.mapperFacade.map(source.getActionTime(),
                com.alliander.osgp.dto.valueobjects.ActionTimeType.class));
        schedule.setEndDay(source.getEndDay());
        schedule.setLightValue(this.mapperFacade.mapAsList(source.getLightValue(),
                com.alliander.osgp.dto.valueobjects.LightValue.class));
        schedule.setStartDay(source.getStartDay());
        schedule.setTime(source.getTime());
        schedule.setTriggerType(this.mapperFacade.map(source.getTriggerType(),
                com.alliander.osgp.dto.valueobjects.TriggerType.class));
        schedule.setTriggerWindow(this.mapperFacade.map(source.getTriggerWindow(),
                com.alliander.osgp.dto.valueobjects.WindowType.class));
        schedule.setWeekDay(this.mapperFacade.map(source.getWeekDay(),
                com.alliander.osgp.dto.valueobjects.WeekDayType.class));
        return schedule;
    }

}
