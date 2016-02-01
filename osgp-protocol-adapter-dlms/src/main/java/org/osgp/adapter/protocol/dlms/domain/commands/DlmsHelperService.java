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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.CosemDate;
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

@Service(value = "dlmsHelperService")
public class DlmsHelperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsHelperService.class);

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

    public Long readLong(final DataObject resultData, final String description) throws ProtocolAdapterException {
        LOGGER.debug(description + " - ResultData: {}", this.getDebugInfo(resultData));
        if (resultData == null || resultData.isNull()) {
            return null;
        }
        if (!resultData.isNumber()) {
            LOGGER.error("Unexpected ResultData for Long value: {}", this.getDebugInfo(resultData));
            throw new ProtocolAdapterException("Expected ResultData of Number, got: " + resultData.choiceIndex());
        }
        return ((Number) resultData.value()).longValue();
    }

    public DateTime readDateTime(final GetResult getResult, final String description) throws ProtocolAdapterException {
        this.checkResultCode(getResult, description);
        final DataObject resultData = getResult.resultData();
        LOGGER.debug(description + " - ResultData: {}", this.getDebugInfo(resultData));
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

    public DateTime convertDataObjectToDateTime(final DataObject object) throws ProtocolAdapterException {
        if (object.isByteArray()) {
            return this.fromDateTimeValue((byte[]) object.value());
        } else if (object.isCosemDateFormat()) {
            return this.fromDateTimeValue(((CosemDateTime) object.value()).encode());
        } else {
            LOGGER.error("Unexpected ResultData for DateTime value: {}", this.getDebugInfo(object));
            throw new ProtocolAdapterException("Expected ResultData of ByteArray or CosemDateFormat, got: "
                    + object.choiceIndex());
        }
    }

    public DateTime fromDateTimeValue(final byte[] dateTimeValue) throws ProtocolAdapterException {

        final ByteBuffer bb = ByteBuffer.wrap(dateTimeValue);
        int year = bb.getShort();
        final boolean yearUnspecified = year == (short) 0xFFFF;
        int monthOfYear = bb.get();
        final boolean monthUnspecified = monthOfYear == (byte) 0xFF;
        int dayOfMonth = bb.get();
        final boolean dayUnspecified = dayOfMonth == (byte) 0xFF;

        if (yearUnspecified && monthUnspecified && dayUnspecified) {
            // use dummy values, standing out as not realistic
            year = 1;
            monthOfYear = 1;
            dayOfMonth = 1;
        } else if (yearUnspecified) {
            throw new ProtocolAdapterException("Handling unspecified year in date-time value is not supported.");
        } else if (monthUnspecified) {
            throw new ProtocolAdapterException("Handling unspecified month in date-time value is not supported.");
        } else if (dayUnspecified) {
            throw new ProtocolAdapterException("Handling unspecified day of month in date-time value is not supported.");
        }
        // final int dayOfWeek =
        bb.get();
        int hourOfDay = bb.get();
        if (hourOfDay == (byte) 0xFF) {
            // treat time part as start of day if not specified
            hourOfDay = 0;
        }
        int minuteOfHour = bb.get();
        if (minuteOfHour == (byte) 0xFF) {
            // treat time part as start of day if not specified
            minuteOfHour = 0;
        }
        int secondOfMinute = bb.get();
        if (secondOfMinute == (byte) 0xFF) {
            // treat time part as start of day if not specified
            secondOfMinute = 0;
        }
        int hundredthsOfSecond = bb.get();
        if (hundredthsOfSecond == (byte) 0xFF) {
            // treat time part as start of day if not specified
            hundredthsOfSecond = 0;
        }
        int deviation = bb.getShort();
        if (deviation == (short) 0x8000) {
            // treat unspecified deviation as no deviation
            deviation = 0;
        }
        // final int clockStatus =
        bb.get();

        return new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute,
                hundredthsOfSecond * 10, DateTimeZone.forOffsetMillis(-deviation * MILLISECONDS_PER_MINUTE));
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

    public String getDebugInfo(final DataObject dataObject) {
        if (dataObject == null) {
            return null;
        }

        final String dataType = getDataType(dataObject);

        String objectText;
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

        final Choices choiceIndex = dataObject.choiceIndex();
        final String choiceText;
        if (choiceIndex == null) {
            choiceText = "null";
        } else {
            choiceText = choiceIndex.name() + "(" + choiceIndex.getValue() + ")";
        }
        final Object rawValue = dataObject.rawValue();
        final String rawValueClass;
        if (rawValue == null) {
            rawValueClass = "null";
        } else {
            rawValueClass = rawValue.getClass().getName();
        }
        return "DataObject: Choice=" + choiceText + ", ResultData is" + dataType + ", value=[" + rawValueClass + "]: "
                + objectText;
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
}
