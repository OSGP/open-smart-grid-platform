/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObject;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ProfileEntryValue;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntry;

public class ProfileGenericDataResponseMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();

    private final static String[] EXPECTED_CLASS = new String[] { String.class.getSimpleName(),
            "XMLGregorianCalendarImpl", BigDecimal.class.getSimpleName(), Long.class.getSimpleName() };

    @Test
    public void testProfileGenericDataResponse() {
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse source = this
                .makeresponseVo();
        final ProfileGenericDataResponse target = this.monitoringMapper.map(source, ProfileGenericDataResponse.class);

        assertNotNull("mapping ProfileGenericDataResponse should not return null", target);
        assertEquals(target.getCaptureObjectList().getCaptureObjects().size(), 1);
        assertEquals(target.getProfileEntryList().getProfileEntries().size(), 2);
        assertNotNull(target.getProfileEntryList().getProfileEntries().get(0).getProfileEntryValue());
        assertEquals(target.getProfileEntryList().getProfileEntries().get(0).getProfileEntryValue().size(), 4);

        int i = 0;
        for (final ProfileEntryValue profileEntryValue : target.getProfileEntryList().getProfileEntries().get(0)
                .getProfileEntryValue()) {
            assertNotNull(profileEntryValue.getStringValueOrDateValueOrFloatValue());
            assertEquals(1, profileEntryValue.getStringValueOrDateValueOrFloatValue().size());
            final Class<?> clazz = profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0).getClass();
            System.out.println(clazz.getSimpleName());
            assertEquals(EXPECTED_CLASS[i++], clazz.getSimpleName());
            assertTrue(profileEntryValue.getStringValueOrDateValueOrFloatValue() != null
                    && !profileEntryValue.getStringValueOrDateValueOrFloatValue().isEmpty());
        }
    }

    @Test
    public void testCaptureObject() {
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObject captureObjectVo = this
                .makeCaptureObjectVo();
        final CaptureObject captureObject = this.monitoringMapper.map(captureObjectVo, CaptureObject.class);
        assertNotNull("mapping ProfileGenericDataResponse should not return null", captureObject);
    }

    @Test
    public void testProfileEntry() {
        com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue profileEntryValueVo = new com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue(
                "test");
        ProfileEntryValue profileEntryValue = this.monitoringMapper.map(profileEntryValueVo, ProfileEntryValue.class);
        assertEquals(profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0), "test");

        profileEntryValueVo = new com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue(
                new Date());
        profileEntryValue = this.monitoringMapper.map(profileEntryValueVo, ProfileEntryValue.class);
        assertThat(profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0),
                instanceOf(XMLGregorianCalendar.class));

        profileEntryValueVo = new com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue(
                new BigDecimal(100.0d));
        profileEntryValue = this.monitoringMapper.map(profileEntryValueVo, ProfileEntryValue.class);
        assertTrue(
                ((BigDecimal) profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0)).doubleValue() == 100.d);

        profileEntryValueVo = new com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue(12345L);
        profileEntryValue = this.monitoringMapper.map(profileEntryValueVo, ProfileEntryValue.class);
        assertTrue(((Long) profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0)).doubleValue() == 12345L);
    }

    private com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse makeresponseVo() {
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse result = new com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse(
                this.makeObisCode(), this.makeCaptureObjectsVo(), this.makeProfileEntriesVo());
        return result;
    }

    private ObisCodeValues makeObisCode() {
        return new ObisCodeValues((byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1);
    }

    private List<ProfileEntry> makeProfileEntriesVo() {
        final List<ProfileEntry> profileEntries = new ArrayList<>();
        profileEntries.add(this.makeProfileEntryVo());
        profileEntries.add(this.makeProfileEntryVo());
        return profileEntries;
    }

    private List<com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObject> makeCaptureObjectsVo() {
        final List<com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObject> captureObjectVos = new ArrayList<>();
        captureObjectVos.add(this.makeCaptureObjectVo());
        return captureObjectVos;
    }

    private com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObject makeCaptureObjectVo() {
        return new com.alliander.osgp.domain.core.valueobjects.smartmetering.CaptureObject(10L, "0.0.1.0.0.255", 10, 1,
                OsgpUnitType.UNDEFINED.name());
    }

    private ProfileEntry makeProfileEntryVo() {
        final List<com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue> entriesVo = new ArrayList<>();
        entriesVo.add(new com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue("test"));
        entriesVo.add(new com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue(new Date()));
        entriesVo.add(new com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue(
                new BigDecimal(100.5d)));
        entriesVo.add(new com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileEntryValue(12345L));
        return new ProfileEntry(entriesVo);
    }

}
