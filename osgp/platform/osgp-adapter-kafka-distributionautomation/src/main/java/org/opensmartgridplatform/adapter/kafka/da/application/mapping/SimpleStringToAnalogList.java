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

import org.opensmartgridplatform.adapter.kafka.da.avro.Analog;
import org.opensmartgridplatform.adapter.kafka.da.avro.UnitMultiplier;
import org.opensmartgridplatform.adapter.kafka.da.avro.UnitSymbol;

public class SimpleStringToAnalogList implements StringArrayToAnalogList {

    private static final int VOLTAGE_START_INDEX = 1;
    private static final int CURRENT_START_INDEX = 4;
    private static final int CURRENT_RETURNED_START_INDEX = 7;
    private static final int END_INDEX = 10;

    @Override
    public List<Analog> convertToAnalogList(final String[] values) {

        final int lengthOfInputArray = values.length;
        if (lengthOfInputArray != END_INDEX) {
            throw new IllegalArgumentException(
                    "Invalid value string length " + lengthOfInputArray + ", expected " + END_INDEX);
        }

        final List<Analog> measurements = new ArrayList<>();

        final String eanCode = values[0];
        for (int index = VOLTAGE_START_INDEX; index < CURRENT_START_INDEX; index++) {
            final String description = eanCode + ":voltage_L" + index;
            measurements.add(this.createAnalog(description, Float.valueOf(values[index]), UnitSymbol.V));
        }

        for (int index = CURRENT_START_INDEX; index < CURRENT_RETURNED_START_INDEX; index++) {
            final String description = eanCode + ":current_in_L" + (index - CURRENT_START_INDEX + 1);
            measurements.add(this.createAnalog(description, Float.valueOf(values[index]), UnitSymbol.A));
        }

        for (int index = CURRENT_RETURNED_START_INDEX; index < END_INDEX; index++) {
            final String description = eanCode + ":current_returned_L" + (index - CURRENT_RETURNED_START_INDEX + 1);
            measurements.add(this.createAnalog(description, Float.valueOf(values[index]), UnitSymbol.A));
        }

        return measurements;

    }

    private Analog createAnalog(final String description, final Float value, final UnitSymbol unitSymbol) {
        return this.createAnalog(description, value, unitSymbol, UnitMultiplier.none);
    }

}
