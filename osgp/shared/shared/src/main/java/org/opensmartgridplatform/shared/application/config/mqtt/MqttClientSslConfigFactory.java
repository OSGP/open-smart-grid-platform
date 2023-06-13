// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.application.config.mqtt;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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

  public static MqttClientSslConfig getMqttClientSslConfig(final Resource certFileLocation) {

    return MqttClientSslConfig.builder()
        .trustManagerFactory(getTruststoreFactory(certFileLocation))
        .build();
  }

  private static TrustManagerFactory getTruststoreFactory(
      final Resource trustStoreResource,
      final String trustStorePassword,
      final String trustStoreType) {

    try (final InputStream in = trustStoreResource.getInputStream()) {
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

  /**
   * Creates a TrustManagerFactory from a .crt file.
   *
   * @param certFileLocation Location of the .crt file
   * @return TrustManagerFactory
   */
  private static TrustManagerFactory getTruststoreFactory(final Resource certFileLocation) {
    try (final InputStream in = certFileLocation.getInputStream()) {
      LOGGER.info("Load truststore from path: {}", certFileLocation.getURI());

      final CertificateFactory cf = CertificateFactory.getInstance("X.509");
      final X509Certificate caCert = (X509Certificate) cf.generateCertificate(in);

      final TrustManagerFactory tmf =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      keyStore.load(null); // You don't need the KeyStore instance to come from a file.
      keyStore.setCertificateEntry("caCert", caCert);

      tmf.init(keyStore);

      return tmf;
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    } catch (final GeneralSecurityException e) {
      throw new SecurityException(e);
    }
  }
}
