/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.mapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.openmuc.jdlms.DataObject;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.dto.valueobjects.smartmetering.WeekProfile;

public class WeekProfileConverter extends BidirectionalConverter<HashSet<WeekProfile>, DataObject> {

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public DataObject convertTo(final HashSet<WeekProfile> source, final Type<DataObject> destinationType) {
        if (source == null) {
            return null;
        }

        final DataObject weekArray = DataObject.newArrayData(this.getWeekObjectList(source));

        return weekArray;

    }

    @Override
    public HashSet<WeekProfile> convertFrom(final DataObject source, final Type<HashSet<WeekProfile>> destinationType) {

        throw new IllegalStateException("convertTo is not supported");
    }

    private List<DataObject> getWeekObjectList(final HashSet<WeekProfile> weekProfileSet) {
        final List<DataObject> weekList = new ArrayList<>();
        for (final WeekProfile weekProfile : weekProfileSet) {

            final DataObject weekStructure = DataObject.newStructureData(this.getWeekStructure(weekProfile));

            weekList.add(weekStructure);
        }
        return weekList;
    }

    private List<DataObject> getWeekStructure(final WeekProfile weekProfile) {
        final List<DataObject> weekElements = new ArrayList<>();

        weekElements.add(DataObject.newOctetStringData(weekProfile.getWeekProfileName().getBytes()));
        weekElements.add(DataObject.newUInteger32Data(weekProfile.getMonday().getDayId()));
        weekElements.add(DataObject.newUInteger32Data(weekProfile.getTuesday().getDayId()));
        weekElements.add(DataObject.newUInteger32Data(weekProfile.getWednesday().getDayId()));
        weekElements.add(DataObject.newUInteger32Data(weekProfile.getThursday().getDayId()));
        weekElements.add(DataObject.newUInteger32Data(weekProfile.getFriday().getDayId()));
        weekElements.add(DataObject.newUInteger32Data(weekProfile.getSaturday().getDayId()));
        weekElements.add(DataObject.newUInteger32Data(weekProfile.getSunday().getDayId()));

        return weekElements;
    }
}
