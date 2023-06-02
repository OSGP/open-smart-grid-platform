//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemPagingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.Transactional;

public class DeviceLogItemSteps {

  @Autowired private DeviceLogItemPagingRepository deviceLogItemRepository;

  @Autowired
  @Qualifier("txMgrLogging")
  JpaTransactionManager txMgrLogging;

  @Given("^I have a device log item$")
  @Transactional("txMgrLogging")
  public void iHaveADeviceLogItem(final Map<String, String> settings) {

    final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION);
    final String deviceUid =
        getString(settings, PlatformKeys.KEY_DEVICE_UID, PlatformDefaults.DEVICE_UID);
    final String decodedMessage = "O S L P";
    final String encodedMessage = "0x4F 0x53 0x4C 0x50";
    final boolean incoming = true;
    final String organisationIdentification =
        getString(
            settings,
            PlatformKeys.KEY_ORGANIZATION,
            PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
    final Integer payloadMessageSerializedSize = 4;
    final boolean valid = true;

    final DeviceLogItem deviceLogItem =
        new DeviceLogItem.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withDeviceUid(deviceUid)
            .withDecodedMessage(decodedMessage)
            .withEncodedMessage(encodedMessage)
            .withIncoming(incoming)
            .withOrganisationIdentification(organisationIdentification)
            .withPayloadMessageSerializedSize(payloadMessageSerializedSize)
            .withValid(valid)
            .build();

    final DeviceLogItem savedDeviceLogItem = this.deviceLogItemRepository.save(deviceLogItem);

    final String modificationTimeString = getString(settings, PlatformKeys.KEY_MODIFICATION_TIME);
    if (modificationTimeString != null) {
      final ZonedDateTime modificationTime = ZonedDateTime.parse(modificationTimeString);
      this.updateModificationTime(savedDeviceLogItem.getId(), modificationTime);
    }
  }

  private void updateModificationTime(
      final long deviceLogItemId, final ZonedDateTime modificationTime) {
    if (modificationTime != null) {
      this.deviceLogItemRepository.setModificationTime(
          deviceLogItemId, Date.from(modificationTime.toInstant()));
    }
  }

  @Given("^I have (\\d+) device log items$")
  public void iHaveDeviceLogItems(final int number, final Map<String, String> settings) {
    for (int i = 0; i < number; i++) {
      this.iHaveADeviceLogItem(settings);

      // Sleep a couple milliseconds to space out the creation /
      // modification times of the device log item records.
      // This ensures the messages are ordered.
      try {
        Thread.sleep(5);
      } catch (final InterruptedException e) {
        // Ignore this InterruptedException.
      }
    }
  }

  @Then(
      "^the get administrative status communication for device \"([^\"]*)\" should be in the device_log_item "
          + "table$")
  public void theGetAdministrativeStatusCommunicationForDeviceShouldBeInTheDeviceLogItemTable(
      final String deviceIdentification) {

    final Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
    final List<org.opensmartgridplatform.logging.domain.entities.DeviceLogItem> deviceLogItems =
        this.deviceLogItemRepository
            .findByDeviceIdentification(deviceIdentification, pageable)
            .getContent();
    assertThat(this.countGetAdministrativeStatusLogItems(deviceLogItems) > 0)
        .as("number of device log items for " + deviceIdentification)
        .isTrue();
  }

  @Then(
      "^the get administrative status communication for device \"([^\"]*)\" should not be in the device_log_item table$")
  public void theGetAdministrativeStatusCommunicationForDeviceShouldNotBeInTheDeviceLogItemTable(
      final String deviceIdentification) {

    final Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
    final List<org.opensmartgridplatform.logging.domain.entities.DeviceLogItem> deviceLogItems =
        this.deviceLogItemRepository
            .findByDeviceIdentification(deviceIdentification, pageable)
            .getContent();
    assertThat(this.countGetAdministrativeStatusLogItems(deviceLogItems))
        .as("number of device log items for " + deviceIdentification)
        .isEqualTo(0);
  }

  private long countGetAdministrativeStatusLogItems(final List<DeviceLogItem> deviceLogItems) {
    return deviceLogItems.stream()
        .filter(deviceLogItem -> this.isGetAdministrativeStatusLogItem(deviceLogItem))
        .count();
  }

  private boolean isGetAdministrativeStatusLogItem(final DeviceLogItem deviceLogItem) {
    return deviceLogItem.getEncodedMessage() != null
        && deviceLogItem.getDecodedMessage().contains("GET_ADMINISTRATIVE_STATUS");
  }
}
