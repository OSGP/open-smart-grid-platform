/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventLogCategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = "dataObjectToEventListConverter")
public class DataObjectToEventListConverter {

  public static final String EVENT_DATA_VALUE_IS_NOT_A_NUMBER = "eventData value is not a number";

  private final DlmsHelper dlmsHelper;

  @Autowired
  public DataObjectToEventListConverter(final DlmsHelper dlmsHelper) {
    this.dlmsHelper = dlmsHelper;
  }

  public List<EventDto> convert(final DataObject source, final EventLogCategoryDto eventLogCategory)
      throws ProtocolAdapterException {
    final List<EventDto> eventList = new ArrayList<>();
    if (source == null) {
      throw new ProtocolAdapterException("DataObject should not be null");
    }

    final List<DataObject> listOfEvents = source.getValue();
    for (final DataObject eventDataObject : listOfEvents) {
      eventList.add(this.getEvent(eventDataObject, eventLogCategory));
    }

    return eventList;
  }

  private EventDto getEvent(
      final DataObject eventDataObject, final EventLogCategoryDto eventLogCategory)
      throws ProtocolAdapterException {

    final List<DataObject> eventData = eventDataObject.getValue();

    if (eventData == null) {
      throw new ProtocolAdapterException("eventData DataObject should not be null");
    }

    if (eventData.size() != eventLogCategory.getNumberOfEventElements()) {
      throw new ProtocolAdapterException(
          "eventData size should be " + eventLogCategory.getNumberOfEventElements());
    }

    // extract values from List<DataObject> eventData.
    final DateTime dateTime = this.extractDateTime(eventData);
    final Short code = this.extractCode(eventData);
    final Integer eventCounter = this.extractEventCounter(eventLogCategory, eventData);
    final String eventLogCategoryName = eventLogCategory.name();

    log.info(
        "Event time is {}, event code is {}, event category is {} and event counter is {}",
        dateTime,
        code,
        eventLogCategoryName,
        eventCounter);

    // build a new EventDto with those values.
    return new EventDto(dateTime, code.intValue(), eventCounter, eventLogCategoryName);
  }

  private DateTime extractDateTime(final List<DataObject> eventData)
      throws ProtocolAdapterException {

    final DateTime dateTime =
        this.dlmsHelper.convertDataObjectToDateTime(eventData.get(0)).asDateTime();
    if (dateTime == null) {
      throw new ProtocolAdapterException("eventData time is null/unspecified");
    }
    return dateTime;
  }

  private Short extractCode(final List<DataObject> eventData) throws ProtocolAdapterException {

    if (!eventData.get(1).isNumber()) {
      throw new ProtocolAdapterException(EVENT_DATA_VALUE_IS_NOT_A_NUMBER);
    }
    return eventData.get(1).getValue();
  }

  private Integer extractEventCounter(
      final EventLogCategoryDto eventLogCategory, final List<DataObject> eventData)
      throws ProtocolAdapterException {

    Integer eventCounter = null;

    if (eventLogCategory.getNumberOfEventElements() == 3) {
      if (!eventData.get(2).isNumber()) {
        throw new ProtocolAdapterException(EVENT_DATA_VALUE_IS_NOT_A_NUMBER);
      }
      eventCounter = eventData.get(2).getValue();
    }

    return eventCounter;
  }
}
