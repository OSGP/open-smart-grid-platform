// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class DeviceAuthorizationSteps {

  @Autowired private DeviceAuthorizationRepository deviceAuthorizationRepository;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private OrganisationRepository organizationRepository;

  public boolean hasAuthorization;

  /**
   * Generic method which adds a device authorization using the settings.
   *
   * @param settings The settings for the device authorization to be used.
   */
  @Given("^a device authorization$")
  @Transactional("txMgrCore")
  public void aDeviceAuthorization(final Map<String, String> settings) {

    final Device device =
        this.deviceRepository.findByDeviceIdentification(
            getString(
                settings,
                PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    final Organisation organization =
        this.organizationRepository.findByOrganisationIdentification(
            getString(
                settings,
                PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

    final DeviceFunctionGroup functionGroup =
        getEnum(
            settings,
            PlatformKeys.KEY_DEVICE_FUNCTION_GROUP,
            DeviceFunctionGroup.class,
            DeviceFunctionGroup.OWNER);

    final DeviceAuthorization authorization = device.addAuthorization(organization, functionGroup);

    this.deviceAuthorizationRepository.save(authorization);
  }

  /**
   * The test passes if the device authorizations are created as expected in the database.
   *
   * @param expectedEntity The expected settings.
   */
  @Then("^the entity device authorization exists$")
  public void thenTheEntityDeviceAuthorizationExists(final Map<String, String> expectedEntity) {
    this.thenTheEntityDeviceAuthorizationsExist(expectedEntity);
  }

  /**
   * The test passes if all the device authorizations are created as expected in the database.
   *
   * @param expectedEntity The expected settings.
   */
  @Then("^the entity device authorizations exist$")
  public void thenTheEntityDeviceAuthorizationsExist(final Map<String, String> expectedEntity) {
    final String authorizationsStringList =
        expectedEntity.get(PlatformKeys.KEY_DEVICE_FUNCTION_GROUP);
    final String[] authorizations = StringUtils.split(authorizationsStringList, ',');

    final Device device =
        this.deviceRepository.findByDeviceIdentification(
            expectedEntity.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));

    Wait.until(
        () -> {
          final List<String> storedDeviceAuthorizations =
              this.deviceAuthorizationRepository.findByDevice(device).stream()
                  .map(da -> da.getFunctionGroup().name())
                  .collect(Collectors.toList());
          assertThat(storedDeviceAuthorizations).contains(authorizations);
        });

    final List<DeviceAuthorization> storedDeviceAuthorizations =
        this.deviceAuthorizationRepository.findByDevice(device);

    final String organizationIdentification =
        getString(
            expectedEntity,
            PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
            PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);

    for (final String authorization : authorizations) {
      assertThat(
              this.entityDeviceHasAuthorization(
                  authorization, organizationIdentification, storedDeviceAuthorizations))
          .isTrue();
    }
  }

  /**
   * Checks if the expected authorization of a certain organization is in the list of stored
   * authorizations.
   */
  private boolean entityDeviceHasAuthorization(
      final String expectedAuthorization,
      final String expectedOrganizationIdentification,
      final List<DeviceAuthorization> storedAuthorizations) {
    boolean hasExpectedAuthorization = false;

    final DeviceFunctionGroup expectedFunctionGroup =
        org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup.valueOf(
            expectedAuthorization);

    for (final DeviceAuthorization deviceAuthorization : storedAuthorizations) {
      if (expectedOrganizationIdentification.equals(
              deviceAuthorization.getOrganisation().getOrganisationIdentification())
          && expectedFunctionGroup == deviceAuthorization.getFunctionGroup()) {
        hasExpectedAuthorization = true;
        break;
      }
    }

    return hasExpectedAuthorization;
  }

  /**
   * The test passes if the device authorizations are NOT created as expected in the database.
   *
   * @param expectedEntity The expected settings.
   */
  @Then("^the entity device authorization does not exist$")
  public void thenTheEntityDeviceAuthorizationDoesNotExist(
      final Map<String, String> expectedEntity) {
    final String expectedAuthorization = expectedEntity.get(PlatformKeys.KEY_DEVICE_FUNCTION_GROUP);
    final Device device =
        this.deviceRepository.findByDeviceIdentification(
            expectedEntity.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));

    Wait.until(
        () -> {
          final List<DeviceAuthorization> storedDeviceAuthorizations =
              this.deviceAuthorizationRepository.findByDevice(device);

          final String organizationIdentification =
              getString(
                  expectedEntity,
                  PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                  PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);

          assertThat(
                  this.entityDeviceHasAuthorization(
                      expectedAuthorization,
                      organizationIdentification,
                      storedDeviceAuthorizations))
              .isFalse();
        });
  }
}
