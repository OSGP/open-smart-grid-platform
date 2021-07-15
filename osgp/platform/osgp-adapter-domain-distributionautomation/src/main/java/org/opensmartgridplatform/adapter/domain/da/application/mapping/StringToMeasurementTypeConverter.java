/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
