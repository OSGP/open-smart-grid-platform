// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec60870;

import java.util.TimeZone;
import org.opensmartgridplatform.iec60870.Iec60870AsduHandlerRegistry;
import org.opensmartgridplatform.iec60870.Iec60870ConnectionRegistry;
import org.opensmartgridplatform.iec60870.Iec60870Server;
import org.opensmartgridplatform.iec60870.Iec60870ServerEventListener;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduGenerator;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.support.CronTrigger;

@Configuration
@ComponentScan(basePackageClasses = {Iec60870Server.class})
@EnableScheduling
@PropertySource(
    value = "classpath:osgp-protocol-simulator-iec60870.properties",
    ignoreResourceNotFound = false)
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/SimulatorProtocolIec60870/config}",
    ignoreResourceNotFound = true)
public class Iec60870SimulatorConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870SimulatorConfig.class);

  @Value("${iec60870.simulator.connection.timeout}")
  private int connectionTimeout;

  @Value("${iec60870.simulator.sendEndOfInitialization:false}")
  private boolean sendEndOfInitialization;

  @Value("${iec60870.simulator.port:2404}")
  private int port;

  @Value("${job.asdu.generator.cron:0 0/1 * * * ?}")
  private String cronExpression;

  @Bean(destroyMethod = "stop")
  public Iec60870Server iec60870Server(
      final Iec60870ConnectionRegistry iec60870ConnectionRegistry,
      final Iec60870AsduHandlerRegistry iec60870AsduHandlerRegistry) {
    LOGGER.debug("Creating IEC60870 Simulator Bean.");

    final Iec60870Server server =
        new Iec60870Server(
            new Iec60870ServerEventListener(
                iec60870ConnectionRegistry,
                iec60870AsduHandlerRegistry,
                this.connectionTimeout,
                this.sendEndOfInitialization),
            this.port);

    LOGGER.debug("Starting IEC60870 Simulator.");
    server.start();

    return server;
  }

  /**
   * Bean used to generate ASDUs for testing purposes. By default no ASDUs will be generated. To
   * activate generation, add job.asdu.generator.enabled to the application's properties.
   */
  @Bean
  @ConditionalOnProperty("job.asdu.generator.enabled")
  public Iec60870AsduGeneratorService asduGeneratorService(
      final Iec60870ConnectionRegistry iec60870ConnectionRegistry,
      final Iec60870AsduGenerator iec60870AsduGenerator) {
    LOGGER.info("ASDU generator in simulator is enabled");

    final CronTrigger cronTrigger =
        new CronTrigger(this.cronExpression, TimeZone.getTimeZone("UTC"));

    return new Iec60870AsduGeneratorService(
        iec60870ConnectionRegistry, cronTrigger, iec60870AsduGenerator);
  }
}
