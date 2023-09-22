// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.OutageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = "dataObjectToOutageListConverter")
public class DataObjectToOutageListConverter {

  private static final String EVENT_DATA_VALUE_IS_NOT_A_NUMBER = "eventData value is not a number";
  private static final int NUMBER_OF_ELEMENTS = 2;
  private final DlmsHelper dlmsHelper;

  @Autowired
  public DataObjectToOutageListConverter(final DlmsHelper dlmsHelper) {
    this.dlmsHelper = dlmsHelper;
  }

  public List<OutageDto> convert(final DataObject source) throws ProtocolAdapterException {
    final List<OutageDto> eventList = new ArrayList<>();
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

    if (outageData.size() != NUMBER_OF_ELEMENTS) {
      throw new ProtocolAdapterException("outageData size should be " + NUMBER_OF_ELEMENTS);
    }

    final ZonedDateTime endTime = this.extractDateTime(outageData);
    final Long duration = this.extractEventDuration(outageData);

    final OutageDto outage = new OutageDto(endTime, duration);

    log.info("Converted dataObject to outage: {}", outage);
    return outage;
  }

  private ZonedDateTime extractDateTime(final List<DataObject> eventData)
      throws ProtocolAdapterException {
    final ZonedDateTime dateTime =
        this.dlmsHelper.convertDataObjectToDateTime(eventData.get(0)).asDateTime();

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
