/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.DataObject;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.DayProfile;
import com.alliander.osgp.dto.valueobjects.smartmetering.DayProfileAction;

@Component(value = "dayProfileConverter")
public class DayProfileConverter {

    @Autowired
    private DlmsHelperService dlmsHelperService;

    public DataObject convert(final HashSet<DayProfile> source) {
        if (source == null) {
            return null;
        }

        final DataObject dayArray = DataObject.newArrayData(this.getDayObjectList(source));

        return dayArray;

    }

    private List<DataObject> getDayObjectList(final HashSet<DayProfile> dayProfileSet) {
        final List<DataObject> dayObjectList = new ArrayList<>();

        for (final DayProfile dayProfile : dayProfileSet) {
            final DataObject dayObject = DataObject.newStructureData(this.getDayObjectElements(dayProfile));
            dayObjectList.add(dayObject);
        }

        return dayObjectList;
    }

    private List<DataObject> getDayObjectElements(final DayProfile dayProfile) {
        final List<DataObject> dayObjectElements = new ArrayList<>();

        final DataObject dayId = DataObject.newUInteger32Data(dayProfile.getDayId());
        final DataObject dayActionObjectList = DataObject.newArrayData(this.getDayActionObjectList(dayProfile
                .getDayProfileActionList()));
        dayObjectElements.addAll(Arrays.asList(dayId, dayActionObjectList));

        return dayObjectElements;
    }

    private List<DataObject> getDayActionObjectList(final List<DayProfileAction> dayProfileActionList) {
        final List<DataObject> dayActionObjectList = new ArrayList<>();
        for (final DayProfileAction dayProfileAction : dayProfileActionList) {

            final DataObject dayObject = DataObject.newStructureData(this.getDayActionObjectElements(dayProfileAction));
            dayActionObjectList.add(dayObject);

        }
        return dayActionObjectList;
    }

    private List<DataObject> getDayActionObjectElements(final DayProfileAction dayProfileAction) {
        final List<DataObject> dayActionObjectElements = new ArrayList<>();

        final DateTime dt = new DateTime(dayProfileAction.getStartTime());
        final DataObject startTimeObject = this.dlmsHelperService.asDataObject(dt);

        // See "DSMR P3 v4.2.2 Final P3.pdf" Tariffication Script Table (Class
        // ID: 9). Value: 0-0:10.0.100.255
        final DataObject nameObject = DataObject.newOctetStringData(new byte[] { 0, 0, 10, 0, 100, (byte) 255 });
        final DataObject scriptSelectorObject = DataObject.newUInteger64Data(dayProfileAction.getScriptSelector());

        dayActionObjectElements.addAll(Arrays.asList(startTimeObject, nameObject, scriptSelectorObject));
        return dayActionObjectElements;
    }
}
