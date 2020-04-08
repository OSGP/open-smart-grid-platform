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
import java.util.List;
import java.util.Map;

import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ScalerUnitInfo;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GetPowerQualityProfileNoSelectiveAccessHandler extends AbstractGetPowerQualityProfileHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPowerQualityProfileNoSelectiveAccessHandler.class);

    public GetPowerQualityProfileNoSelectiveAccessHandler(final DlmsHelper dlmsHelper) {
        super(dlmsHelper);
    }

    @Override
    protected List<ProfileEntryValueDto> createProfileEntryValueDto(final DataObject profileEntryDataObject,
            final List<ScalerUnitInfo> scalerUnitInfos, ProfileEntryDto previousProfileEntryDto,
            final Map<Integer, CaptureObjectDefinitionDto> selectableCaptureObjects, int timeInterval) {

        final List<ProfileEntryValueDto> result = new ArrayList<>();
        final List<DataObject> dataObjects = profileEntryDataObject.getValue();

        for (int i = 0; i < dataObjects.size(); i++) {

            if (selectableCaptureObjects.containsKey(i)) {
                ProfileEntryValueDto currentProfileEntryValueDto = this
                        .makeProfileEntryValueDto(dataObjects.get(i), scalerUnitInfos.get(i), previousProfileEntryDto,
                                timeInterval);
                result.add(currentProfileEntryValueDto);
            } else {
                LOGGER.info("PQ -- we are skipping element at position {} ", i);
            }
        }

        return result;
    }

    @Override
    protected DataObject convertSelectableCaptureObjects(List<CaptureObjectDefinitionDto> selectableCaptureObjects) {
        return DataObject.newArrayData(new ArrayList<>());
    }

}
