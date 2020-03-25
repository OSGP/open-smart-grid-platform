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

import org.joda.time.DateTime;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ScalarUnitInfo;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PowerQualityProfileDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GetPowerQualityProfileCommandExecutorSelectiveAccess
        extends AbstractGetPowerQualityProfileCommandExecutor {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(GetPowerQualityProfileCommandExecutorSelectiveAccess.class);

    public GetPowerQualityProfileCommandExecutorSelectiveAccess(final DlmsHelper dlmsHelper) {
        super(dlmsHelper);
    }

    @Override
    public GetPowerQualityProfileResponseDto execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final GetPowerQualityProfileRequestDataDto getPowerQualityProfileRequestDataDto)
            throws ProtocolAdapterException {

        LOGGER.info("----- EXECUTE GetPowerQualityProfileCommandExecutor "
                + "GetPowerQualityProfileCommandExecutorSelectiveAccess ----");

        final String profileType = getPowerQualityProfileRequestDataDto.getProfileType();
        final List<Profile> profiles = determineProfileForDevice(profileType);
        final GetPowerQualityProfileResponseDto response = new GetPowerQualityProfileResponseDto();
        final List<PowerQualityProfileDataDto> responseDatas = new ArrayList<>();

        for (final Profile profile : profiles) {

            final ObisCode obisCode = makeObisCode(profile.getObisCodeValuesDto());
            final DateTime beginDateTime = new DateTime(getPowerQualityProfileRequestDataDto.getBeginDate());
            final DateTime endDateTime = new DateTime(getPowerQualityProfileRequestDataDto.getEndDate());

            // all value types that can be selected within this profile.
            final List<GetResult> captureObjects = retrieveCaptureObjects(conn, device, obisCode);

            // the units of measure for all capture objects
            final List<ScalarUnitInfo> scalarUnitInfos = createScalarUnitInfos(conn, device, captureObjects);

            // the values that are allowed to be retrieved from the meter, used as filter either before (SMR 5.1+) or
            // after data retrieval
            Map<Integer, CaptureObjectDefinitionDto> selectableCaptureObjects = this
                    .createSelectableCaptureObjects(captureObjects, profile.getLogicalNames());

            for (Integer i : selectableCaptureObjects.keySet()) {
                LOGGER.info("PQ -- Profile {}  has selectable object {} at position {}", profile.name(),
                        selectableCaptureObjects.get(i).getLogicalName(), i);
            }

            final List<GetResult> bufferList = retrieveBuffer(conn, device, obisCode, beginDateTime, endDateTime,
                    new ArrayList<>(selectableCaptureObjects.values()));

            final PowerQualityProfileDataDto responseDataDto = processData(profile, captureObjects, scalarUnitInfos,
                    selectableCaptureObjects, bufferList);

            responseDatas.add(responseDataDto);
        }

        response.setPowerQualityProfileDatas(responseDatas);

        return response;
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
