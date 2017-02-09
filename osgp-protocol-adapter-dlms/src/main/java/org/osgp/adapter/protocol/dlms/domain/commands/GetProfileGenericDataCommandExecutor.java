/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.BufferedDateTimeValidationException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.CaptureObjectDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CaptureObjectItemDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTimeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileEntryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileEntryItemDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataResponseDto;

@Component()
public class GetProfileGenericDataCommandExecutor extends
AbstractCommandExecutor<ProfileGenericDataRequestDto, ProfileGenericDataResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetProfileGenericDataCommandExecutor.class);

    // private static final int BUFFER_INDEX_CLOCK = 0;
    private static final int ACCESS_SELECTOR_RANGE_DESCRIPTOR = 1;
    private static final int CLASS_ID_PROFILE_GENERIC = 7;
    private static final byte ATTRIBUTE_ID_BUFFER = 2;
    private static final byte ATTRIBUTE_ID_CAPTURE_OBJECTS = 3;
    private static final byte ATTRIBUTE_ID_SCALER_UNIT = 3;

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
        final ObisCode obisCode = this.makeObisCode(profileGenericDataRequestDto);

        // Retrieve capture objects
        AttributeAddress captureObjectsAttributeAddress = new AttributeAddress(CLASS_ID_PROFILE_GENERIC, obisCode,
                ATTRIBUTE_ID_CAPTURE_OBJECTS);
        List<GetResult> captureObjects = this.dlmsHelperService.getAndCheck(conn, device,
                "retrieve profile generic capture objects", captureObjectsAttributeAddress);

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

        // Retrieve the buffer
        final SelectiveAccessDescription access = this.getSelectiveAccessDescription(beginDateTime, endDateTime,
                device.isSelectiveAccessSupported());
        AttributeAddress bufferAttributeAddress = new AttributeAddress(CLASS_ID_PROFILE_GENERIC, obisCode,
                ATTRIBUTE_ID_BUFFER, access);
        List<GetResult> buffer = this.dlmsHelperService.getAndCheck(conn, device, "retrieve profile generic buffer",
                bufferAttributeAddress);

        // Process data
        // Add units to capture objects
        // Calculate the proper values in the buffer using the scaler

        final AttributeAddress[] profileBufferAndScalerUnit = this.getProfileBufferAndScalerUnit(obisCode,
                beginDateTime, endDateTime, device.isSelectiveAccessSupported());

        LOGGER.debug("Retrieving profile generic data for interval, from: {}, to: {}", beginDateTime, endDateTime);

        /*
         * workaround for a problem when using with_list and retrieving a
         * profile buffer, this will be returned erroneously.
         */
        final List<GetResult> getResultList = new ArrayList<>(profileBufferAndScalerUnit.length);
        for (final AttributeAddress address : profileBufferAndScalerUnit) {

            conn.getDlmsMessageListener().setDescription(
                    "GetProfileGenericData " + PeriodTypeDto.INTERVAL + " from " + beginDateTime + " until "
                            + endDateTime + ", retrieve attribute: "
                            + JdlmsObjectToStringUtil.describeAttributes(address));

            getResultList.addAll(this.dlmsHelperService.getAndCheck(conn, device, "retrieve profile generic data for "
                    + PeriodTypeDto.INTERVAL, address));
        }

        final DataObject resultData = this.dlmsHelperService.readDataObject(getResultList.get(0),
                "Profile Generic Data");
        final List<DataObject> bufferedObjectsList = resultData.getValue();

        final List<CaptureObjectDto> captureObjectsDto = new ArrayList<>();
        for (final DataObject bufferedObject : bufferedObjectsList) {
            final List<DataObject> bufferedObjects = bufferedObject.getValue();
            try {
                captureObjectsDto.add(this.processNextProfileGenericData(PeriodTypeDto.INTERVAL, beginDateTime,
                        endDateTime, bufferedObjects, getResultList));
            } catch (final BufferedDateTimeValidationException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        return this.makeTestResult(profileGenericDataRequestDto);
    }

    private List<AttributeAddress> getScalerUnits(List<GetResult> captureObjects) {
        List<AttributeAddress> scalerUnits = new ArrayList<AttributeAddress>();
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

    // TODO JRB tijdelijke hack om flow te testen: weghalen <!--------
    private ProfileGenericDataResponseDto makeTestResult(final ProfileGenericDataRequestDto profileGenericDataRequestDto) {
        final ObisCodeValuesDto logicalName = profileGenericDataRequestDto.getObisCode();
        final List<CaptureObjectItemDto> captureObjects = new ArrayList<CaptureObjectItemDto>();
        captureObjects.add(new CaptureObjectItemDto(new CaptureObjectDto(7L, logicalName, 3, 0, "kwu")));
        final List<ProfileEntryItemDto> profileEntries = new ArrayList<ProfileEntryItemDto>();
        profileEntries.add(this.makeProfileEntryItemDto());
        profileEntries.add(this.makeProfileEntryItemDto());
        return new ProfileGenericDataResponseDto(logicalName, captureObjects, profileEntries);
    }

    private ProfileEntryItemDto makeProfileEntryItemDto() {
        List<ProfileEntryDto> entriesDto = new ArrayList<ProfileEntryDto>();
        entriesDto.add(new ProfileEntryDto("test"));
        entriesDto.add(new ProfileEntryDto(BigDecimal.valueOf(10.5)));
        entriesDto.add(new ProfileEntryDto(205L));
        entriesDto.add(new ProfileEntryDto(new Date()));
        return new ProfileEntryItemDto(entriesDto);
    }

    private ObisCode makeObisCode(final ProfileGenericDataRequestDto profileGenericDataRequestDto) {
        final ObisCodeValuesDto obisCodeValues = profileGenericDataRequestDto.getObisCode();
        final byte[] obisCodeBytes = { obisCodeValues.getA(), obisCodeValues.getB(), obisCodeValues.getC(),
                obisCodeValues.getD(), obisCodeValues.getE(), obisCodeValues.getF() };
        return new ObisCode(obisCodeBytes);
    }

    // -------->

    private CaptureObjectDto processNextProfileGenericData(final PeriodTypeDto periodType,
            final DateTime beginDateTime, final DateTime endDateTime, final List<DataObject> bufferedObjects,
            final List<GetResult> results) throws ProtocolAdapterException, BufferedDateTimeValidationException {

        final CosemDateTimeDto cosemDateTime = this.dlmsHelperService.readDateTime(bufferedObjects.get(1),
                "Clock from " + periodType + " buffer");
        final DateTime bufferedDateTime = cosemDateTime == null ? null : cosemDateTime.asDateTime();

        this.validateBufferedDateTime(bufferedDateTime, cosemDateTime, beginDateTime, endDateTime);

        LOGGER.debug("Processing profile (" + periodType + ") objects captured at: {}", cosemDateTime);

        return this.getNextProfileGenericDataForInterval(bufferedObjects, bufferedDateTime, results);
    }

    private CaptureObjectDto getNextProfileGenericDataForInterval(final List<DataObject> bufferedObjects,
            final DateTime bufferedDateTime, final List<GetResult> results) throws ProtocolAdapterException {

        // final DlmsMeterValueDto positiveActiveEnergy =
        // this.dlmsHelperService.getScaledMeterValue(
        // bufferedObjects.get(BUFFER_INDEX_A_POS),
        // results.get(RESULT_INDEX_IMPORT).getResultData(),
        // "positiveActiveEnergy");

        return null;
    }

}
