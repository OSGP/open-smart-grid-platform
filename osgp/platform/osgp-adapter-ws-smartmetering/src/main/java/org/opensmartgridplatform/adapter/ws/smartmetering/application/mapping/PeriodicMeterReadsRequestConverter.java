// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.time.Instant;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;

public class PeriodicMeterReadsRequestConverter
    extends CustomConverter<
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequest,
        PeriodicMeterReadsQuery> {

  @Override
  public boolean canConvert(final Type<?> sourceType, final Type<?> destinationType) {
    return this.sourceType.isAssignableFrom(sourceType)
        && this.destinationType.equals(destinationType);
  }

  @Override
  public PeriodicMeterReadsQuery convert(
      final PeriodicReadsRequest source,
      final Type<? extends PeriodicMeterReadsQuery> destinationType,
      final MappingContext context) {
    return new PeriodicMeterReadsQuery(
        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodType.valueOf(
            source.getPeriodicReadsRequestData().getPeriodType().name()),
        Instant.ofEpochMilli(
            source
                .getPeriodicReadsRequestData()
                .getBeginDate()
                .toGregorianCalendar()
                .getTime()
                .getTime()),
        Instant.ofEpochMilli(
            source
                .getPeriodicReadsRequestData()
                .getEndDate()
                .toGregorianCalendar()
                .getTime()
                .getTime()),
        source instanceof PeriodicMeterReadsGasRequest,
        "");
  }
}
