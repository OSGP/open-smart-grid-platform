/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsDateTimeConverter.toDateTime;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import org.joda.time.DateTime;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.DataObjectToEventListConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventLogCategoryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventMessageDataResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FindEventsRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class FindEventsCommandExecutor
    extends AbstractCommandExecutor<FindEventsRequestDto, List<EventDto>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FindEventsCommandExecutor.class);

  private static final int ACCESS_SELECTOR_RANGE_DESCRIPTOR = 1;

  private static final EnumMap<EventLogCategoryDto, DlmsObjectType>
      EVENT_LOG_CATEGORY_OBISCODE_MAP = new EnumMap<>(EventLogCategoryDto.class);

  static {
    EVENT_LOG_CATEGORY_OBISCODE_MAP.put(
        EventLogCategoryDto.STANDARD_EVENT_LOG, DlmsObjectType.STANDARD_EVENT_LOG);
    EVENT_LOG_CATEGORY_OBISCODE_MAP.put(
        EventLogCategoryDto.FRAUD_DETECTION_LOG, DlmsObjectType.FRAUD_DETECTION_EVENT_LOG);
    EVENT_LOG_CATEGORY_OBISCODE_MAP.put(
        EventLogCategoryDto.COMMUNICATION_SESSION_LOG,
        DlmsObjectType.COMMUNICATION_SESSIONS_EVENT_LOG);
    EVENT_LOG_CATEGORY_OBISCODE_MAP.put(
        EventLogCategoryDto.M_BUS_EVENT_LOG, DlmsObjectType.MBUS_EVENT_LOG);
    EVENT_LOG_CATEGORY_OBISCODE_MAP.put(
        EventLogCategoryDto.POWER_QUALITY_EVENT_LOG, DlmsObjectType.POWER_QUALITY_EVENT_LOG);
    EVENT_LOG_CATEGORY_OBISCODE_MAP.put(
        EventLogCategoryDto.AUXILIARY_EVENT_LOG, DlmsObjectType.AUXILIARY_EVENT_LOG);
    EVENT_LOG_CATEGORY_OBISCODE_MAP.put(
        EventLogCategoryDto.POWER_QUALITY_EXTENDED_EVENT_LOG,
        DlmsObjectType.POWER_QUALITY_EXTENDED_EVENT_LOG);
  }

  private final DataObjectToEventListConverter dataObjectToEventListConverter;

  private final DlmsHelper dlmsHelper;

  private final DlmsObjectConfigService dlmsObjectConfigService;

  @Autowired
  public FindEventsCommandExecutor(
      final DlmsHelper dlmsHelper,
      final DataObjectToEventListConverter dataObjectToEventListConverter,
      final DlmsObjectConfigService dlmsObjectConfigService) {
    super(FindEventsRequestDto.class);
    this.dlmsHelper = dlmsHelper;
    this.dataObjectToEventListConverter = dataObjectToEventListConverter;
    this.dlmsObjectConfigService = dlmsObjectConfigService;
  }

  @Override
  public ActionResponseDto asBundleResponse(final List<EventDto> executionResult)
      throws ProtocolAdapterException {
    return new EventMessageDataResponseDto(executionResult);
  }

  @Override
  public List<EventDto> execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final FindEventsRequestDto findEventsQuery,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final SelectiveAccessDescription selectiveAccessDescription =
        this.getSelectiveAccessDescription(
            device, findEventsQuery.getFrom(), findEventsQuery.getUntil());

    final DlmsObject eventLogObject =
        this.dlmsObjectConfigService.getDlmsObject(
            device, EVENT_LOG_CATEGORY_OBISCODE_MAP.get(findEventsQuery.getEventLogCategory()));

    final AttributeAddress eventLogBuffer =
        new AttributeAddress(
            eventLogObject.getClassId(),
            eventLogObject.getObisCode(),
            eventLogObject.getDefaultAttributeId(),
            selectiveAccessDescription);

    conn.getDlmsMessageListener()
        .setDescription(
            "RetrieveEvents for "
                + findEventsQuery.getEventLogCategory()
                + " from "
                + findEventsQuery.getFrom()
                + " until "
                + findEventsQuery.getUntil()
                + ", retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(eventLogBuffer));

    final GetResult getResult;
    try {
      getResult = conn.getConnection().get(eventLogBuffer);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }

    if (getResult == null) {
      throw new ProtocolAdapterException(
          "No GetResult received while retrieving event register "
              + findEventsQuery.getEventLogCategory());
    }

    if (!AccessResultCode.SUCCESS.equals(getResult.getResultCode())) {
      LOGGER.info(
          "Result of getting events for {} is {}",
          findEventsQuery.getEventLogCategory(),
          getResult.getResultCode());
      throw new ProtocolAdapterException(
          "Getting the events for  "
              + findEventsQuery.getEventLogCategory()
              + " from the meter resulted in: "
              + getResult.getResultCode());
    }

    final DataObject resultData = getResult.getResultData();
    return this.dataObjectToEventListConverter.convert(
        resultData, findEventsQuery.getEventLogCategory());
  }

  private SelectiveAccessDescription getSelectiveAccessDescription(
      final DlmsDevice device, final DateTime beginDateTime, final DateTime endDateTime)
      throws ProtocolAdapterException {

    /*
     * Define the clock object {8,0-0:1.0.0.255,2,0} to be used as
     * restricting object in a range descriptor with a from value and to
     * value to determine which elements from the buffered array should be
     * retrieved.
     */

    final DateTime convertedBeginDateTime = toDateTime(beginDateTime, device);
    final DateTime convertedEndDateTime = toDateTime(endDateTime, device);

    final DlmsObject clockObject =
        this.dlmsObjectConfigService.getDlmsObject(device, DlmsObjectType.CLOCK);

    final DataObject clockDefinition =
        DataObject.newStructureData(
            Arrays.asList(
                DataObject.newUInteger16Data(clockObject.getClassId()),
                DataObject.newOctetStringData(clockObject.getObisCode().bytes()),
                DataObject.newInteger8Data((byte) clockObject.getDefaultAttributeId()),
                DataObject.newUInteger16Data(0)));

    final DataObject fromValue = this.dlmsHelper.asDataObject(convertedBeginDateTime);
    final DataObject toValue = this.dlmsHelper.asDataObject(convertedEndDateTime);

    /*
     * Retrieve all captured objects by setting selectedValues to an empty
     * array.
     */
    final DataObject selectedValues = DataObject.newArrayData(Collections.emptyList());

    final DataObject accessParameter =
        DataObject.newStructureData(
            Arrays.asList(clockDefinition, fromValue, toValue, selectedValues));

    return new SelectiveAccessDescription(ACCESS_SELECTOR_RANGE_DESCRIPTOR, accessParameter);
  }
}
