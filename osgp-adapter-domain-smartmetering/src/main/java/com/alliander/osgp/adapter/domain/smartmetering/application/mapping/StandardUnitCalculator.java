/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnit;

/**
 * Calculate a meter value:
 *
 * <pre>
 * - apply the scaler by dividing through a power of 10
 * - determine the multiplier for the DlmsUnit
 * - apply the multiplier
 * - round to 3 fraction digits
 * </pre>
 *
 */
@Component(value = "standardUnitCalculator")
public class StandardUnitCalculator {

    public static final String FRACTION_DIGITS = "fraction_digits";

    @Resource
    private Environment environment;

    @PostConstruct
    private void init() {
        if (this.environment.containsProperty(FRACTION_DIGITS)) {
            this.fraction_digits = this.environment.getProperty(FRACTION_DIGITS, Integer.class);
        }
    }

    public double calculateStandardizedValue(final long meterValue, final ScalerUnit scalerUnit) {
        final double multiplier = this.getMultiplierToOsgpUnit(scalerUnit.getDlmsUnit());
        final double power = scalerUnit.getScaler() == 0 ? 1 : Math.pow(10, scalerUnit.getScaler());
        return round((meterValue / power) * multiplier, this.fraction_digits);
    }

    private int fraction_digits = 3;

    private double getMultiplierToOsgpUnit(final DlmsUnit dlmsUnit) {
        switch (dlmsUnit) {
        case WH:
            // convert to KWH
            return 0.001d;
        case M3:
        case M3COR:
            return 1d;
        default:
            throw new IllegalArgumentException(String.format("dlms unit %s not supported yet", dlmsUnit.name()));
        }
    }

    /**
     * Round to certain number of decimals
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    public static double round(final double d, final int decimalPlace) {
        return BigDecimal.valueOf(d).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
