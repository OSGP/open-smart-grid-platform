/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObisCode;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObisCodeDto;

public class CosemObisCodeConverter
    extends BidirectionalConverter<CosemObisCodeDto, CosemObisCode> {

  @Override
  public CosemObisCodeDto convertFrom(
      final CosemObisCode source,
      final Type<CosemObisCodeDto> destinationType,
      final MappingContext mappingContext) {
    if (source == null) {
      return null;
    }

    return new CosemObisCodeDto(source.toIntArray());
  }

  @Override
  public CosemObisCode convertTo(
      final CosemObisCodeDto source,
      final Type<CosemObisCode> destinationType,
      final MappingContext mappingContext) {
    if (source == null) {
      return null;
    }

    return new CosemObisCode(source.toIntArray());
  }
}
