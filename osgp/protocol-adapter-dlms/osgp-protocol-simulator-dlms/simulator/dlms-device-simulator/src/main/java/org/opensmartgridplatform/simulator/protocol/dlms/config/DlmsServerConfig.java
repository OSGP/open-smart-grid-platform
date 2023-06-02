//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.config;

import java.io.IOException;
import java.util.Map;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.DlmsServer;
import org.openmuc.jdlms.DlmsServer.TcpServerBuilder;
import org.openmuc.jdlms.LogicalDevice;
import org.openmuc.jdlms.sessionlayer.server.ServerSessionLayerFactories;
import org.openmuc.jdlms.settings.client.ReferencingMethod;
import org.opensmartgridplatform.simulator.protocol.dlms.interceptor.OsgpServerConnectionListener;
import org.opensmartgridplatform.simulator.protocol.dlms.server.LogicalDeviceBuilder;
import org.opensmartgridplatform.simulator.protocol.dlms.server.SecurityLevel;
import org.opensmartgridplatform.simulator.protocol.dlms.util.KeyPathProvider;
import org.opensmartgridplatform.simulator.protocol.dlms.util.LogicalDeviceIdsConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"org.opensmartgridplatform.simulator"})
public class DlmsServerConfig implements ApplicationContextAware {

  private static final Logger LOGGER = LoggerFactory.getLogger(DlmsServerConfig.class);

  // Manufacturer ID must be three characters long.
  private static final String MANUFACTURER_ID = "OSG";
  private static final String LOGICAL_DEVICE_NAME = "device name";
  private static final long DEVICE_ID = 9999L;

  private static final int DLMS_PUBLIC_CLIENT_ID = 16;
  private static final int DLMS_MANAGEMENT_CLIENT = 1;

  private ApplicationContext applicationContext;

  @Autowired private KeyPathProvider keyPathProvider;

  @Value("${port}")
  private int port;

  @Value("${security.level}")
  private int securityLevel;

  @Value("${logicalDeviceIds}")
  private String logicalDeviceIds;

  @Value("${referencing.method}")
  private String referencingMethod;

  @Value("${use.hdlc}")
  private boolean useHdlc;

  @Bean
  public DlmsServer dlmsServer(final OsgpServerConnectionListener osgpServerConnectionListener)
      throws IOException {
    final DlmsServer serverConnection;
    TcpServerBuilder serverBuilder = DlmsServer.tcpServerBuilder(this.port);

    if (this.useHdlc) {
      serverBuilder =
          serverBuilder.setSessionLayerFactory(
              ServerSessionLayerFactories.newHdlcSessionLayerFactory());
    }
    serverBuilder = serverBuilder.setConnectionListener(osgpServerConnectionListener);

    for (final Integer logicalDeviceId : LogicalDeviceIdsConverter.convert(this.logicalDeviceIds)) {
      LOGGER.info("preparing logical device {} on port {}", logicalDeviceId, this.port);
      final LogicalDevice logdev = this.buildDevice(logicalDeviceId).build();

      serverBuilder =
          serverBuilder
              .registerLogicalDevice(logdev)
              .setReferencingMethod(
                  ReferencingMethod.valueOf(this.referencingMethod.toUpperCase()));
    }
    serverConnection = serverBuilder.build();

    return serverConnection;
  }

  private LogicalDeviceBuilder buildDevice(final int logicalDeviceId) {
    final Map<String, CosemInterfaceObject> cosemClasses =
        (Map<String, CosemInterfaceObject>) this.applicationContext.getBean("cosemClasses");

    final LogicalDeviceBuilder builder =
        new LogicalDeviceBuilder()
            .addCosemClasses(cosemClasses.values().toArray(new CosemInterfaceObject[0]))
            .setDeviceId(DEVICE_ID)
            .setLogicalDeviceId(logicalDeviceId)
            .setLogicalDeviceName(LOGICAL_DEVICE_NAME)
            .setManufacturer(MANUFACTURER_ID)
            .setSecurityLevel(this.securityLevel);

    switch (SecurityLevel.fromNumber(this.securityLevel)) {
      case NO_SECURITY:
        builder.setClientId(DLMS_PUBLIC_CLIENT_ID);
        break;
      case LLS1, HLS5: // LLS1 needs same keys as HLS5
        builder
            .setClientId(DLMS_MANAGEMENT_CLIENT)
            .setAuthenticationKeyPath(
                this.keyPathProvider.getAuthenticationKeyFile(logicalDeviceId))
            .setEncryptionKeyPath(this.keyPathProvider.getEncryptionKeyFile(logicalDeviceId))
            .setMasterKeyPath(this.keyPathProvider.getMasterKeyFile(logicalDeviceId));
        break;
    }

    return builder;
  }

  @Override
  public void setApplicationContext(final ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }
}
