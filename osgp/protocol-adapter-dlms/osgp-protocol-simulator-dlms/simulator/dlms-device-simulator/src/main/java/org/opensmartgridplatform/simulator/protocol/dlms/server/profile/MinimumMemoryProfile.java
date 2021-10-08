/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.server.profile;

import java.util.Map;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.opensmartgridplatform.simulator.protocol.dlms.interceptor.OsgpServerConnectionListener;
import org.opensmartgridplatform.simulator.protocol.dlms.util.KeyPathProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * This profile enables starting a device simulator that attempts to use as little memory as
 * possible. It does this by:
 *
 * <p>a) Having all logical devices use the same map of CosemClasses. Thus, if some cosemClass is
 * changed for one device, it changes for all devices.
 *
 * <p>b) Having all logical devices use the same keys, thus reducing I/O operations at startup.
 *
 * <p>Further optimization may be possible in the future, after profiling of the application.
 */
@Configuration
@Profile("minimumMemory")
public class MinimumMemoryProfile {

  @Autowired private org.springframework.context.ApplicationContext applicationContext;

  @Autowired private String authenticationKeyPath;

  @Autowired private String encryptionKeyPath;

  @Autowired private String masterKeyPath;

  @Bean
  KeyPathProvider keyPathProvider() {
    return new KeyPathProvider(
        this.authenticationKeyPath, this.encryptionKeyPath, this.masterKeyPath);
  }

  @Bean
  public Map<String, CosemInterfaceObject> cosemClasses() {
    return this.applicationContext.getBeansOfType(CosemInterfaceObject.class);
  }

  @Bean
  public OsgpServerConnectionListener osgpServerConnectionListener() {
    return new OsgpServerConnectionListener(0);
  }
}
