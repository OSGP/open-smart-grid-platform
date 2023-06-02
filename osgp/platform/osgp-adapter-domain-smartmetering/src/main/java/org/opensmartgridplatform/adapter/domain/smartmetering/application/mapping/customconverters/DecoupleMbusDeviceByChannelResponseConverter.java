//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceResponseDto;
import org.springframework.stereotype.Component;

@Component
public class DecoupleMbusDeviceByChannelResponseConverter
    extends CustomConverter<DecoupleMbusDeviceResponseDto, DecoupleMbusDeviceByChannelResponse> {

  @Override
  public DecoupleMbusDeviceByChannelResponse convert(
      final DecoupleMbusDeviceResponseDto source,
      final Type<? extends DecoupleMbusDeviceByChannelResponse> type,
      final MappingContext mappingContext) {

    if (source == null) {
      return null;
    }

    return new DecoupleMbusDeviceByChannelResponse(
        source.getMbusDeviceIdentification(), source.getChannelElementValues().getChannel());
  }
}
