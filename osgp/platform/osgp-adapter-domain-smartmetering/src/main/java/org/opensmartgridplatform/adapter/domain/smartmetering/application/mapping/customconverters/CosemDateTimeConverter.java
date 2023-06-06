// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import java.util.Objects;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClockStatus;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDate;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDateTime;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemTime;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClockStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto;

public class CosemDateTimeConverter
    extends BidirectionalConverter<CosemDateTimeDto, CosemDateTime> {

  private final ConfigurationMapper mapper;

  public CosemDateTimeConverter() {
    this.mapper = new ConfigurationMapper();
  }

  public CosemDateTimeConverter(final ConfigurationMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof CosemDateTimeConverter)) {
      return false;
    }
    if (!super.equals(other)) {
      return false;
    }
    final CosemDateTimeConverter o = (CosemDateTimeConverter) other;
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
  public CosemDateTime convertTo(
      final CosemDateTimeDto source,
      final Type<CosemDateTime> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final ClockStatus clockStatus = new ClockStatus(source.getClockStatus().getStatus());

    return new CosemDateTime(
        this.mapper.map(source.getDate(), CosemDate.class),
        this.mapper.map(source.getTime(), CosemTime.class),
        source.getDeviation(),
        clockStatus);
  }

  @Override
  public CosemDateTimeDto convertFrom(
      final CosemDateTime source,
      final Type<CosemDateTimeDto> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final ClockStatusDto clockStatus = new ClockStatusDto(source.getClockStatus().getStatus());

    return new CosemDateTimeDto(
        this.mapper.map(source.getDate(), CosemDateDto.class),
        this.mapper.map(source.getTime(), CosemTimeDto.class),
        source.getDeviation(),
        clockStatus);
  }
}
