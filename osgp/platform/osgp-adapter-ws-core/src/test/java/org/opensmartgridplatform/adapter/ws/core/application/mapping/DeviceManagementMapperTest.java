//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceActivatedFilterType.ACTIVE;
import static org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceExternalManagedFilterType.EXTERNAL_MANAGEMENT;
import static org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceInMaintetanceFilterType.IN_MAINTENANCE;
import static org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FirmwareModuleFilterType.ACTIVE_FIRMWARE;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.core.common.Address;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceFilter;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceLifecycleStatus;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.RelayType;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdatedDevice;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceActivatedFilterType;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceExternalManagedFilterType;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceInMaintenanceFilterType;

public class DeviceManagementMapperTest {

  private static final String STREET = "Main street";
  private static final int HOUSE_NUMBER = 10;
  private static final String HOUSE_NUMBER_ADDITION = "a";
  private static final String POSTAL_CODE = "1000 AA";
  private static final String CITY = "City";
  private static final String MUNICIPALITY = "Municipality";
  private static final int RELAY_EXTERNAL_ID = 3;
  private static final int RELAY_INTERNAL_ID = 2;
  private static final String DOS_ALIAS = "dosAlias";

  private DeviceManagementMapper mapper;

  private org.opensmartgridplatform.domain.core.valueobjects.DeviceFilter expectedMapped(
      final DeviceFilter deviceFilter) {
    final org.opensmartgridplatform.domain.core.valueobjects.DeviceFilter expected =
        new org.opensmartgridplatform.domain.core.valueobjects.DeviceFilter();
    expected.setOrganisationIdentification(deviceFilter.getOrganisationIdentification());
    expected.setAlias(deviceFilter.getAlias());
    expected.setDeviceIdentification(deviceFilter.getDeviceIdentification());
    expected.setCity(deviceFilter.getCity());
    expected.setPostalCode(deviceFilter.getPostalCode());
    expected.setStreet(deviceFilter.getStreet());
    expected.setNumber(deviceFilter.getNumber());
    expected.setMunicipality(deviceFilter.getMunicipality());
    expected.setDeviceExternalManaged(DeviceExternalManagedFilterType.EXTERNAL_MANAGEMENT);
    expected.setDeviceActivated(DeviceActivatedFilterType.ACTIVE);
    expected.setDeviceInMaintenance(DeviceInMaintenanceFilterType.IN_MAINTENANCE);
    expected.setSortDir(deviceFilter.getSortDir());
    expected.setSortedBy(deviceFilter.getSortedBy());
    expected.setHasTechnicalInstallation(true);
    expected.setOwner(deviceFilter.getOwner());
    expected.setDeviceType(deviceFilter.getDeviceType());
    expected.setManufacturer(deviceFilter.getManufacturer());
    expected.setModel(deviceFilter.getModel());
    expected.setFirmwareModuleType(
        org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleFilterType
            .ACTIVE_FIRMWARE);
    expected.setFirmwareModuleVersion(deviceFilter.getFirmwareModuleVersion());
    expected.setExactMatch(deviceFilter.isExactMatch());
    expected.setDeviceIdentificationsToUse(deviceFilter.getDeviceIdentificationsToUse());
    expected.setDeviceIdentificationsToExclude(deviceFilter.getDeviceIdentificationsToExclude());
    return expected;
  }

  @Test
  public void mapsDeviceFilter() {
    final DeviceFilter deviceFilter = new DeviceFilter();
    deviceFilter.setOrganisationIdentification("organisationIdentification1");
    deviceFilter.setAlias("alias1");
    deviceFilter.setDeviceIdentification("deviceIdentification1");
    deviceFilter.setCity("city1");
    deviceFilter.setPostalCode("postalCode1");
    deviceFilter.setStreet("street1");
    deviceFilter.setNumber("number1");
    deviceFilter.setMunicipality("municipality1");
    deviceFilter.setDeviceExternalManaged(EXTERNAL_MANAGEMENT);
    deviceFilter.setDeviceActivated(ACTIVE);
    deviceFilter.setDeviceInMaintenance(IN_MAINTENANCE);
    deviceFilter.setSortDir("sortDir1");
    deviceFilter.setSortedBy("sortedBy1");
    deviceFilter.setHasTechnicalInstallation(true);
    deviceFilter.setOwner("owner1");
    deviceFilter.setDeviceType("deviceType1");
    deviceFilter.setManufacturer("manufacturer1");
    deviceFilter.setModel("model1");
    deviceFilter.setFirmwareModuleType(ACTIVE_FIRMWARE);
    deviceFilter.setFirmwareModuleVersion("firmwareModuleVersion1");
    deviceFilter.setExactMatch(true);
    deviceFilter.getDeviceIdentificationsToUse().addAll(asList("toUse1", "toUse2"));
    deviceFilter.getDeviceIdentificationsToExclude().addAll(asList("toExclude1", "toExclude2"));

    final org.opensmartgridplatform.domain.core.valueobjects.DeviceFilter result =
        this.mapper.map(
            deviceFilter, org.opensmartgridplatform.domain.core.valueobjects.DeviceFilter.class);

    assertThat(result).usingRecursiveComparison().isEqualTo(this.expectedMapped(deviceFilter));
  }

  @Test
  public void mapsUpdatedDeviceToDevice() {

    final UpdatedDevice updatedDevice = new UpdatedDevice();
    final DeviceOutputSetting outputSetting = new DeviceOutputSetting();
    outputSetting.setAlias(DOS_ALIAS);
    outputSetting.setInternalId(RELAY_INTERNAL_ID);
    outputSetting.setExternalId(RELAY_EXTERNAL_ID);
    outputSetting.setRelayType(RelayType.LIGHT);
    updatedDevice.getOutputSettings().add(outputSetting);

    final Address address = new Address();
    address.setStreet(STREET);
    address.setNumber(HOUSE_NUMBER);
    address.setNumberAddition(HOUSE_NUMBER_ADDITION);
    address.setPostalCode(POSTAL_CODE);
    address.setCity(CITY);
    address.setMunicipality(MUNICIPALITY);
    updatedDevice.setContainerAddress(address);

    updatedDevice.setDeviceLifecycleStatus(DeviceLifecycleStatus.IN_USE);

    final Ssld expected = new Ssld();
    final List<org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting>
        expectedOutputSettings = new ArrayList<>();
    final org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting expectedOutputSetting =
        new org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting(
            RELAY_INTERNAL_ID,
            RELAY_EXTERNAL_ID,
            org.opensmartgridplatform.domain.core.valueobjects.RelayType.LIGHT,
            DOS_ALIAS);
    expectedOutputSettings.add(expectedOutputSetting);
    expected.updateOutputSettings(expectedOutputSettings);

    final org.opensmartgridplatform.domain.core.valueobjects.Address expectedAddress =
        new org.opensmartgridplatform.domain.core.valueobjects.Address(
            CITY, POSTAL_CODE, STREET, HOUSE_NUMBER, HOUSE_NUMBER_ADDITION, MUNICIPALITY);
    expected.setContainerAddress(expectedAddress);

    expected.setDeviceLifecycleStatus(
        org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus.IN_USE);

    final Ssld actual = this.mapper.map(updatedDevice, Ssld.class);

    assertThat(expected)
        .usingRecursiveComparison()
        .ignoringFields("creationTime", "modificationTime")
        .isEqualTo(actual);
  }

  @BeforeEach
  public void setUp() {
    this.mapper = new DeviceManagementMapper();
    this.mapper.initialize();
  }
}
