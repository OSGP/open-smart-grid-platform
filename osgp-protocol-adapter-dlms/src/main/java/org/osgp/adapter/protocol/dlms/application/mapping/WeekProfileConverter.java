/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.mapping;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.openmuc.jdlms.DataObject;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.WeekProfile;

@Component(value = "weekProfileConverter")
public class WeekProfileConverter {

    @Autowired
    private DlmsHelperService dlmsHelperService;

    public DataObject convert(final HashSet<WeekProfile> source) {
        if (source == null) {
            return null;
        }

        final DataObject weekArray = DataObject.newArrayData(this.getWeekObjectList(source));

        return weekArray;

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

        weekElements.add(DataObject.newOctetStringData(weekProfile.getWeekProfileName()
                .getBytes(StandardCharsets.UTF_8)));
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
