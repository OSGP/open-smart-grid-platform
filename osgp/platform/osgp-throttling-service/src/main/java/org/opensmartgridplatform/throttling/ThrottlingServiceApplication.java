// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.TimeZone;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(
    exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@PropertySource("classpath:osgp-throttling-service.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/ThrottlingService/config}", ignoreResourceNotFound = true)
public class ThrottlingServiceApplication extends SpringBootServletInitializer {

  public static void main(final String[] args) {
    /*
     * Main method for running the ThrottlingServiceApplication outside of a servlet container.
     * Note that this will use configuration from the classpath, as the environment resources from
     * context.xml are not defined.
     */
    SpringApplication.run(ThrottlingServiceApplication.class);
  }

  @Override
  protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    this.initializeLogging(builder);
    return builder.sources(ThrottlingServiceApplication.class);
  }

  private void initializeLogging(final SpringApplicationBuilder builder) {
    String logConfigLocation = null;
    try {
      logConfigLocation =
          (String) new InitialContext().lookup("java:comp/env/osgp/ThrottlingService/log-config");
      if (Files.exists(Paths.get(logConfigLocation))) {
        final Properties props = new Properties();
        props.setProperty("logging.config", logConfigLocation);
        builder.application().setDefaultProperties(props);
      }
    } catch (final NamingException e) {
      this.logger.warn(
          "NamingException initializing logging, staying with config by logback-spring.xml on classpath.",
          e);
    }
  }
}
