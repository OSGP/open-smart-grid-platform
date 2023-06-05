// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PowerQualityObject;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PowerQualityValue;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityResponseDto;

@EqualsAndHashCode(callSuper = true)
public class ActualPowerQualityDtoConverter
    extends CustomConverter<ActualPowerQualityResponseDto, ActualPowerQualityResponse> {

  private final MapperFactory mapperFactory;

  public ActualPowerQualityDtoConverter(final MapperFactory mapperFactory) {
    this.mapperFactory = mapperFactory;
  }

  @Override
  public ActualPowerQualityResponse convert(
      final ActualPowerQualityResponseDto source,
      final Type<? extends ActualPowerQualityResponse> destinationType,
      final MappingContext mappingContext) {

    if (source.getActualPowerQualityData() != null) {
      final ActualPowerQualityDataDto responseDataDto = source.getActualPowerQualityData();

      final List<PowerQualityObject> powerQualityObjects =
          new ArrayList<>(
              this.mapperFacade.mapAsList(
                  responseDataDto.getPowerQualityObjects(), PowerQualityObject.class));

      final List<PowerQualityValue> powerQualityValues =
          this.makePowerQualityValues(responseDataDto);

      final ActualPowerQualityData actualPowerQualityData =
          new ActualPowerQualityData(powerQualityObjects, powerQualityValues);
      return new ActualPowerQualityResponse(actualPowerQualityData);
    } else {
      return new ActualPowerQualityResponse(null);
    }
  }

  private List<PowerQualityValue> makePowerQualityValues(
      final ActualPowerQualityDataDto responseDataDto) {

    return responseDataDto.getPowerQualityValues().stream()
        .map(dto -> this.mapperFactory.getMapperFacade().map(dto, PowerQualityValue.class))
        .collect(Collectors.toList());
  }
}
