// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.application.config.messaging;

import javax.net.ssl.SSLException;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsBrokerSslSettings {

  private static final Logger LOGGER = LoggerFactory.getLogger(JmsBrokerSslSettings.class);

  private final String clientKeyStore;
  private final String clientKeyStorePwd;
  private final String trustKeyStore;
  private final String trustKeyStorePwd;

  public JmsBrokerSslSettings(
      final String clientKeyStore,
      final String clientKeyStorePwd,
      final String trustKeyStore,
      final String trustKeyStorePwd) {
    this.clientKeyStore = clientKeyStore;
    this.clientKeyStorePwd = clientKeyStorePwd;
    this.trustKeyStore = trustKeyStore;
    this.trustKeyStorePwd = trustKeyStorePwd;
  }

  /**
   * Sets the SSL settings for the ActiveMQ broker.
   *
   * @param factory The connection factory to which the SSL settings are applied.
   * @throws SSLException Thrown when applying the SSL settings fails or when the broker URL is
   *     null.
   */
  public void applyToFactory(final ActiveMQSslConnectionFactory factory) throws SSLException {
    try {
      if (factory.getBrokerURL().contains("ssl://")) {
        // SSL configuration.
        LOGGER.info(
            "Using SSL for ActiveMQ broker {}, set KeyStore and TrustStore",
            factory.getBrokerURL());
        LOGGER.info("Keystore: {}", this.clientKeyStore);
        factory.setKeyStore(this.clientKeyStore);
        factory.setKeyStorePassword(this.clientKeyStorePwd);
        LOGGER.info("Truststore: {}", this.trustKeyStore);
        factory.setTrustStore(this.trustKeyStore);
        factory.setTrustStorePassword(this.trustKeyStorePwd);
      } else {
        LOGGER.info("Not using SSL for ActiveMQ broker, don't set KeyStore and TrustStore");
      }
    } catch (final Exception e) {
      throw new SSLException(
          "Applying the SSL settings for the ActiveMQ connection factory failed", e);
    }
  }
}
