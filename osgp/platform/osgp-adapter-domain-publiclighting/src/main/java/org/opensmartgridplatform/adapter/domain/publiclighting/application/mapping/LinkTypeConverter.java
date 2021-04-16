/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.LinkType;

public class LinkTypeConverter
    extends BidirectionalConverter<
        org.opensmartgridplatform.dto.valueobjects.LinkTypeDto, LinkType> {

  @Override
  public LinkType convertTo(
      final org.opensmartgridplatform.dto.valueobjects.LinkTypeDto source,
      final Type<LinkType> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    return LinkType.valueOf(source.toString());
  }

  @Override
  public org.opensmartgridplatform.dto.valueobjects.LinkTypeDto convertFrom(
      final LinkType source,
      final Type<org.opensmartgridplatform.dto.valueobjects.LinkTypeDto> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    return org.opensmartgridplatform.dto.valueobjects.LinkTypeDto.valueOf(source.toString());
  }
}
