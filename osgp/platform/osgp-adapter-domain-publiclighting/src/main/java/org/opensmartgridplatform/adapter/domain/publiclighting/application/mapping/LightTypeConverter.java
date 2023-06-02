//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.publiclighting.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.LightType;

public class LightTypeConverter
    extends BidirectionalConverter<
        org.opensmartgridplatform.dto.valueobjects.LightTypeDto, LightType> {

  @Override
  public LightType convertTo(
      final org.opensmartgridplatform.dto.valueobjects.LightTypeDto source,
      final Type<LightType> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    return LightType.valueOf(source.toString());
  }

  @Override
  public org.opensmartgridplatform.dto.valueobjects.LightTypeDto convertFrom(
      final LightType source,
      final Type<org.opensmartgridplatform.dto.valueobjects.LightTypeDto> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    return org.opensmartgridplatform.dto.valueobjects.LightTypeDto.valueOf(source.toString());
  }
}
