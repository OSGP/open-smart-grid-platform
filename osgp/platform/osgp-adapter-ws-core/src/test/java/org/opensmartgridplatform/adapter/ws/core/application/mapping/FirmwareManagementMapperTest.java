/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleDataBuilder;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData;


public class FirmwareManagementMapperTest {
    private FirmwareManagementMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new FirmwareManagementMapper();
        mapper.initialize();
    }

    @Test
    public void mapsFirmwareModuleData() {
        org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleData source =
                new FirmwareModuleDataBuilder().build();

        FirmwareModuleData mappedValue = map(source);
        Assertions.assertThat(mapper.map(source, FirmwareModuleData.class))
                .isEqualToComparingFieldByFieldRecursively(mappedValue);
    }

    private FirmwareModuleData map(
            org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleData source) {
        return new FirmwareModuleData(source.getModuleVersionComm(),
                source.getModuleVersionFunc(), source.getModuleVersionMa(), source.getModuleVersionMbus(),
                source.getModuleVersionSec(), source.getModuleVersionMBusDriverActive());
    }
}