/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.CosemDate;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.CosemDateTime.ClockStatus;
import org.openmuc.jdlms.datatypes.CosemTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.BufferedDateTimeValidationException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClockStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObisCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MessageTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TransportServiceTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.WindowElementDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DlmsHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsHelper.class);

    private static final Map<Integer, TransportServiceTypeDto> TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE = new TreeMap<>();

    static {
        TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.put(0, TransportServiceTypeDto.TCP);
        TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.put(1, TransportServiceTypeDto.UDP);
        TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.put(2, TransportServiceTypeDto.FTP);
        TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.put(3, TransportServiceTypeDto.SMTP);
        TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.put(4, TransportServiceTypeDto.SMS);
        TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.put(5, TransportServiceTypeDto.HDLC);
        TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.put(6, TransportServiceTypeDto.M_BUS);
        TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.put(7, TransportServiceTypeDto.ZIG_BEE);
    }

    public static final int MILLISECONDS_PER_MINUTE = 60000;

    private DlmsHelper() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Gets a single result from a meter, and returns the result data if
     * retrieval was successful (resultCode of the GetResult equals
     * AccessResultCode.SUCCESS).
     *
     * @return a result from trying to retrieve the value for the attribute
     *         identified by {@code attributeAddress}.
     */
    public static DataObject getAttributeValue(final DlmsConnectionManager conn,
            final AttributeAddress attributeAddress) throws FunctionalException {
        Objects.requireNonNull(conn, "conn must not be null");
        Objects.requireNonNull(attributeAddress, "attributeAddress must not be null");
        try {
            final GetResult getResult = conn.getConnection().get(attributeAddress);
            final AccessResultCode resultCode = getResult.getResultCode();
            if (AccessResultCode.SUCCESS == resultCode) {
                return getResult.getResultData();
            }

            final String errorMessage = String
                    .format("Retrieving attribute value for { %d, %s, %d }. Result: resultCode(%d), with data: %s",
                            attributeAddress.getClassId(), attributeAddress.getInstanceId().asShortObisCodeString(),
                            attributeAddress.getId(), resultCode.getCode(), getDebugInfo(getResult.getResultData()));

            LOGGER.error(errorMessage);
            throw new FunctionalException(FunctionalExceptionType.ERROR_RETRIEVING_ATTRIBUTE_VALUE,
                    ComponentType.PROTOCOL_DLMS, new OsgpException(ComponentType.PROTOCOL_DLMS, errorMessage));

        } catch (final IOException e) {
            throw new ConnectionException(e);
        }
    }

    /**
     * get results from the meter and check if the number of results equals the
     * number of attribute addresses provided.
     */
    public static List<GetResult> getAndCheck(final DlmsConnectionManager conn, final DlmsDevice device,
            final String description, final AttributeAddress... params) throws ProtocolAdapterException {
        final List<GetResult> getResults = getWithList(conn, device, params);
        checkResultList(getResults, params.length, description);
        return getResults;
    }

    /**
     * Check if the number of result matches the number of expected results,
     * when there is only one result the {@link AccessResultCode} of that result
     * is checked.
     *
     * @param getResultList
     *         the list of results to be checked, when null a
     *         nullpointerexception is thrown
     * @param expectedResults
     *         the number of results expected
     * @param description
     *         a description that will be used in exceptions thrown, may be
     *         null
     *
     * @throws ProtocolAdapterException
     *         when the number of results does not match the expected number
     *         or when the one and only result is erroneous.
     */
    public static void checkResultList(final List<GetResult> getResultList, final int expectedResults,
            final String description) throws ProtocolAdapterException {
        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No GetResult received: " + description);
        } else if (getResultList.size() == 1 && AccessResultCode.SUCCESS != getResultList.get(0).getResultCode()) {
            throw new ProtocolAdapterException(getResultList.get(0).getResultCode().name());
        }

        if (getResultList.size() != expectedResults) {
            throw new ProtocolAdapterException(
                    "Expected " + expectedResults + " GetResults: " + description + ", got " + getResultList.size());
        }
    }

    public static List<GetResult> getWithList(final DlmsConnectionManager conn, final DlmsDevice device,
            final AttributeAddress... params) throws ProtocolAdapterException {
        try {
            if (device.isWithListSupported()) {
                return conn.getConnection().get(Arrays.asList(params));
            } else {
                return getWithListWorkaround(conn, params);
            }
        } catch (final IOException e) {
            throw new ConnectionException(e);
        } catch (final Exception e) {
            throw new ProtocolAdapterException("Error retrieving values with-list.", e);
        }
    }

    public static DataObject getClockDefinition() {
        return DataObjectDefinitions.getClockDefinition();
    }

    public static DataObject getAccessSelectionTimeRangeParameter(final DateTime from, final DateTime to,
            final DataObject selectedValues) {

        /*
         * Define the clock object {8,0-0:1.0.0.255,2,0} to be used as
         * restricting object in a range descriptor with a from value and to
         * value to determine which elements from the buffered array should be
         * retrieved.
         */
        final DataObject clockDefinition = getClockDefinition();

        final DataObject fromValue = asDataObject(from);
        final DataObject toValue = asDataObject(to);

        return DataObject.newStructureData(Arrays.asList(clockDefinition, fromValue, toValue, selectedValues));
    }

    /**
     * create a dlms meter value, apply the scaler and determine the unit on the
     * meter.
     *
     * @return the meter value with dlms unit or null when
     *         {@link #readLong(GetResult, String)} is null
     */
    public static DlmsMeterValueDto getScaledMeterValue(final GetResult value, final GetResult scalerUnit,
            final String description) throws ProtocolAdapterException {
        return getScaledMeterValue(value.getResultData(), scalerUnit.getResultData(), description);
    }

    public static DlmsMeterValueDto getScaledMeterValue(final DataObject value, final DataObject scalerUnitObject,
            final String description) throws ProtocolAdapterException {
        LOGGER.debug(getDebugInfo(value));
        LOGGER.debug(getDebugInfo(scalerUnitObject));
        final Long rawValue = readLong(value, description);
        if (rawValue == null) {
            return null;
        }

        if (!scalerUnitObject.isComplex()) {
            throw new ProtocolAdapterException(
                    "complex data (structure) expected while retrieving scaler and unit." + getDebugInfo(
                            scalerUnitObject));
        }
        final List<DataObject> dataObjects = scalerUnitObject.getValue();
        if (dataObjects.size() != 2) {
            throw new ProtocolAdapterException(
                    "expected 2 values while retrieving scaler and unit." + getDebugInfo(scalerUnitObject));
        }
        final int scaler = readLongNotNull(dataObjects.get(0), description).intValue();
        final DlmsUnitTypeDto unit = DlmsUnitTypeDto
                .getUnitType(readLongNotNull(dataObjects.get(1), description).intValue());

        // determine value
        BigDecimal scaledValue = BigDecimal.valueOf(rawValue);
        if (scaler != 0) {
            scaledValue = scaledValue.multiply(BigDecimal.valueOf(Math.pow(10, scaler)));
        }

        return new DlmsMeterValueDto(scaledValue, unit);
    }

    public static DataObject getAMRProfileDefinition() {
        return DataObjectDefinitions.getAMRProfileDefinition();
    }

    /**
     * Workaround method mimicking a Get-Request with-list for devices that do
     * not support the actual functionality from DLMS.
     *
     * @see #getWithList(DlmsConnectionManager, DlmsDevice, AttributeAddress...)
     */
    private static List<GetResult> getWithListWorkaround(final DlmsConnectionManager conn,
            final AttributeAddress... params) throws IOException {
        final List<GetResult> getResultList = new ArrayList<>();
        for (final AttributeAddress param : params) {
            getResultList.add(conn.getConnection().get(param));
        }
        return getResultList;
    }

    private static void checkResultCode(final GetResult getResult, final String description)
            throws ProtocolAdapterException {
        final AccessResultCode resultCode = getResult.getResultCode();
        LOGGER.debug("{} - AccessResultCode: {}", description, resultCode);
        if (resultCode != AccessResultCode.SUCCESS) {
            throw new ProtocolAdapterException(
                    "No success retrieving " + description + ": AccessResultCode = " + resultCode);
        }
    }

    public static Long readLong(final GetResult getResult, final String description) throws ProtocolAdapterException {
        checkResultCode(getResult, description);
        return readLong(getResult.getResultData(), description);
    }

    public static Long readLong(final DataObject resultData, final String description) throws ProtocolAdapterException {
        final Number number = readNumber(resultData, description);
        if (number == null) {
            return null;
        }
        return number.longValue();
    }

    public static Long readLongNotNull(final GetResult getResult, final String description)
            throws ProtocolAdapterException {
        checkResultCode(getResult, description);
        return readLongNotNull(getResult.getResultData(), description);
    }

    public static Long readLongNotNull(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        final Long result = readLong(resultData, description);
        if (result == null) {
            throw new ProtocolAdapterException(String.format("Unexpected null value for %s,", description));
        }
        return result;
    }

    public static Integer readInteger(final GetResult getResult, final String description)
            throws ProtocolAdapterException {
        checkResultCode(getResult, description);
        final Long value = readLong(getResult.getResultData(), description);
        return (value == null) ? null : value.intValue();
    }

    public static Short readShort(final GetResult getResult, final String description) throws ProtocolAdapterException {
        checkResultCode(getResult, description);
        final Long value = readLong(getResult.getResultData(), description);
        return (value == null) ? null : value.shortValue();
    }

    public static DataObject readDataObject(final GetResult getResult, final String description)
            throws ProtocolAdapterException {
        checkResultCode(getResult, description);
        return getResult.getResultData();
    }

    public static String readString(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        final byte[] bytes = readByteArray(resultData, description, "String");
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static CosemDateTimeDto readDateTime(final GetResult getResult, final String description)
            throws ProtocolAdapterException {
        checkResultCode(getResult, description);
        return readDateTime(getResult.getResultData(), description);
    }

    public static CosemDateTimeDto readDateTime(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        logDebugResultData(resultData, description);
        if (resultData == null || resultData.isNull()) {
            return null;
        }
        if (resultData.isByteArray()) {
            return fromDateTimeValue(resultData.getValue());
        } else if (resultData.isCosemDateFormat()) {
            final CosemDateTime cosemDateTime = resultData.getValue();
            return fromDateTimeValue(cosemDateTime.encode());
        } else {
            LOGGER.error("Unexpected ResultData for DateTime value: {}", getDebugInfo(resultData));
            throw new ProtocolAdapterException(
                    "Expected ResultData of ByteArray or CosemDateFormat, got: " + resultData.getType());
        }
    }

    public static CosemDateTimeDto convertDataObjectToDateTime(final DataObject object)
            throws ProtocolAdapterException {
        CosemDateTimeDto dateTime = null;
        if (object.isByteArray()) {
            dateTime = fromDateTimeValue(object.getValue());
        } else if (object.isCosemDateFormat()) {
            final CosemDateTime cosemDateTime = object.getValue();
            dateTime = fromDateTimeValue(cosemDateTime.encode());
        } else {
            logAndThrowExceptionForUnexpectedResultData(object, "ByteArray or CosemDateFormat");
        }
        return dateTime;
    }

    public static CosemDateTimeDto fromDateTimeValue(final byte[] dateTimeValue) {

        final ByteBuffer bb = ByteBuffer.wrap(dateTimeValue);

        final int year = bb.getShort() & 0xFFFF;
        final int monthOfYear = bb.get() & 0xFF;
        final int dayOfMonth = bb.get() & 0xFF;
        final int dayOfWeek = bb.get() & 0xFF;
        final int hourOfDay = bb.get() & 0xFF;
        final int minuteOfHour = bb.get() & 0xFF;
        final int secondOfMinute = bb.get() & 0xFF;
        final int hundredthsOfSecond = bb.get() & 0xFF;
        final int deviation = bb.getShort();
        final byte clockStatusValue = bb.get();

        final CosemDateDto date = new CosemDateDto(year, monthOfYear, dayOfMonth, dayOfWeek);
        final CosemTimeDto time = new CosemTimeDto(hourOfDay, minuteOfHour, secondOfMinute, hundredthsOfSecond);
        final ClockStatusDto clockStatus = new ClockStatusDto(clockStatusValue);
        return new CosemDateTimeDto(date, time, deviation, clockStatus);
    }

    /**
     * Creates a COSEM date-time object based on the given {@code dateTime}.
     * <p>
     * The deviation and clock status (is daylight saving active or not) are
     * based on the zone of the given {@code dateTime}.
     * <p>
     * To use a DateTime as indication of the instant of time to be used with a
     * specific deviation (that does not have to match the zone of the
     * DateTime), use {@link #asDataObject(DateTime, int, boolean)} instead.
     *
     * @param dateTime
     *         a DateTime to translate into COSEM date-time format.
     *
     * @return a DataObject having a CosemDateTime matching the given DateTime
     *         as value.
     */
    public static DataObject asDataObject(final DateTime dateTime) {

        final CosemDate cosemDate = new CosemDate(dateTime.getYear(), dateTime.getMonthOfYear(),
                dateTime.getDayOfMonth());
        final CosemTime cosemTime = new CosemTime(dateTime.getHourOfDay(), dateTime.getMinuteOfHour(),
                dateTime.getSecondOfMinute(), dateTime.getMillisOfSecond() / 10);
        final int deviation = -(dateTime.getZone().getOffset(dateTime.getMillis()) / MILLISECONDS_PER_MINUTE);
        final ClockStatus[] clockStatusBits;
        if (dateTime.getZone().isStandardOffset(dateTime.getMillis())) {
            clockStatusBits = new ClockStatus[0];
        } else {
            clockStatusBits = new ClockStatus[1];
            clockStatusBits[0] = ClockStatus.DAYLIGHT_SAVING_ACTIVE;
        }
        final CosemDateTime cosemDateTime = new CosemDateTime(cosemDate, cosemTime, deviation, clockStatusBits);
        return DataObject.newDateTimeData(cosemDateTime);
    }

    /**
     * Creates a COSEM date-time object based on the given {@code dateTime}.
     * This COSEM date-time will be for the same instant in time as the given
     * {@code dateTime} but may be for another time zone.
     * <p>
     * Because the time zone with the {@code deviation} may be different than
     * the one with the {@code dateTime}, and the {@code deviation} alone does
     * not provide sufficient information on whether daylight savings is active
     * for the given instant in time, {@code dst} has to be provided to indicate
     * whether daylight savings are active.
     * <p>
     * If a DateTime for an instant in time is known with the correct time zone
     * set, you can use {@link #asDataObject(DateTime)} as a simpler
     * alternative.
     *
     * @param dateTime
     *         a DateTime indicating an instant in time to be used for the
     *         COSEM date-time.
     * @param deviation
     *         the deviation in minutes of local time to GMT to be included
     *         in the COSEM date-time.
     * @param dst
     *         {@code true} if daylight savings are active for the instant of
     *         the COSEM date-time, otherwise {@code false}.
     *
     * @return a DataObject having a CosemDateTime for the instant of the given
     *         DateTime, with the given deviation and DST status information, as
     *         value.
     */
    public static DataObject asDataObject(final DateTime dateTime, final int deviation, final boolean dst) {
        /*
         * Create a date time that may not point to the right instant in time,
         * but that will give proper values getting the different fields for the
         * COSEM date and time objects.
         */
        final DateTime dateTimeWithOffset = dateTime.toDateTime(DateTimeZone.UTC).minusMinutes(deviation);
        final CosemDate cosemDate = new CosemDate(dateTimeWithOffset.getYear(), dateTimeWithOffset.getMonthOfYear(),
                dateTimeWithOffset.getDayOfMonth());
        final CosemTime cosemTime = new CosemTime(dateTimeWithOffset.getHourOfDay(),
                dateTimeWithOffset.getMinuteOfHour(), dateTimeWithOffset.getSecondOfMinute(),
                dateTimeWithOffset.getMillisOfSecond() / 10);
        final ClockStatus[] clockStatusBits;

        if (dst) {
            clockStatusBits = new ClockStatus[1];
            clockStatusBits[0] = ClockStatus.DAYLIGHT_SAVING_ACTIVE;
        } else {
            clockStatusBits = new ClockStatus[0];
        }
        final CosemDateTime cosemDateTime = new CosemDateTime(cosemDate, cosemTime, deviation, clockStatusBits);
        return DataObject.newDateTimeData(cosemDateTime);
    }

    public static DataObject asDataObject(final CosemDateDto date) {

        final CosemDate cosemDate = new CosemDate(date.getYear(), date.getMonth(), date.getDayOfMonth(),
                date.getDayOfWeek());
        return DataObject.newDateData(cosemDate);
    }

    public static List<CosemObjectDefinitionDto> readListOfObjectDefinition(final GetResult getResult,
            final String description) throws ProtocolAdapterException {
        checkResultCode(getResult, description);
        return readListOfObjectDefinition(getResult.getResultData(), description);
    }

    public static List<CosemObjectDefinitionDto> readListOfObjectDefinition(final DataObject resultData,
            final String description) throws ProtocolAdapterException {
        final List<DataObject> listOfObjectDefinition = readList(resultData, description);
        if (listOfObjectDefinition == null) {
            return Collections.emptyList();
        }
        final List<CosemObjectDefinitionDto> objectDefinitionList = new ArrayList<>();
        for (final DataObject objectDefinitionObject : listOfObjectDefinition) {
            objectDefinitionList
                    .add(readObjectDefinition(objectDefinitionObject, "Object Definition from " + description));
        }
        return objectDefinitionList;
    }

    public static CosemObjectDefinitionDto readObjectDefinition(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        final List<DataObject> objectDefinitionElements = readList(resultData, description);
        if (objectDefinitionElements == null) {
            return null;
        }
        if (objectDefinitionElements.size() != 4) {
            LOGGER.error("Unexpected ResultData for Object Definition value: {}", getDebugInfo(resultData));
            throw new ProtocolAdapterException(
                    "Expected list for Object Definition to contain 4 elements, got: " + objectDefinitionElements
                            .size());
        }
        final Long classId = readLongNotNull(objectDefinitionElements.get(0), "Class ID from " + description);
        final CosemObisCodeDto logicalName = DlmsHelper
                .readLogicalName(objectDefinitionElements.get(1), "Logical Name from " + description);
        final Long attributeIndex = DlmsHelper
                .readLongNotNull(objectDefinitionElements.get(2), "Attribute Index from " + description);
        final Long dataIndex = readLongNotNull(objectDefinitionElements.get(3), "Data Index from " + description);

        return new CosemObjectDefinitionDto(classId.intValue(), logicalName, attributeIndex.intValue(),
                dataIndex.intValue());
    }

    public static CosemObisCodeDto readLogicalName(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        final byte[] bytes = readByteArray(resultData, description, "Logical Name");
        return new CosemObisCodeDto(bytes);
    }

    public static SendDestinationAndMethodDto readSendDestinationAndMethod(final GetResult getResult,
            final String description) throws ProtocolAdapterException {
        checkResultCode(getResult, description);
        return readSendDestinationAndMethod(getResult.getResultData(), description);
    }

    public static SendDestinationAndMethodDto readSendDestinationAndMethod(final DataObject resultData,
            final String description) throws ProtocolAdapterException {
        final List<DataObject> sendDestinationAndMethodElements = readList(resultData, description);
        if (sendDestinationAndMethodElements == null) {
            return null;
        }
        final TransportServiceTypeDto transportService = DlmsHelper
                .readTransportServiceType(sendDestinationAndMethodElements.get(0),
                        "Transport Service from " + description);
        final String destination = DlmsHelper
                .readString(sendDestinationAndMethodElements.get(1), "Destination from " + description);
        final MessageTypeDto message = DlmsHelper
                .readMessageType(sendDestinationAndMethodElements.get(2), "Message from " + description);

        return new SendDestinationAndMethodDto(transportService, destination, message);
    }

    public static TransportServiceTypeDto readTransportServiceType(final DataObject resultData,
            final String description) throws ProtocolAdapterException {
        final Number number = readNumber(resultData, description, "Enum");
        if (number == null) {
            return null;
        }
        final int enumValue = number.intValue();
        final TransportServiceTypeDto transportService = getTransportServiceTypeForEnumValue(enumValue);
        if (transportService == null) {
            LOGGER.error("Unexpected Enum value for TransportServiceType: {}", enumValue);
            throw new ProtocolAdapterException("Unknown Enum value for TransportServiceType: " + enumValue);
        }
        return transportService;
    }

    private static TransportServiceTypeDto getTransportServiceTypeForEnumValue(final int enumValue) {
        if ((enumValue >= 200) && (enumValue <= 255)) {
            return TransportServiceTypeDto.MANUFACTURER_SPECIFIC;
        }
        return TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.get(enumValue);
    }

    public static MessageTypeDto readMessageType(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        final Number number = readNumber(resultData, description, "Enum");
        if (number == null) {
            return null;
        }
        final MessageTypeDto message;
        final int enumValue = number.intValue();
        switch (enumValue) {
        case 0:
            message = MessageTypeDto.A_XDR_ENCODED_X_DLMS_APDU;
            break;
        case 1:
            message = MessageTypeDto.XML_ENCODED_X_DLMS_APDU;
            break;
        default:
            if (enumValue < 128 || enumValue > 255) {
                LOGGER.error("Unexpected Enum value for MessageType: {}", enumValue);
                throw new ProtocolAdapterException("Unknown Enum value for MessageType: " + enumValue);
            }
            message = MessageTypeDto.MANUFACTURER_SPECIFIC;
        }
        return message;
    }

    public static List<WindowElementDto> readListOfWindowElement(final GetResult getResult, final String description)
            throws ProtocolAdapterException {
        checkResultCode(getResult, description);
        return readListOfWindowElement(getResult.getResultData(), description);
    }

    public static List<WindowElementDto> readListOfWindowElement(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        final List<DataObject> listOfWindowElement = readList(resultData, description);
        if (listOfWindowElement == null) {
            return Collections.emptyList();
        }
        final List<WindowElementDto> windowElementList = new ArrayList<>();
        for (final DataObject windowElementObject : listOfWindowElement) {
            windowElementList.add(readWindowElement(windowElementObject, "Window Element from " + description));
        }
        return windowElementList;
    }

    public static WindowElementDto readWindowElement(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        final List<DataObject> windowElementElements = readList(resultData, description);
        if (windowElementElements == null) {
            return null;
        }
        return buildWindowElementFromDataObjects(windowElementElements, description);
    }

    private static WindowElementDto buildWindowElementFromDataObjects(final List<DataObject> elements,
            final String description) throws ProtocolAdapterException {
        if (elements.size() != 2) {
            LOGGER.error("Unexpected number of ResultData elements for WindowElement value: {}", elements.size());
            throw new ProtocolAdapterException(
                    "Expected list for WindowElement to contain 2 elements, got: " + elements.size());
        }

        final CosemDateTimeDto startTime = readDateTime(elements.get(0), "Start Time from " + description);
        final CosemDateTimeDto endTime = readDateTime(elements.get(1), "End Time from " + description);

        return new WindowElementDto(startTime, endTime);
    }

    public static String getDebugInfo(final DataObject dataObject) {
        if (dataObject == null) {
            return null;
        }

        final String dataType = getDataType(dataObject);
        final String objectText = getObjectTextForDebugInfo(dataObject);
        final String choiceText = getChoiceTextForDebugInfo(dataObject);
        final String rawValueClass = getRawValueClassForDebugInfo(dataObject);

        return "DataObject: Choice=" + choiceText + ", ResultData is" + dataType + ", value=[" + rawValueClass + "]: "
                + objectText;
    }

    private static String getObjectTextForDebugInfo(final DataObject dataObject) {

        final String objectText;
        if (dataObject.isComplex()) {
            if (dataObject.getValue() instanceof List) {
                final StringBuilder builder = new StringBuilder();
                builder.append("[");
                builder.append(System.lineSeparator());
                appendItemValues(dataObject, builder);
                builder.append("]");
                builder.append(System.lineSeparator());
                objectText = builder.toString();
            } else {
                objectText = String.valueOf(dataObject.getRawValue());
            }
        } else if (dataObject.isByteArray()) {
            objectText = getDebugInfoByteArray(dataObject.getValue());
        } else if (dataObject.isBitString()) {
            final BitString bitString = dataObject.getValue();
            objectText = getDebugInfoBitStringBytes(bitString.getBitString());
        } else if (dataObject.isCosemDateFormat() && dataObject.getValue() instanceof CosemDateTime) {
            final CosemDateTime cosemDateTime = dataObject.getValue();
            objectText = getDebugInfoDateTimeBytes(cosemDateTime.encode());
        } else {
            objectText = String.valueOf(dataObject.getRawValue());
        }

        return objectText;
    }

    private static String getChoiceTextForDebugInfo(final DataObject dataObject) {
        final Type choiceIndex = dataObject.getType();
        if (choiceIndex == null) {
            return "null";
        }
        return choiceIndex.name();
    }

    private static String getRawValueClassForDebugInfo(final DataObject dataObject) {
        final Object rawValue = dataObject.getRawValue();
        if (rawValue == null) {
            return "null";
        }
        return rawValue.getClass().getName();
    }

    private static void appendItemValues(final DataObject dataObject, final StringBuilder builder) {
        for (final Object obj : (List<?>) dataObject.getValue()) {
            builder.append("\t");
            if (obj instanceof DataObject) {
                builder.append(getDebugInfo((DataObject) obj));
            } else {
                builder.append(obj);
            }
            builder.append(System.lineSeparator());
        }
    }

    private static String getDataType(final DataObject dataObject) {
        final String dataType;
        if (dataObject.isBitString()) {
            dataType = "BitString";
        } else if (dataObject.isBoolean()) {
            dataType = "Boolean";
        } else if (dataObject.isByteArray()) {
            dataType = "ByteArray";
        } else if (dataObject.isComplex()) {
            dataType = "Complex";
        } else if (dataObject.isCosemDateFormat()) {
            dataType = "CosemDateFormat";
        } else if (dataObject.isNull()) {
            dataType = "Null";
        } else if (dataObject.isNumber()) {
            dataType = "Number";
        } else {
            dataType = "?";
        }
        return dataType;
    }

    public static String getDebugInfoByteArray(final byte[] bytes) {
        /*
         * The guessing of the object type by byte length may turn out to be
         * ambiguous at some time. If this occurs the debug info will have to be
         * determined in some more robust way. Until now this appears to work OK
         * for debugging purposes.
         */
        if (bytes.length == 6) {
            return getDebugInfoLogicalName(bytes);
        } else if (bytes.length == 12) {
            return getDebugInfoDateTimeBytes(bytes);
        }

        final StringBuilder sb = new StringBuilder();

        // list the unsigned values of the bytes
        for (final byte b : bytes) {
            sb.append(b & 0xFF).append(", ");
        }
        if (sb.length() > 0) {
            // remove the last ", "
            sb.setLength(sb.length() - 2);
        }

        return "bytes[" + sb.toString() + "]";
    }

    public static String getDebugInfoLogicalName(final byte[] logicalNameValue) {

        if (logicalNameValue.length != 6) {
            throw new IllegalArgumentException("LogicalName values should be 6 bytes long: " + logicalNameValue.length);
        }

        return "logical name: " + (logicalNameValue[0] & 0xFF) + '-' + (logicalNameValue[1] & 0xFF) + ':' + (
                logicalNameValue[2] & 0xFF) + '.' + (logicalNameValue[3] & 0xFF) + '.' + (logicalNameValue[4] & 0xFF)
                + '.' + (logicalNameValue[5] & 0xFF);
    }

    public static String getDebugInfoDateTimeBytes(final byte[] dateTimeValue) {

        if (dateTimeValue.length != 12) {
            throw new IllegalArgumentException("DateTime values should be 12 bytes long: " + dateTimeValue.length);
        }

        final StringBuilder sb = new StringBuilder();

        final ByteBuffer bb = ByteBuffer.wrap(dateTimeValue);
        final int year = bb.getShort();
        final int monthOfYear = bb.get();
        final int dayOfMonth = bb.get();
        final int dayOfWeek = bb.get();
        final int hourOfDay = bb.get();
        final int minuteOfHour = bb.get();
        final int secondOfMinute = bb.get();
        final int hundredthsOfSecond = bb.get();
        final int deviation = bb.getShort();
        final int clockStatus = bb.get();

        sb.append("year=").append(year).append(", month=").append(monthOfYear).append(", day=").append(dayOfMonth)
                .append(", weekday=").append(dayOfWeek).append(", hour=").append(hourOfDay).append(", minute=")
                .append(minuteOfHour).append(", second=").append(secondOfMinute).append(", hundredths=")
                .append(hundredthsOfSecond).append(", deviation=").append(deviation).append(", clockstatus=")
                .append(clockStatus);

        return sb.toString();
    }

    public static String getDebugInfoBitStringBytes(final byte[] bitStringValue) {
        if (bitStringValue == null) {
            return null;
        }

        final BigInteger bigValue = byteArrayToBigInteger(bitStringValue);
        final String stringValue = byteArrayToString(bitStringValue);

        return "number of bytes=" + bitStringValue.length + ", value=" + bigValue + ", bits=" + stringValue;
    }

    private static String byteArrayToString(final byte[] bitStringValue) {
        if (bitStringValue == null || bitStringValue.length == 0) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (final byte element : bitStringValue) {
            sb.append(StringUtils.leftPad(Integer.toBinaryString(element & 0xFF), 8, "0"));
            sb.append(" ");
        }
        return sb.toString();
    }

    private static BigInteger byteArrayToBigInteger(final byte[] bitStringValue) {
        if (bitStringValue == null || bitStringValue.length == 0) {
            return null;
        }
        BigInteger value = BigInteger.valueOf(0);
        for (final byte element : bitStringValue) {
            value = value.shiftLeft(8);
            value = value.add(BigInteger.valueOf(element & 0xFF));
        }
        return value;
    }

    private static Number readNumber(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        return readNumber(resultData, description, "Number");
    }

    private static Number readNumber(final DataObject resultData, final String description, final String interpretation)
            throws ProtocolAdapterException {
        logDebugResultData(resultData, description);
        if (resultData == null || resultData.isNull()) {
            return null;
        }
        final Object resultValue = resultData.getValue();
        if (!resultData.isNumber() || !(resultValue instanceof Number)) {
            logAndThrowExceptionForUnexpectedResultData(resultData, interpretation);
        }
        return (Number) resultValue;
    }

    private static byte[] readByteArray(final DataObject resultData, final String description,
            final String interpretation) throws ProtocolAdapterException {
        logDebugResultData(resultData, description);
        if (resultData == null || resultData.isNull()) {
            return new byte[0];
        }
        final Object resultValue = resultData.getValue();
        if (!resultData.isByteArray() || !(resultValue instanceof byte[])) {
            logAndThrowExceptionForUnexpectedResultData(resultData,
                    "byte array to be interpreted as " + interpretation);
        }
        return (byte[]) resultValue;
    }

    @SuppressWarnings("unchecked")
    private static List<DataObject> readList(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        logDebugResultData(resultData, description);
        if (resultData == null || resultData.isNull()) {
            return Collections.emptyList();
        }
        final Object resultValue = resultData.getValue();
        if (!resultData.isComplex() || !(resultValue instanceof List)) {
            logAndThrowExceptionForUnexpectedResultData(resultData, "List");
        }
        return (List<DataObject>) resultValue;
    }

    private static void logDebugResultData(final DataObject resultData, final String description) {
        LOGGER.debug("{} - ResultData: {}", description, getDebugInfo(resultData));
    }

    private static void logAndThrowExceptionForUnexpectedResultData(final DataObject resultData,
            final String expectedType) throws ProtocolAdapterException {
        LOGGER.error("Unexpected ResultData for {} value: {}", expectedType, getDebugInfo(resultData));
        final String resultDataType =
                resultData.getValue() == null ? "null" : resultData.getValue().getClass().getName();
        throw new ProtocolAdapterException(
                "Expected ResultData of " + expectedType + ", got: " + resultData.getType() + ", value type: "
                        + resultDataType);
    }

    public static void validateBufferedDateTime(final DateTime bufferedDateTime, final CosemDateTimeDto cosemDateTime,
            final DateTime beginDateTime, final DateTime endDateTime) throws BufferedDateTimeValidationException {

        if (bufferedDateTime == null) {
            final DateTimeFormatter dtf = ISODateTimeFormat.dateTime();
            throw new BufferedDateTimeValidationException(
                    "Not using an object from capture buffer (clock=" + cosemDateTime
                            + "), because the date does not match the given period, since it is not fully specified: ["
                            + dtf.print(beginDateTime) + " .. " + dtf.print(endDateTime) + "].");
        }
        if (bufferedDateTime.isBefore(beginDateTime) || bufferedDateTime.isAfter(endDateTime)) {
            final DateTimeFormatter dtf = ISODateTimeFormat.dateTime();
            throw new BufferedDateTimeValidationException(
                    "Not using an object from capture buffer (clock=" + dtf.print(bufferedDateTime)
                            + "), because the date does not match the given period: [" + dtf.print(beginDateTime)
                            + " .. " + dtf.print(endDateTime) + "].");
        }
    }
}
