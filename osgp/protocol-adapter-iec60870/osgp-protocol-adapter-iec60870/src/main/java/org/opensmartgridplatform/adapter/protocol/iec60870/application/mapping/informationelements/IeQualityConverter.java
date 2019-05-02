/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.informationelements;

import org.openmuc.j60870.IeQuality;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class IeQualityConverter extends CustomConverter<IeQuality, BitmaskMeasurementElementDto> {

    private static final int BIT_OVERFLOW = 1 << 0;
    private static final int BIT_BLOCKED = 1 << 4;
    private static final int BIT_SUBSTITUTED = 1 << 5;
    private static final int BIT_NOT_TOPICAL = 1 << 6;
    private static final int BIT_INVALID = 1 << 7;

    @Override
    public BitmaskMeasurementElementDto convert(final IeQuality source,
            final Type<? extends BitmaskMeasurementElementDto> destinationType, final MappingContext mappingContext) {
        int value = 0;
        value += source.isOverflow() ? BIT_OVERFLOW : 0;
        value += source.isBlocked() ? BIT_BLOCKED : 0;
        value += source.isSubstituted() ? BIT_SUBSTITUTED : 0;
        value += source.isNotTopical() ? BIT_NOT_TOPICAL : 0;
        value += source.isInvalid() ? BIT_INVALID : 0;
        return new BitmaskMeasurementElementDto((byte) value);
    }
}
