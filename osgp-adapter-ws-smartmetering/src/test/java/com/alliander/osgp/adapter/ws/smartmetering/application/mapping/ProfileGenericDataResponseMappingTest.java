/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObject;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ProfileEntryValue;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntry;

public class ProfileGenericDataResponseMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();

    @Test
    public void testProfileGenericDataResponse() {
        com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse voResponse = this
                .makeresponseVo();
        ProfileGenericDataResponse wsResponse = this.monitoringMapper.map(voResponse, ProfileGenericDataResponse.class);
        Assert.assertTrue(wsResponse != null && wsResponse instanceof ProfileGenericDataResponse);
    }

    @Test
    public void testCaptureObject() {
        com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObject captureObjectVo = this
                .makeCaptureObjectVo();
        CaptureObject captureObject = this.monitoringMapper.map(captureObjectVo, CaptureObject.class);
        Assert.assertTrue(captureObject != null);
    }

    @Test
    public void testProfileEntry() {
        com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue profileEntryValueVo = new com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue(
                "test");
        ProfileEntryValue profileEntryValue = this.monitoringMapper.map(profileEntryValueVo, ProfileEntryValue.class);
        Assert.assertTrue(profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0).equals("test"));

        profileEntryValueVo = new com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue(
                new Date());
        profileEntryValue = this.monitoringMapper.map(profileEntryValueVo, ProfileEntryValue.class);
        Assert.assertTrue(profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0) instanceof Date);

        profileEntryValueVo = new com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue(
                new BigDecimal(100.0d));
        profileEntryValue = this.monitoringMapper.map(profileEntryValueVo, ProfileEntryValue.class);
        Assert.assertTrue(((BigDecimal) profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0)).doubleValue() == 100.d);

        profileEntryValueVo = new com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue(12345L);
        profileEntryValue = this.monitoringMapper.map(profileEntryValueVo, ProfileEntryValue.class);
        Assert.assertTrue(((Long) profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0)).doubleValue() == 12345L);
    }

    private com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse makeresponseVo() {
        com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse result = new com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse(
                this.makeObisCode(), this.makeCaptureObjectsVo(), this.makeProfileEntriesVo());
        return result;
    }

    private ObisCodeValues makeObisCode() {
        return new ObisCodeValues((byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1);
    }

    private List<ProfileEntry> makeProfileEntriesVo() {
        List<ProfileEntry> profileEntries = new ArrayList<ProfileEntry>();
        profileEntries.add(this.makeProfileEntryVo());
        profileEntries.add(this.makeProfileEntryVo());
        return profileEntries;
    }

    private List<com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObject> makeCaptureObjectsVo() {
        final List<com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObject> captureObjectVos = new ArrayList<com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObject>();
        captureObjectVos.add(this.makeCaptureObjectVo());
        return captureObjectVos;
    }

    private com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObject makeCaptureObjectVo() {
        return new com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObject(10L, "0.0.1.0.0.255", 10, 1,
                OsgpUnitType.UNDEFINED.name());
    }

    private ProfileEntry makeProfileEntryVo() {
        List<com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue> entriesVo = new ArrayList<com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue>();
        entriesVo.add(new com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue("test"));
        return new ProfileEntry(entriesVo);
    }

}
