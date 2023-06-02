//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
