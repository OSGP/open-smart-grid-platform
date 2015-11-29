/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.mapping;

import java.util.List;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.openmuc.jdlms.DataObject;

import com.alliander.osgp.dto.valueobjects.smartmetering.SeasonProfile;

public class ActivityCalendarConverter extends BidirectionalConverter<List<SeasonProfile>, DataObject> {

    @Override
    public DataObject convertTo(final List<SeasonProfile> source, final Type<DataObject> destinationType) {
        if (source == null) {
            return null;
        }
        // TODO
        return null;

    }

    @Override
    public List<SeasonProfile> convertFrom(final DataObject source, final Type<List<SeasonProfile>> destinationType) {

        throw new IllegalStateException("convertTo is not supported");
    }
}
