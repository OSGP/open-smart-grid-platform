/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device;
import com.alliander.osgp.domain.core.entities.SmartMeter;

class SmartMeterConverter extends BidirectionalConverter<SmartMeter, Device> {

    private final DeviceConverterHelper<SmartMeter> helper = new DeviceConverterHelper<>(SmartMeter.class);

    @Override
    public SmartMeter convertFrom(final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device source,
            final Type<SmartMeter> destinationType) {
        return this.helper.initEntity(source);
    }

    @Override
    public com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device convertTo(final SmartMeter source,
            final Type<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device> destinationType) {
        return this.helper.initJaxb(source);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.helper == null) ? 0 : this.helper.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final SmartMeterConverter other = (SmartMeterConverter) obj;
        if (this.helper == null) {
            if (other.helper != null) {
                return false;
            }
        } else if (!this.helper.equals(other.helper)) {
            return false;
        }
        return true;
    }

}