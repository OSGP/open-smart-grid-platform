/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObjectItemVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObjectVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryItemVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryVo;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponseVo;

public class ProfileGenericDataResponseMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();

    @Test
    public void test() {
        ProfileGenericDataResponseVo voResponse = this.makeValueObject();
        ProfileGenericDataResponse wsResponse = this.monitoringMapper.map(voResponse, ProfileGenericDataResponse.class);
        Assert.assertTrue(wsResponse != null && wsResponse instanceof ProfileGenericDataResponse);
    }

    private ProfileGenericDataResponseVo makeValueObject() {
        List<CaptureObjectItemVo> captureObjects = new ArrayList<CaptureObjectItemVo>();
        captureObjects.add(this.makeCaptureObjectVo());
        List<ProfileEntryItemVo> profileEntries = new ArrayList<ProfileEntryItemVo>();
        profileEntries.add(this.makeProfileEntryVo());
        profileEntries.add(this.makeProfileEntryVo());
        ProfileGenericDataResponseVo result = new ProfileGenericDataResponseVo(this.makeObisCode(), captureObjects,
                profileEntries);
        return result;
    }

    private ObisCodeValues makeObisCode() {
        return new ObisCodeValues((byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1);
    }

    private CaptureObjectItemVo makeCaptureObjectVo() {
        return new CaptureObjectItemVo(new CaptureObjectVo(10L, this.makeObisCode(), 10, 1, "kwu"));
    }

    private ProfileEntryItemVo makeProfileEntryVo() {
        List<ProfileEntryVo> entriesVo = new ArrayList<ProfileEntryVo>();
        entriesVo.add(new ProfileEntryVo("test", null, null, null));
        return new ProfileEntryItemVo(entriesVo);
    }

}
