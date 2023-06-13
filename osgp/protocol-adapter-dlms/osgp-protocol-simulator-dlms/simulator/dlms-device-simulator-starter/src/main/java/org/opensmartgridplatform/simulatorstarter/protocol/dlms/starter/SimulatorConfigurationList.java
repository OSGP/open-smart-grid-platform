// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulatorstarter.protocol.dlms.starter;

import java.util.List;

/** Represents the full list of simulator configurations with which the starter is started. */
public class SimulatorConfigurationList {
  private List<SimulatorConfiguration> simulators;

  public List<SimulatorConfiguration> getSimulators() {
    return this.simulators;
  }
}
