/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.opensmartgridplatform.adapter.kafka.da.avro.AccumulationKind;
import org.opensmartgridplatform.adapter.kafka.da.avro.Analog;
import org.opensmartgridplatform.adapter.kafka.da.avro.AnalogValue;
import org.opensmartgridplatform.adapter.kafka.da.avro.MeasuringPeriodKind;
import org.opensmartgridplatform.adapter.kafka.da.avro.Name;
import org.opensmartgridplatform.adapter.kafka.da.avro.PhaseCode;
import org.opensmartgridplatform.adapter.kafka.da.avro.UnitMultiplier;
import org.opensmartgridplatform.adapter.kafka.da.avro.UnitSymbol;

public interface StringArrayToAnalogList {

    List<Analog> convertToAnalogList(String[] values);

    default Analog createAnalog(final String description, final Float value, final UnitSymbol unitSymbol,
            final UnitMultiplier unitMultiplier) {
        return new Analog(description, UUID.randomUUID().toString(), AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, unitMultiplier, unitSymbol, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(value, null, null)));
    }

}
