// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AdministrativeStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AdministrativeStatusTypeResponse;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeResponseDto;

public class AdministrativeStatusResponseConverter
    extends CustomConverter<AdministrativeStatusTypeResponseDto, AdministrativeStatusTypeResponse> {

  @Override
  public AdministrativeStatusTypeResponse convert(
      final AdministrativeStatusTypeResponseDto source,
      final Type<? extends AdministrativeStatusTypeResponse> destinationType,
      final MappingContext context) {

    if (source == null) {
      return null;
    }

    return new AdministrativeStatusTypeResponse(
        this.mapperFacade.map(
            source.getAdministrativeStatusTypeDto(), AdministrativeStatusType.class));
  }
}
