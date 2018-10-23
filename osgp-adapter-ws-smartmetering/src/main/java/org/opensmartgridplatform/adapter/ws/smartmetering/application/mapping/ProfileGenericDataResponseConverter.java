/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.List;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObjects;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntries;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntry;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntryValue;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataResponse;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class ProfileGenericDataResponseConverter extends
        CustomConverter<org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse, ProfileGenericDataResponse> {

    @Override
    public ProfileGenericDataResponse convert(
            final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse source,
            final Type<? extends ProfileGenericDataResponse> destinationType, final MappingContext context) {

        final ProfileGenericDataResponse result = new ProfileGenericDataResponse();
        result.setLogicalName(this.mapperFacade.map(source.getLogicalName(), ObisCodeValues.class));

        final CaptureObjects captureObjects = new CaptureObjects();
        captureObjects.getCaptureObjects()
                .addAll(this.mapperFacade.mapAsList(source.getCaptureObjects(), CaptureObject.class));
        result.setCaptureObjectList(captureObjects);

        final ProfileEntries profileEntries = new ProfileEntries();
        profileEntries.getProfileEntries().addAll(this.mapProfileEntries(source));
        result.setProfileEntryList(profileEntries);

        return result;
    }

    private List<ProfileEntry> mapProfileEntries(
            final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse source) {
        final List<ProfileEntry> result = new ArrayList<>();
        for (final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntry profileEntryValuesVo : source
                .getProfileEntries()) {
            final ProfileEntry profileEntry = new ProfileEntry();

            for (final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue profileEntryValueVo : profileEntryValuesVo
                    .getProfileEntryValues()) {
                profileEntry.getProfileEntryValue()
                        .add(this.mapperFacade.map(profileEntryValueVo, ProfileEntryValue.class));
            }

            result.add(profileEntry);
        }
        return result;
    }
}
