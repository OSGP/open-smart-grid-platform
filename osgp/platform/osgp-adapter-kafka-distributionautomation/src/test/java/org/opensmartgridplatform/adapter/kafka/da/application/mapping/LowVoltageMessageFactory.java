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
    final List<Analog> measurements = new ArrayList<>();
    measurements.add(createAnalog("Frequency", 49.98f, UnitSymbol.Hz));
    measurements.add(createAnalog("Temperature", 12.0f, UnitSymbol.degC));
    measurements.add(createAnalog("IRMS-N", 0.11f, UnitSymbol.A));
    return measurements;
  }

  private static List<Analog> expectedMeasurementsVersion1() {
    final List<Analog> measurements = new ArrayList<>();
    measurements.add(createAnalog("U-L1", 0.1f, UnitSymbol.V));
    measurements.add(createAnalog("U-L2", 0.2f, UnitSymbol.V));
    measurements.add(createAnalog("U-L3", 0.3f, UnitSymbol.V));
    measurements.addAll(createCommonExpectedMeasurements(0.4f));
    return measurements;
  }

  private static List<Analog> expectedMeasurementsVersion2() {
    final List<Analog> measurements = new ArrayList<>();
    measurements.add(createAnalog("U-avg", 0.1f, UnitSymbol.V));
    measurements.addAll(createCommonExpectedMeasurements(0.2f));
    double value = measurements.size() * 0.1;
    measurements.add(createAnalog("IrmsN", (float) (value += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("Pp", (float) (value += 0.1), UnitSymbol.none));
    measurements.add(createAnalog("Pm", (float) (value += 0.1), UnitSymbol.none));
    measurements.add(createAnalog("Qp", (float) (value += 0.1), UnitSymbol.none));
    measurements.add(createAnalog("Qm", (float) (value += 0.1), UnitSymbol.none));
    measurements.add(createAnalog("U-L1", (float) (value += 0.1), UnitSymbol.V));
    measurements.add(createAnalog("U-L2", (float) (value += 0.1), UnitSymbol.V));
    measurements.add(createAnalog("U-L3", (float) (value += 0.1), UnitSymbol.V));
    measurements.add(createAnalog("Temp", (float) (value += 0.1), UnitSymbol.C));
    measurements.add(createAnalog("F", (float) (value += 0.1), UnitSymbol.Hz));
    return measurements;
  }

  private static List<Analog> createCommonExpectedMeasurements(final float value) {
    final List<Analog> measurements = new ArrayList<>();
    double dValue = value;
    measurements.add(createAnalog("I-L1", value, UnitSymbol.A));
    measurements.add(createAnalog("I-L2", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("I-L3", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(
        createAnalog("Tot-P", (float) (dValue += 0.1), UnitSymbol.W, UnitMultiplier.k));
    measurements.add(
        createAnalog("Tot-Q", (float) (dValue += 0.1), UnitSymbol.VAr, UnitMultiplier.k));
    measurements.add(
        createAnalog("P-L1", (float) ((dValue += 0.1)), UnitSymbol.W, UnitMultiplier.k));
    measurements.add(createAnalog("P-L2", (float) (dValue += 0.1), UnitSymbol.W, UnitMultiplier.k));
    measurements.add(createAnalog("P-L3", (float) (dValue += 0.1), UnitSymbol.W, UnitMultiplier.k));
    measurements.add(
        createAnalog("Q-L1", (float) (dValue += 0.1), UnitSymbol.VAr, UnitMultiplier.k));
    measurements.add(
        createAnalog("Q-L2", (float) (dValue += 0.1), UnitSymbol.VAr, UnitMultiplier.k));
    measurements.add(
        createAnalog("Q-L3", (float) (dValue += 0.1), UnitSymbol.VAr, UnitMultiplier.k));
    measurements.add(createAnalog("PF-L1", (float) (dValue += 0.1), UnitSymbol.none));
    measurements.add(createAnalog("PF-L2", (float) (dValue += 0.1), UnitSymbol.none));
    measurements.add(createAnalog("PF-L3", (float) (dValue += 0.1), UnitSymbol.none));
    measurements.add(createAnalog("THDi-L1", (float) (dValue += 0.1), UnitSymbol.PerCent));
    measurements.add(createAnalog("THDi-L2", (float) (dValue += 0.1), UnitSymbol.PerCent));
    measurements.add(createAnalog("THDi-L3", (float) (dValue += 0.1), UnitSymbol.PerCent));
    measurements.add(createAnalog("H3-I1", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H3-I2", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H3-I3", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H5-I1", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H5-I2", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H5-I3", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H7-I1", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H7-I2", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H7-I3", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H9-I1", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H9-I2", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H9-I3", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H11-I1", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H11-I2", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H11-I3", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H13-I1", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H13-I2", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H13-I3", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H15-I1", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H15-I2", (float) (dValue += 0.1), UnitSymbol.A));
    measurements.add(createAnalog("H15-I3", (float) (dValue += 0.1), UnitSymbol.A));
    return measurements;
  }
}
