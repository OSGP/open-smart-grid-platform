/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.mapping;

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
