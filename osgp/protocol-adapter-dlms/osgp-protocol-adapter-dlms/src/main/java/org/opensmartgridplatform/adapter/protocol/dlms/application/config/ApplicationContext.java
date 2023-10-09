// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import java.time.Instant;
import java.time.ZoneId;
import org.opensmartgridplatform.adapter.protocol.jasper.config.JasperWirelessConfig;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.config.MetricsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(
    basePackages = {
      "org.opensmartgridplatform.adapter.protocol.dlms",
      "org.opensmartgridplatform.shared.domain.services",
      "org.opensmartgridplatform.shared.security"
    })
@EnableTransactionManagement()
@EnableIntegration
@Import({
  MessagingConfig.class,
  DlmsPersistenceConfig.class,
  JasperWirelessConfig.class,
  MetricsConfig.class
})
@PropertySource("classpath:osgp-adapter-protocol-dlms.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolDlms/config}", ignoreResourceNotFound = true)
public class ApplicationContext extends AbstractConfig {

  private static final String LOCAL_TIME_ZONE_IDENTIFIER = "Europe/Paris";
  private static final ZoneId LOCAL_TIME_ZONE = ZoneId.of(LOCAL_TIME_ZONE_IDENTIFIER);
  public static final int SECONDS_PER_MINUTE = 60;
  private static final int TIME_ZONE_OFFSET_MINUTES =
      LOCAL_TIME_ZONE.getRules().getStandardOffset(Instant.now()).getTotalSeconds()
          / SECONDS_PER_MINUTE;

  // === Time zone config ===

  @Bean
  public String localTimeZoneIdentifier() {
    return LOCAL_TIME_ZONE_IDENTIFIER;
  }

  @Bean
  public ZoneId localTimeZone() {
    return LOCAL_TIME_ZONE;
  }

  @Bean
  public Integer timeZoneOffsetMinutes() {
    return TIME_ZONE_OFFSET_MINUTES;
  }
}
