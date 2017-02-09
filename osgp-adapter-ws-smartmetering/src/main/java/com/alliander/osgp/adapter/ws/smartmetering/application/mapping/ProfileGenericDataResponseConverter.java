/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObject;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObjectItem;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ProfileEntry;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ProfileEntryItem;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObjectItemVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObjectVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryItemVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponseVo;

public class ProfileGenericDataResponseConverter extends
        CustomConverter<ProfileGenericDataResponseVo, ProfileGenericDataResponse> {

    @Override
    public ProfileGenericDataResponse convert(ProfileGenericDataResponseVo source,
            Type<? extends ProfileGenericDataResponse> destinationType) {

        ProfileGenericDataResponse result = new ProfileGenericDataResponse();

        final ObisCodeValues obisCode = this.map(source.getLogicalName());
        List<CaptureObjectItem> captureObjectItemList = source.getCaptureObjects().stream().map(obj -> this.map(obj))
                .collect(Collectors.toList());
        List<ProfileEntryItem> profileEntryItemList = source.getProfileEntries().stream().map(obj -> this.map(obj))
                .collect(Collectors.toList());

        result.setLogicalName(obisCode);
        result.getCaptureObjects().addAll(captureObjectItemList);
        result.getProfileEntries().addAll(profileEntryItemList);
        return result;
    }

    private ObisCodeValues map(final com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues source) {
        return this.mapperFacade.convert(source, ObisCodeValues.class, null);
    }

    private CaptureObjectItem map(final CaptureObjectItemVo source) {
        CaptureObjectItem result = new CaptureObjectItem();
        CaptureObject targetCaptureObject = new CaptureObject();
        CaptureObjectVo sourceCaptureObjectVo = source.getCaptureObject();
        targetCaptureObject.setAttribute(BigInteger.valueOf(sourceCaptureObjectVo.getAttribute()));
        targetCaptureObject.setClassId(sourceCaptureObjectVo.getClassId());
        targetCaptureObject.setObisCode(this.map(sourceCaptureObjectVo.getLogicalName()));
        targetCaptureObject.setVersion(BigInteger.valueOf(sourceCaptureObjectVo.getVersion()));
        OsgpUnitType unitType = this.mapUnitType(sourceCaptureObjectVo.getUnit());
        targetCaptureObject.setUnit(unitType);
        result.setCaptureObject(targetCaptureObject);
        return result;
    }

    // TODO add more types OsgpUnitType, that correspond with sourceType
    private OsgpUnitType mapUnitType(String sourceType) {
        return OsgpUnitType.KWH;
    }

    private ProfileEntryItem map(final ProfileEntryItemVo source) {
        ProfileEntryItem result = new ProfileEntryItem();
        for (ProfileEntryVo entryVo : source.getProfileEntry()) {
            ProfileEntry entry = new ProfileEntry();
            entry.getStringValueOrDateValueOrFloatValue().add(entryVo.getValue());
            result.getProfileEntry().add(entry);
        }
        return result;
    }
}
