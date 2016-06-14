/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.math.BigDecimal;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnitDto;

/**
 * Calculate a osgp meter value:
 *
 * <pre>
 * - determine the osgp unit
 * - determine the multiplier for the conversion of DlmsUnit to OsgpUnit
 * - apply the multiplier
 * </pre>
 *
 */
public class DlmsMeterValueConverter extends CustomConverter<DlmsMeterValueDto, OsgpMeterValue> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsMeterValueConverter.class);

    @Override
    public OsgpMeterValue convert(final DlmsMeterValueDto source, final Type<? extends OsgpMeterValue> destinationType) {
        if (source == null) {
            return null;
        }
        final BigDecimal multiplier = this.getMultiplierToOsgpUnit(source.getDlmsUnit(),
                this.toStandardUnit(source.getDlmsUnit()));
        final BigDecimal calculated = source.getValue().multiply(multiplier);
        LOGGER.debug(String.format("calculated %s from %s", calculated, source));
        return new OsgpMeterValue(calculated, this.toStandardUnit(source.getDlmsUnit()));
    }

    /**
     * return the multiplier to get from a dlms unit to a osgp unit
     *
     * @throws IllegalArgumentException
     *             when no multiplier is found
     * @param dlmsUnit
     * @return
     */
    private BigDecimal getMultiplierToOsgpUnit(final DlmsUnitDto dlmsUnit, final OsgpUnit osgpUnit) {

        switch (dlmsUnit) {
        case WH:
            return BigDecimal.valueOf(0.001d);
        case M3: // intentional fallthrough.
        case M3COR: // intentional fallthrough.
        case UNDEFINED:
            return BigDecimal.valueOf(1d);
        default:
            break;
        }

        throw new IllegalArgumentException(String.format("calculating %s from %s not supported yet", osgpUnit.name(),
                dlmsUnit.name()));
    }

    /**
     * return the osgp unit that corresponds to a dlms unit
     *
     * @throws IllegalArgumentException
     *             when no osgp unit is found
     */
    private OsgpUnit toStandardUnit(final DlmsUnitDto dlmsUnit) {
        switch (dlmsUnit) {
        case WH:
            return OsgpUnit.KWH;
        case M3: // intentional fallthrough.
        case M3COR:
            return OsgpUnit.M3;
        case UNDEFINED:
            return OsgpUnit.UNDEFINED;
        default:
            throw new IllegalArgumentException(String.format("dlms unit %s not supported yet", dlmsUnit.name()));
        }
    }

}
