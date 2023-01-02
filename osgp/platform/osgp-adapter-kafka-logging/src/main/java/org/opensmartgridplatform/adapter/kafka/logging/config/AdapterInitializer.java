/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.logging.config;

import org.opensmartgridplatform.shared.application.config.AbstractApplicationInitializer;

/** Web application Java configuration class. */
public class AdapterInitializer extends AbstractApplicationInitializer {

  public AdapterInitializer() {
    super(ApplicationContext.class, "java:comp/env/osgp/AdapterKafkaLogging/log-config");
  }
}
