/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CaptureObject;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityData;

public class ActualPowerQualityResponseMappingTest {

    private static final String MAPPED_FIELD_VALUE_MESSAGE = "Mapped fields should have the same value.";
    private static final String MAPPED_LIST_SIZE_MESSAGE = "Mapped lists should have the same size.";

    private final MonitoringMapper monitoringMapper = new MonitoringMapper();

    @Test
    public void shouldConvertActualPowerQualityResponse() {
        // Arrange
        final List<CaptureObject> captureObjects = this.makeCaptureObjects();
        final List<ActualValue> actualValues = this.makeActualValues();

        final ActualPowerQualityData responseData = new ActualPowerQualityData(captureObjects,
                actualValues);

        final ActualPowerQualityResponse source = new ActualPowerQualityResponse();
        source.setActualPowerQualityData(responseData);

        // Act
        final org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActualPowerQualityResponse target
                = this.monitoringMapper
                .map(source,
                        org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActualPowerQualityResponse.class);

        // Assert
        final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityData mappedResponseData = target
                .getActualPowerQualityData();

        this.assertCaptureObjects(mappedResponseData.getCaptureObjects().getCaptureObjects(), captureObjects);
        this.assertActualValues(mappedResponseData.getActualValues().getActualValue(), actualValues);

    }

    private void assertCaptureObject(
            final org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObject actualCaptureObject,
            final CaptureObject sourceCaptureObject) throws AssertionError {

        assertThat(actualCaptureObject.getClassId()).as(MAPPED_FIELD_VALUE_MESSAGE)
                                                    .isEqualTo(sourceCaptureObject.getClassId());
        assertThat(actualCaptureObject.getLogicalName()).as(MAPPED_FIELD_VALUE_MESSAGE)
                                                        .isEqualTo(sourceCaptureObject.getLogicalName());
        assertThat(actualCaptureObject.getAttributeIndex().intValue()).as(MAPPED_FIELD_VALUE_MESSAGE).isEqualTo(
                sourceCaptureObject.getAttributeIndex());
        assertThat(actualCaptureObject.getDataIndex()).as(MAPPED_FIELD_VALUE_MESSAGE)
                                                      .isEqualTo(sourceCaptureObject.getDataIndex());
    }

    private void assertCaptureObjects(
            final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObject> actualCaptureObjects,
            final List<CaptureObject> sourceCaptureObjects) throws AssertionError {

        assertThat(actualCaptureObjects.size()).as(MAPPED_LIST_SIZE_MESSAGE).isEqualTo(sourceCaptureObjects.size());
        for (int i = 0; i < actualCaptureObjects.size(); i++) {
            this.assertCaptureObject(actualCaptureObjects.get(i), sourceCaptureObjects.get(i));
        }
    }

    private void assertActualValues(
            final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualValue> actualValues,
            final List<ActualValue> sourceActualValues) throws AssertionError {

        assertThat(actualValues.size()).as(MAPPED_LIST_SIZE_MESSAGE).isEqualTo(sourceActualValues.size());
        for (int i = 0; i < actualValues.size(); i++) {
            this.assertActualValue(actualValues.get(i), sourceActualValues.get(i));
        }
    }

    private void assertActualValue(
            final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualValue actualValue,
            final ActualValue sourceActualValue) throws AssertionError {

        final Object actual = actualValue.getStringValueOrDateValueOrFloatValue().get(0);
        if (actual instanceof XMLGregorianCalendar) {
            final Date actualDate = ((XMLGregorianCalendar) actual).toGregorianCalendar().getTime();
            assertThat(actualDate).as(MAPPED_FIELD_VALUE_MESSAGE).isEqualTo(sourceActualValue.getValue());
        } else {
            assertThat(actual).as(MAPPED_FIELD_VALUE_MESSAGE).isEqualTo(sourceActualValue.getValue());
        }
    }

    private List<CaptureObject> makeCaptureObjects() {
        final List<CaptureObject> captureObjects = new ArrayList<>();
        captureObjects.add(new CaptureObject(8L, "0.0.1.0.0.255", 2, 0, OsgpUnitType.UNDEFINED.name()));
        captureObjects.add(new CaptureObject(3L, "1.1.1.0.0.255", 2, 0, OsgpUnitType.W.name()));
        captureObjects.add(new CaptureObject(3L, "2.2.2.0.0.255", 2, 0, OsgpUnitType.W.name()));
        captureObjects.add(new CaptureObject(4L, "3.3.3.0.0.255", 2, 0, OsgpUnitType.W.name()));
        return captureObjects;
    }

    private List<ActualValue> makeActualValues() {
        return this.makeActualValue(new DateTime(2017, 1, 1, 1, 0, 0, DateTimeZone.UTC).toDate(), "test1",
                new BigDecimal(1.1d), 111L);
    }

    private List<ActualValue> makeActualValue(final Serializable... values) {
        final List<ActualValue> actualValues = new ArrayList<>();
        for (final Serializable value : values) {
            actualValues.add(new ActualValue(value));
        }
        return actualValues;
    }

}
