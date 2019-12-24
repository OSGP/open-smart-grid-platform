/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetProfileGenericDataRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObjectDefinitions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataRequestData;

public class GetProfileGenericDataRequestMappingTest {

    private static final String MAPPED_OBJECT_NULL_MESSAGE = "Mapped object should not be null.";
    private static final String MAPPED_FIELD_VALUE_MESSAGE = "Mapped field should have the same value.";

    private static final DateTime BEGIN_DATE = new DateTime(2017, 1, 1, 0, 0, 0, DateTimeZone.UTC);
    private static final DateTime END_DATE = new DateTime(2017, 2, 1, 0, 0, 0, DateTimeZone.UTC);

    private final MonitoringMapper mapper = new MonitoringMapper();

    private ObisCodeValues makeObisCodeValues() {
        final ObisCodeValues result = new ObisCodeValues();
        result.setA((short) 1);
        result.setB((short) 2);
        result.setC((short) 3);
        result.setD((short) 4);
        result.setE((short) 5);
        result.setF((short) 6);
        return result;
    }

    private GetProfileGenericDataRequest makeRequest() {
        final GetProfileGenericDataRequest result = new GetProfileGenericDataRequest();
        result.setObisCode(this.makeObisCodeValues());
        result.setBeginDate(this.toGregorianCalendar(BEGIN_DATE));
        result.setEndDate(this.toGregorianCalendar(END_DATE));
        result.setSelectedValues(new CaptureObjectDefinitions());
        return result;
    }

    @Test
    public void shouldConvertGetProfileGenericDataRequest() {
        final GetProfileGenericDataRequest source = this.makeRequest();
        final ProfileGenericDataRequestData result = this.mapper.map(source, ProfileGenericDataRequestData.class);
        assertThat(result).as(MAPPED_OBJECT_NULL_MESSAGE).isNotNull();

        assertThat(result.getObisCode().getA()).as(MAPPED_FIELD_VALUE_MESSAGE)
                .isEqualTo((byte) source.getObisCode().getA());
        assertThat(result.getObisCode().getB()).as(MAPPED_FIELD_VALUE_MESSAGE)
                .isEqualTo((byte) source.getObisCode().getB());
        assertThat(result.getObisCode().getC()).as(MAPPED_FIELD_VALUE_MESSAGE)
                .isEqualTo((byte) source.getObisCode().getC());
        assertThat(result.getObisCode().getD()).as(MAPPED_FIELD_VALUE_MESSAGE)
                .isEqualTo((byte) source.getObisCode().getD());
        assertThat(result.getObisCode().getE()).as(MAPPED_FIELD_VALUE_MESSAGE)
                .isEqualTo((byte) source.getObisCode().getE());
        assertThat(result.getObisCode().getF()).as(MAPPED_FIELD_VALUE_MESSAGE)
                .isEqualTo((byte) source.getObisCode().getF());

        assertThat(this.toGregorianCalendar(new DateTime(result.getBeginDate()))).as(MAPPED_FIELD_VALUE_MESSAGE)
                .isEqualTo(source.getBeginDate());
        assertThat(this.toGregorianCalendar(new DateTime(result.getEndDate()))).as(MAPPED_FIELD_VALUE_MESSAGE)
                .isEqualTo(source.getEndDate());
    }

    private XMLGregorianCalendar toGregorianCalendar(final DateTime dateTime) {
        final GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(dateTime.toDate());
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        } catch (final DatatypeConfigurationException e) {
            throw new RuntimeException("error creating XMLGregorianCalendar");
        }
    }
}
