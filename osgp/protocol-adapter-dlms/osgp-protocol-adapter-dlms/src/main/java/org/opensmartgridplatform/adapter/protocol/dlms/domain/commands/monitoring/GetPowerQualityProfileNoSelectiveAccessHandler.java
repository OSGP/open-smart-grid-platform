/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.monitoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;
import org.springframework.stereotype.Component;

@Component
public class GetPowerQualityProfileNoSelectiveAccessHandler
    extends AbstractGetPowerQualityProfileHandler {

  public GetPowerQualityProfileNoSelectiveAccessHandler(
      final DlmsHelper dlmsHelper, final ObjectConfigService objectConfigService) {
    super(dlmsHelper, objectConfigService);
  }

  @Override
  protected List<ProfileEntryValueDto> createProfileEntryValueDto(
      final DataObject profileEntryDataObject,
      final ProfileEntryDto previousProfileEntryDto,
      final Map<Integer, SelectableObject> selectableCaptureObjects,
      final int timeInterval) {

    final List<ProfileEntryValueDto> result = new ArrayList<>();
    final List<DataObject> dataObjects = profileEntryDataObject.getValue();

    for (int i = 0; i < dataObjects.size(); i++) {

      if (selectableCaptureObjects.containsKey(i)) {
        final ProfileEntryValueDto currentProfileEntryValueDto =
            this.makeProfileEntryValueDto(
                dataObjects.get(i),
                selectableCaptureObjects.get(result.size()),
                previousProfileEntryDto,
                timeInterval);
        result.add(currentProfileEntryValueDto);
      }
    }

    return result;
  }

  @Override
  protected DataObject convertSelectableCaptureObjects(
      final List<SelectableObject> selectableCaptureObjects) {
    return DataObject.newArrayData(new ArrayList<>());
  }
}
