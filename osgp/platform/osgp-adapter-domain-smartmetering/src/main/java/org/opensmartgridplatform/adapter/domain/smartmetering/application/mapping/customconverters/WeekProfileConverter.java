// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import java.util.Objects;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DayProfile;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.WeekProfile;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DayProfileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.WeekProfileDto;

public class WeekProfileConverter extends BidirectionalConverter<WeekProfileDto, WeekProfile> {

  private final ConfigurationMapper mapper;

  public WeekProfileConverter() {
    this.mapper = new ConfigurationMapper();
  }

  public WeekProfileConverter(final ConfigurationMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof WeekProfileConverter)) {
      return false;
    }
    if (!super.equals(other)) {
      return false;
    }
    final WeekProfileConverter o = (WeekProfileConverter) other;
    if (this.mapper == null) {
      return o.mapper == null;
    }
    return this.mapper.getClass().equals(o.mapper.getClass());
  }

  @Override
  public int hashCode() {
    return super.hashCode() + Objects.hashCode(this.mapper);
  }

  @Override
  public WeekProfile convertTo(
      final WeekProfileDto source,
      final Type<WeekProfile> destinationType,
      final MappingContext context) {

    if (source == null) {
      return null;
    }

    return WeekProfile.newBuilder()
        .withWeekProfileName(source.getWeekProfileName())
        .withMonday(this.convertDayProfileTo(source.getMonday()))
        .withTuesday(this.convertDayProfileTo(source.getTuesday()))
        .withWednesday(this.convertDayProfileTo(source.getWednesday()))
        .withThursday(this.convertDayProfileTo(source.getThursday()))
        .withFriday(this.convertDayProfileTo(source.getFriday()))
        .withSaturday(this.convertDayProfileTo(source.getSaturday()))
        .withSunday(this.convertDayProfileTo(source.getSunday()))
        .build();
  }

  private DayProfile convertDayProfileTo(final DayProfileDto dayProfile) {
    return this.mapper.map(dayProfile, DayProfile.class);
  }

  @Override
  public WeekProfileDto convertFrom(
      final WeekProfile source,
      final Type<WeekProfileDto> destinationType,
      final MappingContext context) {

    if (source == null) {
      return null;
    }

    return WeekProfileDto.newBuilder()
        .withWeekProfileName(source.getWeekProfileName())
        .withMonday(this.convertDayProfileFrom(source.getMonday()))
        .withTuesday(this.convertDayProfileFrom(source.getTuesday()))
        .withWednesday(this.convertDayProfileFrom(source.getWednesday()))
        .withThursday(this.convertDayProfileFrom(source.getThursday()))
        .withFriday(this.convertDayProfileFrom(source.getFriday()))
        .withSaturday(this.convertDayProfileFrom(source.getSaturday()))
        .withSunday(this.convertDayProfileFrom(source.getSunday()))
        .build();
  }

  private DayProfileDto convertDayProfileFrom(final DayProfile dayProfile) {
    return this.mapper.map(dayProfile, DayProfileDto.class);
  }
}
