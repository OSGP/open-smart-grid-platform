//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.distributionautomation;

public class PlatformDistributionAutomationDefaults {

  public static final String MQTT_HOST = "127.0.0.1";
  public static final Integer MQTT_PORT = 8883;
  public static final String MQTT_QOS = "AT_LEAST_ONCE";
  public static final Boolean MQTT_CLIENT_CLEAN_SESSION = Boolean.TRUE;
  public static final Integer MQTT_CLIENT_KEEP_ALIVE = 60;
  public static final String PROFILE = "default_controlled_station";

  public static final Boolean MQTT_SSL_ENABLED = true;
  public static final String MQTT_SSL_TRUSTSTORE_LOCATION = "classpath:mqtt_client_truststore.jks";
  public static final String MQTT_SSL_TRUSTSTORE_PASSWORD = "123456";
  public static final String MQTT_SSL_TRUSTSTORE_TYPE = "JKS";
}
