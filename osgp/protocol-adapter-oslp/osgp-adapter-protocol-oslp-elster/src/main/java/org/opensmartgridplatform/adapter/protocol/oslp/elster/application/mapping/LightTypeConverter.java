// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.dto.valueobjects.LightTypeDto;
import org.opensmartgridplatform.oslp.Oslp;

public class LightTypeConverter extends BidirectionalConverter<LightTypeDto, Oslp.LightType> {

  @Override
  public org.opensmartgridplatform.oslp.Oslp.LightType convertTo(
      final LightTypeDto source,
      final Type<org.opensmartgridplatform.oslp.Oslp.LightType> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    return Oslp.LightType.valueOf(source.toString());
  }

  @Override
  public LightTypeDto convertFrom(
      final org.opensmartgridplatform.oslp.Oslp.LightType source,
      final Type<LightTypeDto> destinationType,
      final MappingContext context) {
    if (source == null || source == Oslp.LightType.LT_NOT_SET) {
      return null;
    }

    return LightTypeDto.valueOf(source.toString());
  }
}
