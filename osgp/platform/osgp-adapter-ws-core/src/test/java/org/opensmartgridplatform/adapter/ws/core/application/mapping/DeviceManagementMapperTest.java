/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceActivatedFilterType.ACTIVE;
import static org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceExternalManagedFilterType.EXTERNAL_MANAGEMENT;
import static org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceInMaintetanceFilterType.IN_MAINTENANCE;
import static org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FirmwareModuleFilterType.ACTIVE_FIRMWARE;

import org.junit.Before;
import org.junit.Test;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceFilter;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceActivatedFilterType;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceExternalManagedFilterType;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceInMaintenanceFilterType;

public class DeviceManagementMapperTest {
    private DeviceManagementMapper mapper;

    @Before
    public void setUp() {
        this.mapper = new DeviceManagementMapper();
        this.mapper.initialize();
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

        final org.opensmartgridplatform.domain.core.valueobjects.DeviceFilter result = this.mapper.map(deviceFilter,
                org.opensmartgridplatform.domain.core.valueobjects.DeviceFilter.class);

        assertThat(result).isEqualToComparingFieldByFieldRecursively(this.expectedMapped(deviceFilter));
    }

    private org.opensmartgridplatform.domain.core.valueobjects.DeviceFilter expectedMapped(
            final DeviceFilter deviceFilter) {
        final org.opensmartgridplatform.domain.core.valueobjects.DeviceFilter expected = new org.opensmartgridplatform.domain.core.valueobjects.DeviceFilter();
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
                org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleFilterType.ACTIVE_FIRMWARE);
        expected.setFirmwareModuleVersion(deviceFilter.getFirmwareModuleVersion());
        expected.setExactMatch(deviceFilter.isExactMatch());
        expected.setDeviceIdentificationsToUse(deviceFilter.getDeviceIdentificationsToUse());
        expected.setDeviceIdentificationsToExclude(deviceFilter.getDeviceIdentificationsToExclude());
        return expected;
    }
}