/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.core.db.api.iec61850.entities.DeviceOutputSetting;
import com.alliander.osgp.dto.valueobjects.RelayMap;
import com.alliander.osgp.dto.valueobjects.RelayType;

public class DeviceOutputSettingToRelayMapConverter extends BidirectionalConverter<DeviceOutputSetting, RelayMap> {

    @Override
    public RelayMap convertTo(final DeviceOutputSetting source, final Type<RelayMap> destinationType) {
        final RelayType relayType = RelayType.valueOf(source.getRelayType().name());

        return new RelayMap(source.getExternalId(), source.getInternalId(), relayType, source.getAlias());
    }

    @Override
    public DeviceOutputSetting convertFrom(final RelayMap source, final Type<DeviceOutputSetting> destinationType) {

        final com.alliander.osgp.core.db.api.iec61850valueobjects.RelayType relayType = com.alliander.osgp.core.db.api.iec61850valueobjects.RelayType
                .valueOf(source.getRelayType().name());

        return new DeviceOutputSetting(source.getAddress(), source.getIndex(), relayType, source.getAlias());
    }
}
