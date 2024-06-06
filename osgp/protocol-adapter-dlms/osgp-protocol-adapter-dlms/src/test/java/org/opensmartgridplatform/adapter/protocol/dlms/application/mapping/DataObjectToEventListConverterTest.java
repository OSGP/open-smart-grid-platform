// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperty;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDetailDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventLogCategoryDto;

class DataObjectToEventListConverterTest {
  private DlmsHelper dlmsHelper;
  private ObjectConfigService objectConfigService;
  private DataObjectToEventListConverter converter;

  @BeforeEach
  void setup() {
    try {
      this.dlmsHelper = new DlmsHelper();
      this.objectConfigService = new ObjectConfigService();
      this.converter =
          new DataObjectToEventListConverter(this.dlmsHelper, this.objectConfigService);
    } catch (final ObjectConfigException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void testDlmsObjectFound() throws ObjectConfigException {
    final Protocol protocol = Protocol.SMR_5_0_0;

    final DateTime dateTime1 = new DateTime(2021, 9, 16, 10, 35, 10, DateTimeZone.UTC);
    final DataObject eventDataObject =
        this.createEventDataObjectMagnitudeDuration(dateTime1, 93, 11, 21);
    final DataObject source = DataObject.newArrayData(Arrays.asList(eventDataObject));

    final Throwable thrown =
        catchThrowable(
            () ->
                this.converter.convert(
                    source, EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG, protocol));

    assertThat(thrown)
        .isInstanceOf(ProtocolAdapterException.class)
        .hasMessage("No CosemObject found for dlmstype POWER_QUALITY_EXTENDED_EVENT_MAGNITUDE")
        .hasCauseInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void testMissingSourceObjectMapping() throws ObjectConfigException {
    final Protocol protocol = Protocol.SMR_5_2;

    final ObjectConfigService objectConfigServiceMock = mock(ObjectConfigService.class);
    final DataObjectToEventListConverter converterWithMockedObjectConfig =
        new DataObjectToEventListConverter(this.dlmsHelper, objectConfigServiceMock);
    final CosemObject cosemObjectWithoutSourceObjectMapping = mock(CosemObject.class);
    when(objectConfigServiceMock.getCosemObject(
            protocol.getName(),
            protocol.getVersion(),
            DlmsObjectType.POWER_QUALITY_EXTENDED_EVENT_MAGNITUDE))
        .thenReturn(cosemObjectWithoutSourceObjectMapping);
    when(cosemObjectWithoutSourceObjectMapping.getProperties()).thenReturn(new HashMap<>());

    final DateTime dateTime1 = new DateTime(2021, 9, 16, 10, 35, 10, DateTimeZone.UTC);
    final DataObject eventDataObject =
        this.createEventDataObjectMagnitudeDuration(dateTime1, 93, 11, 21);
    final DataObject source = DataObject.newArrayData(Arrays.asList(eventDataObject));

    final Throwable thrown =
        catchThrowable(
            () ->
                converterWithMockedObjectConfig.convert(
                    source, EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG, protocol));

    assertThat(thrown)
        .isInstanceOf(ProtocolAdapterException.class)
        .hasMessage(
            "No SOURCE_OBJECT property available for dlms objecttype POWER_QUALITY_EXTENDED_EVENT_MAGNITUDE");
  }

  @Test
  void testNoSourceObjectMappedOnEventCode() throws ObjectConfigException {
    final Protocol protocol = Protocol.SMR_5_2;

    final DateTime dateTime1 = new DateTime(2021, 9, 16, 10, 35, 10, DateTimeZone.UTC);
    final DataObject eventDataObject =
        this.createEventDataObjectMagnitudeDuration(dateTime1, 1, 11, 21);
    final DataObject source = DataObject.newArrayData(Arrays.asList(eventDataObject));

    final Throwable thrown =
        catchThrowable(
            () ->
                this.converter.convert(
                    source, EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG, protocol));

    assertThat(thrown)
        .isInstanceOf(ProtocolAdapterException.class)
        .hasMessage(
            "No sourceObject mapped for event 1 on dlmsobject POWER_QUALITY_EXTENDED_EVENT_MAGNITUDE");
  }

  @Test
  void testMappedSourceObjectNotRegisterClass() throws ObjectConfigException {
    final Protocol protocol = Protocol.SMR_5_2;

    final ObjectConfigService objectConfigServiceMock = mock(ObjectConfigService.class);
    final DataObjectToEventListConverter converterWithMockedObjectConfig =
        new DataObjectToEventListConverter(this.dlmsHelper, objectConfigServiceMock);
    final CosemObject cosemObjectWithSourceObjectMapping = mock(CosemObject.class);
    when(objectConfigServiceMock.getCosemObject(
            protocol.getName(),
            protocol.getVersion(),
            DlmsObjectType.POWER_QUALITY_EXTENDED_EVENT_MAGNITUDE))
        .thenReturn(cosemObjectWithSourceObjectMapping);
    when(objectConfigServiceMock.getCosemObject(
            protocol.getName(),
            protocol.getVersion(),
            DlmsObjectType.POWER_QUALITY_EXTENDED_EVENT_CODE))
        .thenReturn(
            new CosemObject(
                null, null, InterfaceClass.DATA.id(), 0, null, null, null, null, null, null));
    when(cosemObjectWithSourceObjectMapping.getProperties())
        .thenReturn(
            Map.of(
                ObjectProperty.SOURCE_OBJECTS, Map.of("93", "POWER_QUALITY_EXTENDED_EVENT_CODE")));

    final DateTime dateTime1 = new DateTime(2021, 9, 16, 10, 35, 10, DateTimeZone.UTC);
    final DataObject eventDataObject =
        this.createEventDataObjectMagnitudeDuration(dateTime1, 93, 11, 21);
    final DataObject source = DataObject.newArrayData(Arrays.asList(eventDataObject));

    final Throwable thrown =
        catchThrowable(
            () ->
                converterWithMockedObjectConfig.convert(
                    source, EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG, protocol));

    assertThat(thrown)
        .isInstanceOf(ProtocolAdapterException.class)
        .hasMessage(
            "SourceObject mapped on event 93 with dlmstype POWER_QUALITY_EXTENDED_EVENT_MAGNITUDE"
                + " is not of class REGISTER");
  }

  @Test
  void testSourceIsNull() {
    final DataObject source = null;
    final Protocol protocol = Protocol.SMR_5_0_0;
    final Throwable thrown =
        catchThrowable(
            () -> this.converter.convert(source, EventLogCategoryDto.STANDARD_EVENT_LOG, protocol));

    assertThat(thrown)
        .isInstanceOf(ProtocolAdapterException.class)
        .hasMessage("DataObject should not be null");
  }

  @Test
  void testEventDataIsNull() {
    final DataObject source =
        DataObject.newArrayData(Collections.singletonList(DataObject.newNullData()));
    final Protocol protocol = Protocol.SMR_5_0_0;

    final Throwable thrown =
        catchThrowable(
            () -> this.converter.convert(source, EventLogCategoryDto.STANDARD_EVENT_LOG, protocol));

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
    final Protocol protocol = Protocol.SMR_5_0_0;

    final Throwable thrown =
        catchThrowable(
            () -> this.converter.convert(source, EventLogCategoryDto.STANDARD_EVENT_LOG, protocol));

    assertThat(thrown)
        .isInstanceOf(ProtocolAdapterException.class)
        .hasMessage("eventData size should be 2");
  }

  @Test
  void testEventsWithCodeAndTimeStamp() throws ProtocolAdapterException {

    final Protocol protocol = Protocol.SMR_5_0_0;

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
        this.converter.convert(source, EventLogCategoryDto.STANDARD_EVENT_LOG, protocol);

    // THEN
    assertThat(events)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(expectedEvent1, expectedEvent2);
  }

  @Test
  void testEventsWithCodeTimeStampAndCounter() throws ProtocolAdapterException {

    final Protocol protocol = Protocol.SMR_5_0_0;

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
        this.converter.convert(source, EventLogCategoryDto.COMMUNICATION_SESSION_LOG, protocol);

    // THEN
    assertThat(events)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(expectedEvent1, expectedEvent2);
  }

  @Test
  void testEventsWithMagnitudeAndDurationDetails() throws ProtocolAdapterException {

    final Protocol protocol = Protocol.SMR_5_2;

    // GIVEN
    final DateTime dateTime1 = new DateTime(2021, 9, 16, 10, 35, 10, DateTimeZone.UTC);
    final DateTime dateTime2 = new DateTime(2021, 9, 17, 11, 22, 45, DateTimeZone.UTC);
    final DataObject eventDataObject1 =
        this.createEventDataObjectMagnitudeDuration(dateTime1, 93, 11, 21);
    final DataObject eventDataObject2 =
        this.createEventDataObjectMagnitudeDuration(dateTime2, 94, 12, 22);
    final DataObject source =
        DataObject.newArrayData(Arrays.asList(eventDataObject1, eventDataObject2));

    final String MAGNITUDE = "magnitude";
    final String DURATION = "duration";
    final EventDto expectedEvent1 =
        new EventDto(
            dateTime1, 93, null, EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG.name());
    expectedEvent1.addEventDetail(new EventDetailDto(MAGNITUDE, "1.1 V"));
    expectedEvent1.addEventDetail(new EventDetailDto(DURATION, "2.1 s"));
    final EventDto expectedEvent2 =
        new EventDto(
            dateTime2, 94, null, EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG.name());
    expectedEvent2.addEventDetail(new EventDetailDto(MAGNITUDE, "1.2 V"));
    expectedEvent2.addEventDetail(new EventDetailDto(DURATION, "2.2 s"));

    // WHEN
    final List<EventDto> events =
        this.converter.convert(
            source, EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG, protocol);

    // THEN
    assertThat(events)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(expectedEvent1, expectedEvent2);
  }

  @Test
  void testEventsWithMagnitudeDetails() throws ProtocolAdapterException {

    final Protocol protocol = Protocol.SMR_5_2;

    // GIVEN
    final DateTime dateTime1 = new DateTime(2021, 9, 16, 10, 35, 10, DateTimeZone.UTC);
    final DateTime dateTime2 = new DateTime(2021, 9, 17, 11, 22, 45, DateTimeZone.UTC);
    final DataObject eventDataObject1 = this.createEventDataObjectMagnitude(dateTime1, 51, 11);
    final DataObject eventDataObject2 = this.createEventDataObjectMagnitude(dateTime2, 52, 12);
    final String MAGNITUDE = "magnitude";

    final DataObject source =
        DataObject.newArrayData(Arrays.asList(eventDataObject1, eventDataObject2));
    final EventDto expectedEvent1 =
        new EventDto(dateTime1, 51, null, EventLogCategoryDto.POWER_QUALITY_THD_EVENT_LOG.name());
    expectedEvent1.addEventDetail(new EventDetailDto(MAGNITUDE, "11 %"));
    final EventDto expectedEvent2 =
        new EventDto(dateTime2, 52, null, EventLogCategoryDto.POWER_QUALITY_THD_EVENT_LOG.name());
    expectedEvent2.addEventDetail(new EventDetailDto(MAGNITUDE, "12 %"));

    // WHEN
    final List<EventDto> events =
        this.converter.convert(source, EventLogCategoryDto.POWER_QUALITY_THD_EVENT_LOG, protocol);

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

  private DataObject createEventDataObjectMagnitudeDuration(
      final DateTime dateTime, final int number, final int intMagnitude, final int intDuration) {
    final DataObject eventCode = this.getDataObject(number);
    final DataObject timeStamp = this.dlmsHelper.asDataObject(dateTime);
    final DataObject magnitude = this.getDataObject(intMagnitude);
    final DataObject duration = this.getDataObject(intDuration);
    return DataObject.newStructureData(Arrays.asList(timeStamp, eventCode, magnitude, duration));
  }

  private DataObject createEventDataObjectMagnitude(
      final DateTime dateTime, final int number, final int intMagnitude) {
    final DataObject eventCode = this.getDataObject(number);
    final DataObject timeStamp = this.dlmsHelper.asDataObject(dateTime);
    final DataObject magnitude = this.getDataObject(intMagnitude);
    return DataObject.newStructureData(Arrays.asList(timeStamp, eventCode, magnitude));
  }

  private DataObject getDataObject(final int number) {
    return DataObject.newInteger32Data(number);
  }
}
