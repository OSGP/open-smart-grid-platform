/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config.mqtt;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import javax.net.ssl.TrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

public class MqttClientSslConfigFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(MqttClientSslConfigFactory.class);

  private MqttClientSslConfigFactory() {
    // hide implicit constructor
  }

  public static MqttClientSslConfig getMqttClientSslConfig(
      final Resource truststoreLocation,
      final String truststorePassword,
      final String truststoreType) {

    return MqttClientSslConfig.builder()
        .trustManagerFactory(
            getTruststoreFactory(truststoreLocation, truststorePassword, truststoreType))
        .build();
  }

  private static TrustManagerFactory getTruststoreFactory(
      final Resource trustStoreResource,
      final String trustStorePassword,
      final String trustStoreType) {

    try (InputStream in = trustStoreResource.getInputStream()) {
      LOGGER.info("Load truststore from path: {}", trustStoreResource.getURI());

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
