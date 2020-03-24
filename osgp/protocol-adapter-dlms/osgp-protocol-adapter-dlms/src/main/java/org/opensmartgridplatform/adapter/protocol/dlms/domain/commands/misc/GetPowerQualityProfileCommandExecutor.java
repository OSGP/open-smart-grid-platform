/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.openmuc.jdlms.interfaceclass.attribute.ClockAttribute;
import org.openmuc.jdlms.interfaceclass.attribute.DemandRegisterAttribute;
import org.openmuc.jdlms.interfaceclass.attribute.ExtendedRegisterAttribute;
import org.openmuc.jdlms.interfaceclass.attribute.ProfileGenericAttribute;
import org.openmuc.jdlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ScalarUnitInfo;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PowerQualityProfileDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class GetPowerQualityProfileCommandExecutor
        extends AbstractCommandExecutor<GetPowerQualityProfileRequestDataDto, GetPowerQualityProfileResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPowerQualityProfileCommandExecutor.class);

    private static final String CAPTURE_OBJECT = "capture-object";

    private static final int ACCESS_SELECTOR_RANGE_DESCRIPTOR = 1;

    static final ObisCodeValuesDto OBIS_CODE_DEFINABLE_LOAD_PROFILE = new ObisCodeValuesDto((byte) 0, (byte) 1,
            (byte) 94, (byte) 31, (byte) 6, (byte) 255);
    static final ObisCodeValuesDto OBIS_CODE_PROFILE_1 = new ObisCodeValuesDto((byte) 1, (byte) 0, (byte) 99, (byte) 1,
            (byte) 1, (byte) 255);
    static final ObisCodeValuesDto OBIS_CODE_PROFILE_2 = new ObisCodeValuesDto((byte) 1, (byte) 0, (byte) 99, (byte) 1,
            (byte) 2, (byte) 255);

    private static final int INTERVAL_DEFINABLE_LOAD_PROFILE = 15;
    private static final int INTERVAL_PROFILE_1 = 15;
    private static final int INTERVAL_PROFILE_2 = 10;
    private static final String PUBLIC = "PUBLIC";
    private static final String PRIVATE = "PRIVATE";

    enum Profile {

        DEFINABLE_LOAD_PROFILE_PUBLIC(OBIS_CODE_DEFINABLE_LOAD_PROFILE, INTERVAL_DEFINABLE_LOAD_PROFILE,
                getLogicalNamesPublicDefinableLoadProfile()),
        PROFILE_1_PRIVATE(OBIS_CODE_PROFILE_1, INTERVAL_PROFILE_1, getLogicalNamesPrivateProfile1()),
        PROFILE_2_PUBLIC(OBIS_CODE_PROFILE_2, INTERVAL_PROFILE_2, getLogicalNamesPublicProfile2()),
        PROFILE_2_PRIVATE(OBIS_CODE_PROFILE_2, INTERVAL_PROFILE_2, getLogicalNamesPrivateProfile2());

        private final ObisCodeValuesDto obisCodeValuesDto;
        private final int interval;
        private final List<String> logicalNames;

        Profile(ObisCodeValuesDto obisCodeValuesDto, int interval, List<String> logicalNames) {
            this.obisCodeValuesDto = obisCodeValuesDto;
            this.interval = interval;
            this.logicalNames = logicalNames;
        }

        public ObisCodeValuesDto getObisCodeValuesDto() {
            return obisCodeValuesDto;
        }

        public int getInterval() {
            return interval;
        }

        public List<String> getLogicalNames() {
            return logicalNames;
        }
    }

    private static final byte[] OBIS_BYTES_CLOCK = new byte[] { 0, 0, 1, 0, 0, (byte) 255 };

    private static final Map<Integer, Integer> SCALAR_UNITS_MAP = new HashMap<>();

    static {
        SCALAR_UNITS_MAP.put(InterfaceClass.REGISTER.id(), RegisterAttribute.SCALER_UNIT.attributeId());
        SCALAR_UNITS_MAP
                .put(InterfaceClass.EXTENDED_REGISTER.id(), ExtendedRegisterAttribute.SCALER_UNIT.attributeId());
        SCALAR_UNITS_MAP.put(InterfaceClass.DEMAND_REGISTER.id(), DemandRegisterAttribute.SCALER_UNIT.attributeId());
    }

    private final DlmsHelper dlmsHelper;

    public GetPowerQualityProfileCommandExecutor(final DlmsHelper dlmsHelper) {
        super(GetPowerQualityProfileRequestDataDto.class);

        this.dlmsHelper = dlmsHelper;
    }

    @Override
    public GetPowerQualityProfileResponseDto execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final GetPowerQualityProfileRequestDataDto getPowerQualityProfileRequestDataDto)
            throws ProtocolAdapterException {

        final String profileType = getPowerQualityProfileRequestDataDto.getProfileType();
        final List<Profile> profiles = determineProfileForDevice(profileType);
        final GetPowerQualityProfileResponseDto response = new GetPowerQualityProfileResponseDto();
        final List<PowerQualityProfileDataDto> responseDatas = new ArrayList<>();

        for (final Profile profile : profiles) {

            final ObisCode obisCode = this.makeObisCode(profile.getObisCodeValuesDto());
            final DateTime beginDateTime = new DateTime(getPowerQualityProfileRequestDataDto.getBeginDate());
            final DateTime endDateTime = new DateTime(getPowerQualityProfileRequestDataDto.getEndDate());

            // all value types that can be selected within this profile.
            final List<GetResult> captureObjects = this.retrieveCaptureObjects(conn, device, obisCode);

            // the units of measure for all capture objects
            final List<ScalarUnitInfo> scalarUnitInfos = this.createScalarUnitInfos(conn, device, captureObjects);

            // the values that are allowed to be retrieved from the meter, used as filter either before (SMR 5.1+) or
            // after data retrieval
            Map<Integer, CaptureObjectDefinitionDto> selectableCaptureObjects = this
                    .createSelectableCaptureObjects(captureObjects, profile.getLogicalNames());

            final List<GetResult> bufferList = this.retrieveBuffer(conn, device, obisCode, beginDateTime, endDateTime,
                    new ArrayList<>(selectableCaptureObjects.values()));

            final PowerQualityProfileDataDto responseDataDto = this
                    .processData(profile, captureObjects, scalarUnitInfos, selectableCaptureObjects, bufferList);

            responseDatas.add(responseDataDto);
        }

        response.setPowerQualityProfileDatas(responseDatas);

        return response;
    }

    private List<Profile> determineProfileForDevice(final String profileType) {

        switch (profileType) {
        case PUBLIC:
            return Arrays.asList(Profile.DEFINABLE_LOAD_PROFILE_PUBLIC, Profile.PROFILE_2_PUBLIC);
        case PRIVATE:
            return Arrays.asList(Profile.PROFILE_1_PRIVATE, Profile.PROFILE_2_PRIVATE);
        default:
            throw new IllegalArgumentException(
                    "GetPowerQualityProfile: an unknown profileType was requested: " + profileType);
        }
    }

    private List<GetResult> retrieveCaptureObjects(final DlmsConnectionManager conn, final DlmsDevice device,
            final ObisCode obisCode) throws ProtocolAdapterException {
        final AttributeAddress captureObjectsAttributeAddress = new AttributeAddress(
                InterfaceClass.PROFILE_GENERIC.id(), obisCode, ProfileGenericAttribute.CAPTURE_OBJECTS.attributeId());

        return this.dlmsHelper
                .getAndCheck(conn, device, "retrieve profile generic capture objects", captureObjectsAttributeAddress);
    }

    private List<GetResult> retrieveBuffer(final DlmsConnectionManager conn, final DlmsDevice device,
            final ObisCode obisCode, final DateTime beginDateTime, final DateTime endDateTime,
            final List<CaptureObjectDefinitionDto> selectableCaptureObjects) throws ProtocolAdapterException {

        final SelectiveAccessDescription selectiveAccessDescription = this
                .getSelectiveAccessDescription(beginDateTime, endDateTime, selectableCaptureObjects,
                        device.isSelectiveAccessSupported());
        final AttributeAddress bufferAttributeAddress = new AttributeAddress(InterfaceClass.PROFILE_GENERIC.id(),
                obisCode, ProfileGenericAttribute.BUFFER.attributeId(), selectiveAccessDescription);

        return this.dlmsHelper.getAndCheck(conn, device, "retrieve profile generic buffer", bufferAttributeAddress);
    }

    private PowerQualityProfileDataDto processData(final Profile profile, final List<GetResult> captureObjects,
            final List<ScalarUnitInfo> scalarUnitInfos,
            final Map<Integer, CaptureObjectDefinitionDto> selectableCaptureObjects, final List<GetResult> bufferList)
            throws ProtocolAdapterException {

        final List<CaptureObjectDto> captureObjectDtos = this.createCaptureObjects(captureObjects, scalarUnitInfos,
                new ArrayList<>(selectableCaptureObjects.values()));
        final List<ProfileEntryDto> profileEntryDtos = this
                .createProfileEntries(bufferList, scalarUnitInfos, selectableCaptureObjects, profile.getInterval());
        return new PowerQualityProfileDataDto(profile.getObisCodeValuesDto(), captureObjectDtos, profileEntryDtos);
    }

    private List<ProfileEntryDto> createProfileEntries(final List<GetResult> bufferList,
            final List<ScalarUnitInfo> scalarUnitInfos,
            final Map<Integer, CaptureObjectDefinitionDto> selectableCaptureObjects, int timeInterval) {

        final List<ProfileEntryDto> profileEntryDtos = new ArrayList<>();

        // there is always only one GetResult, which is an array of array data
        for (final GetResult buffer : bufferList) {
            final DataObject dataObject = buffer.getResultData();

            final List<DataObject> dataObjectValue = dataObject.getValue();
            ProfileEntryDto previousProfileEntryDto = null;

            for (final DataObject profileEntryDataObject : dataObjectValue) {

                ProfileEntryDto profileEntryDto = new ProfileEntryDto(
                        this.makeProfileEntryValueDto(profileEntryDataObject, scalarUnitInfos, previousProfileEntryDto,
                                selectableCaptureObjects, timeInterval));

                profileEntryDtos.add(profileEntryDto);

                previousProfileEntryDto = profileEntryDto;
            }
        }
        return profileEntryDtos;
    }

    private List<CaptureObjectDto> createCaptureObjects(final List<GetResult> captureObjects,
            final List<ScalarUnitInfo> scalarUnitInfos, final List<CaptureObjectDefinitionDto> selectedValues)
            throws ProtocolAdapterException {

        final List<CaptureObjectDto> captureObjectDtos = new ArrayList<>();
        for (final GetResult captureObjectResult : captureObjects) {
            final DataObject dataObject = captureObjectResult.getResultData();
            final List<DataObject> captureObjectList = dataObject.getValue();
            for (int i = 0; i < captureObjectList.size(); i++) {
                final boolean addCaptureObject = this.isSelectedValue(selectedValues, captureObjectList.get(i));
                if (addCaptureObject) {
                    captureObjectDtos.add(this.makeCaptureObjectDto(captureObjectList.get(i), scalarUnitInfos.get(i)));
                }
            }
        }
        return captureObjectDtos;
    }

    private boolean isSelectedValue(final List<CaptureObjectDefinitionDto> selectedValues, final DataObject dataObject)
            throws ProtocolAdapterException {

        final CosemObjectDefinitionDto cosemObjectDefinitionDto = this.dlmsHelper
                .readObjectDefinition(dataObject, CAPTURE_OBJECT);

        if (this.isClockDefinition(cosemObjectDefinitionDto)) {
            // The captured clock is always included.
            return true;
        }

        for (final CaptureObjectDefinitionDto captureObjectDefinition : selectedValues) {
            if (this.isDefinitionOfSameObject(cosemObjectDefinitionDto, captureObjectDefinition)) {
                return true;
            }
        }
        return false;
    }

    private boolean isClockDefinition(final CosemObjectDefinitionDto cosemObjectDefinitionDto) {

        final int classId = cosemObjectDefinitionDto.getClassId();
        final byte[] obisBytes = cosemObjectDefinitionDto.getLogicalName().toByteArray();
        final byte attributeIndex = (byte) cosemObjectDefinitionDto.getAttributeIndex();
        final int dataIndex = cosemObjectDefinitionDto.getDataIndex();

        return InterfaceClass.CLOCK.id() == classId && Arrays.equals(OBIS_BYTES_CLOCK, obisBytes)
                && ClockAttribute.TIME.attributeId() == attributeIndex && 0 == dataIndex;
    }

    private boolean isDefinitionOfSameObject(final CosemObjectDefinitionDto cosemObjectDefinitionDto,
            final CaptureObjectDefinitionDto captureObjectDefinition) {

        final int classIdCosemObjectDefinition = cosemObjectDefinitionDto.getClassId();
        final byte[] obisBytesCosemObjectDefinition = cosemObjectDefinitionDto.getLogicalName().toByteArray();
        final byte attributeIndexCosemObjectDefinition = (byte) cosemObjectDefinitionDto.getAttributeIndex();
        final int dataIndexCosemObjectDefinition = cosemObjectDefinitionDto.getDataIndex();

        final int classIdCaptureObjectDefinition = captureObjectDefinition.getClassId();
        final byte[] obisBytesCaptureObjectDefinition = captureObjectDefinition.getLogicalName().toByteArray();
        final byte attributeIndexCaptureObjectDefinition = captureObjectDefinition.getAttributeIndex();
        final int dataIndexCaptureObjectDefinition;
        if (captureObjectDefinition.getDataIndex() == null) {
            dataIndexCaptureObjectDefinition = 0;
        } else {
            dataIndexCaptureObjectDefinition = captureObjectDefinition.getDataIndex();
        }

        return classIdCaptureObjectDefinition == classIdCosemObjectDefinition && Arrays
                .equals(obisBytesCaptureObjectDefinition, obisBytesCosemObjectDefinition)
                && attributeIndexCaptureObjectDefinition == attributeIndexCosemObjectDefinition
                && dataIndexCaptureObjectDefinition == dataIndexCosemObjectDefinition;
    }

    private SelectiveAccessDescription getSelectiveAccessDescription(final DateTime beginDateTime,
            final DateTime endDateTime, final List<CaptureObjectDefinitionDto> captureObjectDefinitions,
            final boolean isSelectingValuesSupported) {

        /*
         * Define the clock object {8,0-0:1.0.0.255,2,0} to be used as
         * restricting object in a range descriptor with a from value and to
         * value to determine which elements from the buffered array should be
         * retrieved.
         */
        final DataObject clockDefinition = this.dlmsHelper.getClockDefinition();

        final DataObject fromValue = this.dlmsHelper.asDataObject(beginDateTime);
        final DataObject toValue = this.dlmsHelper.asDataObject(endDateTime);

        /*
         * List of object definitions to determine which of the capture objects
         * to retrieve from the buffer.
         */
        final DataObject selectedValues = this.makeSelectedValues(captureObjectDefinitions, isSelectingValuesSupported);

        final DataObject accessParameter = DataObject
                .newStructureData(Arrays.asList(clockDefinition, fromValue, toValue, selectedValues));

        return new SelectiveAccessDescription(ACCESS_SELECTOR_RANGE_DESCRIPTOR, accessParameter);
    }

    private DataObject makeSelectedValues(final List<CaptureObjectDefinitionDto> captureObjectDefinitions,
            final boolean isSelectingValuesSupported) {
        final List<DataObject> objectDefinitions = new ArrayList<>();
        if (isSelectingValuesSupported && !captureObjectDefinitions.isEmpty()) {
            // The captured clock is always included.
            objectDefinitions.add(this.dlmsHelper.getClockDefinition());
            for (final CaptureObjectDefinitionDto captureObjectDefinition : captureObjectDefinitions) {
                final int classId = captureObjectDefinition.getClassId();
                final byte[] obisBytes = captureObjectDefinition.getLogicalName().toByteArray();
                final byte attributeIndex = captureObjectDefinition.getAttributeIndex();
                final int dataIndex;
                if (captureObjectDefinition.getDataIndex() == null) {
                    dataIndex = 0;
                } else {
                    dataIndex = captureObjectDefinition.getDataIndex();
                }
                objectDefinitions.add(DataObject.newStructureData(
                        Arrays.asList(DataObject.newUInteger16Data(classId), DataObject.newOctetStringData(obisBytes),
                                DataObject.newInteger8Data(attributeIndex), DataObject.newUInteger16Data(dataIndex))));
            }
        }
        return DataObject.newArrayData(objectDefinitions);
    }

    private ObisCode makeObisCode(final ObisCodeValuesDto obisCodeValues) {
        return new ObisCode(obisCodeValues.toByteArray());
    }

    private CaptureObjectDto makeCaptureObjectDto(final DataObject captureObjectDataObject,
            final ScalarUnitInfo scalarUnitInfo) throws ProtocolAdapterException {

        final CosemObjectDefinitionDto cosemObjectDefinitionDto = this.dlmsHelper
                .readObjectDefinition(captureObjectDataObject, CAPTURE_OBJECT);

        return new CaptureObjectDto(cosemObjectDefinitionDto.getClassId(),
                cosemObjectDefinitionDto.getLogicalName().toString(), cosemObjectDefinitionDto.getAttributeIndex(),
                cosemObjectDefinitionDto.getDataIndex(), this.getUnit(scalarUnitInfo));
    }

    private DlmsUnitTypeDto getUnitType(final ScalarUnitInfo scalarUnitInfo) {
        if (scalarUnitInfo.getScalarUnit() != null) {
            final List<DataObject> dataObjects = scalarUnitInfo.getScalarUnit().getValue();
            final int index = Integer.parseInt(dataObjects.get(1).getValue().toString());
            final DlmsUnitTypeDto unitType = DlmsUnitTypeDto.getUnitType(index);
            if (unitType != null) {
                return unitType;
            }
        }
        return DlmsUnitTypeDto.UNDEFINED;
    }

    private String getUnit(final ScalarUnitInfo scalarUnitInfo) {
        return this.getUnitType(scalarUnitInfo).getUnit();
    }

    private List<ProfileEntryValueDto> makeProfileEntryValueDto(final DataObject profileEntryDataObject,
            final List<ScalarUnitInfo> scalarUnitInfos, ProfileEntryDto previousProfileEntryDto,
            final Map<Integer, CaptureObjectDefinitionDto> selectableCaptureObjects, int timeInterval) {

        final List<ProfileEntryValueDto> result = new ArrayList<>();
        final List<DataObject> dataObjects = profileEntryDataObject.getValue();

        for (int i = 0; i < dataObjects.size(); i++) {

            if (selectableCaptureObjects.containsKey(i)) {
                ProfileEntryValueDto currentProfileEntryValueDto = this
                        .makeProfileEntryValueDto(dataObjects.get(i), scalarUnitInfos.get(i), previousProfileEntryDto,
                                timeInterval);
                result.add(currentProfileEntryValueDto);
            }
        }
        return result;
    }

    private ProfileEntryValueDto makeProfileEntryValueDto(final DataObject dataObject,
            final ScalarUnitInfo scalarUnitInfo, ProfileEntryDto previousProfileEntryDto, int timeInterval) {
        if (InterfaceClass.CLOCK.id() == scalarUnitInfo.getClassId()) {
            return this.makeDateProfileEntryValueDto(dataObject, previousProfileEntryDto, timeInterval);
        } else if (dataObject.isNumber()) {
            return this.makeNumericProfileEntryValueDto(dataObject, scalarUnitInfo);
        } else {
            final String dbgInfo = this.dlmsHelper.getDebugInfo(dataObject);
            LOGGER.debug("creating ProfileEntryDto from {} {} ", dbgInfo, scalarUnitInfo);
            return new ProfileEntryValueDto(dbgInfo);
        }
    }

    private ProfileEntryValueDto makeDateProfileEntryValueDto(final DataObject dataObject,
            ProfileEntryDto previousProfileEntryDto, int timeInterval) {
        final CosemDateTimeDto cosemDateTime;

        cosemDateTime = this.dlmsHelper.convertDataObjectToDateTime(dataObject);

        if (cosemDateTime == null) {
            // in case of null date, we calculate the date based on the always existing previous value plus interval
            Date previousDate = (Date) previousProfileEntryDto.getProfileEntryValues().get(0).getValue();
            LocalDateTime newLocalDateTime = Instant.ofEpochMilli(previousDate.getTime()).atZone(ZoneId.systemDefault())
                                                    .toLocalDateTime().plusMinutes(timeInterval);

            return new ProfileEntryValueDto(Date.from(newLocalDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        } else {
            return new ProfileEntryValueDto(cosemDateTime.asDateTime().toDate());
        }
    }

    private ProfileEntryValueDto makeNumericProfileEntryValueDto(final DataObject dataObject,
            final ScalarUnitInfo scalarUnitInfo) {
        try {
            if (scalarUnitInfo.getScalarUnit() != null) {
                final DlmsMeterValueDto meterValue = this.dlmsHelper
                        .getScaledMeterValue(dataObject, scalarUnitInfo.getScalarUnit(), "getScaledMeterValue");
                if (DlmsUnitTypeDto.COUNT.equals(this.getUnitType(scalarUnitInfo))) {
                    return new ProfileEntryValueDto(meterValue.getValue().longValue());
                } else {
                    return new ProfileEntryValueDto(meterValue.getValue());
                }
            } else {
                final long value = this.dlmsHelper.readLong(dataObject, "read long");
                return new ProfileEntryValueDto(value);
            }
        } catch (final ProtocolAdapterException e) {
            LOGGER.error("Error creating ProfileEntryDto from {}", dataObject, e);
            final String dbgInfo = this.dlmsHelper.getDebugInfo(dataObject);
            return new ProfileEntryValueDto(dbgInfo);
        }
    }

    private Map<Integer, CaptureObjectDefinitionDto> createSelectableCaptureObjects(
            final List<GetResult> captureObjects, List<String> logicalNames) throws ProtocolAdapterException {

        Map<Integer, CaptureObjectDefinitionDto> selectableCaptureObjects = new HashMap<>();

        // there is always only one GetResult
        for (final GetResult captureObjectResult : captureObjects) {

            final List<DataObject> dataObjects = captureObjectResult.getResultData().getValue();

            int i = 0;

            for (DataObject dataObject : dataObjects) {

                final CosemObjectDefinitionDto cosemObjectDefinitionDto = this.dlmsHelper
                        .readObjectDefinition(dataObject, CAPTURE_OBJECT);

                final String logicalName = cosemObjectDefinitionDto.getLogicalName().toString();

                if (logicalNames.contains(logicalName)) {
                    selectableCaptureObjects.put(i,
                            new CaptureObjectDefinitionDto(cosemObjectDefinitionDto.getClassId(),
                                    new ObisCodeValuesDto(logicalName),
                                    (byte) cosemObjectDefinitionDto.getAttributeIndex(),
                                    cosemObjectDefinitionDto.getDataIndex()));
                }
                i++;
            }
        }

        return selectableCaptureObjects;
    }

    private List<ScalarUnitInfo> createScalarUnitInfos(final DlmsConnectionManager conn, final DlmsDevice device,
            final List<GetResult> captureObjects) throws ProtocolAdapterException {

        List<ScalarUnitInfo> scalarUnitInfos = new ArrayList<>();

        // there is always only one GetResult
        for (final GetResult captureObjectResult : captureObjects) {

            final List<DataObject> dataObjects = captureObjectResult.getResultData().getValue();

            for (DataObject dataObject : dataObjects) {

                final CosemObjectDefinitionDto cosemObjectDefinitionDto = this.dlmsHelper
                        .readObjectDefinition(dataObject, CAPTURE_OBJECT);

                final int classId = cosemObjectDefinitionDto.getClassId();
                final String logicalName = cosemObjectDefinitionDto.getLogicalName().toString();

                scalarUnitInfos.add(createScalarUnitInfo(conn, device, classId, logicalName));
            }
        }

        return scalarUnitInfos;
    }

    private ScalarUnitInfo createScalarUnitInfo(DlmsConnectionManager conn, DlmsDevice device, int classId,
            String logicalName) throws ProtocolAdapterException {

        if (this.hasScalerUnit(classId)) {
            final AttributeAddress addr = new AttributeAddress(classId, logicalName, SCALAR_UNITS_MAP.get(classId));
            final List<GetResult> scalarUnitResult = this.dlmsHelper
                    .getAndCheck(conn, device, "retrieve scalar unit for capture object", addr);
            final DataObject scalarUnitDataObject = scalarUnitResult.get(0).getResultData();
            return new ScalarUnitInfo(logicalName, classId, scalarUnitDataObject);
        } else {
            return new ScalarUnitInfo(logicalName, classId, null);
        }
    }

    private static List<String> getLogicalNamesPublicDefinableLoadProfile() {

        return Arrays
                .asList("0.0.1.0.0.255", "1.0.32.32.0.255", "1.0.52.32.0.255", "1.0.72.32.0.255", "1.0.32.36.0.255",
                        "1.0.52.36.0.255", "1.0.72.36.0.255", "0.0.96.7.21.255", "0.1.25.6.0.255", "0.0.25.6.0.255",
                        "0.1.25.6.0.255", "0.0.25.6.0.255", "0.1.24.1.0.255", "0.2.24.1.0.255", "0.1.24.9.0.255",
                        "0.2.24.9.0.255", "0.1.24.9.0.255", "0.2.24.9.0.255");
    }

    private static List<String> getLogicalNamesPublicProfile2() {
        return Arrays.asList("0.0.1.0.0.255", "1.0.32.24.0.255", "1.0.52.24.0.255", "1.0.72.24.0.255");
    }

    private static List<String> getLogicalNamesPrivateProfile1() {
        return Arrays.asList("0.0.1.0.0.255", "1.0.21.4.0.255", "1.0.41.4.0.255", "1.0.61.4.0.255", "1.0.22.4.0.255",
                "1.0.42.4.0.255", "1.0.62.4.0.255", "1.0.23.4.0.255", "1.0.43.4.0.255", "1.0.63.4.0.255",
                "1.0.24.4.0.255", "1.0.44.4.0.255", "1.0.64.4.0.255");
    }

    private static List<String> getLogicalNamesPrivateProfile2() {
        return Arrays
                .asList("0.0.1.0.0.255", "1.0.31.24.0.255", "1.0.51.24.0.255", "1.0.71.24.0.255", "1.0.31.7.0.255");
    }

    private boolean hasScalerUnit(final int classId) {
        return SCALAR_UNITS_MAP.containsKey(classId);
    }

}
