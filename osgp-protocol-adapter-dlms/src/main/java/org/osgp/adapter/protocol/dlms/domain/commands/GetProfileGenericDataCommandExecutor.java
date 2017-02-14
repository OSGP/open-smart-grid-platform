/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.CaptureObjectDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CaptureObjectItemDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileEntryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileEntryItemDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataResponseDto;

@Component()
public class GetProfileGenericDataCommandExecutor extends
AbstractCommandExecutor<ProfileGenericDataRequestDto, ProfileGenericDataResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetProfileGenericDataCommandExecutor.class);

    private static final int ACCESS_SELECTOR_RANGE_DESCRIPTOR = 1;
    private static final int CLASS_ID_PROFILE_GENERIC = 7;
    private static final byte ATTRIBUTE_ID_BUFFER = 2;
    private static final byte ATTRIBUTE_ID_CAPTURE_OBJECTS = 3;
    private static final byte ATTRIBUTE_ID_SCALER_UNIT = 3;

    private static final int CLASS_ID_INDEX = 0;
    private static final int OBIS_CODE_INDEX = 1;
    private static final int ATTR_INDEX = 2;
    private static final int VERSION_INDEX = 3;

    private static final int CLASS_ID_REGISTER = 3;
    private static final int CLASS_ID_EXTENDED = 4;
    private static final int CLASS_ID_DEMAND = 5;
    private static final int CLASS_ID_CLOCK = 8;

    private static final int[] HAS_SCALAR_UNITS = new int[] { CLASS_ID_REGISTER, CLASS_ID_EXTENDED, CLASS_ID_DEMAND };

    @Autowired
    private DlmsHelperService dlmsHelperService;

    public GetProfileGenericDataCommandExecutor() {
        super(ProfileGenericDataRequestDto.class);
    }

    @Override
    public ProfileGenericDataResponseDto execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final ProfileGenericDataRequestDto profileGenericDataRequestDto) throws ProtocolAdapterException {

        final DateTime beginDateTime = new DateTime(profileGenericDataRequestDto.getBeginDate());
        final DateTime endDateTime = new DateTime(profileGenericDataRequestDto.getEndDate());
        final ObisCode obisCode = this.makeObisCode(profileGenericDataRequestDto.getObisCode());
        final ObisCodeValuesDto inputObisCodes = profileGenericDataRequestDto.getObisCode();

        LOGGER.debug("Retrieving profile generic data for, from: {}, to: {}", beginDateTime, endDateTime);

        List<GetResult> captureObjects = this.retrieveCaptureObjects(conn, device, obisCode);
        List<GetResult> bufferList = this
                .retrieveProfileEntryBuffer(conn, device, beginDateTime, endDateTime, obisCode);
        List<ObjectDataInfo> dataObjectInfoList = this.retrieveScalarUnits(conn, device, captureObjects);
        return this.processData(inputObisCodes, captureObjects, bufferList, dataObjectInfoList);
    }

    private List<GetResult> retrieveCaptureObjects(DlmsConnectionHolder conn, DlmsDevice device, final ObisCode obisCode)
            throws ProtocolAdapterException {
        AttributeAddress captureObjectsAttributeAddress = new AttributeAddress(CLASS_ID_PROFILE_GENERIC, obisCode,
                ATTRIBUTE_ID_CAPTURE_OBJECTS);

        return this.dlmsHelperService.getAndCheck(conn, device, "retrieve profile generic capture objects",
                captureObjectsAttributeAddress);
    }

    private List<GetResult> retrieveProfileEntryBuffer(final DlmsConnectionHolder conn, final DlmsDevice device,
            final DateTime beginDateTime, final DateTime endDateTime, final ObisCode obisCode)
            throws ProtocolAdapterException {
        final SelectiveAccessDescription access = this.getSelectiveAccessDescription(beginDateTime, endDateTime,
                device.isSelectiveAccessSupported());
        AttributeAddress bufferAttributeAddress = new AttributeAddress(CLASS_ID_PROFILE_GENERIC, obisCode,
                ATTRIBUTE_ID_BUFFER, access);
        return this.dlmsHelperService.getAndCheck(conn, device, "retrieve profile generic buffer",
                bufferAttributeAddress);
    }

    /*
     * Process data Add units to capture objects Calculate the proper values in
     * the buffer using the scaler
     */
    private ProfileGenericDataResponseDto processData(final ObisCodeValuesDto obisCode,
            final List<GetResult> captureObjects, final List<GetResult> bufferList,
            List<ObjectDataInfo> dataObjectInfoList) throws ProtocolAdapterException {

        List<CaptureObjectItemDto> captureObjectItemDtoList = this.makeCaptureObjectItemDtoList(captureObjects);
        List<ProfileEntryItemDto> profileEntryItemDtoList = this.makeProfileEntryItemDtoList(bufferList,
                dataObjectInfoList);
        return new ProfileGenericDataResponseDto(obisCode, captureObjectItemDtoList, profileEntryItemDtoList);
    }

    private List<ProfileEntryItemDto> makeProfileEntryItemDtoList(final List<GetResult> bufferList,
            final List<ObjectDataInfo> dataObjectInfoList) throws ProtocolAdapterException {

        List<ProfileEntryItemDto> profileEntryItemDtoList = new ArrayList<>();
        for (GetResult buffer : bufferList) {
            DataObject dataObject = buffer.getResultData();
            final List<DataObject> dataObjectList1 = dataObject.getValue();
            for (DataObject profEntryDataObject : dataObjectList1) {
                profileEntryItemDtoList.add(new ProfileEntryItemDto(this.makeProfileEntryDto(profEntryDataObject,
                        dataObjectInfoList)));
            }
        }
        return profileEntryItemDtoList;
    }

    private List<CaptureObjectItemDto> makeCaptureObjectItemDtoList(final List<GetResult> captureObjects)
            throws ProtocolAdapterException {

        List<CaptureObjectItemDto> captureObjectItemDtoList = new ArrayList<>();
        for (GetResult captureObjectResult : captureObjects) {
            DataObject dataObject = captureObjectResult.getResultData();
            final List<DataObject> dataObjectList1 = dataObject.getValue();
            for (DataObject captureObjectDataObject : dataObjectList1) {
                captureObjectItemDtoList.add(new CaptureObjectItemDto(this
                        .makeCaptureObjectDto(captureObjectDataObject)));
            }
        }
        return captureObjectItemDtoList;
    }

    private SelectiveAccessDescription getSelectiveAccessDescription(final DateTime beginDateTime,
            final DateTime endDateTime, final boolean isSelectingValuesSupported) {

        final int accessSelector = ACCESS_SELECTOR_RANGE_DESCRIPTOR;

        /*
         * Define the clock object {8,0-0:1.0.0.255,2,0} to be used as
         * restricting object in a range descriptor with a from value and to
         * value to determine which elements from the buffered array should be
         * retrieved.
         */
        final DataObject clockDefinition = this.dlmsHelperService.getClockDefinition();

        final DataObject fromValue = this.dlmsHelperService.asDataObject(beginDateTime);
        final DataObject toValue = this.dlmsHelperService.asDataObject(endDateTime);

        /*
         * List of object definitions to determine which of the capture objects
         * to retrieve from the buffer.
         */
        final List<DataObject> objectDefinitions = new ArrayList<>();
        final DataObject selectedValues = DataObject.newArrayData(objectDefinitions);

        final DataObject accessParameter = DataObject.newStructureData(Arrays.asList(clockDefinition, fromValue,
                toValue, selectedValues));

        return new SelectiveAccessDescription(accessSelector, accessParameter);
    }

    private ObisCode makeObisCode(final ObisCodeValuesDto obisCodeValues) {
        final byte[] obisCodeBytes = { obisCodeValues.getA(), obisCodeValues.getB(), obisCodeValues.getC(),
                obisCodeValues.getD(), obisCodeValues.getE(), obisCodeValues.getF() };
        return new ObisCode(obisCodeBytes);
    }

    private CaptureObjectDto makeCaptureObjectDto(final DataObject captureObjectDataObject)
            throws ProtocolAdapterException {
        final List<DataObject> dataObjectList = captureObjectDataObject.getValue();

        final int classId = this.getNumericValue(dataObjectList.get(CLASS_ID_INDEX));
        final String logicalName = this.dlmsHelperService.readString(dataObjectList.get(OBIS_CODE_INDEX), "obis-code");
        final int attribute = this.getIntValue(dataObjectList.get(ATTR_INDEX));
        final int version = this.getVersion(dataObjectList.get(VERSION_INDEX));
        return new CaptureObjectDto(classId, logicalName, attribute, version, "Unit");
    }

    private List<ProfileEntryDto> makeProfileEntryDto(final DataObject profEntryDataObjects,
            List<ObjectDataInfo> dataObjectInfoList) throws ProtocolAdapterException {

        final List<ProfileEntryDto> result = new ArrayList<>();

        final List<DataObject> dataObjectList = profEntryDataObjects.getValue();
        int index = 0;
        for (DataObject dataObject : dataObjectList) {
            result.add(this.makeProfileEntryDto(dataObject, dataObjectInfoList.get(index)));
            index++;
        }

        return result;
    }

    private ProfileEntryDto makeProfileEntryDto(final DataObject dataObject, final ObjectDataInfo dataObjectInfo) {
        final String dbgInfo = this.dlmsHelperService.getDebugInfo(dataObject);
        LOGGER.debug("creating ProfileEntryDto from " + dbgInfo + " " + dataObjectInfo);
        if (CLASS_ID_CLOCK == dataObjectInfo.classId) {
            return this.makeDateProfileEntryDto(dataObject);
        } else if (dataObject.isNumber()) {
            return this.makeNumericProfileEntryDto(dataObject, dataObjectInfo);
        } else {
            return new ProfileEntryDto(dbgInfo);
        }
    }

    private ProfileEntryDto makeDateProfileEntryDto(final DataObject dataObject) {
        CosemDateTime cosemDateTime = CosemDateTime.decode((byte[]) dataObject.getValue());
        return new ProfileEntryDto(cosemDateTime.toCalendar().getTime());
    }

    private ProfileEntryDto makeNumericProfileEntryDto(final DataObject dataObject, final ObjectDataInfo dataObjectInfo) {
        try {
            long value = this.dlmsHelperService.readLong(dataObject, "read long");
            return new ProfileEntryDto(value);
        } catch (ProtocolAdapterException e) {
            return null;
        }
    }

    private int getNumericValue(final DataObject dataObject) throws ProtocolAdapterException {
        if (DataObject.Type.LONG_UNSIGNED != dataObject.getType()) {
            this.throwUnexpectedTypeProtocolAdapterException();
        }
        return ((Number) dataObject.getValue()).intValue();
    }

    private int getIntValue(final DataObject dataObject) throws ProtocolAdapterException {
        if (DataObject.Type.INTEGER != dataObject.getType()) {
            this.throwUnexpectedTypeProtocolAdapterException();
        }
        return ((Number) dataObject.getValue()).intValue();
    }

    private int getVersion(final DataObject dataObject) throws ProtocolAdapterException {
        if (DataObject.Type.LONG_UNSIGNED != dataObject.getType()) {
            this.throwUnexpectedTypeProtocolAdapterException();
        }
        return ((Number) dataObject.getValue()).intValue();
    }

    private void throwUnexpectedTypeProtocolAdapterException() throws ProtocolAdapterException {
        throw new ProtocolAdapterException("Unexpected type of element");
    }

    private List<ObjectDataInfo> retrieveScalarUnits(DlmsConnectionHolder conn, DlmsDevice device,
            List<GetResult> captureObjects) throws ProtocolAdapterException {

        final List<ObjectDataInfo> result = new ArrayList<>();

        for (GetResult captureObjectResult : captureObjects) {
            DataObject dataObject = captureObjectResult.getResultData();
            final List<DataObject> dataObjectList1 = dataObject.getValue();
            for (DataObject captureObjectDataObject : dataObjectList1) {
                final List<DataObject> dataObjectList = captureObjectDataObject.getValue();
                final int classId = this.getNumericValue(dataObjectList.get(CLASS_ID_INDEX));
                final String logicalName = this.dlmsHelperService.readString(dataObjectList.get(OBIS_CODE_INDEX),
                        "obis-code");
                if (this.hasScalarUnit(classId)) {
                    AttributeAddress addr = new AttributeAddress(CLASS_ID_REGISTER, logicalName,
                            ATTRIBUTE_ID_SCALER_UNIT);
                    final List<GetResult> scalarUnitResult = this.dlmsHelperService.getAndCheck(conn, device,
                            "retrieve scaler unit for capture object", addr);
                    DataObject scalarUnitDataObject = scalarUnitResult.get(0).getResultData();
                    result.add(new ObjectDataInfo(logicalName, classId, scalarUnitDataObject));
                } else {
                    result.add(new ObjectDataInfo(logicalName, classId, null));
                }
            }
        }

        return result;
    }

    private boolean hasScalarUnit(final int classId) {
        for (int classIdWithScalar : HAS_SCALAR_UNITS) {
            if (classId == classIdWithScalar) {
                return true;
            }
        }
        return false;
    }

    static class ObjectDataInfo {
        String logicalName;
        int classId;
        DataObject scalarUnit;

        public ObjectDataInfo(String logicalName, int classId, DataObject scalarUnit) {
            super();
            this.logicalName = logicalName;
            this.classId = classId;
            this.scalarUnit = scalarUnit;
        }

        @Override
        public String toString() {
            return "ObjectDataInfo [logicalName=" + this.logicalName + ", classId=" + this.classId + ", scalarUnit="
                    + this.scalarUnit + "]";
        }
    }
}
