/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.EXTERNAL_TRIGGER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.INTERNAL_TRIGGER_ALARM;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.PUSH_SCHEDULER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.PUSH_SETUP_ALARM;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.PUSH_SETUP_SCHEDULER;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigSmr50;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;

public class Smr5AlarmDecoder extends AlarmDecoder {

    private static final int SMR5_NUMBER_OF_BYTES_FOR_ADDRESSING = 8;
    private static final int SMR5_NUMBER_OF_BYTES_FOR_INVOKE_ID = 4;

    private DlmsObjectConfigSmr50 dlmsObjectConfigSmr50 = new DlmsObjectConfigSmr50();

    private static final Logger LOGGER = LoggerFactory.getLogger(Smr5AlarmDecoder.class);

    /**
     * DLMS data types used in the SMR5 push notification
     */
    private static final byte STRUCTURE = 0x02;
    private static final byte OCTET_STRING = 0x09;
    private static final byte DOUBLE_LONG_UNSIGNED = 0x06;

    private DlmsPushNotification.Builder builder = new DlmsPushNotification.Builder();

    public DlmsPushNotification decodeSmr5alarm(final ByteBuf buffer) throws UnrecognizedMessageDataException {

        // Skip addressing, the 0x0F byte and the invoke-id-and-priority bytes
        buffer.skipBytes(SMR5_NUMBER_OF_BYTES_FOR_ADDRESSING + 1 + SMR5_NUMBER_OF_BYTES_FOR_INVOKE_ID);

        // Datetime is not used, so can be skipped as well
        this.skipDateTime(buffer);

        // Next byte should indicate a Structure
        final byte dataTypeByte = buffer.readByte();
        if (dataTypeByte != STRUCTURE) {
            throw new UnrecognizedMessageDataException("Expected a structure (0x02), but encountered: " + dataTypeByte);
        }
        this.builder.appendByte(dataTypeByte);

        // Next byte should indicate the amount of elements in the structure: 2
        // or 3
        final byte dataLength = buffer.readByte();
        if (dataLength != 2 && dataLength != 3) {
            throw new UnrecognizedMessageDataException(
                    "Expected a structure with 2 or 3 elements, but amount is " + dataLength);
        }
        this.builder.appendByte(dataLength);

        // If the alarm contains 3 elements, then the 3rd element is the alarm
        // register
        final boolean alarmRegisterExpected = (dataLength == 3);

        // Decode elements
        this.decodeEquipmentIdentifier(buffer);
        this.decodeLogicalName(buffer, alarmRegisterExpected);

        if (alarmRegisterExpected) {
            this.decodeAlarms(buffer);
        }

        return this.builder.build();
    }

    private void skipDateTime(final ByteBuf buffer) {

        // The first byte of the datetime is the length byte
        final byte dateTimeLength = buffer.readByte();

        // Skip the rest of the datetime bytes
        buffer.skipBytes(dateTimeLength);
    }

    private void decodeEquipmentIdentifier(final ByteBuf buffer) throws UnrecognizedMessageDataException {

        // First byte should indicate octet-string
        final byte dataTypeByte = buffer.readByte();
        if (dataTypeByte != OCTET_STRING) {
            throw new UnrecognizedMessageDataException(
                    "Expected an octet-string (0x09), but encountered: " + dataTypeByte);
        }
        this.builder.appendByte(dataTypeByte);

        // Next byte should be the length of the octet-string
        final byte dataLength = buffer.readByte();
        if (dataLength != EQUIPMENT_IDENTIFIER_LENGTH) {
            throw new UnrecognizedMessageDataException("Expected an identifier with length "
                    + EQUIPMENT_IDENTIFIER_LENGTH + ", but specified length is: " + dataLength);
        }
        this.builder.appendByte(dataLength);

        // Read the identifier
        final byte[] equipmentIdentifierBytes = new byte[dataLength];
        buffer.readBytes(equipmentIdentifierBytes, 0, equipmentIdentifierBytes.length);

        this.builder.withEquipmentIdentifier(new String(equipmentIdentifierBytes, StandardCharsets.US_ASCII));
        this.builder.appendBytes(equipmentIdentifierBytes);
    }

    private void decodeLogicalName(final ByteBuf buffer, final boolean alarmExpected)
            throws UnrecognizedMessageDataException {
        // First byte should indicate octet-string
        final byte dataTypeByte = buffer.readByte();
        if (dataTypeByte != OCTET_STRING) {
            throw new UnrecognizedMessageDataException(
                    "Expected an octet-string (0x09), but encountered: " + dataTypeByte);
        }
        this.builder.appendByte(dataTypeByte);

        // Next byte should be the length of the octet-string
        final byte dataLength = buffer.readByte();
        if (dataLength != NUMBER_OF_BYTES_FOR_LOGICAL_NAME) {
            throw new UnrecognizedMessageDataException("Expected a logical name with length "
                    + NUMBER_OF_BYTES_FOR_LOGICAL_NAME + ", but specified length is: " + dataLength);
        }
        this.builder.appendByte(dataLength);

        // Next bytes are the logical name
        this.decodeObisCodeData(buffer, alarmExpected);
    }

    private void decodeAlarms(final ByteBuf buffer) throws UnrecognizedMessageDataException {
        // First byte should indicate double-long-unsigned
        final byte dataTypeByte = buffer.readByte();
        if (dataTypeByte != DOUBLE_LONG_UNSIGNED) {
            throw new UnrecognizedMessageDataException(
                    "Expected a double-long-unsigned (0x06), but encountered: " + dataTypeByte);
        }
        this.builder.appendByte(dataTypeByte);

        // Next bytes are the alarm
        this.decodeAlarmRegisterData(buffer, this.builder);
    }

    private void decodeObisCodeData(final ByteBuf buffer, final boolean alarmRegisterExpected)
            throws UnrecognizedMessageDataException {

        final byte[] logicalNameBytes = this.read(buffer, NUMBER_OF_BYTES_FOR_LOGICAL_NAME);

        try {
            if (!alarmRegisterExpected && this.isLogicalNameSmsTrigger(logicalNameBytes)) {
                this.builder.withTriggerType(PUSH_SMS_TRIGGER);
            } else if (!alarmRegisterExpected && this.isLogicalNameCsdTrigger(logicalNameBytes)) {
                LOGGER.warn("CSD Push notification not supported");
                this.builder.withTriggerType(PUSH_CSD_TRIGGER);
            } else if (!alarmRegisterExpected && this.isLogicalNameSchedulerTrigger(logicalNameBytes)) {
                LOGGER.warn("Scheduler Push notification not supported");
                this.builder.withTriggerType(PUSH_SCHEDULER_TRIGGER);
            } else if (alarmRegisterExpected && this.isLogicalNameAlarmTrigger(logicalNameBytes)) {
                this.builder.withTriggerType(PUSH_ALARM_TRIGGER);
            } else {
                LOGGER.warn("Unknown Push notification not supported. Unable to decode");
                this.builder.withTriggerType("");
            }
        } catch (final ProtocolAdapterException e) {
            throw new UnrecognizedMessageDataException("Error decoding logical name", e);
        }

        this.builder.withAlarms(null);
        this.builder.appendBytes(logicalNameBytes);
    }

    private boolean isLogicalNameSmsTrigger(final byte[] logicalNameBytes) throws ProtocolAdapterException {
        // SMR5 has one general object for the SMS and CSD triggers
        return Arrays.equals(this.dlmsObjectConfigSmr50.getObisForObject(EXTERNAL_TRIGGER).bytes(), logicalNameBytes);
    }

    private boolean isLogicalNameCsdTrigger(final byte[] logicalNameBytes) throws ProtocolAdapterException {
        // SMR5 has one general object for the SMS and CSD triggers
        return Arrays.equals(this.dlmsObjectConfigSmr50.getObisForObject(EXTERNAL_TRIGGER).bytes(), logicalNameBytes);
    }

    private boolean isLogicalNameSchedulerTrigger(final byte[] logicalNameBytes) throws ProtocolAdapterException {
        return Arrays.equals(this.dlmsObjectConfigSmr50.getObisForObject(PUSH_SCHEDULER).bytes(), logicalNameBytes)
                || Arrays.equals(this.dlmsObjectConfigSmr50.getObisForObject(PUSH_SETUP_SCHEDULER).bytes(),
                        logicalNameBytes);
    }

    private boolean isLogicalNameAlarmTrigger(final byte[] logicalNameBytes) throws ProtocolAdapterException {
        return Arrays.equals(this.dlmsObjectConfigSmr50.getObisForObject(INTERNAL_TRIGGER_ALARM).bytes(),
                logicalNameBytes)
                || Arrays.equals(this.dlmsObjectConfigSmr50.getObisForObject(PUSH_SETUP_ALARM).bytes(),
                        logicalNameBytes);
    }
}
