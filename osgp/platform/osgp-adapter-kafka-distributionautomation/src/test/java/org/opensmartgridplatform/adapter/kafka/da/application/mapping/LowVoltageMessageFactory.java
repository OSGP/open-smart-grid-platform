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
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LowVoltageMessageFactory extends MessageFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(LowVoltageMessageFactory.class);

  public enum Version {
    VERSION_1,
    VERSION_2;
  }

  public static List<Analog> expectedMeasurements(final Version version) {
    if (Version.VERSION_1 == version) {
      return expectedMeasurementsVersion1();
    } else if (Version.VERSION_2 == version) {
      return expectedMeasurementsVersion2();
    } else {
      LOGGER.error("Not given an expected version. Given version: {}", version);
      return null;
    }
  }

  public static List<Analog> expectedMetaMeasurements() {
    return Arrays.asList(
        createAnalog("Frequency", 49.98f, UnitSymbol.Hz),
        createAnalog("Temperature", 12.0f, UnitSymbol.degC),
        createAnalog("IRMS-N", 0.11f, UnitSymbol.A));
  }

  private static List<Analog> expectedMeasurementsVersion1() {
    final List<Analog> measurements = new ArrayList<>();
    measurements.addAll(
        Arrays.asList(
            createAnalog("U-L1", 0.1f, UnitSymbol.V),
            createAnalog("U-L2", 0.2f, UnitSymbol.V),
            createAnalog("U-L3", 0.3f, UnitSymbol.V)));
    measurements.addAll(createCommonExpectedMeasurements(0.4f));
    return measurements;
  }

  private static List<Analog> expectedMeasurementsVersion2() {
    final List<Analog> measurements = new ArrayList<>();
    measurements.add(createAnalog("U-avg", 0.1f, UnitSymbol.V));
    measurements.addAll(createCommonExpectedMeasurements(0.2f));
    double value = measurements.size() * 0.1;
    measurements.addAll(
        Arrays.asList(
            createAnalog("IrmsN", (float) (value += 0.1), UnitSymbol.A),
            createAnalog("Pp", (float) (value += 0.1), UnitSymbol.none),
            createAnalog("Pm", (float) (value += 0.1), UnitSymbol.none),
            createAnalog("Qp", (float) (value += 0.1), UnitSymbol.none),
            createAnalog("Qm", (float) (value += 0.1), UnitSymbol.none),
            createAnalog("U-L1", (float) (value += 0.1), UnitSymbol.V),
            createAnalog("U-L2", (float) (value += 0.1), UnitSymbol.V),
            createAnalog("U-L3", (float) (value += 0.1), UnitSymbol.V),
            createAnalog("Temp", (float) (value += 0.1), UnitSymbol.C),
            createAnalog("F", (float) (value += 0.1), UnitSymbol.Hz)));
    return measurements;
  }

  private static List<Analog> createCommonExpectedMeasurements(final float startValue) {
    double value = startValue;
    return Arrays.asList(
        createAnalog("I-L1", (float) value, UnitSymbol.A),
        createAnalog("I-L2", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("I-L3", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("Tot-P", (float) (value += 0.1), UnitSymbol.W, UnitMultiplier.k),
        createAnalog("Tot-Q", (float) (value += 0.1), UnitSymbol.VAr, UnitMultiplier.k),
        createAnalog("P-L1", (float) ((value += 0.1)), UnitSymbol.W, UnitMultiplier.k),
        createAnalog("P-L2", (float) (value += 0.1), UnitSymbol.W, UnitMultiplier.k),
        createAnalog("P-L3", (float) (value += 0.1), UnitSymbol.W, UnitMultiplier.k),
        createAnalog("Q-L1", (float) (value += 0.1), UnitSymbol.VAr, UnitMultiplier.k),
        createAnalog("Q-L2", (float) (value += 0.1), UnitSymbol.VAr, UnitMultiplier.k),
        createAnalog("Q-L3", (float) (value += 0.1), UnitSymbol.VAr, UnitMultiplier.k),
        createAnalog("PF-L1", (float) (value += 0.1), UnitSymbol.none),
        createAnalog("PF-L2", (float) (value += 0.1), UnitSymbol.none),
        createAnalog("PF-L3", (float) (value += 0.1), UnitSymbol.none),
        createAnalog("THDi-L1", (float) (value += 0.1), UnitSymbol.PerCent),
        createAnalog("THDi-L2", (float) (value += 0.1), UnitSymbol.PerCent),
        createAnalog("THDi-L3", (float) (value += 0.1), UnitSymbol.PerCent),
        createAnalog("H3-I1", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H3-I2", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H3-I3", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H5-I1", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H5-I2", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H5-I3", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H7-I1", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H7-I2", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H7-I3", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H9-I1", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H9-I2", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H9-I3", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H11-I1", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H11-I2", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H11-I3", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H13-I1", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H13-I2", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H13-I3", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H15-I1", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H15-I2", (float) (value += 0.1), UnitSymbol.A),
        createAnalog("H15-I3", (float) (value += 0.1), UnitSymbol.A));
  }
}
