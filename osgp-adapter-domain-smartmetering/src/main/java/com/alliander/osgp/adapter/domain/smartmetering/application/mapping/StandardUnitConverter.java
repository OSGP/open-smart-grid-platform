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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnitResponse;

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
@Component(value = "standardUnitConverter")
public class StandardUnitConverter {

    public static final String FRACTION_DIGITS = "fraction_digits";

    private static final Logger LOGGER = LoggerFactory.getLogger(StandardUnitConverter.class);

    @Resource
    private Environment environment;

    private int fractionDigits = 3;

    @PostConstruct
    private void init() {
        if (this.environment.containsProperty(FRACTION_DIGITS)) {
            this.fractionDigits = this.environment.getProperty(FRACTION_DIGITS, Integer.class);
        }
    }

    /**
     * Return a meterValue in standardized unit and fraction digits. When the
     * argument value is null, null is returned.
     * 
     * @param meterValue
     * @param scalerUnitResponse
     * @return
     */
    public Double calculateStandardizedValue(final Long meterValue, final ScalerUnitResponse scalerUnitResponse) {
        if (meterValue == null) {
            return null;
        }
        final ScalerUnit scalerUnit = scalerUnitResponse.getScalerUnit();
        final double multiplier = this.getMultiplierToOsgpUnit(scalerUnit.getDlmsUnit());
        final double power = scalerUnit.getScaler() == 0 ? 1 : Math.pow(10, scalerUnit.getScaler());
        final double calculated = round((meterValue / power) * multiplier, this.fractionDigits);
        LOGGER.debug(String.format("calculated %s from %s using %s", calculated, meterValue, scalerUnit));
        return calculated;
    }

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

    public OsgpUnit toStandardUnit(final ScalerUnitResponse scalerUnitResponse) {
        switch (scalerUnitResponse.getScalerUnit().getDlmsUnit()) {
        case WH:
            return OsgpUnit.KWH;
        case M3:
        case M3COR:
            return OsgpUnit.M3;
        default:
            throw new IllegalArgumentException(String.format("dlms unit %s not supported yet", scalerUnitResponse
                    .getScalerUnit().getDlmsUnit().name()));
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
