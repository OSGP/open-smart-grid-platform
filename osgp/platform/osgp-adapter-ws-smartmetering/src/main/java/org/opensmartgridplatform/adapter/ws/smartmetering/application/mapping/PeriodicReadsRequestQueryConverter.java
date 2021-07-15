/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestQuery;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;

public class PeriodicReadsRequestQueryConverter
    extends CustomConverter<
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .PeriodicReadsRequestQuery,
        PeriodicMeterReadsQuery> {

  @Override
  public PeriodicMeterReadsQuery convert(
      final PeriodicReadsRequestQuery source,
      final Type<? extends PeriodicMeterReadsQuery> destinationType,
      final MappingContext context) {
    return new PeriodicMeterReadsQuery(
        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodType.valueOf(
            source.getPeriodicReadsRequestData().getPeriodType().name()),
        source.getPeriodicReadsRequestData().getBeginDate().toGregorianCalendar().getTime(),
        source.getPeriodicReadsRequestData().getEndDate().toGregorianCalendar().getTime(),
        false,
        "");
  }
}
