/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.domain.defaultcontrolledstation;

import org.openmuc.j60870.ASdu;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("default_controlled_station")
public class DefaultControlledStationMeasurementAsduGenerator implements Iec60870AsduGenerator {

  @Autowired private DefaultControlledStationAsduFactory factory;

  @Override
  public ASdu getNextAsdu() {
    return this.factory.createShortFloatingPointMeasurementAsdu();
  }
}
