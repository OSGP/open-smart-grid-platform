package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import org.opensmartgridplatform.domain.core.repositories.SsldRepository;

public class TestableDeviceManagementMapper extends DeviceManagementMapper {

    public TestableDeviceManagementMapper(final SsldRepository ssldRepository) {
        super(ssldRepository);
    }

}
