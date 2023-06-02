//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.mapping;

import com.google.protobuf.ByteString;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.dto.valueobjects.RelayDataDto;
import org.opensmartgridplatform.oslp.Oslp;

public class RelayDataConverter extends BidirectionalConverter<RelayDataDto, Oslp.RelayData> {

  @Override
  public RelayDataDto convertFrom(
      final Oslp.RelayData source,
      final Type<RelayDataDto> destinationType,
      final MappingContext context) {

    int index = 0;
    if (source.hasIndex()) {
      index = source.getIndex().byteAt(0);
    }

    int totalLightingMinutes = 0;
    if (source.hasTotalLightingMinutes()) {
      totalLightingMinutes = source.getTotalLightingMinutes();
    }

    return new RelayDataDto(index, totalLightingMinutes);
  }

  @Override
  public Oslp.RelayData convertTo(
      final RelayDataDto source,
      final Type<Oslp.RelayData> destinationType,
      final MappingContext context) {

    final ByteString index = this.mapperFacade.map(source.getIndex(), ByteString.class);
    final int totalLightingMinutes = source.getTotalLightingMinutes();

    return Oslp.RelayData.newBuilder()
        .setIndex(index)
        .setTotalLightingMinutes(totalLightingMinutes)
        .build();
  }
}
