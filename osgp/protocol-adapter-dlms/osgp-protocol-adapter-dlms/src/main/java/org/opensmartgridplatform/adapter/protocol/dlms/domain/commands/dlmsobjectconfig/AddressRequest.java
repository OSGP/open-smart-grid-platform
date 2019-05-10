/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.Medium;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;

public class AddressRequest {
    private final DlmsDevice device;
    private final DlmsObject dlmsObject;
    private final Integer channel;
    private final DateTime from;
    private final DateTime to;
    private final Medium filterMedium;

    public AddressRequest(DlmsDevice device, DlmsObject dlmsObject, Integer channel, DateTime from, DateTime to,
            Medium filterMedium) {
        this.device = device;
        this.dlmsObject = dlmsObject;
        this.channel = channel;
        this.from = from;
        this.to = to;
        this.filterMedium = filterMedium;
    }

    public DlmsDevice getDevice() {
        return device;
    }

    public DlmsObject getDlmsObject() {
        return dlmsObject;
    }

    public Integer getChannel() {
        return channel;
    }

    public DateTime getFrom() {
        return from;
    }

    public DateTime getTo() {
        return to;
    }

    public Medium getFilterMedium() {
        return filterMedium;
    }
}
