// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
        source.getPeriodicReadsRequestData().getBeginDate().toGregorianCalendar().toInstant(),
        source.getPeriodicReadsRequestData().getEndDate().toGregorianCalendar().toInstant(),
        false,
        "");
  }
}
