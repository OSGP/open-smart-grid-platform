/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
