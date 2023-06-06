// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.core.application.mapping;

import java.util.Map;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.DaliConfiguration;

public class DaliConfigurationConverter
    extends BidirectionalConverter<
        org.opensmartgridplatform.dto.valueobjects.DaliConfigurationDto, DaliConfiguration> {

  @Override
  public DaliConfiguration convertTo(
      final org.opensmartgridplatform.dto.valueobjects.DaliConfigurationDto source,
      final Type<DaliConfiguration> destinationType,
      final MappingContext context) {

    final Integer numberOfLights = source.getNumberOfLights();
    final Map<Integer, Integer> indexAddressMap = source.getIndexAddressMap();

    return new DaliConfiguration(numberOfLights, indexAddressMap);
  }

  @Override
  public org.opensmartgridplatform.dto.valueobjects.DaliConfigurationDto convertFrom(
      final DaliConfiguration source,
      final Type<org.opensmartgridplatform.dto.valueobjects.DaliConfigurationDto> destinationType,
      final MappingContext context) {

    final Integer numberOfLights = source.getNumberOfLights();
    final Map<Integer, Integer> indexAddressMap = source.getIndexAddressMap();

    return new org.opensmartgridplatform.dto.valueobjects.DaliConfigurationDto(
        numberOfLights, indexAddressMap);
  }
}
