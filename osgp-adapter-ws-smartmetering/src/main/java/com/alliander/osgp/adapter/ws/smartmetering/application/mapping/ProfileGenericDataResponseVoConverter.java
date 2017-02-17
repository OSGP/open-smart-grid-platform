/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObject;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObjects;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ProfileEntries;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ProfileEntry;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponseVo;

public class ProfileGenericDataResponseVoConverter extends
        CustomConverter<ProfileGenericDataResponseVo, ProfileGenericDataResponse> {

    @Override
    public ProfileGenericDataResponse convert(ProfileGenericDataResponseVo source,
            Type<? extends ProfileGenericDataResponse> destinationType) {

        ProfileGenericDataResponse result = new ProfileGenericDataResponse();
        result.setLogicalName(this.mapperFacade.map(source.getLogicalName(), ObisCodeValues.class));

        final CaptureObjects captureObjects = new CaptureObjects();
        captureObjects.getCaptureObject().addAll(
                this.mapperFacade.mapAsList(source.getCaptureObjects(), CaptureObject.class));
        result.setCaptureObjects(captureObjects);

        final ProfileEntries profileEntries = new ProfileEntries();
        profileEntries.getProfileEntry().addAll(
                this.mapperFacade.mapAsList(source.getProfileEntries(), ProfileEntry.class));

        result.setProfileEntries(profileEntries);

        return result;
    }
}
