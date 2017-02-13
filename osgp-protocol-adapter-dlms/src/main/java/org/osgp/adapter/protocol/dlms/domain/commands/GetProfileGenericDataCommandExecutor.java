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

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
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

        // Retrieve capture objects
        List<GetResult> captureObjects = this.retrieveCaptureObjects(conn, device, obisCode);
        this.retrieveScalarUnits(conn, device, captureObjects);
        List<GetResult> bufferList = this
                .retrieveProfileEntryBuffer(conn, device, beginDateTime, endDateTime, obisCode);

        ProfileGenericDataResponseDto responseDto = this.processData(inputObisCodes, captureObjects, bufferList);
        return responseDto;
    }

    private void retrieveScalarUnits(DlmsConnectionHolder conn, DlmsDevice device, List<GetResult> captureObjects)
            throws ProtocolAdapterException {
        // Determine for which capture objects to retrieve the scaler units
        // Look in Blue Book, capture objects of type register (class id 3?, and
        // demand register (class id 5?) are candidates
        // Get scalar units addresses
        List<AttributeAddress> scalerUnitAddresses = this.getScalerUnits(captureObjects);

        // Retrieve the scaler units
        List<GetResult> scalerUnits = new ArrayList<>();
        for (AttributeAddress scalerUnitAttributeAddress : scalerUnitAddresses) {
            scalerUnits.addAll(this.dlmsHelperService.getAndCheck(conn, device,
                    "retrieve scaler unit for capture object", scalerUnitAttributeAddress));
        }
    }

    private List<GetResult> retrieveCaptureObjects(DlmsConnectionHolder conn, DlmsDevice device, final ObisCode obisCode)
            throws ProtocolAdapterException {
        AttributeAddress captureObjectsAttributeAddress = new AttributeAddress(CLASS_ID_PROFILE_GENERIC, obisCode,
                ATTRIBUTE_ID_CAPTURE_OBJECTS);
        List<GetResult> captureObjects = this.dlmsHelperService.getAndCheck(conn, device,
                "retrieve profile generic capture objects", captureObjectsAttributeAddress);
        return captureObjects;
    }

    private List<GetResult> retrieveProfileEntryBuffer(DlmsConnectionHolder conn, DlmsDevice device,
            final DateTime beginDateTime, final DateTime endDateTime, final ObisCode obisCode)
            throws ProtocolAdapterException {
        final SelectiveAccessDescription access = this.getSelectiveAccessDescription(beginDateTime, endDateTime,
                device.isSelectiveAccessSupported());
        AttributeAddress bufferAttributeAddress = new AttributeAddress(CLASS_ID_PROFILE_GENERIC, obisCode,
                ATTRIBUTE_ID_BUFFER, access);
        List<GetResult> bufferList = this.dlmsHelperService.getAndCheck(conn, device,
                "retrieve profile generic buffer", bufferAttributeAddress);
        return bufferList;
    }

    /*
     * Process data Add units to capture objects Calculate the proper values in
     * the buffer using the scaler
     */
    private ProfileGenericDataResponseDto processData(final ObisCodeValuesDto obisCode, List<GetResult> captureObjects,
            List<GetResult> bufferList) throws ProtocolAdapterException {

        List<CaptureObjectItemDto> captureObjectItemDtoList = new ArrayList<>();
        for (GetResult captureObjectResult : captureObjects) {
            DataObject dataObject = captureObjectResult.getResultData();
            final List<DataObject> dataObjectList1 = dataObject.getValue();
            for (DataObject captureObjectDataObject : dataObjectList1) {
                captureObjectItemDtoList.add(new CaptureObjectItemDto(this
                        .makeCaptureObjectDto(captureObjectDataObject)));
            }

        }

        List<ProfileEntryItemDto> profileEntryItemDtoList = new ArrayList<>();
        for (GetResult buffer : bufferList) {
            DataObject dataObject = buffer.getResultData();
            final List<DataObject> dataObjectList1 = dataObject.getValue();
            for (DataObject profEntry : dataObjectList1) {
                profileEntryItemDtoList.add(new ProfileEntryItemDto(this.makeProfileEntryDto(profEntry)));
            }
        }

        return new ProfileGenericDataResponseDto(obisCode, captureObjectItemDtoList, profileEntryItemDtoList);
    }

    private ObisCode makeObisCode(final ObisCodeValuesDto obisCodeValues) {
        final byte[] obisCodeBytes = { obisCodeValues.getA(), obisCodeValues.getB(), obisCodeValues.getC(),
                obisCodeValues.getD(), obisCodeValues.getE(), obisCodeValues.getF() };
        return new ObisCode(obisCodeBytes);
    }

    private CaptureObjectDto makeCaptureObjectDto(DataObject captureObjectDataObject) throws ProtocolAdapterException {
        final List<DataObject> dataObjectList = captureObjectDataObject.getValue();

        // final CosemObjectDefinitionDto objectDef =
        // this.dlmsHelperService.readObjectDefinition(dataObject2, "capture
        // object");
        final int classId = this.getNumericValue(dataObjectList.get(CLASS_ID_INDEX));
        final String logicalName = this.dlmsHelperService.readString(dataObjectList.get(OBIS_CODE_INDEX), "obis-code");
        final int attribute = this.getIntValue(dataObjectList.get(ATTR_INDEX));
        final int version = this.getVersion(dataObjectList.get(VERSION_INDEX));
        return new CaptureObjectDto(classId, logicalName, attribute, version, "Unit");
    }

    private List<ProfileEntryDto> makeProfileEntryDto(DataObject dataObject) throws ProtocolAdapterException {
        List<ProfileEntryDto> result = new ArrayList<>();

        final List<DataObject> dataObjectList = dataObject.getValue();

        for (DataObject obj : dataObjectList) {
            // TODO JRB
            String dbgInfo = this.dlmsHelperService.getDebugInfo(obj);
            result.add(new ProfileEntryDto(dbgInfo));
        }

        return result;
    }

    private List<AttributeAddress> getScalerUnits(List<GetResult> captureObjects) {
        List<AttributeAddress> scalerUnits = new ArrayList<AttributeAddress>();

        for (GetResult getResult : captureObjects) {
            getResult.getResultData();
        }

        return scalerUnits;
    }

    private AttributeAddress[] getProfileBufferAndScalerUnit(final ObisCode obisCode, final DateTime beginDateTime,
            final DateTime endDateTime, final boolean isSelectingValuesSupported) throws ProtocolAdapterException {

        final SelectiveAccessDescription access = this.getSelectiveAccessDescription(beginDateTime, endDateTime,
                isSelectingValuesSupported);

        final List<AttributeAddress> profileBuffer = new ArrayList<>();
        profileBuffer.add(new AttributeAddress(CLASS_ID_PROFILE_GENERIC, obisCode, ATTRIBUTE_ID_BUFFER, access));
        profileBuffer.addAll(this.createScalerUnitForInterval(obisCode));
        return profileBuffer.toArray(new AttributeAddress[profileBuffer.size()]);
    }

    private List<AttributeAddress> createScalerUnitForInterval(final ObisCode obisCode) {
        final List<AttributeAddress> scalerUnit = new ArrayList<>();
        scalerUnit.add(new AttributeAddress(CLASS_ID_PROFILE_GENERIC, obisCode, ATTRIBUTE_ID_SCALER_UNIT));
        return scalerUnit;
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

    private ObisCodeValuesDto makeObisCodeValuesDto(final String logicalName) {
        String strBytes[] = StringUtils.split(logicalName, '.');
        byte bytes[] = new byte[strBytes.length];
        for (int i = 0; i < strBytes.length; i++) {
            String str = strBytes[i];
            bytes[i] = str.equals("255") ? -1 : Byte.valueOf(strBytes[i]);
        }
        return new ObisCodeValuesDto(bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5]);

    }

}
