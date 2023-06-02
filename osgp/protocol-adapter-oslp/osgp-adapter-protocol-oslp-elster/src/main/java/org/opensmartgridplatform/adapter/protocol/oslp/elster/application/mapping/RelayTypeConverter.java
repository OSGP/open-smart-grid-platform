//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.dto.valueobjects.RelayTypeDto;
import org.opensmartgridplatform.oslp.Oslp;

public class RelayTypeConverter extends BidirectionalConverter<RelayTypeDto, Oslp.RelayType> {

  @Override
  public org.opensmartgridplatform.oslp.Oslp.RelayType convertTo(
      final RelayTypeDto source,
      final Type<org.opensmartgridplatform.oslp.Oslp.RelayType> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    return Oslp.RelayType.valueOf(source.toString());
  }

  @Override
  public RelayTypeDto convertFrom(
      final org.opensmartgridplatform.oslp.Oslp.RelayType source,
      final Type<RelayTypeDto> destinationType,
      final MappingContext context) {
    if ((source == null) || (source == Oslp.RelayType.RT_NOT_SET)) {
      return null;
    }

    return RelayTypeDto.valueOf(source.toString());
  }

  @Override
  public boolean canConvert(final Type<?> sourceType, final Type<?> destinationType) {
    return this.sourceType.isAssignableFrom(sourceType)
        && this.destinationType.equals(destinationType);
  }
}
