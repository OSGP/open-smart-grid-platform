/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventLogCategoryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetOutagesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.OutageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = "dataObjectToOutageListConverter")
public class DataObjectToOutageListConverter {

    public static final String EVENT_DATA_VALUE_IS_NOT_A_NUMBER = "eventData value is not a number";
    private final DlmsHelper dlmsHelper;

    @Autowired
    public DataObjectToOutageListConverter(final DlmsHelper dlmsHelper) {
        this.dlmsHelper = dlmsHelper;
    }

    public List<OutageDto> convert(final DataObject source, final GetOutagesRequestDto getOutagesRequestDto)
            throws ProtocolAdapterException {
        final List<OutageDto> eventList = new ArrayList<OutageDto>();
        if (source == null) {
            throw new ProtocolAdapterException("DataObject should not be null");
        }

        final List<DataObject> dataObjects = source.getValue();
        for (final DataObject dataObject : dataObjects) {
            eventList.add(this.getOutageDto(dataObject));
        }

        return eventList;

    }

    private OutageDto getOutageDto(final DataObject outageDataObject)
            throws ProtocolAdapterException {

        final List<DataObject> outageData = outageDataObject.getValue();

        if (outageData == null) {
            throw new ProtocolAdapterException("outageData DataObject should not be null");
        }

        if (outageData.size() != EventLogCategoryDto.POWER_FAILURE_EVENT_LOG.getNumberOfEventElements()) {
            throw new ProtocolAdapterException(
                    "outageData size should be " + EventLogCategoryDto.POWER_FAILURE_EVENT_LOG.getNumberOfEventElements());
        }

        final DateTime endTime = this.extractDateTime(outageData);
        final Long duration = this.extractEventDuration(outageData);
        final String eventLogCategoryName = EventLogCategoryDto.POWER_FAILURE_EVENT_LOG.name();

        OutageDto outage = new OutageDto(endTime, 1, eventLogCategoryName, duration);

        log.info("Converted dataObject to outage: {}", outage);
        return outage;
    }

    private DateTime extractDateTime(final List<DataObject> eventData) throws ProtocolAdapterException {
        final DateTime dateTime = this.dlmsHelper.convertDataObjectToDateTime(eventData.get(0)).asDateTime();

        if (dateTime == null) {
            throw new ProtocolAdapterException("eventData time is null/unspecified");
        }

        return dateTime;
    }

    private Long extractEventDuration(final List<DataObject> eventData)
            throws ProtocolAdapterException {
        if (!eventData.get(1).isNumber()) {
            throw new ProtocolAdapterException(EVENT_DATA_VALUE_IS_NOT_A_NUMBER);
        }

        return eventData.get(1).getValue();
    }

}
