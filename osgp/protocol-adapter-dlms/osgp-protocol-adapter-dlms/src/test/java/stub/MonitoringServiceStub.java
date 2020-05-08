/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package stub;

import java.io.Serializable;

import org.opensmartgridplatform.adapter.protocol.dlms.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileResponseDto;

public class MonitoringServiceStub extends MonitoringService {

    public MonitoringServiceStub() {
        super(null, null, null, null, null, null, null);
    }

    @Override
    public Serializable requestPowerQualityProfile(final DlmsConnectionManager conn, final DlmsDevice device,
            final GetPowerQualityProfileRequestDataDto powerQualityProfileRequestDataDto) {

        return new GetPowerQualityProfileResponseDto();
    }
}
