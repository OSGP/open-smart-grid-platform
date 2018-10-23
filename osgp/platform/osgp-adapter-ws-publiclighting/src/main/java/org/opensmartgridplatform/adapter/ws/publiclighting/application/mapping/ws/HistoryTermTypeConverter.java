/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.ws;

import org.opensmartgridplatform.domain.core.valueobjects.HistoryTermType;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class HistoryTermTypeConverter extends
        CustomConverter<org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.HistoryTermType, HistoryTermType> {
    @Override
    public HistoryTermType convert(
            final org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.HistoryTermType source,
            final Type<? extends HistoryTermType> destinationType, final MappingContext context) {
        return HistoryTermType.valueOf(source.value());
    }
}
