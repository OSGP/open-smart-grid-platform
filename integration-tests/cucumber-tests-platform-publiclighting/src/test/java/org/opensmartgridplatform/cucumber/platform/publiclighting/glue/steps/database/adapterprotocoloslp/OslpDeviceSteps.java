// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.database.adapterprotocoloslp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.OslpDevice;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.PendingSetScheduleRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.repositories.OslpDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.repositories.PendingSetScheduleRequestRepository;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.core.SsldDeviceSteps;
import org.opensmartgridplatform.dto.valueobjects.ScheduleMessageDataContainerDto;
import org.springframework.beans.factory.annotation.Autowired;

/** OSLP device specific steps. */
public class OslpDeviceSteps {

  public static final String DEFAULT_DEVICE_UID = "dGVzdDEyMzQ1Njc4";
  private static final String DEVICE_PUBLIC_KEY =
      "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEFhUImXFJdqmputquVAc2CPdnn9Ju"
          + "00M3m/Ice7wABNN+oAYKQbw/OceqvZmFF1+r4nO/vCm/f1JO5nEorE2jNQ==";

  @Autowired private OslpDeviceRepository oslpDeviceRepository;

  @Autowired private SsldDeviceSteps ssldDeviceSteps;

  @Autowired private PendingSetScheduleRequestRepository pendingSetScheduleRequestRepository;

  @Given("^an ssld oslp device$")
  public void anSsldOslpDevice(final Map<String, String> settings) {

    // First create the device itself in the OSGP core database
    this.ssldDeviceSteps.anSsldDevice(settings);

    // Now create the OSLP device in the OSLP database
    final String deviceIdentification =
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);
    final OslpDevice device =
        new OslpDevice(
            getString(settings, PlatformKeys.KEY_DEVICE_UID, DEFAULT_DEVICE_UID),
            deviceIdentification,
            getString(
                settings, PlatformKeys.KEY_DEVICE_TYPE, PlatformDefaults.DEFAULT_DEVICE_TYPE));
    device.setSequenceNumber(0);
    device.setRandomDevice(0);
    device.setRandomPlatform(0);
    device.updatePublicKey(DEVICE_PUBLIC_KEY);
    this.oslpDeviceRepository.save(device);
  }

  @Given("^a pending set schedule request that expires within \"([^\"]*)\" minutes$")
  public void aPendingSetScheduleRequest(
      final int expiresInMinutes, final Map<String, String> settings) {

    final String deviceIdentification =
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);
    final String deviceUid =
        getString(settings, PlatformKeys.KEY_DEVICE_UID, PlatformDefaults.DEVICE_UID);
    final String organisationIdentification =
        getString(
            settings,
            PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
            PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);

    final Instant expireDateTime = ZonedDateTime.now().plusMinutes(expiresInMinutes).toInstant();

    // just add a dummy DeviceRequest and a dummy
    // ScheduleMessageDataContainerDto
    final PendingSetScheduleRequest pendingSetScheduleRequest =
        PendingSetScheduleRequest.builder()
            .deviceIdentification(deviceIdentification)
            .deviceUid(deviceUid)
            .expiredAt(expireDateTime)
            .deviceRequest(
                new DeviceRequest(organisationIdentification, deviceIdentification, null, 4))
            .scheduleMessageDataContainerDto(
                new ScheduleMessageDataContainerDto.Builder(null).build())
            .build();

    this.pendingSetScheduleRequestRepository.save(pendingSetScheduleRequest);
  }

  @Given("^a single ssld oslp device$")
  public void ssldOslpDevice(final Map<String, String> settings) {
    this.ssldOslpDevices(1, settings);
  }

  @Given("^(\\d++) ssld oslp devices$")
  public void ssldOslpDevices(final int numberOfDevices, final Map<String, String> settings) {

    for (int i = 0; i < numberOfDevices; i++) {
      final String deviceIdentification = "TST-" + (i + 1);
      final Map<String, String> deviceSettings = new HashMap<>();
      deviceSettings.put(PlatformKeys.KEY_DEVICE_IDENTIFICATION, deviceIdentification);
      deviceSettings.put(
          PlatformKeys.KEY_DEVICE_UID,
          Base64.encodeBase64String(deviceIdentification.getBytes(StandardCharsets.US_ASCII)));
      deviceSettings.put(PlatformKeys.KEY_VERSION, "0");
      deviceSettings.putAll(settings);
      this.anSsldOslpDevice(deviceSettings);
    }
  }

  @Then("^the ssld oslp device contains$")
  public void theSsldOslpDeviceContains(final Map<String, String> expectedEntity) {

    Wait.until(
        () -> {
          final OslpDevice entity =
              this.oslpDeviceRepository.findByDeviceIdentification(
                  getString(expectedEntity, PlatformKeys.KEY_DEVICE_IDENTIFICATION));

          assertThat(entity.getDeviceType())
              .isEqualTo(getString(expectedEntity, PlatformKeys.KEY_DEVICE_TYPE));
          assertThat(entity.getDeviceUid())
              .isEqualTo(getString(expectedEntity, PlatformKeys.KEY_DEVICE_UID));
        });

    this.ssldDeviceSteps.theSsldDeviceContains(expectedEntity);
  }
}
