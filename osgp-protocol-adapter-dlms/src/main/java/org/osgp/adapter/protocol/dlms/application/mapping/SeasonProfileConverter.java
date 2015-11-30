/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.mapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.joda.time.DateTime;
import org.openmuc.jdlms.DataObject;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.dto.valueobjects.smartmetering.SeasonProfile;

public class SeasonProfileConverter extends BidirectionalConverter<List<SeasonProfile>, DataObject> {

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public DataObject convertTo(final List<SeasonProfile> source, final Type<DataObject> destinationType) {
        if (source == null) {
            return null;
        }

        try {
            final DataObject seasonsArray = DataObject.newArrayData(this.getSeasonList(source));
            return seasonsArray;
        } catch (IllegalArgumentException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public List<SeasonProfile> convertFrom(final DataObject source, final Type<List<SeasonProfile>> destinationType) {

        throw new IllegalStateException("convertTo is not supported");
    }

    private List<DataObject> getSeasonList(final List<SeasonProfile> seasonProfileList) throws IOException {
        final List<DataObject> seasonList = new ArrayList<>();
        for (final SeasonProfile seasonProfile : seasonProfileList) {
            final DataObject seasonStructure = DataObject.newStructureData(this.getSeason(seasonProfile));
            seasonList.add(seasonStructure);
        }
        return seasonList;
    }

    private List<DataObject> getSeason(final SeasonProfile seasonProfile) throws IOException {
        final List<DataObject> seasonElements = new ArrayList<>();

        seasonProfile.getSeasonProfileName();
        seasonProfile.getSeasonStart();
        seasonProfile.getWeekProfile().getWeekProfileName();

        final DataObject seasonProfileNameObject = DataObject.newOctetStringData(seasonProfile.getSeasonProfileName()
                .getBytes());
        seasonElements.add(seasonProfileNameObject);

        final DateTime dt = new DateTime(seasonProfile.getSeasonStart());
        final DataObject seasonStartObject = this.dlmsHelperService.asDataObject(dt);
        seasonElements.add(seasonStartObject);

        final DataObject seasonWeekProfileNameObject = DataObject.newOctetStringData(seasonProfile.getWeekProfile()
                .getWeekProfileName().getBytes());
        seasonElements.add(seasonWeekProfileNameObject);

        return seasonElements;
    }

}
