// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.server.profile;

import java.util.Map;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.opensmartgridplatform.simulator.protocol.dlms.interceptor.OsgpServerConnectionListener;
import org.opensmartgridplatform.simulator.protocol.dlms.util.KeyPathProvider;
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

  @Bean
  KeyPathProvider keyPathProvider(
      final String authenticationKeyPath,
      final String encryptionKeyPath,
      final String masterKeyPath) {
    return new KeyPathProvider(authenticationKeyPath, encryptionKeyPath, masterKeyPath);
  }

  @Bean
  public Map<String, CosemInterfaceObject> cosemClasses(
      final org.springframework.context.ApplicationContext applicationContext) {
    return applicationContext.getBeansOfType(CosemInterfaceObject.class);
  }

  @Bean
  public OsgpServerConnectionListener osgpServerConnectionListener() {
    return new OsgpServerConnectionListener(0);
  }
}
