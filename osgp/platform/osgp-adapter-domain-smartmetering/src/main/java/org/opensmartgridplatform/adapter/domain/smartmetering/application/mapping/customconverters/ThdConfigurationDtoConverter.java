// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ThdConfiguration;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ThdConfigurationDto;

public class ThdConfigurationDtoConverter
    extends CustomConverter<ThdConfiguration, ThdConfigurationDto> {

  @Override
  public ThdConfigurationDto convert(
      final ThdConfiguration source,
      final Type<? extends ThdConfigurationDto> destinationType,
      final MappingContext context) {

    if (source == null) {
      return null;
    }

    return new ThdConfigurationDto.Builder()
        .withTimeThreshold(source.getTimeThreshold())
        .withValueThreshold(source.getValueThreshold())
        .withMinDurationNormalToOver(source.getMinDurationNormalToOver())
        .withMinDurationOverToNormal(source.getMinDurationOverToNormal())
        .withValueHysteresis(source.getValueHysteresis())
        .build();
  }
}
