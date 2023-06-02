//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.config;

import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import java.io.File;
import java.io.FileNotFoundException;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingInitializer {
  public LoggingInitializer() throws FileNotFoundException, JoranException {
    final String logLocation = "/etc/osp/test/logback.xml";
    if (new File(logLocation).exists()) {
      System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, logLocation);
    }
  }
}
