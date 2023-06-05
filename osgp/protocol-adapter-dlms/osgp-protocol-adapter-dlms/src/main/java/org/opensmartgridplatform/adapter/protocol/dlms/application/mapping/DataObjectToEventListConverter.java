// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDetailDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventLogCategoryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventLogCategoryDto.EventLogDetailsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = "dataObjectToEventListConverter")
public class DataObjectToEventListConverter {

  private static final String EVENT_DATA_VALUE_IS_NOT_A_NUMBER =
      "eventData value for %s is not a number";
  private static final String EVENT_CODE = "event code";
  private static final String COUNTER = "counter";
  private static final String MAGNITUDE = "magnitude";
  private static final String DURATION = "duration";

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

    final int numberOfEventElements = eventLogCategory.getDetailsType().getNumberOfEventElements();
    if (eventData.size() != numberOfEventElements) {
      throw new ProtocolAdapterException("eventData size should be " + numberOfEventElements);
    }

    // extract values from List<DataObject> eventData.
    final DateTime dateTime = this.extractDateTime(eventData);
    final Integer code = this.extractCode(eventData);
    final Integer eventCounter = this.extractEventCounter(eventLogCategory, eventData);
    final String eventLogCategoryName = eventLogCategory.name();

    log.info(
        "Event time is {}, event code is {}, event category is {} and event counter is {}",
        dateTime,
        code,
        eventLogCategoryName,
        eventCounter);

    // build a new EventDto with those values.
    final EventDto event = new EventDto(dateTime, code, eventCounter, eventLogCategoryName);

    // add details
    event.addEventDetails(this.extractEventDetails(eventLogCategory, eventData));

    return event;
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

  private Integer extractCode(final List<DataObject> eventData) throws ProtocolAdapterException {

    this.checkIsNumber(eventData.get(1), EVENT_CODE);
    final Number codeValue = eventData.get(1).getValue();
    return codeValue.intValue();
  }

  private Integer extractEventCounter(
      final EventLogCategoryDto eventLogCategory, final List<DataObject> eventData)
      throws ProtocolAdapterException {

    Integer eventCounter = null;

    if (eventLogCategory.getDetailsType() == EventLogDetailsType.COUNTER) {
      this.checkIsNumber(eventData.get(2), COUNTER);
      eventCounter = eventData.get(2).getValue();
    }

    return eventCounter;
  }

  private List<EventDetailDto> extractEventDetails(
      final EventLogCategoryDto eventLogCategory, final List<DataObject> eventData)
      throws ProtocolAdapterException {

    if (eventLogCategory.getDetailsType() == EventLogDetailsType.MAGNITUDE_AND_DURATION) {
      return this.extractMagnitudeAndDuration(eventData);
    }

    return Collections.emptyList();
  }

  private List<EventDetailDto> extractMagnitudeAndDuration(final List<DataObject> eventData)
      throws ProtocolAdapterException {

    final List<EventDetailDto> eventDetails = new ArrayList<>();

    this.checkIsNumber(eventData.get(2), MAGNITUDE);
    eventDetails.add(
        new EventDetailDto(
            MAGNITUDE,
            this.readValueWithScalerAndUnit(eventData.get(2), BigDecimal.valueOf(0.1), "V")));

    this.checkIsNumber(eventData.get(3), DURATION);
    eventDetails.add(
        new EventDetailDto(
            DURATION,
            this.readValueWithScalerAndUnit(eventData.get(3), BigDecimal.valueOf(0.1), "s")));

    return eventDetails;
  }

  private void checkIsNumber(final DataObject data, final String description)
      throws ProtocolAdapterException {
    if (!data.isNumber()) {
      throw new ProtocolAdapterException(
          String.format(EVENT_DATA_VALUE_IS_NOT_A_NUMBER, description));
    }
  }

  private String readValueWithScalerAndUnit(
      final DataObject data, final BigDecimal scaler, final String unit) {
    final int value = data.getValue();
    final BigDecimal scaledValue = scaler.multiply(BigDecimal.valueOf(value));
    return scaledValue + " " + unit;
  }
}
