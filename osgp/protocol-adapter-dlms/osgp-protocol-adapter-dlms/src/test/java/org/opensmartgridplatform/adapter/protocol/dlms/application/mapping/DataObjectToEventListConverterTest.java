/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDetailDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventLogCategoryDto;

class DataObjectToEventListConverterTest {
  private final DlmsHelper dlmsHelper = new DlmsHelper();
  private final DataObjectToEventListConverter converter =
      new DataObjectToEventListConverter(this.dlmsHelper);

  @Test
  void testSourceIsNull() {
    final DataObject source = null;

    final Throwable thrown =
        catchThrowable(
            () -> this.converter.convert(source, EventLogCategoryDto.STANDARD_EVENT_LOG));

    assertThat(thrown)
        .isInstanceOf(ProtocolAdapterException.class)
        .hasMessage("DataObject should not be null");
  }

  @Test
  void testEventDataIsNull() {
    final DataObject source =
        DataObject.newArrayData(Collections.singletonList(DataObject.newNullData()));

    final Throwable thrown =
        catchThrowable(
            () -> this.converter.convert(source, EventLogCategoryDto.STANDARD_EVENT_LOG));

    assertThat(thrown)
        .isInstanceOf(ProtocolAdapterException.class)
        .hasMessage("eventData DataObject should not be null");
  }

  @Test
  void testWrongEventElementListSize() {
    final DataObject source =
        DataObject.newArrayData(
            Collections.singletonList(
                DataObject.newArrayData(Collections.singletonList(this.getDataObject(1)))));

    final Throwable thrown =
        catchThrowable(
            () -> this.converter.convert(source, EventLogCategoryDto.STANDARD_EVENT_LOG));

    assertThat(thrown)
        .isInstanceOf(ProtocolAdapterException.class)
        .hasMessage("eventData size should be 2");
  }

  @Test
  void testEventsWithCodeAndTimeStamp() throws ProtocolAdapterException {

    // GIVEN
    final DateTime dateTime1 = new DateTime(2021, 9, 16, 10, 35, 10, DateTimeZone.UTC);
    final DateTime dateTime2 = new DateTime(2021, 9, 17, 11, 22, 45, DateTimeZone.UTC);

    final DataObject eventDataObject1 = this.createEventDataObject(dateTime1, 1);
    final DataObject eventDataObject2 = this.createEventDataObject(dateTime2, 2);

    final DataObject source =
        DataObject.newArrayData(Arrays.asList(eventDataObject1, eventDataObject2));
    final EventDto expectedEvent1 =
        new EventDto(dateTime1, 1, null, EventLogCategoryDto.STANDARD_EVENT_LOG.name());
    final EventDto expectedEvent2 =
        new EventDto(dateTime2, 2, null, EventLogCategoryDto.STANDARD_EVENT_LOG.name());

    // WHEN
    final List<EventDto> events =
        this.converter.convert(source, EventLogCategoryDto.STANDARD_EVENT_LOG);

    // THEN
    assertThat(events)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(expectedEvent1, expectedEvent2);
  }

  @Test
  void testEventsWithCodeTimeStampAndCounter() throws ProtocolAdapterException {

    // GIVEN
    final DateTime dateTime1 = new DateTime(2021, 9, 16, 10, 35, 10, DateTimeZone.UTC);
    final DateTime dateTime2 = new DateTime(2021, 9, 17, 11, 22, 45, DateTimeZone.UTC);
    final DataObject eventDataObject1 = this.createEventDataObject(dateTime1, 1, 11);
    final DataObject eventDataObject2 = this.createEventDataObject(dateTime2, 2, 12);

    final DataObject source =
        DataObject.newArrayData(Arrays.asList(eventDataObject1, eventDataObject2));
    final EventDto expectedEvent1 =
        new EventDto(dateTime1, 1, 11, EventLogCategoryDto.COMMUNICATION_SESSION_LOG.name());
    final EventDto expectedEvent2 =
        new EventDto(dateTime2, 2, 12, EventLogCategoryDto.COMMUNICATION_SESSION_LOG.name());

    // WHEN
    final List<EventDto> events =
        this.converter.convert(source, EventLogCategoryDto.COMMUNICATION_SESSION_LOG);

    // THEN
    assertThat(events)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(expectedEvent1, expectedEvent2);
  }

  @Test
  void testEventsWithEventDetails() throws ProtocolAdapterException {

    // GIVEN
    final DateTime dateTime1 = new DateTime(2021, 9, 16, 10, 35, 10, DateTimeZone.UTC);
    final DateTime dateTime2 = new DateTime(2021, 9, 17, 11, 22, 45, DateTimeZone.UTC);
    final DataObject eventDataObject1 = this.createEventDataObject(dateTime1, 1, 11, 21);
    final DataObject eventDataObject2 = this.createEventDataObject(dateTime2, 2, 12, 22);
    final String MAGNITUDE = "magnitude";
    final String DURATION = "duration";

    final DataObject source =
        DataObject.newArrayData(Arrays.asList(eventDataObject1, eventDataObject2));
    final EventDto expectedEvent1 =
        new EventDto(
            dateTime1, 1, null, EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG.name());
    expectedEvent1.addEventDetail(new EventDetailDto(MAGNITUDE, "1.1 V"));
    expectedEvent1.addEventDetail(new EventDetailDto(DURATION, "2.1 s"));
    final EventDto expectedEvent2 =
        new EventDto(
            dateTime2, 2, null, EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG.name());
    expectedEvent2.addEventDetail(new EventDetailDto(MAGNITUDE, "1.2 V"));
    expectedEvent2.addEventDetail(new EventDetailDto(DURATION, "2.2 s"));

    // WHEN
    final List<EventDto> events =
        this.converter.convert(source, EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG);

    // THEN
    assertThat(events)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(expectedEvent1, expectedEvent2);
  }

  private DataObject createEventDataObject(final DateTime dateTime, final int number) {
    final DataObject eventCode = this.getDataObject(number);
    final DataObject timeStamp = this.dlmsHelper.asDataObject(dateTime);
    return DataObject.newStructureData(Arrays.asList(timeStamp, eventCode));
  }

  private DataObject createEventDataObject(
      final DateTime dateTime, final int number, final int intCounter) {
    final DataObject eventCode = this.getDataObject(number);
    final DataObject timeStamp = this.dlmsHelper.asDataObject(dateTime);
    final DataObject counter = this.getDataObject(intCounter);
    return DataObject.newStructureData(Arrays.asList(timeStamp, eventCode, counter));
  }

  private DataObject createEventDataObject(
      final DateTime dateTime, final int number, final int intMagnitude, final int intDuration) {
    final DataObject eventCode = this.getDataObject(number);
    final DataObject timeStamp = this.dlmsHelper.asDataObject(dateTime);
    final DataObject magnitude = this.getDataObject(intMagnitude);
    final DataObject duration = this.getDataObject(intDuration);
    return DataObject.newStructureData(Arrays.asList(timeStamp, eventCode, magnitude, duration));
  }

  private DataObject getDataObject(final int number) {
    return DataObject.newInteger32Data(number);
  }
}
