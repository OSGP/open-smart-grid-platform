package org.osgp.adapter.protocol.dlms.domain.commands;

import java.nio.ByteBuffer;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.DataObject;
import org.springframework.stereotype.Service;

@Service(value = "dlmsHelperService")
public class DlmsHelperService {

    public DateTime fromDateTimeValue(final byte[] dateTimeValue) {

        final ByteBuffer bb = ByteBuffer.wrap(dateTimeValue);
        final int year = bb.getShort();
        final int monthOfYear = bb.get();
        final int dayOfMonth = bb.get();
        // final int dayOfWeek =
        bb.get();
        final int hourOfDay = bb.get();
        final int minuteOfHour = bb.get();
        final int secondOfMinute = bb.get();
        final int hundredthsOfSecond = bb.get();
        // final int deviation =
        bb.getShort();
        // final int clockStatus =
        bb.get();

        return new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute,
                hundredthsOfSecond * 10);

    }

    public DataObject asDataObject(final DateTime dateTime) {

        final ByteBuffer bb = ByteBuffer.allocate(12);

        bb.putShort((short) dateTime.getYear());
        bb.put((byte) dateTime.getMonthOfYear());
        bb.put((byte) dateTime.getDayOfMonth());
        // leave day of week unspecified (0xFF)
        bb.put((byte) 0xFF);
        bb.put((byte) dateTime.getHourOfDay());
        bb.put((byte) dateTime.getMinuteOfHour());
        bb.put((byte) dateTime.getSecondOfMinute());
        bb.put((byte) (dateTime.getMillisOfSecond() / 10));
        // deviation high byte
        bb.put((byte) 0x80);
        // deviation low byte
        bb.put((byte) 0x00);
        // clock status
        bb.put((byte) 128);

        return DataObject.newOctetStringData(bb.array());
    }

    public String getDebugInfo(final DataObject dataObject) {

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
            objectText = this.getDebugInfoDateTimeBytes((byte[]) dataObject.value());
        } else {
            objectText = String.valueOf(dataObject.rawValue());
        }

        return "DataObject: Choice=" + dataObject.choiceIndex().name() + "(" + dataObject.choiceIndex().getValue()
                + "), ResultData is" + dataType + ", value=[" + dataObject.rawValue().getClass().getName() + "]: "
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
}
