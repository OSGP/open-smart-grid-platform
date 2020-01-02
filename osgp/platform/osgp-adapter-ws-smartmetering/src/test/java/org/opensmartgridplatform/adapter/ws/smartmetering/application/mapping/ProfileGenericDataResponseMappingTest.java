/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntryValue;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntry;

public class ProfileGenericDataResponseMappingTest {

    private final static String[] EXPECTED_CLASS = new String[] { String.class.getSimpleName(),
            "XMLGregorianCalendarImpl", BigDecimal.class.getSimpleName(), Long.class.getSimpleName() };

    private MonitoringMapper monitoringMapper = new MonitoringMapper();

    private List<org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject> makeCaptureObjectsVo() {
        final List<org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject> captureObjectVos = new ArrayList<>();
        captureObjectVos.add(this.makeCaptureObjectVo());
        return captureObjectVos;
    }

    private org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject makeCaptureObjectVo() {
        return new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject(10L, "0.0.1.0.0.255",
                10, 1, OsgpUnitType.UNDEFINED.name());
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

    private ProfileEntry makeProfileEntryVo() {
        final List<org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue> entriesVo = new ArrayList<>();
        entriesVo.add(new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue("test"));
        entriesVo.add(
                new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue(new Date()));
        entriesVo.add(new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue(
                new BigDecimal(100.5d)));
        entriesVo.add(new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue(12345L));
        return new ProfileEntry(entriesVo);
    }

    private org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse makeresponseVo() {
        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse result = new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse(
                this.makeObisCode(), this.makeCaptureObjectsVo(), this.makeProfileEntriesVo());
        return result;
    }

    @Test
    public void testCaptureObject() {
        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject captureObjectVo = this
                .makeCaptureObjectVo();
        final CaptureObject captureObject = this.monitoringMapper.map(captureObjectVo, CaptureObject.class);
        assertThat(captureObject).as("mapping ProfileGenericDataResponse should not return null").isNotNull();
    }

    @Test
    public void testProfileEntry() {
        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue profileEntryValueVo = new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue(
                "test");
        ProfileEntryValue profileEntryValue = this.monitoringMapper.map(profileEntryValueVo, ProfileEntryValue.class);
        assertThat(profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0)).isEqualTo("test");

        profileEntryValueVo = new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue(
                new Date());
        profileEntryValue = this.monitoringMapper.map(profileEntryValueVo, ProfileEntryValue.class);
        assertThat(profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0))
                .isInstanceOf(XMLGregorianCalendar.class);

        profileEntryValueVo = new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue(
                new BigDecimal(100.0d));
        profileEntryValue = this.monitoringMapper.map(profileEntryValueVo, ProfileEntryValue.class);
        assertThat(
                ((BigDecimal) profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0)).doubleValue() == 100.d)
                        .isTrue();

        profileEntryValueVo = new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue(
                12345L);
        profileEntryValue = this.monitoringMapper.map(profileEntryValueVo, ProfileEntryValue.class);
        assertThat(((Long) profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0)).doubleValue() == 12345L)
                .isTrue();
    }

    @Test
    public void testProfileGenericDataResponse() {
        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse source = this
                .makeresponseVo();
        final ProfileGenericDataResponse target = this.monitoringMapper.map(source, ProfileGenericDataResponse.class);

        assertThat(target).as("mapping ProfileGenericDataResponse should not return null").isNotNull();
        assertThat(target.getCaptureObjectList().getCaptureObjects().size()).isEqualTo(1);
        assertThat(target.getProfileEntryList().getProfileEntries().size()).isEqualTo(2);
        assertThat(target.getProfileEntryList().getProfileEntries().get(0).getProfileEntryValue()).isNotNull();
        assertThat(target.getProfileEntryList().getProfileEntries().get(0).getProfileEntryValue().size()).isEqualTo(4);

        int i = 0;
        for (final ProfileEntryValue profileEntryValue : target.getProfileEntryList().getProfileEntries().get(0)
                .getProfileEntryValue()) {
            assertThat(profileEntryValue.getStringValueOrDateValueOrFloatValue()).isNotNull();
            assertThat(profileEntryValue.getStringValueOrDateValueOrFloatValue().size()).isEqualTo(1);
            final Class<?> clazz = profileEntryValue.getStringValueOrDateValueOrFloatValue().get(0).getClass();
            System.out.println(clazz.getSimpleName());
            assertThat(clazz.getSimpleName()).isEqualTo(EXPECTED_CLASS[i++]);
            assertThat(profileEntryValue.getStringValueOrDateValueOrFloatValue() != null
                    && !profileEntryValue.getStringValueOrDateValueOrFloatValue().isEmpty()).isTrue();
        }
    }

}
