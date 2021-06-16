/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.config;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import javax.net.ssl.TrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@Conditional(MqttClientSslEnabledCondition.class)
public class MqttClientSslEnabledConfig {

  private static final Logger LOG = LoggerFactory.getLogger(MqttClientSslEnabledConfig.class);

  @Bean
  public MqttClientSslConfig mqttClientSslConfig(
      @Value("${mqtt.client.ssl.truststore.location}") final Resource truststoreLocation,
      @Value("${mqtt.client.ssl.truststore.password}") final String truststorePassword,
      @Value("${mqtt.client.ssl.truststore.type}") final String truststoreType) {

    LOG.info("MQTT SSL ENABLED.");

    return MqttClientSslConfig.builder()
        .trustManagerFactory(
            this.getTruststoreFactory(truststoreLocation, truststorePassword, truststoreType))
        .build();
  }

  private TrustManagerFactory getTruststoreFactory(
      final Resource trustStoreResource,
      final String trustStorePassword,
      final String trustStoreType) {

    try (InputStream in = trustStoreResource.getInputStream()) {
      LOG.info("Load truststore from path: {}", trustStoreResource.getURI());

      final KeyStore trustStore = KeyStore.getInstance(trustStoreType);
      trustStore.load(in, trustStorePassword.toCharArray());

      final TrustManagerFactory tmf =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(trustStore);

      return tmf;
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    } catch (final GeneralSecurityException e) {
      throw new SecurityException(e);
    }
  }
}
