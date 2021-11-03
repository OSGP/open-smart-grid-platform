/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.server;

import org.openmuc.jdlms.DlmsServer;
import org.opensmartgridplatform.simulator.protocol.dlms.config.DlmsServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * This is the main class to start a DeviceServer simulator. Defaults are defined in
 * application.properties, and can be overruled by spring-boot constructs.
 */
@SpringBootApplication
public class DeviceServer {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceServer.class);

  public static void main(final String... args) {

    new DeviceServer().getDlmsServer(args);
  }

  public DlmsServer getDlmsServer(final String[] args) {

    final SpringApplication dlmsSimulator = new SpringApplication(DlmsServerConfig.class);
    dlmsSimulator.setWebApplicationType(WebApplicationType.NONE);

    final ApplicationContext ctx = dlmsSimulator.run(args);

    LOGGER.info("Starting server");
    // Retrieve bean so DlmsServer is created and started.
    final DlmsServer serverConnection = ctx.getBean(DlmsServer.class);
    LOGGER.info("Server is running", serverConnection);
    return serverConnection;
  }
}
