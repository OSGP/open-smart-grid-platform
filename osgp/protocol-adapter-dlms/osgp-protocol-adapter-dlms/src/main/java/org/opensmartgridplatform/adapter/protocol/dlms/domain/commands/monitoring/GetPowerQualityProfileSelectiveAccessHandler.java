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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;
import org.springframework.stereotype.Component;

@Component
public class GetPowerQualityProfileSelectiveAccessHandler
    extends AbstractGetPowerQualityProfileHandler {

  public GetPowerQualityProfileSelectiveAccessHandler(
      final DlmsHelper dlmsHelper, final ObjectConfigService objectConfigService) {
    super(dlmsHelper, objectConfigService);
  }

  @Override
  protected List<ProfileEntryValueDto> createProfileEntryValueDto(
      final DataObject profileEntryDataObject,
      final ProfileEntryDto previousProfileEntryDto,
      final LinkedHashMap<Integer, SelectableObject> selectedObjects,
      final int timeInterval) {

    final List<ProfileEntryValueDto> result = new ArrayList<>();
    final List<DataObject> dataObjects = profileEntryDataObject.getValue();

    // The values are retrieved using selective access, so we should have the same amount of
    // selectedObjects. Since we use a LinkedHashMap the selectedObjects are in the right order
    // and we can simply iterate over them.
    final var iterator = selectedObjects.entrySet().iterator();

    for (final DataObject dataObject : dataObjects) {
      if (iterator.hasNext()) {
        final ProfileEntryValueDto currentProfileEntryValueDto =
            super.makeProfileEntryValueDto(
                dataObject, iterator.next().getValue(), previousProfileEntryDto, timeInterval);
        result.add(currentProfileEntryValueDto);
      }
    }

    return result;
  }

  @Override
  protected DataObject convertSelectableCaptureObjects(
      final List<SelectableObject> selectableCaptureObjects) {

    final List<DataObject> objectDefinitions = new ArrayList<>();

    if (!selectableCaptureObjects.isEmpty()) {
      for (final SelectableObject selectableCaptureObject : selectableCaptureObjects) {
        final int classId = selectableCaptureObject.getClassId();
        final byte[] obisBytes = selectableCaptureObject.getObisAsBytes();
        final byte attributeIndex = selectableCaptureObject.getAttributeIndex();
        final int dataIndex =
            selectableCaptureObject.getDataIndex() == null
                ? 0
                : selectableCaptureObject.getDataIndex();

        objectDefinitions.add(
            DataObject.newStructureData(
                Arrays.asList(
                    DataObject.newUInteger16Data(classId),
                    DataObject.newOctetStringData(obisBytes),
                    DataObject.newInteger8Data(attributeIndex),
                    DataObject.newUInteger16Data(dataIndex))));
      }
    }
    return DataObject.newArrayData(objectDefinitions);
  }
}
