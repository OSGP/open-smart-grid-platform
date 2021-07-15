/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import java.util.Arrays;

import com.alliander.data.scadameasurementpublishedevent.AccumulationKind;
import com.alliander.data.scadameasurementpublishedevent.Analog;
import com.alliander.data.scadameasurementpublishedevent.AnalogValue;
import com.alliander.data.scadameasurementpublishedevent.MeasuringPeriodKind;
import com.alliander.data.scadameasurementpublishedevent.UnitMultiplier;
import com.alliander.data.scadameasurementpublishedevent.UnitSymbol;

public interface LowVoltageMeasurementDefinition {

    int getIndex();

    String getDescription();

    UnitSymbol getUnitSymbol();

    UnitMultiplier getUnitMultiplier();

    default Analog createAnalog(final Float value) {
        return new Analog(Arrays.asList(new AnalogValue(null, value)), AccumulationKind.none, this.getDescription(),
                MeasuringPeriodKind.none, this.getUnitMultiplier(), this.getUnitSymbol());
    }
}
