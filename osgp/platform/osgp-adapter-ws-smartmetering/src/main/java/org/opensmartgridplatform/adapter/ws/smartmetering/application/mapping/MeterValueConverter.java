// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.MeterValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpMeterValue;

public class MeterValueConverter extends CustomConverter<OsgpMeterValue, MeterValue> {

  @Override
  public MeterValue convert(
      final OsgpMeterValue source,
      final Type<? extends MeterValue> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }
    final MeterValue m = new MeterValue();
    m.setValue(source.getValue());
    m.setUnit(OsgpUnitType.fromValue(source.getOsgpUnit().name()));
    return m;
  }
}
