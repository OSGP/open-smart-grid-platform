/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import java.math.BigDecimal;
import java.util.Date;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualValue;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualValueDto;

public class ActualValueConverter extends CustomConverter<ActualValueDto, ActualValue> {

    @Override
    public ActualValue convert(final ActualValueDto source,
            final Type<? extends ActualValue> destinationType, final MappingContext context) {
        final Object value = source.getValue();
        if (value != null) {
            if (value instanceof Long) {
                return new ActualValue((Long) value);
            } else if (value instanceof Date) {
                return new ActualValue((Date) value);
            } else if (value instanceof BigDecimal) {
                return new ActualValue((BigDecimal) value);
            } else {
                return new ActualValue((String) value);
            }
        } else {
            return new ActualValue((String) null);
        }
    }

}
