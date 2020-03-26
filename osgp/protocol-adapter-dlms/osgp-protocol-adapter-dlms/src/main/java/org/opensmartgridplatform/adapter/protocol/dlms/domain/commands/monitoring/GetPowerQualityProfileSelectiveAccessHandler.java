/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.monitoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ScalarUnitInfo;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GetPowerQualityProfileSelectiveAccessHandler
        extends AbstractGetPowerQualityProfileHandler {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(GetPowerQualityProfileSelectiveAccessHandler.class);

    public GetPowerQualityProfileSelectiveAccessHandler(final DlmsHelper dlmsHelper) {
        super(dlmsHelper);
    }

    @Override
    protected List<ProfileEntryValueDto> createProfileEntryValueDto(final DataObject profileEntryDataObject,
            final List<ScalarUnitInfo> scalarUnitInfos, ProfileEntryDto previousProfileEntryDto,
            final Map<Integer, CaptureObjectDefinitionDto> selectableCaptureObjects, int timeInterval) {

        final List<ProfileEntryValueDto> result = new ArrayList<>();
        final List<DataObject> dataObjects = profileEntryDataObject.getValue();

        for (int i = 0; i < dataObjects.size(); i++) {

            ProfileEntryValueDto currentProfileEntryValueDto = super
                    .makeProfileEntryValueDto(dataObjects.get(i), scalarUnitInfos.get(i), previousProfileEntryDto,
                            timeInterval);
            result.add(currentProfileEntryValueDto);
        }

        LOGGER.info("PQ -- ProfileEntryValueDto is of size {} ", result.size());
        return result;
    }

    @Override
    protected DataObject convertSelectableCaptureObjects(List<CaptureObjectDefinitionDto> selectableCaptureObjects) {

        final List<DataObject> objectDefinitions = new ArrayList<>();

        if (!selectableCaptureObjects.isEmpty()) {
            // The captured clock is always included.
            objectDefinitions.add(dlmsHelper.getClockDefinition());
            for (final CaptureObjectDefinitionDto captureObjectDefinition : selectableCaptureObjects) {
                final int classId = captureObjectDefinition.getClassId();
                final byte[] obisBytes = captureObjectDefinition.getLogicalName().toByteArray();
                final byte attributeIndex = captureObjectDefinition.getAttributeIndex();
                final int dataIndex =
                        captureObjectDefinition.getDataIndex() == null ? 0 : captureObjectDefinition.getDataIndex();

                objectDefinitions.add(DataObject.newStructureData(
                        Arrays.asList(DataObject.newUInteger16Data(classId), DataObject.newOctetStringData(obisBytes),
                                DataObject.newInteger8Data(attributeIndex), DataObject.newUInteger16Data(dataIndex))));
            }
        }
        return DataObject.newArrayData(objectDefinitions);
    }

}
