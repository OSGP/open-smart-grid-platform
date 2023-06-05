// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.kafka.logging.config;

import org.opensmartgridplatform.shared.application.config.AbstractApplicationInitializer;

/** Web application Java configuration class. */
public class AdapterInitializer extends AbstractApplicationInitializer {

  public AdapterInitializer() {
    super(ApplicationContext.class, "java:comp/env/osgp/AdapterKafkaLogging/log-config");
  }
}
