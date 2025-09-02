/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.cucumber.platform.smartmetering;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Objects;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;

@Slf4j
public class CucumberTestsPlatformSmartmeteringProperties {

  private final Properties properties;

  public CucumberTestsPlatformSmartmeteringProperties() {
    this.properties = new Properties();
    try {
      final InputStream inputStream =
          ClassLoader.getSystemResourceAsStream("cucumber-tests-platform-smartmetering.properties");
      this.properties.load(inputStream);
      if (inputStream != null) {
        inputStream.close();
      }
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public InetAddress getNetworkAddress() {
    InetAddress inetAddress;
    try {
      final String configuredAddress =
          this.properties.getProperty("simulator.network.inet.address");
      final String simulatorNetworkAddress =
          Objects.requireNonNullElse(configuredAddress, PlatformDefaults.LOCALHOST);
      log.info(
          "Using {} network address for simulator (simulator.network.inet.address): {}",
          configuredAddress != null ? "configured" : "default",
          simulatorNetworkAddress);
      inetAddress = InetAddress.getByName(simulatorNetworkAddress);
    } catch (final IOException e) {
      log.error(
          "Network address for simulator (simulator.network.inet.address) is not a valid Inet address.");
      inetAddress = null;
    }
    return inetAddress;
  }

  public int getAlarmNotificationsPort() {
    return Integer.parseInt(this.properties.getProperty("alarm.notifications.port"));
  }

  public String getAlarmNotificationsHost() {
    return this.properties.getProperty("alarm.notifications.host");
  }

  public String getServiceEndpointHost() {
    return this.properties.getProperty("service.endpoint.host");
  }
}
