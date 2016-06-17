package com.alliander.osgp.adapter.ws.core.application.mapping;

import com.alliander.osgp.domain.core.repositories.SsldRepository;

public class TestableDeviceManagementMapper extends DeviceManagementMapper {

    public TestableDeviceManagementMapper(final SsldRepository ssldRepository) {
        super(ssldRepository);
    }

}
