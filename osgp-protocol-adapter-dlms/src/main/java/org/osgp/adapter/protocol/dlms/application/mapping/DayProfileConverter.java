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
import java.util.List;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import org.joda.time.DateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.dto.valueobjects.smartmetering.DayProfile;
import com.alliander.osgp.dto.valueobjects.smartmetering.DayProfileAction;

public class DayProfileConverter extends CustomConverter<DayProfile, DataObject> {

    @Autowired
    private DlmsHelperService dlmsHelperService;

    private List<DataObject> getDayObjectElements(final DayProfile dayProfile) {
        final List<DataObject> dayObjectElements = new ArrayList<>();

        final DataObject dayId = DataObject.newUInteger8Data(dayProfile.getDayId().shortValue());
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

        final DataObject startTimeObject = this.dlmsHelperService.dateAsDataObjectOctetString(new DateTime(
                dayProfileAction.getStartTime()));
        // See "DSMR P3 v4.2.2 Final P3.pdf" Tariffication Script Table (Class
        // ID: 9). Value: 0-0:10.0.100.255
        final DataObject nameObject = DataObject.newOctetStringData(new byte[] { 0, 0, 10, 0, 100, (byte) 255 });
        final DataObject scriptSelectorObject = DataObject.newUInteger16Data(dayProfileAction.getScriptSelector());

        dayActionObjectElements.addAll(Arrays.asList(startTimeObject, nameObject, scriptSelectorObject));
        return dayActionObjectElements;
    }

    @Override
    public DataObject convert(final DayProfile source, final Type<? extends DataObject> destinationType) {
        if (source == null) {
            return null;
        }

        return DataObject.newStructureData(this.getDayObjectElements(source));
    }
}
