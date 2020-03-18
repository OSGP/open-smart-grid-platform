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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ScalerUnitInfo;
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

        DEFINABLE_LOAD_PROFILE(OBIS_CODE_DEFINABLE_LOAD_PROFILE, INTERVAL_DEFINABLE_LOAD_PROFILE),
        PROFILE_1(OBIS_CODE_PROFILE_1, INTERVAL_PROFILE_1),
        PROFILE_2(OBIS_CODE_PROFILE_2, INTERVAL_PROFILE_2);

        private final ObisCodeValuesDto obisCodeValuesDto;
        private final int interval;

        Profile(ObisCodeValuesDto obisCodeValuesDto, int interval) {
            this.obisCodeValuesDto = obisCodeValuesDto;
            this.interval = interval;
        }

        public ObisCodeValuesDto getObisCodeValuesDto() {
            return obisCodeValuesDto;
        }

        public int getInterval() {
            return interval;
        }
    }

    private static final byte[] OBIS_BYTES_CLOCK = new byte[] { 0, 0, 1, 0, 0, (byte) 255 };

    private static final Map<Integer, Integer> SCALER_UNITS_MAP = new HashMap<>();

    static {
        SCALER_UNITS_MAP.put(InterfaceClass.REGISTER.id(), RegisterAttribute.SCALER_UNIT.attributeId());
        SCALER_UNITS_MAP
                .put(InterfaceClass.EXTENDED_REGISTER.id(), ExtendedRegisterAttribute.SCALER_UNIT.attributeId());
        SCALER_UNITS_MAP.put(InterfaceClass.DEMAND_REGISTER.id(), DemandRegisterAttribute.SCALER_UNIT.attributeId());
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

        LOGGER.info("executing GetPowerQualityProfileResponseDto for {}", profileType);

        final List<Profile> profiles = determineProfileForDevice(profileType);

        final GetPowerQualityProfileResponseDto response = new GetPowerQualityProfileResponseDto();
        final List<PowerQualityProfileDataDto> responseDatas = new ArrayList<>();

        for (final Profile profile : profiles) {

            final ObisCode obisCode = this.makeObisCode(profile.getObisCodeValuesDto());
            final DateTime beginDateTime = new DateTime(getPowerQualityProfileRequestDataDto.getBeginDate());
            final DateTime endDateTime = new DateTime(getPowerQualityProfileRequestDataDto.getEndDate());
            final List<CaptureObjectDefinitionDto> selectedValues = getPowerQualityProfileRequestDataDto
                    .getSelectedValues();

            LOGGER.info("Retrieving power quality data for {}, from: {}, to: {}, selected values: {}",
                    profile.getObisCodeValuesDto(), beginDateTime, endDateTime,
                    selectedValues.isEmpty() ? "all capture objects" : selectedValues);

            final List<GetResult> captureObjects = this.retrieveCaptureObjects(conn, device, obisCode);
            final List<ScalerUnitInfo> scalerUnitInfos = this.retrieveScalerUnits(conn, device, captureObjects);
            final List<GetResult> bufferList = this
                    .retrieveBuffer(conn, device, obisCode, beginDateTime, endDateTime, selectedValues);

            final PowerQualityProfileDataDto responseDataDto = this
                    .processData(profile, captureObjects, scalerUnitInfos, selectedValues,
                            device.isSelectiveAccessSupported(), bufferList);

            responseDatas.add(responseDataDto);
        }

        response.setPowerQualityProfileDatas(responseDatas);

        return response;
    }

    private List<Profile> determineProfileForDevice(final String profileType) {

        switch (profileType) {
        case PUBLIC:
            return Arrays.asList(Profile.DEFINABLE_LOAD_PROFILE, Profile.PROFILE_2);
        case PRIVATE:
            return Arrays.asList(Profile.PROFILE_1, Profile.PROFILE_2);
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
            final List<CaptureObjectDefinitionDto> selectedValues) throws ProtocolAdapterException {
        final SelectiveAccessDescription access = this
                .getSelectiveAccessDescription(beginDateTime, endDateTime, selectedValues,
                        device.isSelectiveAccessSupported());
        final AttributeAddress bufferAttributeAddress = new AttributeAddress(InterfaceClass.PROFILE_GENERIC.id(),
                obisCode, ProfileGenericAttribute.BUFFER.attributeId(), access);
        return this.dlmsHelper.getAndCheck(conn, device, "retrieve profile generic buffer", bufferAttributeAddress);
    }

    /*
     * Process data Add units to capture objects Calculate the proper values in
     * the buffer using the scaler
     */
    private PowerQualityProfileDataDto processData(final Profile profile, final List<GetResult> captureObjects,
            final List<ScalerUnitInfo> scalerUnitInfos, final List<CaptureObjectDefinitionDto> selectedValues,
            final boolean isSelectingValuesSupported, final List<GetResult> bufferList)
            throws ProtocolAdapterException {

        LOGGER.info("GetPowerQualityProfileCommandExecutor retrieved {} results ", bufferList.size());

        final List<CaptureObjectDto> captureObjectDtos = this
                .makeCaptureObjects(captureObjects, scalerUnitInfos, selectedValues, isSelectingValuesSupported);
        final List<ProfileEntryDto> profileEntryDtos = this
                .makeProfileEntries(bufferList, scalerUnitInfos, profile.getInterval());
        return new PowerQualityProfileDataDto(profile.getObisCodeValuesDto(), captureObjectDtos, profileEntryDtos);
    }

    private List<ProfileEntryDto> makeProfileEntries(final List<GetResult> bufferList,
            final List<ScalerUnitInfo> scalerUnitInfos, int timeInterval) {

        final List<ProfileEntryDto> profileEntryDtos = new ArrayList<>();
        for (final GetResult buffer : bufferList) {
            final DataObject dataObject = buffer.getResultData();
            final List<DataObject> dataObjectList1 = dataObject.getValue();

            ProfileEntryDto previousProfileEntryDto = null;

            for (final DataObject profEntryDataObject : dataObjectList1) {

                ProfileEntryDto profileEntryDto = new ProfileEntryDto(
                        this.makeProfileEntryValueDto(profEntryDataObject, scalerUnitInfos, previousProfileEntryDto,
                                timeInterval));

                profileEntryDtos.add(profileEntryDto);

                previousProfileEntryDto = profileEntryDto;
            }
        }
        return profileEntryDtos;
    }

    private List<CaptureObjectDto> makeCaptureObjects(final List<GetResult> captureObjects,
            final List<ScalerUnitInfo> scalerUnitInfos, final List<CaptureObjectDefinitionDto> selectedValues,
            final boolean isSelectingValuesSupported) throws ProtocolAdapterException {

        final boolean filterCaptureObjects = isSelectingValuesSupported && !selectedValues.isEmpty();

        final List<CaptureObjectDto> captureObjectDtos = new ArrayList<>();
        for (final GetResult captureObjectResult : captureObjects) {
            final DataObject dataObject = captureObjectResult.getResultData();
            final List<DataObject> captureObjectList = dataObject.getValue();
            for (int i = 0; i < captureObjectList.size(); i++) {
                final boolean addCaptureObject =
                        !filterCaptureObjects || this.isSelectedValue(selectedValues, captureObjectList.get(i));
                if (addCaptureObject) {
                    captureObjectDtos.add(this.makeCaptureObjectDto(captureObjectList.get(i), scalerUnitInfos.get(i)));
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
            final ScalerUnitInfo scalerUnitInfo) throws ProtocolAdapterException {

        final CosemObjectDefinitionDto cosemObjectDefinitionDto = this.dlmsHelper
                .readObjectDefinition(captureObjectDataObject, CAPTURE_OBJECT);

        return new CaptureObjectDto(cosemObjectDefinitionDto.getClassId(),
                cosemObjectDefinitionDto.getLogicalName().toString(), cosemObjectDefinitionDto.getAttributeIndex(),
                cosemObjectDefinitionDto.getDataIndex(), this.getUnit(scalerUnitInfo));
    }

    private DlmsUnitTypeDto getUnitType(final ScalerUnitInfo scalerUnitInfo) {
        if (scalerUnitInfo.getScalerUnit() != null) {
            final List<DataObject> dataObjects = scalerUnitInfo.getScalerUnit().getValue();
            final int index = Integer.parseInt(dataObjects.get(1).getValue().toString());
            final DlmsUnitTypeDto unitType = DlmsUnitTypeDto.getUnitType(index);
            if (unitType != null) {
                return unitType;
            }
        }
        return DlmsUnitTypeDto.UNDEFINED;
    }

    private String getUnit(final ScalerUnitInfo scalerUnitInfo) {
        return this.getUnitType(scalerUnitInfo).getUnit();
    }

    private List<ProfileEntryValueDto> makeProfileEntryValueDto(final DataObject profEntryDataObjects,
            final List<ScalerUnitInfo> scalerUnitInfos, ProfileEntryDto previousProfileEntryDto, int timeInterval) {

        final List<ProfileEntryValueDto> result = new ArrayList<>();

        final List<DataObject> dataObjects = profEntryDataObjects.getValue();

        if (dataObjects.size() != scalerUnitInfos.size()) {
            LOGGER.info("Size of dataobjects {} does not equal size of scalar units {}", dataObjects.size(),
                    scalerUnitInfos.size());
        }

        for (int i = 0; i < dataObjects.size(); i++) {

            ProfileEntryValueDto currenProfileEntryValueDto = this
                    .makeProfileEntryValueDto(dataObjects.get(i), scalerUnitInfos.get(i), previousProfileEntryDto,
                            timeInterval);
            result.add(currenProfileEntryValueDto);

        }
        return result;
    }

    private ProfileEntryValueDto makeProfileEntryValueDto(final DataObject dataObject,
            final ScalerUnitInfo scalerUnitInfo, ProfileEntryDto previousProfileEntryDto, int timeInterval) {
        if (InterfaceClass.CLOCK.id() == scalerUnitInfo.getClassId()) {
            return this.makeDateProfileEntryValueDto(dataObject, previousProfileEntryDto, timeInterval);
        } else if (dataObject.isNumber()) {
            return this.makeNumericProfileEntryValueDto(dataObject, scalerUnitInfo);
        } else {
            final String dbgInfo = this.dlmsHelper.getDebugInfo(dataObject);
            LOGGER.debug("creating ProfileEntryDto from {} {} ", dbgInfo, scalerUnitInfo);
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
            final ScalerUnitInfo scalerUnitInfo) {
        try {
            if (scalerUnitInfo.getScalerUnit() != null) {
                final DlmsMeterValueDto meterValue = this.dlmsHelper
                        .getScaledMeterValue(dataObject, scalerUnitInfo.getScalerUnit(), "getScaledMeterValue");
                if (DlmsUnitTypeDto.COUNT.equals(this.getUnitType(scalerUnitInfo))) {
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

    private List<ScalerUnitInfo> retrieveScalerUnits(final DlmsConnectionManager conn, final DlmsDevice device,
            final List<GetResult> captureObjects) throws ProtocolAdapterException {

        final List<ScalerUnitInfo> result = new ArrayList<>();

        for (final GetResult captureObjectResult : captureObjects) {
            final DataObject dataObject = captureObjectResult.getResultData();
            final List<DataObject> dataObjectList1 = dataObject.getValue();
            for (final DataObject captureObjectDataObject : dataObjectList1) {

                final CosemObjectDefinitionDto cosemObjectDefinitionDto = this.dlmsHelper
                        .readObjectDefinition(captureObjectDataObject, CAPTURE_OBJECT);
                final int classId = cosemObjectDefinitionDto.getClassId();
                final String logicalName = cosemObjectDefinitionDto.getLogicalName().toString();
                if (this.hasScalerUnit(classId)) {
                    final AttributeAddress addr = new AttributeAddress(classId, logicalName,
                            SCALER_UNITS_MAP.get(classId));
                    final List<GetResult> scalerUnitResult = this.dlmsHelper
                            .getAndCheck(conn, device, "retrieve scaler unit for capture object", addr);
                    final DataObject scalerUnitDataObject = scalerUnitResult.get(0).getResultData();
                    result.add(new ScalerUnitInfo(logicalName, classId, scalerUnitDataObject));
                } else {
                    result.add(new ScalerUnitInfo(logicalName, classId, null));
                }
            }
        }

        return result;
    }

    private boolean hasScalerUnit(final int classId) {
        return SCALER_UNITS_MAP.containsKey(classId);
    }

}
