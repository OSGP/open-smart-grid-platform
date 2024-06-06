// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperty;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
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

  private final ObjectConfigService objectConfigService;

  @Autowired
  public DataObjectToEventListConverter(
      final DlmsHelper dlmsHelper, final ObjectConfigService objectConfigService) {
    this.dlmsHelper = dlmsHelper;
    this.objectConfigService = objectConfigService;
  }

  public List<EventDto> convert(
      final DataObject source, final EventLogCategoryDto eventLogCategory, final Protocol protocol)
      throws ProtocolAdapterException {
    final List<EventDto> eventList = new ArrayList<>();
    if (source == null) {
      throw new ProtocolAdapterException("DataObject should not be null");
    }

    final List<DataObject> listOfEvents = source.getValue();
    for (final DataObject eventDataObject : listOfEvents) {
      eventList.add(this.getEvent(eventDataObject, eventLogCategory, protocol));
    }

    return eventList;
  }

  private EventDto getEvent(
      final DataObject eventDataObject,
      final EventLogCategoryDto eventLogCategory,
      final Protocol protocol)
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
    final Integer eventCode = this.extractCode(eventData);
    final Integer eventCounter = this.extractEventCounter(eventLogCategory, eventData);
    final String eventLogCategoryName = eventLogCategory.name();

    log.info(
        "Event time is {}, event code is {}, event category is {} and event counter is {}",
        dateTime,
        eventCode,
        eventLogCategoryName,
        eventCounter);

    final EventDto event = new EventDto(dateTime, eventCode, eventCounter, eventLogCategoryName);

    event.addEventDetails(
        this.extractEventDetails(eventLogCategory, eventData, eventCode, protocol));

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
      final EventLogCategoryDto eventLogCategory,
      final List<DataObject> eventData,
      final Integer eventCode,
      final Protocol protocol)
      throws ProtocolAdapterException {

    if (eventLogCategory.getDetailsType() == EventLogDetailsType.MAGNITUDE) {
      return this.extractMagnitudeThd(eventData, eventCode, protocol);
    }
    if (eventLogCategory.getDetailsType() == EventLogDetailsType.MAGNITUDE_AND_DURATION) {
      return this.extractMagnitudeAndDuration(eventData, eventCode, protocol);
    }

    return Collections.emptyList();
  }

  private List<EventDetailDto> extractMagnitudeThd(
      final List<DataObject> eventData, final Integer eventCode, final Protocol protocol)
      throws ProtocolAdapterException {

    final List<EventDetailDto> eventDetails = new ArrayList<>();

    final String scalerUnitMagnitude =
        this.getScalerUnit(eventCode, protocol, DlmsObjectType.POWER_QUALITY_THD_EVENT_MAGNITUDE);

    this.checkIsNumber(eventData.get(2), MAGNITUDE);
    eventDetails.add(
        new EventDetailDto(
            MAGNITUDE, this.readValueWithScalerAndUnit(eventData.get(2), scalerUnitMagnitude)));
    return eventDetails;
  }

  private List<EventDetailDto> extractMagnitudeAndDuration(
      final List<DataObject> eventData, final Integer eventCode, final Protocol protocol)
      throws ProtocolAdapterException {

    final List<EventDetailDto> eventDetails = new ArrayList<>();

    final String scalerUnitMagnitude =
        this.getScalerUnit(
            eventCode, protocol, DlmsObjectType.POWER_QUALITY_EXTENDED_EVENT_MAGNITUDE);

    this.checkIsNumber(eventData.get(2), MAGNITUDE);
    eventDetails.add(
        new EventDetailDto(
            MAGNITUDE, this.readValueWithScalerAndUnit(eventData.get(2), scalerUnitMagnitude)));

    final String scalerUnitDuration =
        this.getScalerUnit(
            eventCode, protocol, DlmsObjectType.POWER_QUALITY_EXTENDED_EVENT_DURATION);

    this.checkIsNumber(eventData.get(3), DURATION);
    eventDetails.add(
        new EventDetailDto(
            DURATION, this.readValueWithScalerAndUnit(eventData.get(3), scalerUnitDuration)));

    return eventDetails;
  }

  private String getScalerUnit(
      final Integer eventCode, final Protocol protocol, final DlmsObjectType dlmsObjectType)
      throws ProtocolAdapterException {
    final CosemObject cosemSourceObject =
        this.getEventRelatedCosemSourceObject(dlmsObjectType, eventCode, protocol);
    if (cosemSourceObject.getClassId() != InterfaceClass.REGISTER.id()) {
      throw new ProtocolAdapterException(
          String.format(
              "SourceObject mapped on event %s with dlmstype %s is not of class REGISTER",
              eventCode, dlmsObjectType));
    }
    final Attribute scalerUnit =
        cosemSourceObject.getAttribute(RegisterAttribute.SCALER_UNIT.attributeId());
    return scalerUnit.getValue();
  }

  private CosemObject getEventRelatedCosemSourceObject(
      final DlmsObjectType dlmsObjectType, final Integer eventCode, final Protocol protocol)
      throws ProtocolAdapterException {

    final CosemObject cosemObject;
    try {
      cosemObject =
          this.objectConfigService.getCosemObject(
              protocol.getName(), protocol.getVersion(), dlmsObjectType);
    } catch (final ObjectConfigException | IllegalArgumentException e) {
      throw new ProtocolAdapterException(
          String.format("No CosemObject found for dlmstype %s", dlmsObjectType), e);
    }

    final Map<String, String> eventMapping =
        (Map<String, String>) cosemObject.getProperties().get(ObjectProperty.SOURCE_OBJECTS);
    if (eventMapping == null || eventMapping.isEmpty()) {
      throw new ProtocolAdapterException(
          String.format(
              "No SOURCE_OBJECT property available for dlms objecttype %s", dlmsObjectType));
    }

    final String sourceObjectName = eventMapping.get(String.valueOf(eventCode));
    if (sourceObjectName == null) {
      throw new ProtocolAdapterException(
          String.format(
              "No sourceObject mapped for event %s on dlmsobject %s", eventCode, dlmsObjectType));
    }

    try {
      final DlmsObjectType dlmsSourceObjectType = DlmsObjectType.valueOf(sourceObjectName);
      return this.objectConfigService.getCosemObject(
          protocol.getName(), protocol.getVersion(), dlmsSourceObjectType);
    } catch (final ObjectConfigException e) {
      final String message =
          String.format(
              "No SourceObject found for dlmstype %s and event %s by name %s",
              dlmsObjectType, eventCode, sourceObjectName);
      throw new ProtocolAdapterException(message, e);
    }
  }

  private void checkIsNumber(final DataObject data, final String description)
      throws ProtocolAdapterException {
    if (!data.isNumber()) {
      throw new ProtocolAdapterException(
          String.format(EVENT_DATA_VALUE_IS_NOT_A_NUMBER, description));
    }
  }

  private String readValueWithScalerAndUnit(final DataObject data, final String scalerUnit) {
    final Long scaler = Long.valueOf(scalerUnit.split(",")[0].trim());
    final BigDecimal multiplier = BigDecimal.valueOf(Math.pow(10, scaler));
    final String unit = scalerUnit.split(",")[1].trim();
    final int value = data.getValue();
    final BigDecimal scaledValue = multiplier.multiply(BigDecimal.valueOf(value));
    return scaledValue.setScale(0 - scaler.intValue()) + " " + unit;
  }
}
