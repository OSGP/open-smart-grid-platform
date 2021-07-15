/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.List;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PowerQualityObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PowerQualityObjects;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PowerQualityValue;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PowerQualityValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityData;

public class ActualPowerQualityResponseDataConverter
    extends CustomConverter<
        ActualPowerQualityData,
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .ActualPowerQualityData> {

  @Override
  public org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityData
      convert(
          final ActualPowerQualityData source,
          final Type<
                  ? extends
                      org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                          .ActualPowerQualityData>
              destinationType,
          final MappingContext mappingContext) {
    if (source == null) {
      return null;
    }

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .ActualPowerQualityData
        result =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                .ActualPowerQualityData();

    final PowerQualityObjects powerQualityObjects = new PowerQualityObjects();
    powerQualityObjects
        .getPowerQualityObject()
        .addAll(
            this.mapperFacade.mapAsList(source.getPowerQualityObjects(), PowerQualityObject.class));
    result.setPowerQualityObjects(powerQualityObjects);

    final PowerQualityValues powerQualityValues = new PowerQualityValues();
    powerQualityValues.getPowerQualityValue().addAll(this.mapPowerQualityValues(source));
    result.setPowerQualityValues(powerQualityValues);

    return result;
  }

  private List<PowerQualityValue> mapPowerQualityValues(final ActualPowerQualityData source) {
    final List<PowerQualityValue> result = new ArrayList<>();
    for (final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PowerQualityValue
        powerQualityValueVo : source.getPowerQualityValues()) {
      result.add(this.mapperFacade.map(powerQualityValueVo, PowerQualityValue.class));
    }
    return result;
  }
}
