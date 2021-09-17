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
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDetailNameTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventLogCategoryDto;

class DataObjectToEventListConverterTest {
  private final DlmsHelper dlmsHelper = new DlmsHelper();
  private final DataObjectToEventListConverter converter =
      new DataObjectToEventListConverter(this.dlmsHelper);

  @Test
  void testSourceIsNull() throws ProtocolAdapterException {

    final Exception exception =
        assertThrows(
            ProtocolAdapterException.class,
            () -> this.converter.convert(null, EventLogCategoryDto.STANDARD_EVENT_LOG));

    assertThat(exception.getMessage()).isEqualTo("DataObject should not be null");
  }

  @Test
  void testEventDataIsNull() throws ProtocolAdapterException {

    final Exception exception =
        assertThrows(
            ProtocolAdapterException.class,
            () ->
                this.converter.convert(
                    DataObject.newArrayData(Collections.singletonList(DataObject.newNullData())),
                    EventLogCategoryDto.STANDARD_EVENT_LOG));

    assertThat(exception.getMessage()).isEqualTo("eventData DataObject should not be null");
  }

  @Test
  void testWrongEventElementListSize() throws ProtocolAdapterException {

    final Exception exception =
        assertThrows(
            ProtocolAdapterException.class,
            () ->
                this.converter.convert(
                    DataObject.newArrayData(
                        Collections.singletonList(
                            DataObject.newArrayData(
                                Collections.singletonList(DataObject.newInteger32Data(1))))),
                    EventLogCategoryDto.STANDARD_EVENT_LOG));

    assertThat(exception.getMessage()).isEqualTo("eventData size should be 2");
  }

  @Test
  void testEventsWithCodeAndTimeStamp() throws ProtocolAdapterException {

    // GIVEN
    final DataObject eventCode1 = DataObject.newInteger32Data(1);
    final DataObject eventCode2 = DataObject.newInteger32Data(2);
    final DateTime dateTime1 = new DateTime(2021, 9, 16, 10, 35, 10, DateTimeZone.UTC);
    final DateTime dateTime2 = new DateTime(2021, 9, 17, 11, 22, 45, DateTimeZone.UTC);
    final DataObject timeStamp1 = this.dlmsHelper.asDataObject(dateTime1);
    final DataObject timeStamp2 = this.dlmsHelper.asDataObject(dateTime2);
    final DataObject dataOfEvent1 =
        DataObject.newStructureData(Arrays.asList(timeStamp1, eventCode1));
    final DataObject dataOfEvent2 =
        DataObject.newStructureData(Arrays.asList(timeStamp2, eventCode2));
    final DataObject source = DataObject.newArrayData(Arrays.asList(dataOfEvent1, dataOfEvent2));

    // WHEN
    final List<EventDto> events =
        this.converter.convert(source, EventLogCategoryDto.STANDARD_EVENT_LOG);

    // THEN
    final EventDto event1 =
        new EventDto(dateTime1, 1, null, EventLogCategoryDto.STANDARD_EVENT_LOG.name());
    final EventDto event2 =
        new EventDto(dateTime2, 2, null, EventLogCategoryDto.STANDARD_EVENT_LOG.name());
    assertThat(events)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(event1, event2);
  }

  @Test
  void testEventsWithCodeTimeStampAndCounter() throws ProtocolAdapterException {

    // GIVEN
    final DataObject eventCode1 = DataObject.newInteger32Data(1);
    final DataObject eventCode2 = DataObject.newInteger32Data(2);
    final DateTime dateTime1 = new DateTime(2021, 9, 16, 10, 35, 10, DateTimeZone.UTC);
    final DateTime dateTime2 = new DateTime(2021, 9, 17, 11, 22, 45, DateTimeZone.UTC);
    final DataObject timeStamp1 = this.dlmsHelper.asDataObject(dateTime1);
    final DataObject timeStamp2 = this.dlmsHelper.asDataObject(dateTime2);
    final DataObject counter1 = DataObject.newInteger32Data(11);
    final DataObject counter2 = DataObject.newInteger32Data(12);
    final DataObject dataOfEvent1 =
        DataObject.newStructureData(Arrays.asList(timeStamp1, eventCode1, counter1));
    final DataObject dataOfEvent2 =
        DataObject.newStructureData(Arrays.asList(timeStamp2, eventCode2, counter2));
    final DataObject source = DataObject.newArrayData(Arrays.asList(dataOfEvent1, dataOfEvent2));

    // WHEN
    final List<EventDto> events =
        this.converter.convert(source, EventLogCategoryDto.COMMUNICATION_SESSION_LOG);

    // THEN
    final EventDto event1 =
        new EventDto(dateTime1, 1, 11, EventLogCategoryDto.COMMUNICATION_SESSION_LOG.name());
    final EventDto event2 =
        new EventDto(dateTime2, 2, 12, EventLogCategoryDto.COMMUNICATION_SESSION_LOG.name());
    assertThat(events)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(event1, event2);
  }

  @Test
  void testEventsWithEventDetails() throws ProtocolAdapterException {

    // GIVEN
    final DataObject eventCode1 = DataObject.newInteger32Data(1);
    final DataObject eventCode2 = DataObject.newInteger32Data(2);
    final DateTime dateTime1 = new DateTime(2021, 9, 16, 10, 35, 10, DateTimeZone.UTC);
    final DateTime dateTime2 = new DateTime(2021, 9, 17, 11, 22, 45, DateTimeZone.UTC);
    final DataObject timeStamp1 = this.dlmsHelper.asDataObject(dateTime1);
    final DataObject timeStamp2 = this.dlmsHelper.asDataObject(dateTime2);
    final DataObject magnitude1 = DataObject.newInteger32Data(11);
    final DataObject magnitude2 = DataObject.newInteger32Data(12);
    final DataObject duration1 = DataObject.newInteger32Data(21);
    final DataObject duration2 = DataObject.newInteger32Data(22);
    final DataObject dataOfEvent1 =
        DataObject.newStructureData(Arrays.asList(timeStamp1, eventCode1, magnitude1, duration1));
    final DataObject dataOfEvent2 =
        DataObject.newStructureData(Arrays.asList(timeStamp2, eventCode2, magnitude2, duration2));
    final DataObject source = DataObject.newArrayData(Arrays.asList(dataOfEvent1, dataOfEvent2));

    // WHEN
    final List<EventDto> events =
        this.converter.convert(source, EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG);

    // THEN
    final EventDto event1 =
        new EventDto(
            dateTime1, 1, null, EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG.name());
    event1.addEventDetail(new EventDetailDto(EventDetailNameTypeDto.MAGNITUDE, "1.1 V"));
    event1.addEventDetail(new EventDetailDto(EventDetailNameTypeDto.DURATION, "2.1 s"));
    final EventDto event2 =
        new EventDto(
            dateTime2, 2, null, EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG.name());
    event2.addEventDetail(new EventDetailDto(EventDetailNameTypeDto.MAGNITUDE, "1.2 V"));
    event2.addEventDetail(new EventDetailDto(EventDetailNameTypeDto.DURATION, "2.2 s"));
    assertThat(events)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(event1, event2);
  }
}
