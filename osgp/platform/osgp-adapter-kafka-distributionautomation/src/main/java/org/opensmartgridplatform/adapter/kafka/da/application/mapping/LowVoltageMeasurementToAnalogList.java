/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alliander.data.scadameasurementpublishedevent.Analog;

public class LowVoltageMeasurementToAnalogList implements StringArrayToAnalogList {

    /**
     * Returns the implementation class to be used to convert a specific number
     * of measurement values.<br>
     * The implementation class is an {@link Enum} that implements
     * {@link LowVoltageMeasurementDefinition}, with constants that belong with
     * each value in the list the
     * {@link LowVoltageMeasurementDefinition#getIndex() index} of the
     * {@link LowVoltageMeasurementDefinition} links the definition to the
     * position in the list of values.
     */
    @SuppressWarnings("unchecked")
    private static <T extends Enum<T> & LowVoltageMeasurementDefinition> Class<T> implementation(final int length) {
        return (Class<T>) Stream.of(LowVoltageMeasurementType.class, LowVoltageMetaMeasurementType.class)
                .filter(clazz -> clazz.getEnumConstants().length == length)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported value string length: " + length));
    }

    @Override
    public List<Analog> convertToAnalogList(final String[] values) {
        final Class<? extends LowVoltageMeasurementDefinition> implementation = implementation(values.length);
        return Arrays.stream(implementation.getEnumConstants())
                .sorted(Comparator.comparingInt(LowVoltageMeasurementDefinition::getIndex))
                .map(definition -> definition.createAnalog(Float.valueOf(values[definition.getIndex()])))
                .collect(Collectors.toList());
    }

}
