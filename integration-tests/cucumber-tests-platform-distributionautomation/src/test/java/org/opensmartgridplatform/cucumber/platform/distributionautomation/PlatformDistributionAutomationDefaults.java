/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation;

public class PlatformDistributionAutomationDefaults {

  public static final String MQTT_HOST = "127.0.0.1";
  public static final Integer MQTT_PORT = 8883;
  public static final String MQTT_QOS = "AT_LEAST_ONCE";
  public static final String PROFILE = "default_controlled_station";

  public static final Boolean MQTT_SSL_ENABLED = true;
  public static final String MQTT_SSL_TRUSTSTORE_LOCATION = "classpath:mqtt_client_truststore.jks";
  public static final String MQTT_SSL_TRUSTSTORE_PASSWORD = "123456";
  public static final String MQTT_SSL_TRUSTSTORE_TYPE = "JKS";
}
