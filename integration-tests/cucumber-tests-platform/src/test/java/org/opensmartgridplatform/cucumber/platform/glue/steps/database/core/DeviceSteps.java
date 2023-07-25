// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getFloat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.core.keys.DeviceKeys;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.springframework.beans.factory.annotation.Autowired;

public class DeviceSteps extends BaseDeviceSteps {

  @Autowired private DeviceAuthorizationRepository deviceAuthorizationRepository;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private SmartMeterRepository smartMeterRepository;

  @Autowired private SsldRepository ssldRepository;

  @Then("^the mbus primary address of device \"([^\"]*)\" is cleared$")
  public void theMBusPrimaryAddressIsCleared(final String gMeter) {
    final SmartMeter mbusDevice = this.smartMeterRepository.findByDeviceIdentification(gMeter);

    assertThat(mbusDevice).as("No MbusDevice found").isNotNull();

    assertThat(mbusDevice.getMbusPrimaryAddress()).as("MbusPrimaryAddress must be empty").isNull();
  }

  @Then("^the channel of device \"([^\"]*)\" is cleared$")
  public void theChannelOfDeviceIsCleared(final String gMeter) {
    final SmartMeter mbusDevice = this.smartMeterRepository.findByDeviceIdentification(gMeter);

    assertThat(mbusDevice).as("No MbusDevice found").isNotNull();

    assertThat(mbusDevice.getChannel()).as("GatewayDevice must be empty").isNull();
  }

  /** Verify that the device exists in the database. */
  @Then("^the device exists")
  public void theDeviceExists(final Map<String, String> settings) throws Throwable {
    final Device device =
        Wait.untilAndReturn(
            () -> {
              final Device entity =
                  this.deviceRepository.findByDeviceIdentification(
                      settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
              if (entity == null) {
                throw new Exception(
                    "Device with identification ["
                        + settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION)
                        + "]");
              }

              return entity;
            });

    if (settings.containsKey(PlatformKeys.ALIAS)) {
      assertThat(device.getAlias()).isEqualTo(getString(settings, PlatformKeys.ALIAS));
    }
    if (settings.containsKey(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION)) {
      assertThat(device.getOwner().getOrganisationIdentification())
          .isEqualTo(getString(settings, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION));
    }
    if (settings.containsKey(PlatformKeys.CONTAINER_POSTALCODE)) {
      assertThat(device.getContainerAddress().getPostalCode())
          .isEqualTo(getString(settings, PlatformKeys.CONTAINER_POSTALCODE));
    }
    if (settings.containsKey(PlatformKeys.CONTAINER_CITY)) {
      assertThat(device.getContainerAddress().getCity())
          .isEqualTo(getString(settings, PlatformKeys.CONTAINER_CITY));
    }
    if (settings.containsKey(PlatformKeys.CONTAINER_STREET)) {
      assertThat(device.getContainerAddress().getStreet())
          .isEqualTo(getString(settings, PlatformKeys.CONTAINER_STREET));
    }
    if (settings.containsKey(PlatformKeys.CONTAINER_NUMBER)) {
      assertThat(device.getContainerAddress().getNumber())
          .isEqualTo(getInteger(settings, PlatformKeys.CONTAINER_NUMBER));
    }
    if (settings.containsKey(PlatformKeys.CONTAINER_MUNICIPALITY)) {
      assertThat(device.getContainerAddress().getMunicipality())
          .isEqualTo(getString(settings, PlatformKeys.CONTAINER_MUNICIPALITY));
    }
    if (settings.containsKey(PlatformKeys.KEY_LATITUDE)) {
      assertThat(device.getGpsCoordinates().getLatitude())
          .isEqualTo(getFloat(settings, PlatformKeys.KEY_LATITUDE));
    }
    if (settings.containsKey(PlatformKeys.KEY_LONGITUDE)) {
      assertThat(device.getGpsCoordinates().getLongitude())
          .isEqualTo(getFloat(settings, PlatformKeys.KEY_LONGITUDE));
    }
    if (settings.containsKey(PlatformKeys.KEY_ACTIVATED)) {
      assertThat(device.isActivated()).isEqualTo(getBoolean(settings, PlatformKeys.KEY_ACTIVATED));
    }
    if (settings.containsKey(PlatformKeys.KEY_DEVICE_LIFECYCLE_STATUS)) {
      assertThat(device.getDeviceLifecycleStatus())
          .isEqualTo(
              getEnum(
                  settings, PlatformKeys.KEY_DEVICE_LIFECYCLE_STATUS, DeviceLifecycleStatus.class));
    }
    if (settings.containsKey(PlatformKeys.KEY_HAS_SCHEDULE)
        || settings.containsKey(PlatformKeys.KEY_PUBLICKEYPRESENT)) {
      final Ssld ssld =
          this.ssldRepository.findByDeviceIdentification(
              getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION));

      if (settings.containsKey(PlatformKeys.KEY_HAS_SCHEDULE)) {
        assertThat(ssld.getHasSchedule())
            .isEqualTo(getBoolean(settings, PlatformKeys.KEY_HAS_SCHEDULE));
      }
      if (settings.containsKey(PlatformKeys.KEY_PUBLICKEYPRESENT)) {
        assertThat(ssld.isPublicKeyPresent())
            .isEqualTo(getBoolean(settings, PlatformKeys.KEY_PUBLICKEYPRESENT));
      }
    }
    if (settings.containsKey(PlatformKeys.KEY_DEVICE_MODEL_MODELCODE)) {
      assertThat(device.getDeviceModel().getModelCode())
          .isEqualTo(getString(settings, PlatformKeys.KEY_DEVICE_MODEL_MODELCODE));
    }
  }

  /** Checks whether the device does not exist in the database. */
  @Then("^the device with id \"([^\"]*)\" should be removed$")
  public void theDeviceShouldBeRemoved(final String deviceIdentification) throws Throwable {
    Wait.until(
        () -> {
          final Device entity =
              this.deviceRepository.findByDeviceIdentification(deviceIdentification);
          assertThat(entity)
              .as("Device with identification [" + deviceIdentification + "] should be removed")
              .isNull();

          final List<DeviceAuthorization> devAuths =
              this.deviceAuthorizationRepository.findByDevice(entity);
          assertThat(devAuths.isEmpty())
              .as(
                  "DeviceAuthorizations for device with identification ["
                      + deviceIdentification
                      + "] should be removed")
              .isTrue();
        });
  }

  /** Checks whether the device does not exist in the database. */
  @Then("^the device with id \"([^\"]*)\" does not exist$")
  public void theDeviceWithIdDoesNotExist(final String deviceIdentification) throws Throwable {
    Wait.until(
        () -> {
          final Device entity =
              this.deviceRepository.findByDeviceIdentification(deviceIdentification);
          assertThat(entity)
              .as("Device with identification [" + deviceIdentification + "]")
              .isNull();
        });
  }

  @Then("^the device with device identification \"([^\"]*)\" should be active$")
  public void theDeviceWithDeviceIdentificationShouldBeActive(final String deviceIdentification)
      throws Throwable {

    Wait.until(
        () -> {
          final Device entity =
              this.deviceRepository.findByDeviceIdentification(deviceIdentification);
          assertThat(entity)
              .as("Device with identification [" + deviceIdentification + "]")
              .isNotNull();

          assertThat(entity.getDeviceLifecycleStatus().equals(DeviceLifecycleStatus.IN_USE))
              .as("Entity is inactive")
              .isTrue();
        });
  }

  @Then("^the device with device identification \"([^\"]*)\" should be inactive$")
  public void theDeviceWithDeviceIdentificationShouldBeInActive(final String deviceIdentification)
      throws Throwable {
    Wait.until(
        () -> {
          final Device entity =
              this.deviceRepository.findByDeviceIdentification(deviceIdentification);
          assertThat(entity)
              .as("Device with identification [" + deviceIdentification + "]")
              .isNotNull();

          assertThat(entity.getDeviceLifecycleStatus().equals(DeviceLifecycleStatus.IN_USE))
              .as("Entity is active")
              .isFalse();
        });
  }

  /** Checks whether the device exists in the database.. */
  @Then("^the device with id \"([^\"]*)\" exists$")
  public void theDeviceWithIdExists(final String deviceIdentification) {
    Wait.until(
        () -> {
          final Device entity =
              this.deviceRepository.findByDeviceIdentification(deviceIdentification);
          assertThat(entity)
              .as("Device with identification [" + deviceIdentification + "]")
              .isNotNull();

          final List<DeviceAuthorization> devAuths =
              this.deviceAuthorizationRepository.findByDevice(entity);

          assertThat(entity).as("No entity found").isNotNull();
          assertThat(devAuths.size() > 0).as("DeviceAuthorizations amount is not > 0").isTrue();
        });
  }

  @Then("^the G-meter \"([^\"]*)\" is Decoupled from device \"([^\"]*)\"$")
  public void theGMeterIsDecoupledFromDevice(final String gMeter, final String eMeter) {
    Wait.until(
        () -> {
          final SmartMeter mbusDevice =
              this.smartMeterRepository.findByDeviceIdentification(gMeter);
          final Device gatewayDevice = this.deviceRepository.findByDeviceIdentification(eMeter);

          assertThat(gatewayDevice).as("No GatewayDevice found").isNotNull();
          assertThat(mbusDevice).as("No MbusDevice found").isNotNull();
          assertThat(mbusDevice.getGatewayDevice()).as("GatewayDevice must be empty").isNull();
        });
  }

  @Then(
      "^the M-Bus device \"([^\"]*)\" is coupled to device \"([^\"]*)\" on M-Bus channel \"([^\"]*)\" with PrimaryAddress \"([^\"]*)\"$")
  public void theMBusDeviceIsCoupledToDeviceOnMBusChannelWithPrimaryAddress(
      final String gMeter, final String eMeter, final Short channel, final Short primaryAddress)
      throws Throwable {
    Wait.until(
        () -> {
          final SmartMeter mbusDevice =
              this.smartMeterRepository.findByDeviceIdentification(gMeter);
          final Device gatewayDevice = this.deviceRepository.findByDeviceIdentification(eMeter);

          assertThat(gatewayDevice).as("No GatewayDevice found").isNotNull();
          assertThat(mbusDevice).as("No MbusDevice found").isNotNull();

          assertThat(mbusDevice.getGatewayDevice())
              .as("GatewayDevice does not match")
              .isEqualTo(gatewayDevice);
          assertThat(mbusDevice.getChannel()).as("Channel does not match").isEqualTo(channel);
          assertThat(mbusDevice.getMbusPrimaryAddress())
              .as("PrimaryAddress does not match")
              .isEqualTo(primaryAddress);
        });
  }

  @Then(
      "^the M-Bus device \"([^\"]*)\" is coupled to device \"([^\"]*)\" on M-Bus channel \"([^\"]*)\"$")
  public void theMBusDeviceIsCoupledToDeviceOnMBusChannel(
      final String gMeter, final String eMeter, final Short channel) throws Throwable {
    Wait.until(
        () -> {
          final SmartMeter mbusDevice =
              this.smartMeterRepository.findByDeviceIdentification(gMeter);
          final Device gatewayDevice = this.deviceRepository.findByDeviceIdentification(eMeter);

          assertThat(gatewayDevice).as("No GatewayDevice found").isNotNull();
          assertThat(mbusDevice).as("No MbusDevice found").isNotNull();

          assertThat(mbusDevice.getGatewayDevice())
              .as("GatewayDevice does not match")
              .isEqualTo(gatewayDevice);
          assertThat(mbusDevice.getChannel()).as("Channel does not match").isEqualTo(channel);
        });
  }

  @Then("^the mbus device \"([^\"]*)\" is not coupled to the device \"([^\"]*)\"$")
  public void theMbusDeviceIsNotCoupledToTheDevice(final String gMeter, final String eMeter) {
    Wait.until(
        () -> {
          final SmartMeter mbusDevice =
              this.smartMeterRepository.findByDeviceIdentification(gMeter);
          final Device gatewayDevice = this.deviceRepository.findByDeviceIdentification(eMeter);

          assertThat(gatewayDevice).as("No GatewayDevice found").isNotNull();
          assertThat(mbusDevice).as("No MbusDevice found").isNotNull();

          assertThat(mbusDevice.getGatewayDevice())
              .as("MbusDevice should not be coupled to this GatewayDevice")
              .isNotEqualTo(gatewayDevice);
        });
  }

  @Then("^the mbus device \"([^\"]*)\" has properties$")
  public void theMbusDeviceHasProperties(
      final String gMeter, final Map<String, String> properties) {
    final SmartMeter mbusDevice =
        Wait.untilAndReturn(
            () -> {
              final SmartMeter smartMeter =
                  this.smartMeterRepository.findByDeviceIdentification(gMeter);
              if (smartMeter == null) {
                throw new Exception("Device with identification [" + gMeter + "]");
              }
              return smartMeter;
            });

    assertThat(mbusDevice).as("No MbusDevice found").isNotNull();
    checkForProperty(properties, DeviceKeys.GATEWAY_DEVICE, () -> mbusDevice.getGatewayDevice());
    checkForProperty(properties, DeviceKeys.CHANNEL, () -> mbusDevice.getChannel());
    checkForProperty(
        properties, DeviceKeys.MBUS_PRIMARY_ADDRESS, () -> mbusDevice.getMbusPrimaryAddress());
  }

  private static void checkForProperty(
      final Map<String, String> properties,
      final String propertyName,
      final Supplier<Object> supplier) {
    if (properties.get(propertyName) != null) {
      assertThat(properties.get(propertyName)).hasToString("" + supplier.get());
    }
  }

  @Then("^the default values for the GPS coordinates remain for device (.+)$")
  public void theDefaultValuesForTheGpsCoordinatesRemainForDevice(
      final String deviceIdentification) {
    Wait.until(
        () -> {
          final Device device =
              this.deviceRepository.findByDeviceIdentification(deviceIdentification);

          assertThat(device).as("Device is null").isNotNull();
          assertThat(device.getGpsCoordinates()).as("GpsCoordinates is not null").isNull();
        });
  }

  @Then("^the device contains$")
  public void theDeviceContains(final Map<String, String> expectedEntity) {
    Wait.until(
        () -> {
          final Device device =
              this.deviceRepository.findByDeviceIdentification(
                  getString(expectedEntity, PlatformKeys.KEY_DEVICE_IDENTIFICATION));

          assertThat(device.getNetworkAddress())
              .as("IP address does not match")
              .isEqualTo(getString(expectedEntity, PlatformKeys.IP_ADDRESS));
        });
  }
}
