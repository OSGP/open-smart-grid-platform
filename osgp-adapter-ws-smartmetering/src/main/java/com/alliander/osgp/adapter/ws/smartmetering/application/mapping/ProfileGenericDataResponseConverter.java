/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.List;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObject;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObjects;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ProfileEntries;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ProfileEntry;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ProfileEntryValue;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataResponse;

public class ProfileGenericDataResponseConverter
        extends
        CustomConverter<com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse, ProfileGenericDataResponse> {

    @Override
    public ProfileGenericDataResponse convert(
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse source,
            final Type<? extends ProfileGenericDataResponse> destinationType) {

        ProfileGenericDataResponse result = new ProfileGenericDataResponse();
        result.setLogicalName(this.mapperFacade.map(source.getLogicalName(), ObisCodeValues.class));

        final CaptureObjects captureObjects = new CaptureObjects();
        captureObjects.getCaptureObject().addAll(
                this.mapperFacade.mapAsList(source.getCaptureObjects(), CaptureObject.class));
        result.setCaptureObjects(captureObjects);

        final ProfileEntries profileEntries = new ProfileEntries();
        profileEntries.getProfileEntry().addAll(this.mapProfileEntries(source));
        result.setProfileEntries(profileEntries);

        return result;
    }

    private List<ProfileEntry> mapProfileEntries(
            com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse source) {
        List<ProfileEntry> result = new ArrayList<>();
        for (com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntry profileEntryValuesVo : source
                .getProfileEntries()) {
            ProfileEntry profileEntry = new ProfileEntry();

            for (com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue profileEntryValueVo : profileEntryValuesVo
                    .getProfileEntryValues()) {
                profileEntry.getProfileEntryValue().add(
                        this.mapperFacade.map(profileEntryValueVo, ProfileEntryValue.class));
            }

            result.add(profileEntry);
        }
        return result;
    }
}
