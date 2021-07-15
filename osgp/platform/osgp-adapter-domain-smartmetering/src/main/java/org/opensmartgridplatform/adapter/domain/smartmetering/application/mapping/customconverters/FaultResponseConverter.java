/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import java.util.ArrayList;
import java.util.List;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FaultResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FaultResponseParameter;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FaultResponseParameters;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseParameterDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseParametersDto;

public class FaultResponseConverter
    extends BidirectionalConverter<FaultResponseDto, FaultResponse> {

  @Override
  public FaultResponse convertTo(
      final FaultResponseDto source,
      final Type<FaultResponse> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final FaultResponseParameters faultResponseParameters =
        new FaultResponseParameters(
            this.createFaultResponseParameterList(source.getFaultResponseParameters()));

    return new FaultResponse.Builder()
        .withCode(source.getCode())
        .withMessage(source.getMessage())
        .withComponent(source.getComponent())
        .withInnerException(source.getInnerException())
        .withInnerMessage(source.getInnerMessage())
        .withFaultResponseParameters(faultResponseParameters)
        .build();
  }

  @Override
  public FaultResponseDto convertFrom(
      final FaultResponse source,
      final Type<FaultResponseDto> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final FaultResponseParametersDto faultResponseParameters =
        new FaultResponseParametersDto(
            this.createFaultResponseParameterList(source.getFaultResponseParameters()));

    return new FaultResponseDto.Builder()
        .withCode(source.getCode())
        .withMessage(source.getMessage())
        .withComponent(source.getComponent())
        .withInnerException(source.getInnerException())
        .withInnerMessage(source.getInnerMessage())
        .withFaultResponseParameters(faultResponseParameters)
        .build();
  }

  private List<FaultResponseParameter> createFaultResponseParameterList(
      final FaultResponseParametersDto faultResponseParametersDto) {

    final List<FaultResponseParameter> faultResponseParameterList = new ArrayList<>();

    for (final FaultResponseParameterDto parameterDto :
        faultResponseParametersDto.getParameterList()) {
      final String key = parameterDto.getKey();
      final String value = parameterDto.getValue();
      final FaultResponseParameter parameter = new FaultResponseParameter(key, value);
      faultResponseParameterList.add(parameter);
    }

    return faultResponseParameterList;
  }

  private List<FaultResponseParameterDto> createFaultResponseParameterList(
      final FaultResponseParameters faultResponseParameters) {

    final List<FaultResponseParameterDto> faultResponseParameterList = new ArrayList<>();

    for (final FaultResponseParameter parameter : faultResponseParameters.getParameterList()) {
      final String key = parameter.getKey();
      final String value = parameter.getValue();
      final FaultResponseParameterDto parameterDto = new FaultResponseParameterDto(key, value);
      faultResponseParameterList.add(parameterDto);
    }

    return faultResponseParameterList;
  }
}
