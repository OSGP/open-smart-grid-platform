//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetAllAttributeValuesResponse;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAllAttributeValuesResponseDto;

public class GetAllAttributeValuesResponseConverter
    extends CustomConverter<GetAllAttributeValuesResponseDto, GetAllAttributeValuesResponse> {

  @Override
  public GetAllAttributeValuesResponse convert(
      final GetAllAttributeValuesResponseDto source,
      final Type<? extends GetAllAttributeValuesResponse> destinationType,
      final MappingContext context) {
    return new GetAllAttributeValuesResponse(source.getAttributeValuesData());
  }
}
