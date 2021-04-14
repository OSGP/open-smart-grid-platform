/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PowerQualityValue;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PowerQualityValueDto;

public class PowerQualityValueConverter
    extends CustomConverter<PowerQualityValueDto, PowerQualityValue> {

  @Override
  public PowerQualityValue convert(
      final PowerQualityValueDto source,
      final Type<? extends PowerQualityValue> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }
    return new PowerQualityValue(source.getValue());
  }
}
