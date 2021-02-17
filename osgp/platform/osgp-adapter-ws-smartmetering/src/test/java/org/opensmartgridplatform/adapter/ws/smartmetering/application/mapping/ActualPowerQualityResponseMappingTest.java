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
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PowerQualityObject;

public class ActualPowerQualityResponseMappingTest {

    private static final String MAPPED_FIELD_VALUE_MESSAGE = "Mapped fields should have the same value.";
    private static final String MAPPED_LIST_SIZE_MESSAGE = "Mapped lists should have the same size.";

    private final MonitoringMapper monitoringMapper = new MonitoringMapper();

    @Test
    public void shouldConvertActualPowerQualityResponse() {
        // Arrange
        final List<PowerQualityObject> powerQualityObjects = this.makePowerQualityObjects();
        final List<ActualValue> actualValues = this.makeActualValues();

        final ActualPowerQualityData responseData = new ActualPowerQualityData(powerQualityObjects,
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

        this.assertPowerQualityObjects(mappedResponseData.getPowerQualityObjects().getPowerQualityObject(),
                powerQualityObjects);
        this.assertActualValues(mappedResponseData.getActualValues().getActualValue(), actualValues);

    }

    @Test
    public void sourceNull() {
        final org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActualPowerQualityResponse target
                = this.monitoringMapper
                .map(null,
                        org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActualPowerQualityResponse.class);

        assertThat(target).isNull();
    }

    private void assertPowerQualityObject(
            final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PowerQualityObject actualPowerQualityObject,
            final PowerQualityObject sourcePowerQualityObject) throws AssertionError {

        assertThat(actualPowerQualityObject.getName()).as(MAPPED_FIELD_VALUE_MESSAGE)
                                                        .isEqualTo(sourcePowerQualityObject.getName());
        assertThat(actualPowerQualityObject.getUnit().value()).as(MAPPED_FIELD_VALUE_MESSAGE)
                                                      .isEqualTo(sourcePowerQualityObject.getUnit());
    }

    private void assertPowerQualityObjects(
            final List<org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PowerQualityObject> actualPowerQualityObjects,
            final List<PowerQualityObject> sourcePowerQualityObjects) throws AssertionError {

        assertThat(actualPowerQualityObjects.size()).as(MAPPED_LIST_SIZE_MESSAGE).isEqualTo(sourcePowerQualityObjects.size());
        for (int i = 0; i < actualPowerQualityObjects.size(); i++) {
            this.assertPowerQualityObject(actualPowerQualityObjects.get(i), sourcePowerQualityObjects.get(i));
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

    private List<PowerQualityObject> makePowerQualityObjects() {
        final List<PowerQualityObject> powerQualityObjects = new ArrayList<>();
        powerQualityObjects.add(new PowerQualityObject("ATTR1", OsgpUnitType.UNDEFINED.name()));
        powerQualityObjects.add(new PowerQualityObject("ATTR2", OsgpUnitType.W.name()));
        powerQualityObjects.add(new PowerQualityObject("ATTR3", OsgpUnitType.W.name()));
        powerQualityObjects.add(new PowerQualityObject("ATTR4", OsgpUnitType.W.name()));
        return powerQualityObjects;
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
