/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.List;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObjects;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualValue;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityData;

public class ActualPowerQualityResponseDataConverter extends
        CustomConverter<ActualPowerQualityData,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityData> {

    @Override
    public org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityData convert(
            final ActualPowerQualityData source,
            final Type<?
                    extends org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityData> destinationType,
            final MappingContext mappingContext) {

        final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityData result =
                new org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityData();

        final CaptureObjects captureObjects = new CaptureObjects();
        captureObjects.getCaptureObjects()
                      .addAll(this.mapperFacade.mapAsList(source.getCaptureObjects(), CaptureObject.class));
        result.setCaptureObjects(captureObjects);

        final ActualValues actualValues = new ActualValues();
        actualValues.getActualValue().addAll(this.mapActualValues(source));
        result.setActualValues(actualValues);

        return result;
    }

    private List<ActualValue> mapActualValues(final ActualPowerQualityData source) {
        final List<ActualValue> result = new ArrayList<>();
        for (final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualValue actualValueVo : source
                .getActualValues()) {
            result.add(this.mapperFacade.map(actualValueVo, ActualValue.class));
        }
        return result;
    }

}
