// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.da.measurements.MeasurementType;

public class StringToMeasurementTypeConverter extends CustomConverter<String, MeasurementType> {

  @Override
  public MeasurementType convert(
      final String sourceReason,
      final Type<? extends MeasurementType> destinationReasonType,
      final MappingContext mappingContext) {

    return MeasurementType.forIdentifierName(sourceReason);
  }
}
