// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.hooks.SimulatePushedAlarmsHooks;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ServiceEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class PushNotificationAlarm {

  private static final Logger LOGGER = LoggerFactory.getLogger(PushNotificationAlarm.class);

  @Autowired private ServiceEndpoint serviceEndpoint;

  @When("^an alarm is received from a device$")
  public void anAlarmIsReceivedFromAKnownDevice(final Map<String, String> settings)
      throws Throwable {
    this.simulateAlarm(settings, new byte[] {0x2C, 0x00, 0x00, 0x01, 0x02});
  }

  @When("^an \"Power Up\" alarm is received from a device$")
  public void anPowerUpAlarmIsReceivedFromADevice(final Map<String, String> settings)
      throws Throwable {
    this.simulateAlarm(settings, new byte[] {0x2C, 0x00, 0x00, 0x00, 0x04});
  }

  @When("^an \"Phase Outage Detected L1\" alarm is received from a device$")
  public void anPhaseOutageDetectedL1IsReceivedFromADevice(final Map<String, String> settings)
      throws Throwable {
    this.simulateAlarm(settings, new byte[] {0x2C, 0x10, 0x00, 0x00, 0x00});
  }

  @When("^a \"New M-Bus device discovered channel (\\d)\" alarm is received from a device$")
  public void aNewMBusDeviceDiscoveredChannelAlarmIsReceivedFromADevice(
      final int channel, final Map<String, String> settings) throws Throwable {
    switch (channel) {
      case 1:
        this.simulateAlarm(settings, new byte[] {0x2C, 0x01, 0x00, 0x00, 0x00});
        break;
      case 2:
        this.simulateAlarm(settings, new byte[] {0x2C, 0x02, 0x00, 0x00, 0x00});
        break;
      case 3:
        this.simulateAlarm(settings, new byte[] {0x2C, 0x04, 0x00, 0x00, 0x00});
        break;
      case 4:
        this.simulateAlarm(settings, new byte[] {0x2C, 0x08, 0x00, 0x00, 0x00});
        break;
      default:
        throw new IllegalArgumentException(
            String.format(
                "'New M-Bus device discovered channel %d' is not an valid alarm", channel));
    }
  }

  private void simulateAlarm(final Map<String, String> settings, final byte[] alarmsToPush) {
    try {
      final String deviceIdentification =
          getString(
              settings,
              PlatformKeys.KEY_DEVICE_IDENTIFICATION,
              PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);
      SimulatePushedAlarmsHooks.simulateAlarm(
          deviceIdentification,
          alarmsToPush,
          this.serviceEndpoint.getAlarmNotificationsHost(),
          this.serviceEndpoint.getAlarmNotificationsPort());
    } catch (final Exception e) {
      LOGGER.error("Error occured simulateAlarm: ", e);
    }
  }
}
