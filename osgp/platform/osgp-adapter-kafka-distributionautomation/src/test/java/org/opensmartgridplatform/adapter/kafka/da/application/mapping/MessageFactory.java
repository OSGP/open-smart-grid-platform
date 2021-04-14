/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import com.alliander.data.scadameasurementpublishedevent.AccumulationKind;
import com.alliander.data.scadameasurementpublishedevent.Analog;
import com.alliander.data.scadameasurementpublishedevent.AnalogValue;
import com.alliander.data.scadameasurementpublishedevent.MeasuringPeriodKind;
import com.alliander.data.scadameasurementpublishedevent.UnitMultiplier;
import com.alliander.data.scadameasurementpublishedevent.UnitSymbol;
import java.util.Arrays;

public class MessageFactory {

  protected static Analog createAnalog(
      final String description, final Float value, final UnitSymbol unitSymbol) {
    return createAnalog(description, value, unitSymbol, UnitMultiplier.none);
  }

  protected static Analog createAnalog(
      final String description,
      final Float value,
      final UnitSymbol unitSymbol,
      final UnitMultiplier unitMultiplier) {
    return new Analog(
        Arrays.asList(new AnalogValue(null, value)),
        AccumulationKind.none,
        description,
        MeasuringPeriodKind.none,
        unitMultiplier,
        unitSymbol);
  }
}
