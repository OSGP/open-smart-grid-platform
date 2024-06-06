// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec60870;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class Iec60870ServletInitializer extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
    return application.sources(Iec60870SimulatorApplication.class);
  }
}
