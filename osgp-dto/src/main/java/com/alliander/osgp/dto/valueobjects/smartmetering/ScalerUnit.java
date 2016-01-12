/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

/**
 * response to {@link ScalerUnitQuery scaler and unit query} for E or GAS
 * meters.
 *
 * @author dev
 */
public class ScalerUnit implements Serializable {
    private static final long serialVersionUID = 3751586818507193990L;

    private final DlmsUnit dlmsUnit;
    private final int scaler;

    public ScalerUnit(DlmsUnit dlmsUnit, int scaler) {
        this.dlmsUnit = dlmsUnit;
        this.scaler = scaler;
    }

    public DlmsUnit getDlmsUnit() {
        return this.dlmsUnit;
    }

    public int getScaler() {
        return this.scaler;
    }

}
