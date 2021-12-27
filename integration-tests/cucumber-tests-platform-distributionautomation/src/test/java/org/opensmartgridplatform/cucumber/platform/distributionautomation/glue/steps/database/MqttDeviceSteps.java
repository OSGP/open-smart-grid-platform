/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps.database;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Given;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities.MqttDevice;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.repositories.MqttDeviceRepository;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationDefaults;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.core.RtuDeviceSteps;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.springframework.beans.factory.annotation.Autowired;

public class MqttDeviceSteps {

  private static final String DEFAULT_DEVICE_TYPE = "PSD";
  private static final String DEFAULT_PROTOCOL = "MQTT";
  private static final String DEFAULT_PROTOCOL_VERSION = "3.1.1";
  private static final String DEFAULT_LAST_COMMUNICATION_TIME = "yesterday";

  private static final Map<String, String> MQTT_DEFAULT_SETTINGS;

  @Autowired private RtuDeviceSteps rtuDeviceSteps;

  @Autowired private MqttDeviceRepository mqttDeviceRepository;

  static {
    final Map<String, String> settingsMap = new HashMap<>();
    settingsMap.put(PlatformKeys.KEY_DEVICE_TYPE, DEFAULT_DEVICE_TYPE);
    settingsMap.put(PlatformKeys.KEY_PROTOCOL, DEFAULT_PROTOCOL);
    settingsMap.put(PlatformKeys.KEY_PROTOCOL_VERSION, DEFAULT_PROTOCOL_VERSION);
    settingsMap.put(PlatformKeys.KEY_LAST_COMMUNICATION_TIME, DEFAULT_LAST_COMMUNICATION_TIME);
    MQTT_DEFAULT_SETTINGS = Collections.unmodifiableMap(settingsMap);
  }

  @Given("an MQTT device")
  public void anMQTTDevice(final Map<String, String> settings) {
    final Map<String, String> mqttSettings =
        SettingsHelper.addAsDefaults(settings, MQTT_DEFAULT_SETTINGS);
    this.addDeviceToCore(mqttSettings);
    this.addDeviceToProtocolAdapter(mqttSettings);
  }

  private void addDeviceToCore(final Map<String, String> settings) {
    this.rtuDeviceSteps.anRtuDevice(settings);
    this.rtuDeviceSteps.updateRtuDevice(settings);
  }

  private void addDeviceToProtocolAdapter(final Map<String, String> settings) {

    final String deviceIdentification =
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);
    final MqttDevice device = new MqttDevice(deviceIdentification);
    device.setHost(
        getString(
            settings,
            PlatformDistributionAutomationKeys.MQTT_HOST,
            PlatformDistributionAutomationDefaults.MQTT_HOST));
    device.setPort(
        getInteger(
            settings,
            PlatformDistributionAutomationKeys.MQTT_PORT,
            PlatformDistributionAutomationDefaults.MQTT_PORT));
    device.setTopics(getTopics(settings));
    device.setQos(
        getString(
            settings,
            PlatformDistributionAutomationKeys.MQTT_QOS,
            PlatformDistributionAutomationDefaults.MQTT_QOS));
    this.mqttDeviceRepository.save(device);
  }

  private static String[] getTopics(final Map<String, String> settings) {
    final String topic = getString(settings, PlatformDistributionAutomationKeys.MQTT_TOPIC);
    return StringUtils.isEmpty(topic) ? new String[] {} : StringUtils.split(topic, ',');
  }
}
