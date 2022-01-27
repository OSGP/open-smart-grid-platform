/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import com.hivemq.client.mqtt.MqttClientSslConfig;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.cucumber.core.ReadSettingsHelper;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationDefaults;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys;
import org.opensmartgridplatform.shared.application.config.mqtt.MqttClientSslConfigFactory;
import org.opensmartgridplatform.simulator.protocol.mqtt.SimulatorSpecPublishingClient;
import org.opensmartgridplatform.simulator.protocol.mqtt.spec.Message;
import org.opensmartgridplatform.simulator.protocol.mqtt.spec.SimulatorSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class MqttDeviceSteps {

  private static final Logger LOGGER = LoggerFactory.getLogger(MqttDeviceSteps.class);

  private static String[] getTopics(final Map<String, String> settings) {
    final String topic = getString(settings, PlatformDistributionAutomationKeys.MQTT_TOPIC);
    return StringUtils.isEmpty(topic) ? new String[] {} : StringUtils.split(topic, ',');
  }

  @When("an MQTT message is published")
  public void anMqttMessageIsPublished(final Map<String, String> parameters) throws IOException {

    final String host =
        getString(
            parameters,
            PlatformDistributionAutomationKeys.MQTT_HOST,
            PlatformDistributionAutomationDefaults.MQTT_HOST);
    final int port =
        getInteger(
            parameters,
            PlatformDistributionAutomationKeys.MQTT_PORT,
            PlatformDistributionAutomationDefaults.MQTT_PORT);
    final String[] topics = getTopics(parameters);

    final String payload = parameters.get(PlatformDistributionAutomationKeys.PAYLOAD);
    LOGGER.info("Payload: {}", payload);

    for (final String topic : topics) {
      this.startPublishingClient(host, port, topic, payload, parameters);
    }
  }

  private void startPublishingClient(
      final String host,
      final int port,
      final String topic,
      final String payload,
      final Map<String, String> parameters) {
    final SimulatorSpec spec = new SimulatorSpec(host, port);
    spec.setStartupPauseMillis(2000);
    final Message message = new Message(topic, payload, 10000);
    final Message[] messages = {message};
    spec.setMessages(messages);

    MqttClientSslConfig mqttClientSslConfig = null;

    final boolean mqttSslEnabled =
        ReadSettingsHelper.getBoolean(
            parameters,
            PlatformDistributionAutomationKeys.MQTT_SSL_ENABLED,
            PlatformDistributionAutomationDefaults.MQTT_SSL_ENABLED);

    if (mqttSslEnabled) {

      final String truststoreLocation =
          ReadSettingsHelper.getString(
              parameters,
              PlatformDistributionAutomationKeys.MQTT_SSL_TRUSTSTORE_LOCATION,
              PlatformDistributionAutomationDefaults.MQTT_SSL_TRUSTSTORE_LOCATION);
      final String truststorePassword =
          ReadSettingsHelper.getString(
              parameters,
              PlatformDistributionAutomationKeys.MQTT_SSL_TRUSTSTORE_PASSWORD,
              PlatformDistributionAutomationDefaults.MQTT_SSL_TRUSTSTORE_PASSWORD);
      final String truststoreType =
          ReadSettingsHelper.getString(
              parameters,
              PlatformDistributionAutomationKeys.MQTT_SSL_TRUSTSTORE_TYPE,
              PlatformDistributionAutomationDefaults.MQTT_SSL_TRUSTSTORE_TYPE);

      final ResourceLoader resourceLoader = new DefaultResourceLoader();
      final Resource trustStoreResource = resourceLoader.getResource(truststoreLocation);

      mqttClientSslConfig =
          MqttClientSslConfigFactory.getMqttClientSslConfig(
              trustStoreResource, truststorePassword, truststoreType);
    }

    final SimulatorSpecPublishingClient publishingClient =
        new SimulatorSpecPublishingClient(spec, mqttClientSslConfig);
    publishingClient.start();
  }
}
