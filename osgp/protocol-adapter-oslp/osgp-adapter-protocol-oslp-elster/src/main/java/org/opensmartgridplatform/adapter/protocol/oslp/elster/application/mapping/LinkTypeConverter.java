// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.dto.valueobjects.LinkTypeDto;
import org.opensmartgridplatform.oslp.Oslp;

public class LinkTypeConverter extends BidirectionalConverter<LinkTypeDto, Oslp.LinkType> {

  @Override
  public org.opensmartgridplatform.oslp.Oslp.LinkType convertTo(
      final LinkTypeDto source,
      final Type<org.opensmartgridplatform.oslp.Oslp.LinkType> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    return Oslp.LinkType.valueOf(source.toString());
  }

  @Override
  public LinkTypeDto convertFrom(
      final org.opensmartgridplatform.oslp.Oslp.LinkType source,
      final Type<LinkTypeDto> destinationType,
      final MappingContext context) {
    if (source == null || source == Oslp.LinkType.LINK_NOT_SET) {
      return null;
    }

    return LinkTypeDto.valueOf(source.toString());
  }
}
