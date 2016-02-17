/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.CosemDate;
import org.openmuc.jdlms.datatypes.CosemDateFormat;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.CosemDateTime.ClockStatus;
import org.openmuc.jdlms.datatypes.CosemTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.internal.asn1.cosem.Data.Choices;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCode;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObjectDefinition;
import com.alliander.osgp.dto.valueobjects.smartmetering.MessageType;
import com.alliander.osgp.dto.valueobjects.smartmetering.SendDestinationAndMethod;
import com.alliander.osgp.dto.valueobjects.smartmetering.TransportServiceType;
import com.alliander.osgp.dto.valueobjects.smartmetering.WindowElement;

@Service(value = "dlmsHelperService")
public class DlmsHelperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsHelperService.class);

    private static final Map<Integer, TransportServiceType> TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE = new TreeMap<>();

    static {
        TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.put(0, TransportServiceType.TCP);
        TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.put(1, TransportServiceType.UDP);
        TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.put(2, TransportServiceType.FTP);
        TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.put(3, TransportServiceType.SMTP);
        TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.put(4, TransportServiceType.SMS);
        TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.put(5, TransportServiceType.HDLC);
        TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.put(6, TransportServiceType.M_BUS);
        TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.put(7, TransportServiceType.ZIG_BEE);
    }

    private static final String YEAR_MILLENIAL_PART = "20";
    private static final String LAST_DAY_OF_MONTH = "FE";
    private static final String SECOND_LAST_DAY_OF_MONTH = "FD";
    private static final String DAYLIGHT_SAVINGS_BEGIN = "FE";
    private static final String DAYLIGHT_SAVINGS_END = "FD";
    private static final String NOT_SPECIFIED = "FF";
    public static final int MILLISECONDS_PER_MINUTE = 60000;

    public List<GetResult> getWithList(final LnClientConnection conn, final DlmsDevice device,
            final AttributeAddress... params) throws ProtocolAdapterException {
        try {
            if (device.isWithListSupported()) {
                return conn.get(params);
            } else {
                return this.getWithListWorkaround(conn, params);
            }
        } catch (final Exception e) {
            throw new ProtocolAdapterException("Error retrieving values with-list.", e);
        }
    }

    public DataObject getClockDefinition() {
        return DataObjectDefinitions.getClockDefinition();
    }

    public DataObject getAMRProfileDefinition() {
        return DataObjectDefinitions.getAMRProfileDefinition();
    }

    /**
     * Workaround method mimicking a Get-Request with-list for devices that do
     * not support the actual functionality from DLMS.
     *
     * @throws IOException
     * @throws TimeoutException
     *
     * @see #getWithList(LnClientConnection, DlmsDevice, AttributeAddress...)
     */
    private List<GetResult> getWithListWorkaround(final LnClientConnection conn, final AttributeAddress... params)
            throws IOException, TimeoutException {
        final List<GetResult> getResultList = new ArrayList<>();
        for (final AttributeAddress param : params) {
            final List<GetResult> getResultListForParam = conn.get(param);
            if (getResultListForParam.size() != 1) {
                throw new AssertionError("GetResult list contains " + getResultListForParam.size()
                        + " elements instead of 1");
            }
            getResultList.add(getResultListForParam.get(0));
        }
        return getResultList;
    }

    private void checkResultCode(final GetResult getResult, final String description) throws ProtocolAdapterException {
        final AccessResultCode resultCode = getResult.resultCode();
        LOGGER.debug(description + " - AccessResultCode: {}", resultCode);
        if (resultCode != AccessResultCode.SUCCESS) {
            throw new ProtocolAdapterException("No success retrieving " + description + ": AccessResultCode = "
                    + resultCode);
        }
    }

    public Long readLong(final GetResult getResult, final String description) throws ProtocolAdapterException {
        this.checkResultCode(getResult, description);
        return this.readLong(getResult.resultData(), description);
    }

    public Long readLongNotNull(final GetResult getResult, final String description) throws ProtocolAdapterException {
        this.checkResultCode(getResult, description);
        return this.readLongNotNull(getResult.resultData(), description);
    }

    public Long readLongNotNull(final DataObject resultData, final String description) throws ProtocolAdapterException {
        final Long result = this.readLong(resultData, description);
        if (result == null) {
            throw new ProtocolAdapterException(String.format("Unexpected null value for %s,", description));
        }
        return result;
    }

    public DataObject readDataObject(final GetResult getResult, final String description)
            throws ProtocolAdapterException {
        this.checkResultCode(getResult, description);
        return getResult.resultData();
    }

    public Long readLong(final DataObject resultData, final String description) throws ProtocolAdapterException {
        final Number number = this.readNumber(resultData, description);
        if (number == null) {
            return null;
        }
        return number.longValue();
    }

    public String readString(final DataObject resultData, final String description) throws ProtocolAdapterException {
        final byte[] bytes = this.readByteArray(resultData, description, "String");
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime readDateTime(final GetResult getResult,
            final String description) throws ProtocolAdapterException {
        this.checkResultCode(getResult, description);
        return this.readDateTime(getResult.resultData(), description);
    }

    public com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime readDateTime(final DataObject resultData,
            final String description) throws ProtocolAdapterException {
        this.logDebugResultData(resultData, description);
        if (resultData == null || resultData.isNull()) {
            return null;
        }
        if (resultData.isByteArray()) {
            return this.fromDateTimeValue((byte[]) resultData.value());
        } else if (resultData.isCosemDateFormat()) {
            return this.fromDateTimeValue(((CosemDateTime) resultData.value()).encode());
        } else {
            LOGGER.error("Unexpected ResultData for DateTime value: {}", this.getDebugInfo(resultData));
            throw new ProtocolAdapterException("Expected ResultData of ByteArray or CosemDateFormat, got: "
                    + resultData.choiceIndex());
        }
    }

    public com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime readCosemDateTime(
            final DataObject resultData, final String description) throws ProtocolAdapterException {
        this.logDebugResultData(resultData, description);
        if (resultData == null || resultData.isNull()) {
            return null;
        }
        CosemDateTime jdlmsCosemDateTime = null;
        if (resultData.isByteArray()) {
            jdlmsCosemDateTime = CosemDateTime.decode((byte[]) resultData.value());
        } else if (resultData.isCosemDateFormat()) {
            jdlmsCosemDateTime = (CosemDateTime) resultData.value();
        } else {
            this.logAndThrowExceptionForUnexpectedResultData(resultData, "ByteArray or CosemDateFormat");
        }
        return this.getDtoDateTimeForJdlmsDateTime(jdlmsCosemDateTime);
    }

    private com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime getDtoDateTimeForJdlmsDateTime(
            final CosemDateTime jdlmsCosemDateTime) {
        if (jdlmsCosemDateTime == null) {
            return null;
        }

        final int year = jdlmsCosemDateTime.valueFor(CosemDateFormat.Field.YEAR);
        final int month = jdlmsCosemDateTime.valueFor(CosemDateFormat.Field.MONTH);
        final int dayOfMonth = jdlmsCosemDateTime.valueFor(CosemDateFormat.Field.DAY_OF_MONTH);
        final int dayOfWeek = jdlmsCosemDateTime.valueFor(CosemDateFormat.Field.DAY_OF_WEEK);
        final com.alliander.osgp.dto.valueobjects.smartmetering.CosemDate date = new com.alliander.osgp.dto.valueobjects.smartmetering.CosemDate(
                year, month, dayOfMonth, dayOfWeek);

        final int hour = jdlmsCosemDateTime.valueFor(CosemDateFormat.Field.HOUR);
        final int minute = jdlmsCosemDateTime.valueFor(CosemDateFormat.Field.MINUTE);
        final int second = jdlmsCosemDateTime.valueFor(CosemDateFormat.Field.SECOND);
        final int hundredths = jdlmsCosemDateTime.valueFor(CosemDateFormat.Field.HUNDREDTHS);
        final com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime time = new com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime(
                hour, minute, second, hundredths);

        final int deviation = jdlmsCosemDateTime.valueFor(CosemDateFormat.Field.DEVIATION);

        final int clockStatusValue = jdlmsCosemDateTime.valueFor(CosemDateFormat.Field.CLOCK_STATUS);
        final com.alliander.osgp.dto.valueobjects.smartmetering.ClockStatus clockStatus = new com.alliander.osgp.dto.valueobjects.smartmetering.ClockStatus(
                clockStatusValue);

        return new com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime(date, time, deviation, clockStatus);
    }

    public com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime convertDataObjectToDateTime(
            final DataObject object) throws ProtocolAdapterException {
        com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime dateTime = null;
        if (object.isByteArray()) {
            dateTime = this.fromDateTimeValue((byte[]) object.value());
        } else if (object.isCosemDateFormat()) {
            dateTime = this.fromDateTimeValue(((CosemDateTime) object.value()).encode());
        } else {
            this.logAndThrowExceptionForUnexpectedResultData(object, "ByteArray or CosemDateFormat");
        }
        return dateTime;
    }

    public com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime fromDateTimeValue(final byte[] dateTimeValue)
            throws ProtocolAdapterException {

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

        final com.alliander.osgp.dto.valueobjects.smartmetering.CosemDate date = new com.alliander.osgp.dto.valueobjects.smartmetering.CosemDate(
                year, monthOfYear, dayOfMonth, dayOfWeek);
        final com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime time = new com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime(
                hourOfDay, minuteOfHour, secondOfMinute, hundredthsOfSecond);
        final com.alliander.osgp.dto.valueobjects.smartmetering.ClockStatus clockStatus = new com.alliander.osgp.dto.valueobjects.smartmetering.ClockStatus(
                clockStatusValue);
        return new com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime(date, time, deviation, clockStatus);
    }

    public DataObject dateAsDataObjectOctetString(final DateTime dateTime) {

        final Integer h = dateTime.getHourOfDay();
        final Integer m = dateTime.getMinuteOfHour();
        final Integer s = dateTime.getSecondOfMinute();

        final byte[] ba = new byte[] { h.byteValue(), m.byteValue(), s.byteValue(), (byte) 0 };
        return DataObject.newOctetStringData(ba);
    }

    public DataObject asDataObject(final DateTime dateTime) {

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
     * The format of the date string is YYMMDD and if the year is unspecified
     * the year positions should hold "FF" as value Also as the date string only
     * holds the decade part of the year, the conversion uses the constant "20"
     * as the centenial/millenial part of the year
     *
     * @param date
     *            the date as String object
     * @return DateObject as OctetString
     */
    public DataObject dateStringToOctetString(final String date) {

        final ByteBuffer bb = ByteBuffer.allocate(5);

        final String year = date.substring(0, 2);
        if (NOT_SPECIFIED.equalsIgnoreCase(year)) {
            bb.putShort((short) 0xFFFF);
        } else {
            bb.putShort(Short.valueOf(YEAR_MILLENIAL_PART + year));
        }

        final String month = date.substring(2, 4);
        if (NOT_SPECIFIED.equalsIgnoreCase(month)) {
            bb.put((byte) 0xFF);
        } else if (DAYLIGHT_SAVINGS_END.equalsIgnoreCase(month)) {
            bb.put((byte) 0xFD);
        } else if (DAYLIGHT_SAVINGS_BEGIN.equalsIgnoreCase(month)) {
            bb.put((byte) 0xFD);
        } else {
            bb.put(Byte.parseByte(month));
        }

        final String dayOfMonth = date.substring(4);
        if (NOT_SPECIFIED.equalsIgnoreCase(dayOfMonth)) {
            bb.put((byte) 0xFF);
        } else if (SECOND_LAST_DAY_OF_MONTH.equalsIgnoreCase(month)) {
            bb.put((byte) 0xFD);
        } else if (LAST_DAY_OF_MONTH.equalsIgnoreCase(month)) {
            bb.put((byte) 0xFE);
        } else {
            bb.put(Byte.parseByte(dayOfMonth));
        }

        // leave day of week unspecified (0xFF)
        bb.put((byte) 0xFF);

        return DataObject.newOctetStringData(bb.array());
    }

    public List<CosemObjectDefinition> readListOfObjectDefinition(final GetResult getResult, final String description)
            throws ProtocolAdapterException {
        this.checkResultCode(getResult, description);
        return this.readListOfObjectDefinition(getResult.resultData(), description);
    }

    public List<CosemObjectDefinition> readListOfObjectDefinition(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        final List<DataObject> listOfObjectDefinition = this.readList(resultData, description);
        if (listOfObjectDefinition == null) {
            return null;
        }
        final List<CosemObjectDefinition> objectDefinitionList = new ArrayList<>();
        for (final DataObject objectDefinitionObject : listOfObjectDefinition) {
            objectDefinitionList.add(this.readObjectDefinition(objectDefinitionObject, "Object Definition from "
                    + description));
        }
        return objectDefinitionList;
    }

    public CosemObjectDefinition readObjectDefinition(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        final List<DataObject> objectDefinitionElements = this.readList(resultData, description);
        if (objectDefinitionElements == null) {
            return null;
        }
        if (objectDefinitionElements.size() != 4) {
            LOGGER.error("Unexpected ResultData for Object Definition value: {}", this.getDebugInfo(resultData));
            throw new ProtocolAdapterException("Expected list for Object Definition to contain 4 elements, got: "
                    + objectDefinitionElements.size());
        }
        final Long classId = this.readLongNotNull(objectDefinitionElements.get(0), "Class ID from " + description);
        final CosemObisCode logicalName = this.readLogicalName(objectDefinitionElements.get(1), "Logical Name from "
                + description);
        final Long attributeIndex = this.readLongNotNull(objectDefinitionElements.get(2), "Attribute Index from "
                + description);
        final Long dataIndex = this.readLongNotNull(objectDefinitionElements.get(0), "Data Index from " + description);

        return new CosemObjectDefinition(classId.intValue(), logicalName, attributeIndex.intValue(),
                dataIndex.intValue());
    }

    public CosemObisCode readLogicalName(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        final byte[] bytes = this.readByteArray(resultData, description, "Logical Name");
        if (bytes == null) {
            return null;
        }
        return new CosemObisCode(bytes);
    }

    public SendDestinationAndMethod readSendDestinationAndMethod(final GetResult getResult, final String description)
            throws ProtocolAdapterException {
        this.checkResultCode(getResult, description);
        return this.readSendDestinationAndMethod(getResult.resultData(), description);
    }

    public SendDestinationAndMethod readSendDestinationAndMethod(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        final List<DataObject> sendDestinationAndMethodElements = this.readList(resultData, description);
        if (sendDestinationAndMethodElements == null) {
            return null;
        }
        final TransportServiceType transportService = this.readTransportServiceType(
                sendDestinationAndMethodElements.get(0), "Transport Service from " + description);
        final String destination = this.readString(sendDestinationAndMethodElements.get(1), "Destination from "
                + description);
        final MessageType message = this.readMessageType(sendDestinationAndMethodElements.get(2), "Message from "
                + description);

        return new SendDestinationAndMethod(transportService, destination, message);
    }

    public TransportServiceType readTransportServiceType(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        final Number number = this.readNumber(resultData, description, "Enum");
        if (number == null) {
            return null;
        }
        final int enumValue = number.intValue();
        final TransportServiceType transportService = this.getTransportServiceTypeForEnumValue(enumValue);
        if (transportService == null) {
            LOGGER.error("Unexpected Enum value for TransportServiceType: {}", enumValue);
            throw new ProtocolAdapterException("Unknown Enum value for TransportServiceType: " + enumValue);
        }
        return transportService;
    }

    private TransportServiceType getTransportServiceTypeForEnumValue(final int enumValue) {
        if (enumValue >= 200 && enumValue <= 255) {
            return TransportServiceType.MANUFACTURER_SPECIFIC;
        }
        return TRANSPORT_SERVICE_TYPE_PER_ENUM_VALUE.get(enumValue);
    }

    public MessageType readMessageType(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        final Number number = this.readNumber(resultData, description, "Enum");
        if (number == null) {
            return null;
        }
        final MessageType message;
        final int enumValue = number.intValue();
        switch (enumValue) {
        case 0:
            message = MessageType.A_XDR_ENCODED_X_DLMS_APDU;
            break;
        case 1:
            message = MessageType.XML_ENCODED_X_DLMS_APDU;
            break;
        default:
            if (enumValue < 128 || enumValue > 255) {
                LOGGER.error("Unexpected Enum value for MessageType: {}", enumValue);
                throw new ProtocolAdapterException("Unknown Enum value for MessageType: " + enumValue);
            }
            message = MessageType.MANUFACTURER_SPECIFIC;
        }
        return message;
    }

    public List<WindowElement> readListOfWindowElement(final GetResult getResult, final String description)
            throws ProtocolAdapterException {
        this.checkResultCode(getResult, description);
        return this.readListOfWindowElement(getResult.resultData(), description);
    }

    public List<WindowElement> readListOfWindowElement(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        final List<DataObject> listOfWindowElement = this.readList(resultData, description);
        if (listOfWindowElement == null) {
            return null;
        }
        final List<WindowElement> windowElementList = new ArrayList<>();
        for (final DataObject windowElementObject : listOfWindowElement) {
            windowElementList.add(this.readWindowElement(windowElementObject, "Window Element from " + description));
        }
        return windowElementList;
    }

    public WindowElement readWindowElement(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        final List<DataObject> windowElementElements = this.readList(resultData, description);
        if (windowElementElements == null) {
            return null;
        }
        return this.buildWindowElementFromDataObjects(windowElementElements, description);
    }

    private WindowElement buildWindowElementFromDataObjects(final List<DataObject> elements, final String description)
            throws ProtocolAdapterException {
        if (elements.size() != 2) {
            LOGGER.error("Unexpected number of ResultData elements for WindowElement value: {}", elements.size());
            throw new ProtocolAdapterException("Expected list for WindowElement to contain 2 elements, got: "
                    + elements.size());
        }

        final com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime startTime = this.readCosemDateTime(
                elements.get(0), "Start Time from " + description);
        final com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime endTime = this.readCosemDateTime(
                elements.get(0), "End Time from " + description);

        return new WindowElement(startTime, endTime);
    }

    public String getDebugInfo(final DataObject dataObject) {
        if (dataObject == null) {
            return null;
        }

        final String dataType = getDataType(dataObject);
        final String objectText = this.getObjectTextForDebugInfo(dataObject);
        final String choiceText = this.getChoiceTextForDebugInfo(dataObject);
        final String rawValueClass = this.getRawValueClassForDebugInfo(dataObject);

        return "DataObject: Choice=" + choiceText + ", ResultData is" + dataType + ", value=[" + rawValueClass + "]: "
                + objectText;
    }

    private String getObjectTextForDebugInfo(final DataObject dataObject) {

        final String objectText;
        if (dataObject.isComplex()) {
            if (dataObject.value() instanceof List) {
                final StringBuilder builder = new StringBuilder();
                builder.append("[");
                builder.append(System.lineSeparator());
                this.appendItemValues(dataObject, builder);
                builder.append("]");
                builder.append(System.lineSeparator());
                objectText = builder.toString();
            } else {
                objectText = String.valueOf(dataObject.rawValue());
            }
        } else if (dataObject.isByteArray()) {
            objectText = this.getDebugInfoByteArray((byte[]) dataObject.value());
        } else if (dataObject.isBitString()) {
            objectText = this.getDebugInfoBitStringBytes(((BitString) dataObject.value()).bitString());
        } else if (dataObject.isCosemDateFormat() && dataObject.value() instanceof CosemDateTime) {
            objectText = this.getDebugInfoDateTimeBytes(((CosemDateTime) dataObject.value()).encode());
        } else {
            objectText = String.valueOf(dataObject.rawValue());
        }

        return objectText;
    }

    private String getChoiceTextForDebugInfo(final DataObject dataObject) {
        final Choices choiceIndex = dataObject.choiceIndex();
        if (choiceIndex == null) {
            return "null";
        }
        return choiceIndex.name() + "(" + choiceIndex.getValue() + ")";
    }

    private String getRawValueClassForDebugInfo(final DataObject dataObject) {
        final Object rawValue = dataObject.rawValue();
        if (rawValue == null) {
            return "null";
        }
        return rawValue.getClass().getName();
    }

    private void appendItemValues(final DataObject dataObject, final StringBuilder builder) {
        for (final Object obj : (List<?>) dataObject.value()) {
            builder.append("\t");
            if (obj instanceof DataObject) {
                builder.append(this.getDebugInfo((DataObject) obj));
            } else {
                builder.append(String.valueOf(obj));
            }
            builder.append(System.lineSeparator());
        }
    }

    private static String getDataType(final DataObject dataObject) {
        String dataType;
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

    public String getDebugInfoByteArray(final byte[] bytes) {
        /*
         * The guessing of the object type by byte length may turn out to be
         * ambiguous at some time. If this occurs the debug info will have to be
         * determined in some more robust way. Until now this appears to work OK
         * for debugging purposes.
         */
        if (bytes.length == 6) {
            return this.getDebugInfoLogicalName(bytes);
        } else if (bytes.length == 12) {
            return this.getDebugInfoDateTimeBytes(bytes);
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

    public String getDebugInfoLogicalName(final byte[] logicalNameValue) {

        if (logicalNameValue.length != 6) {
            throw new IllegalArgumentException("LogicalName values should be 6 bytes long: " + logicalNameValue.length);
        }

        final StringBuilder sb = new StringBuilder();

        sb.append("logical name: ").append(logicalNameValue[0] & 0xFF).append('-').append(logicalNameValue[1] & 0xFF)
        .append(':').append(logicalNameValue[2] & 0xFF).append('.').append(logicalNameValue[3] & 0xFF)
        .append('.').append(logicalNameValue[4] & 0xFF).append('.').append(logicalNameValue[5] & 0xFF);

        return sb.toString();
    }

    public String getDebugInfoDateTimeBytes(final byte[] dateTimeValue) {

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

    public String getDebugInfoBitStringBytes(final byte[] bitStringValue) {
        final BigInteger bigValue = this.byteArrayToBigInteger(bitStringValue);
        final String stringValue = this.byteArrayToString(bitStringValue);

        final StringBuilder sb = new StringBuilder();
        sb.append("number of bytes=").append(bitStringValue.length).append(", value=").append(bigValue)
                .append(", bits=").append(stringValue);

        return sb.toString();
    }

    private String byteArrayToString(final byte[] bitStringValue) {
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

    private BigInteger byteArrayToBigInteger(final byte[] bitStringValue) {
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

    private Number readNumber(final DataObject resultData, final String description) throws ProtocolAdapterException {
        return this.readNumber(resultData, description, "Number");
    }

    private Number readNumber(final DataObject resultData, final String description, final String interpretation)
            throws ProtocolAdapterException {
        this.logDebugResultData(resultData, description);
        if (resultData == null || resultData.isNull()) {
            return null;
        }
        final Object resultValue = resultData.value();
        if (!resultData.isNumber() || !(resultValue instanceof Number)) {
            this.logAndThrowExceptionForUnexpectedResultData(resultData, interpretation);
        }
        return (Number) resultValue;
    }

    private byte[] readByteArray(final DataObject resultData, final String description, final String interpretation)
            throws ProtocolAdapterException {
        this.logDebugResultData(resultData, description);
        if (resultData == null || resultData.isNull()) {
            return null;
        }
        final Object resultValue = resultData.value();
        if (!resultData.isByteArray() || !(resultValue instanceof byte[])) {
            this.logAndThrowExceptionForUnexpectedResultData(resultData, "byte array to be interpreted as "
                    + interpretation);
        }
        return (byte[]) resultValue;
    }

    @SuppressWarnings("unchecked")
    private List<DataObject> readList(final DataObject resultData, final String description)
            throws ProtocolAdapterException {
        this.logDebugResultData(resultData, description);
        if (resultData == null || resultData.isNull()) {
            return null;
        }
        final Object resultValue = resultData.value();
        if (!resultData.isComplex() || !(resultValue instanceof List)) {
            this.logAndThrowExceptionForUnexpectedResultData(resultData, "List");
        }
        return (List<DataObject>) resultValue;
    }

    private void logDebugResultData(final DataObject resultData, final String description) {
        LOGGER.debug(description + " - ResultData: {}", this.getDebugInfo(resultData));
    }

    private void logAndThrowExceptionForUnexpectedResultData(final DataObject resultData, final String expectedType)
            throws ProtocolAdapterException {
        LOGGER.error("Unexpected ResultData for {} value: {}", expectedType, this.getDebugInfo(resultData));
        final String resultDataType = resultData.value() == null ? "null" : resultData.value().getClass().getName();
        throw new ProtocolAdapterException("Expected ResultData of " + expectedType + ", got: "
                + resultData.choiceIndex() + ", value type: " + resultDataType);
    }
}
