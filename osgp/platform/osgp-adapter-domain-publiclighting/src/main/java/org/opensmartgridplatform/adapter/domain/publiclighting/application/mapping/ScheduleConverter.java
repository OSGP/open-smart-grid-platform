// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.publiclighting.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.Schedule;
import org.opensmartgridplatform.domain.core.valueobjects.ScheduleEntry;
import org.opensmartgridplatform.dto.valueobjects.ScheduleDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleEntryDto;

public class ScheduleConverter extends BidirectionalConverter<ScheduleDto, Schedule> {

  @Override
  public Schedule convertTo(
      final ScheduleDto source,
      final Type<Schedule> destinationType,
      final MappingContext context) {

    return new Schedule(
        this.mapperFacade.mapAsList(source.getScheduleList(), ScheduleEntry.class),
        source.getAstronomicalSunriseOffset(),
        source.getAstronomicalSunsetOffset());
  }

  @Override
  public ScheduleDto convertFrom(
      final Schedule source,
      final Type<ScheduleDto> destinationType,
      final MappingContext context) {

    return new ScheduleDto(
        this.mapperFacade.mapAsList(source.getScheduleEntries(), ScheduleEntryDto.class),
        source.getAstronomicalSunriseOffset(),
        source.getAstronomicalSunsetOffset());
  }
}
