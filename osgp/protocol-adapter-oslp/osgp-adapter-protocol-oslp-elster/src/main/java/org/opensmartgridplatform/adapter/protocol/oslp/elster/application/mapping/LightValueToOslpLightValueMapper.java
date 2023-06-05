// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.mapping;

import com.google.protobuf.ByteString;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.dto.valueobjects.LightValueDto;
import org.opensmartgridplatform.oslp.Oslp;

public class LightValueToOslpLightValueMapper
    extends BidirectionalConverter<LightValueDto, Oslp.LightValue> {

  @Override
  public LightValueDto convertFrom(
      final Oslp.LightValue source,
      final Type<LightValueDto> destinationType,
      final MappingContext context) {
    int index = 0;
    if (source.hasIndex()) {
      index = source.getIndex().byteAt(0);
    }

    Integer dimValue = null;
    if (source.hasDimValue() && source.hasOn()) {
      dimValue = (int) source.getDimValue().byteAt(0);
    }

    return new LightValueDto(index, source.hasOn(), dimValue);
  }

  @Override
  public Oslp.LightValue convertTo(
      final LightValueDto source,
      final Type<Oslp.LightValue> desitnationType,
      final MappingContext context) {
    final Oslp.LightValue.Builder builder = Oslp.LightValue.newBuilder();
    builder.setIndex(this.mapperFacade.map(source.getIndex(), ByteString.class));
    builder.setDimValue(this.mapperFacade.map(source.getDimValue(), ByteString.class));
    builder.setOn(source.isOn());

    return builder.build();
  }
}
