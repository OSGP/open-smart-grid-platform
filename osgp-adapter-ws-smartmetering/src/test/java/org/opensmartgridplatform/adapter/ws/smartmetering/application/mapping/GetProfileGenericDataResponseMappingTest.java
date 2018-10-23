/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntry;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileEntryValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse;

public class GetProfileGenericDataResponseMappingTest {

    private static final String MAPPED_FIELD_VALUE_MESSAGE = "Mapped fields should have the same value.";
    private static final String MAPPED_LIST_SIZE_MESSAGE = "Mapped lists should have the same size.";

    private MonitoringMapper monitoringMapper = new MonitoringMapper();

    @Test
    public void shouldConvertProfileGenericDataResponse() {
        // Arrange
        final ObisCodeValues obisCode = this.makeObisCode();
        final List<CaptureObject> captureObjects = this.makeCaptureObjects();
        final List<ProfileEntry> profileEntries = this.makeProfileEntries();

        final ProfileGenericDataResponse source = new ProfileGenericDataResponse(obisCode, captureObjects,
                profileEntries);

        // Act
        final org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ProfileGenericDataResponse target = this.monitoringMapper
                .map(source,
                        org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ProfileGenericDataResponse.class);

        // Assert
        this.assertObisCode(target.getProfileGenericData().getLogicalName(), obisCode);
        this.assertCaptureObjects(target.getProfileGenericData().getCaptureObjectList().getCaptureObjects(),
                captureObjects);
        this.assertProfileEntries(target.getProfileGenericData().getProfileEntryList().getProfileEntries(),
                profileEntries);

    }

    private ObisCodeValues makeObisCode() {
        return new ObisCodeValues((byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6);
    }

    private List<ProfileEntry> makeProfileEntries() {
        final List<ProfileEntry> profileEntries = new ArrayList<>();
        profileEntries.add(this.makeProfileEntry(new DateTime(2017, 1, 1, 1, 0, 0, DateTimeZone.UTC).toDate(), "test1",
                new BigDecimal(1.1d), 111L));
        profileEntries.add(this.makeProfileEntry(new DateTime(2017, 2, 2, 2, 0, 0, DateTimeZone.UTC).toDate(), "test2",
                new BigDecimal(2.2d), 222L));
        return profileEntries;
    }

    private ProfileEntry makeProfileEntry(final Serializable... values) {
        final List<ProfileEntryValue> profileEntries = new ArrayList<>();
        for (final Serializable value : values) {
            profileEntries.add(new ProfileEntryValue(value));
        }
        return new ProfileEntry(profileEntries);
    }

    private List<CaptureObject> makeCaptureObjects() {
        final List<CaptureObject> captureObjects = new ArrayList<>();
        captureObjects.add(new CaptureObject(8L, "0.0.1.0.0.255", 2, 0, OsgpUnitType.UNDEFINED.name()));
        captureObjects.add(new CaptureObject(3L, "1.1.1.0.0.255", 2, 0, OsgpUnitType.W.name()));
        captureObjects.add(new CaptureObject(3L, "2.2.2.0.0.255", 2, 0, OsgpUnitType.W.name()));
        captureObjects.add(new CaptureObject(4L, "3.3.3.0.0.255", 2, 0, OsgpUnitType.W.name()));
        return captureObjects;
    }

    private void assertObisCode(
            final org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues actualObisCode,
            final ObisCodeValues sourceObisCode) throws AssertionError {

        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, sourceObisCode.getA(), actualObisCode.getA());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, sourceObisCode.getB(), actualObisCode.getB());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, sourceObisCode.getC(), actualObisCode.getC());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, sourceObisCode.getD(), actualObisCode.getD());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, sourceObisCode.getE(), actualObisCode.getE());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, sourceObisCode.getF(), actualObisCode.getF());
    }

    private void assertCaptureObjects(
            final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObject> actualCaptureObjects,
            final List<CaptureObject> sourceCaptureObjects) throws AssertionError {

        assertEquals(MAPPED_LIST_SIZE_MESSAGE, actualCaptureObjects.size(), sourceCaptureObjects.size());
        for (int i = 0; i < actualCaptureObjects.size(); i++) {
            this.assertCaptureObject(actualCaptureObjects.get(i), sourceCaptureObjects.get(i));
        }
    }

    private void assertCaptureObject(
            final org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObject actualCaptureObject,
            final CaptureObject sourceCaptureObject) throws AssertionError {
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, sourceCaptureObject.getClassId(), actualCaptureObject.getClassId());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, sourceCaptureObject.getLogicalName(),
                actualCaptureObject.getLogicalName());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, sourceCaptureObject.getAttributeIndex(),
                actualCaptureObject.getAttributeIndex().intValue());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, sourceCaptureObject.getDataIndex(),
                actualCaptureObject.getDataIndex());
    }

    private void assertProfileEntries(
            final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntry> actualProfileEntries,
            final List<ProfileEntry> sourceProfileEntries) throws AssertionError {

        assertEquals(MAPPED_LIST_SIZE_MESSAGE, actualProfileEntries.size(), sourceProfileEntries.size());
        for (int i = 0; i < actualProfileEntries.size(); i++) {
            this.assertProfileEntry(actualProfileEntries.get(i), sourceProfileEntries.get(i));
        }
    }

    private void assertProfileEntry(
            final org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntry actualProfileEntry,
            final ProfileEntry sourceProfileEntry) throws AssertionError {

        assertEquals(MAPPED_LIST_SIZE_MESSAGE, actualProfileEntry.getProfileEntryValue().size(),
                sourceProfileEntry.getProfileEntryValues().size());
        for (int i = 0; i < actualProfileEntry.getProfileEntryValue().size(); i++) {
            this.assertProfileEntryValue(actualProfileEntry.getProfileEntryValue().get(i),
                    sourceProfileEntry.getProfileEntryValues().get(i));
        }
    }

    private void assertProfileEntryValue(
            final org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntryValue actualProfileEntryValue,
            final ProfileEntryValue sourceProfileEntryValue) throws AssertionError {

        final Object actual = actualProfileEntryValue.getStringValueOrDateValueOrFloatValue().get(0);
        if (actual instanceof XMLGregorianCalendar) {
            final Date actualDate = ((XMLGregorianCalendar) actual).toGregorianCalendar().getTime();
            assertEquals(MAPPED_FIELD_VALUE_MESSAGE, sourceProfileEntryValue.getValue(), actualDate);
        } else {
            assertEquals(MAPPED_FIELD_VALUE_MESSAGE, sourceProfileEntryValue.getValue(), actual);
        }
    }
}
