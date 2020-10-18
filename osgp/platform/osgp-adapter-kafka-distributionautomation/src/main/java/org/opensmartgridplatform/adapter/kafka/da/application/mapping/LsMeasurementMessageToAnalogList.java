/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import java.util.ArrayList;
import java.util.List;

import com.alliander.data.scadameasurementpublishedevent.Analog;

public class LsMeasurementMessageToAnalogList implements StringArrayToAnalogList {

    @Override
    public List<Analog> convertToAnalogList(final String[] values) {

        final int lengthOfInputArray = values.length;
        final int expectedLength = LsPeakShavingMeasurementType.getNumberOfElements() + 1;
        if (lengthOfInputArray != expectedLength) {
            throw new IllegalArgumentException(
                    "Invalid value string length " + lengthOfInputArray + ", expected " + expectedLength);
        }

        final List<Analog> measurements = new ArrayList<>();

        final String eanCode = values[0];
        for (int index = 1; index < lengthOfInputArray; index++) {
            final LsPeakShavingMeasurementType measurementType = LsPeakShavingMeasurementType.getMeasurementType(index);
            final String description = eanCode + ":" + measurementType.getDescription();
            measurements.add(this.createAnalog(description, Float.valueOf(values[index]),
                    measurementType.getUnitSymbol(), measurementType.getUnitMultiplier()));
        }

        return measurements;

    }

}
