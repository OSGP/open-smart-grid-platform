//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.tariffswitching.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.ActionTimeType;
import org.opensmartgridplatform.domain.core.valueobjects.LightValue;
import org.opensmartgridplatform.domain.core.valueobjects.ScheduleEntry;
import org.opensmartgridplatform.domain.core.valueobjects.TriggerType;
import org.opensmartgridplatform.domain.core.valueobjects.WeekDayType;
import org.opensmartgridplatform.domain.core.valueobjects.WindowType;

public class ScheduleConverter
    extends BidirectionalConverter<
        org.opensmartgridplatform.dto.valueobjects.ScheduleEntryDto, ScheduleEntry> {

  @Override
  public ScheduleEntry convertTo(
      final org.opensmartgridplatform.dto.valueobjects.ScheduleEntryDto source,
      final Type<ScheduleEntry> destinationType,
      final MappingContext context) {

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
  public org.opensmartgridplatform.dto.valueobjects.ScheduleEntryDto convertFrom(
      final ScheduleEntry source,
      final Type<org.opensmartgridplatform.dto.valueobjects.ScheduleEntryDto> destinationType,
      final MappingContext context) {

    final org.opensmartgridplatform.dto.valueobjects.ScheduleEntryDto schedule =
        new org.opensmartgridplatform.dto.valueobjects.ScheduleEntryDto();
    schedule.setActionTime(
        this.mapperFacade.map(
            source.getActionTime(),
            org.opensmartgridplatform.dto.valueobjects.ActionTimeTypeDto.class));
    schedule.setEndDay(source.getEndDay());
    schedule.setLightValue(
        this.mapperFacade.mapAsList(
            source.getLightValue(),
            org.opensmartgridplatform.dto.valueobjects.LightValueDto.class));
    schedule.setStartDay(source.getStartDay());
    schedule.setTime(source.getTime());
    schedule.setTriggerType(
        this.mapperFacade.map(
            source.getTriggerType(),
            org.opensmartgridplatform.dto.valueobjects.TriggerTypeDto.class));
    schedule.setTriggerWindow(
        this.mapperFacade.map(
            source.getTriggerWindow(),
            org.opensmartgridplatform.dto.valueobjects.WindowTypeDto.class));
    schedule.setWeekDay(
        this.mapperFacade.map(
            source.getWeekDay(), org.opensmartgridplatform.dto.valueobjects.WeekDayTypeDto.class));
    schedule.setIndex(source.getIndex());
    schedule.setIsEnabled(source.getIsEnabled());
    schedule.setMinimumLightsOn(source.getMinimumLightsOn());

    return schedule;
  }
}
