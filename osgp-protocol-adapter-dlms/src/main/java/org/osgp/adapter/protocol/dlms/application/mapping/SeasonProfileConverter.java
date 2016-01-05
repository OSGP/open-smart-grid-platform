/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.mapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.SeasonProfile;

@Component(value = "seasonProfileConverter")
public class SeasonProfileConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeasonProfileConverter.class);

    @Autowired
    private DlmsHelperService dlmsHelperService;

    public DataObject convert(final List<SeasonProfile> source) {
        if (source == null) {
            return null;
        }

        try {
            final DataObject seasonsArray = DataObject.newArrayData(this.getSeasonList(source));
            return seasonsArray;
        } catch (IllegalArgumentException | IOException e) {
            LOGGER.error("Unexpected exception during SeasonProfile converstion to DataObject", e);
        }

        return null;

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

        final DataObject seasonProfileNameObject = DataObject.newOctetStringData(seasonProfile.getSeasonProfileName()
                .getBytes(StandardCharsets.UTF_8));
        seasonElements.add(seasonProfileNameObject);

        final DateTime dt = new DateTime(seasonProfile.getSeasonStart());
        final DataObject seasonStartObject = this.dlmsHelperService.asDataObject(dt);
        seasonElements.add(seasonStartObject);

        final DataObject seasonWeekProfileNameObject = DataObject.newOctetStringData(seasonProfile.getWeekProfile()
                .getWeekProfileName().getBytes(StandardCharsets.UTF_8));
        seasonElements.add(seasonWeekProfileNameObject);

        return seasonElements;
    }

}
