/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulatorstarter;

import java.util.List;

/** Represents the full list of simulator configurations with which the starter is started. */
public class SimulatorConfigurationList {
  private List<SimulatorConfiguration> simulators;

  public List<SimulatorConfiguration> getSimulators() {
    return this.simulators;
  }
}
