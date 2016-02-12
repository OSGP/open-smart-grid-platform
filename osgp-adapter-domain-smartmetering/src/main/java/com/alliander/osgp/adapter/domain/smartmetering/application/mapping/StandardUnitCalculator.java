/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnit;
import java.math.BigDecimal;

public class StandardUnitCalculator {

    public double calculateStandardizedValue(final long meterValue, final ScalerUnit scalerUnit) {
        final double multiplier = getMultiplierToOsgpUnit(scalerUnit.getDlmsUnit());
        return round(meterValue * multiplier * Math.pow(10, scalerUnit.getScaler()), DECIMAL_PLACES);
    }

    public static final int DECIMAL_PLACES = 3;

    public double getMultiplierToOsgpUnit(final DlmsUnit dlmsUnit) {
        switch (dlmsUnit) {
        case WH:
            return 0.001d;
        case M3:
        case M3COR:
            return 1d;
        default:
            throw new IllegalArgumentException(String.format("unit %s not supported yet", dlmsUnit.name()));
        }
    }

    /**
     * Round to certain number of decimals
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    public static double round(double d, int decimalPlace) {
        return new BigDecimal(Double.toString(d)).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
