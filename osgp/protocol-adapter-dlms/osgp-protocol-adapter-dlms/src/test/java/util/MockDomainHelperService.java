package util;

import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;

/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
public class MockDomainHelperService extends DomainHelperService {
    public MockDomainHelperService() {
        super(null, null, null, 0, 0);
    }

    @Override
    public DlmsDevice findDlmsDevice(final String deviceIdentification, final String ipAddress) {
        DlmsDevice dlmsDevice = new DlmsDevice();
        dlmsDevice.setDeviceIdentification(deviceIdentification);
        dlmsDevice.setIpAddress(ipAddress);
        dlmsDevice.setHls5Active(true);

        return dlmsDevice;
    }
}
