/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import com.alliander.data.scadameasurementpublishedevent.Analog;
import com.alliander.data.scadameasurementpublishedevent.UnitMultiplier;
import com.alliander.data.scadameasurementpublishedevent.UnitSymbol;
import java.util.ArrayList;
import java.util.List;

public class LovVoltageMessageFactory extends MessageFactory {
  public static List<Analog> expectedMeasurements() {
    final List<Analog> measurements = new ArrayList<>();
    measurements.add(createAnalog("U-L1-E", 0.1f, UnitSymbol.V));
    measurements.add(createAnalog("U-L2-E", 0.2f, UnitSymbol.V));
    measurements.add(createAnalog("U-L3-E", 0.3f, UnitSymbol.V));
    measurements.add(createAnalog("I-L1", 0.4f, UnitSymbol.A));
    measurements.add(createAnalog("I-L2", 0.5f, UnitSymbol.A));
    measurements.add(createAnalog("I-L3", 0.6f, UnitSymbol.A));
    measurements.add(createAnalog("SomP", 0.7f, UnitSymbol.W, UnitMultiplier.k));
    measurements.add(createAnalog("SomQ", 0.8f, UnitSymbol.VAr, UnitMultiplier.k));
    measurements.add(createAnalog("P-L1", 0.9f, UnitSymbol.W, UnitMultiplier.k));
    measurements.add(createAnalog("P-L2", 1.0f, UnitSymbol.W, UnitMultiplier.k));
    measurements.add(createAnalog("P-L3", 1.1f, UnitSymbol.W, UnitMultiplier.k));
    measurements.add(createAnalog("Q-L1", 1.2f, UnitSymbol.VAr, UnitMultiplier.k));
    measurements.add(createAnalog("Q-L2", 1.3f, UnitSymbol.VAr, UnitMultiplier.k));
    measurements.add(createAnalog("Q-L3", 1.4f, UnitSymbol.VAr, UnitMultiplier.k));
    measurements.add(createAnalog("PF-L1", 1.5f, UnitSymbol.none));
    measurements.add(createAnalog("PF-L2", 1.6f, UnitSymbol.none));
    measurements.add(createAnalog("PF-L3", 1.7f, UnitSymbol.none));
    measurements.add(createAnalog("THDi-L1", 1.8f, UnitSymbol.PerCent));
    measurements.add(createAnalog("THDi-L2", 1.9f, UnitSymbol.PerCent));
    measurements.add(createAnalog("THDi-L3", 2.0f, UnitSymbol.PerCent));
    measurements.add(createAnalog("I1-H3", 2.1f, UnitSymbol.A));
    measurements.add(createAnalog("I2-H3", 2.2f, UnitSymbol.A));
    measurements.add(createAnalog("I3-H3", 2.3f, UnitSymbol.A));
    measurements.add(createAnalog("I1-H5", 2.4f, UnitSymbol.A));
    measurements.add(createAnalog("I2-H5", 2.5f, UnitSymbol.A));
    measurements.add(createAnalog("I3-H5", 2.6f, UnitSymbol.A));
    measurements.add(createAnalog("I1-H7", 2.7f, UnitSymbol.A));
    measurements.add(createAnalog("I2-H7", 2.8f, UnitSymbol.A));
    measurements.add(createAnalog("I3-H7", 2.9f, UnitSymbol.A));
    measurements.add(createAnalog("I1-H9", 3.0f, UnitSymbol.A));
    measurements.add(createAnalog("I2-H9", 3.1f, UnitSymbol.A));
    measurements.add(createAnalog("I3-H9", 3.2f, UnitSymbol.A));
    measurements.add(createAnalog("I1-H11", 3.3f, UnitSymbol.A));
    measurements.add(createAnalog("I2-H11", 3.4f, UnitSymbol.A));
    measurements.add(createAnalog("I3-H11", 3.5f, UnitSymbol.A));
    measurements.add(createAnalog("I1-H13", 3.6f, UnitSymbol.A));
    measurements.add(createAnalog("I2-H13", 3.7f, UnitSymbol.A));
    measurements.add(createAnalog("I3-H13", 3.8f, UnitSymbol.A));
    measurements.add(createAnalog("I1-H15", 3.9f, UnitSymbol.A));
    measurements.add(createAnalog("I2-H15", 4.0f, UnitSymbol.A));
    measurements.add(createAnalog("I3-H15", 4.1f, UnitSymbol.A));
    return measurements;
  }

  public static List<Analog> expectedMetaMeasurements() {
    final List<Analog> measurements = new ArrayList<>();
    measurements.add(createAnalog("Frequency", 49.98f, UnitSymbol.Hz));
    measurements.add(createAnalog("Temperature", 12.0f, UnitSymbol.degC));
    measurements.add(createAnalog("IRMS-N", 0.11f, UnitSymbol.A));
    return measurements;
  }
}
