/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.converters;

import org.opensmartgridplatform.domain.core.valueobjects.LightValue;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

/**
 * If the value isOn is true, map all fields of the LightValue. If the value
 * isOn is false, only map isOn and index, don't map dimValue.
 */
public class LightValueConverter extends
        CustomConverter<LightValue, org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightValue> {

    @Override
    public org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightValue convert(
            final LightValue source,
            final Type<? extends org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightValue> destinationType,
            final MappingContext mappingContext) {

        if (source == null) {
            return null;
        }

        final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightValue lv = new org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightValue();
        lv.setIndex(source.getIndex());
        lv.setOn(source.isOn());
        if (source.isOn()) {
            lv.setDimValue(source.getDimValue());
        }

        return lv;
    }

}
