package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_4;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_1;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.PHASE_OUTAGE_DETECTED_L1;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.REPLACE_BATTERY;

import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.networking.DlmsPushNotificationDecoder.DecodingState;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;

@RunWith(MockitoJUnitRunner.class)
public class DlmsPushNotificationDecoderTest {

    private static final int EQUIPMENT_IDENTIFIER_LENGTH = 17;
    private static final int LOGICAL_NAME_LENGTH = 6;
    private static final int COMMA_LENGTH = 1;

    private static final int SMR5_NUMBER_OF_BYTES_FOR_ADDRESSING = 8;
    private static final int SMR5_NUMBER_OF_BYTES_FOR_INVOKE_ID = 4;
    private static final int SMR5_NUMBER_OF_BYTES_FOR_DATETIME = 7;

    private static final String PUSH_SCHEDULER_TRIGGER = "Push scheduler";
    private static final String PUSH_CSD_TRIGGER = "Push csd wakeup";
    private static final String PUSH_SMS_TRIGGER = "Push sms wakeup";
    private static final String PUSH_ALARM_TRIGGER = "Push alarm monitor";

    private static final byte[] SCHEDULER_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x0F, 0x00, 0x04, (byte)0xFF };
    private static final byte[] SCHEDULER_SETUP_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x19, 0x09, 0x00, (byte)0xFF };
    private static final byte[] CSD_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x02, 0x02, 0x00, (byte) 0xFF };
    private static final byte[] SMS_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x02, 0x03, 0x00, (byte) 0xFF };
    private static final byte[] ALARM_OBISCODE_BYTES = new byte[] { 0x00, 0x01, 0x19, 0x09, 0x00, (byte) 0xFF };
    private static final byte[] SMR5_EXTERNAL_TRIGGER_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x02, 0x02, 0x00,
            (byte) 0xFF };

    private static final String IDENTIFIER = "EXXXX123456789012";
    private static final byte COMMA = 0x2C;

    private static final int STRUCTURE = 0x02;
    private static final int OCTET_STRING = 0x09;
    private static final int DOUBLE_LONG_UNSIGNED = 0x06;

    private DlmsPushNotificationDecoder decoder;

    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private Channel channel;

    @Test
    public void decodeDsmr4AlarmsWithLogicalNames() throws UnrecognizedMessageDataException {

        // Test the 4 possible logical names (3 different trigger types)
        decodeDsmr4AlarmsWithLogicalName(SCHEDULER_OBISCODE_BYTES, PUSH_SCHEDULER_TRIGGER);
        decodeDsmr4AlarmsWithLogicalName(SCHEDULER_SETUP_OBISCODE_BYTES, PUSH_SCHEDULER_TRIGGER);
        decodeDsmr4AlarmsWithLogicalName(CSD_OBISCODE_BYTES, PUSH_CSD_TRIGGER);
        decodeDsmr4AlarmsWithLogicalName(SMS_OBISCODE_BYTES, PUSH_SMS_TRIGGER);

        // Any other logical name should result in an empty trigger type
        decodeDsmr4AlarmsWithLogicalName(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 }, "");
    }

    private void decodeDsmr4AlarmsWithLogicalName(byte[] logicalName, String expecterTriggerType) throws UnrecognizedMessageDataException {

        // SETUP

        decoder = new DlmsPushNotificationDecoder();

        ChannelBuffer buffer = mock(ChannelBuffer.class);
        DecodingState state = DecodingState.EQUIPMENT_IDENTIFIER;

        byte[] bytes = setupDsmr4Buffer(buffer, IDENTIFIER, logicalName);

        // CALL

        Object pushNotificationObject = decoder.decode(ctx, channel, buffer, state);

        // VERIFY

        assertThat(pushNotificationObject instanceof DlmsPushNotification).isTrue();
        DlmsPushNotification dlmsPushNotification = (DlmsPushNotification) pushNotificationObject;
        assertThat(dlmsPushNotification.getEquipmentIdentifier()).isEqualTo(IDENTIFIER);
        assertThat(dlmsPushNotification.getTriggerType()).isEqualTo(expecterTriggerType);
        assertThat(dlmsPushNotification.getAlarms().isEmpty()).isTrue();
        assertThat(dlmsPushNotification.toByteArray()).isEqualTo(bytes);

        verifyDsmr4BufferCalls(buffer, logicalName);
    }

    @Test
    public void decodeDsmr4AlarmsWithAlarmRegister() throws UnrecognizedMessageDataException {

        // SETUP

        decoder = new DlmsPushNotificationDecoder();

        ChannelBuffer buffer = mock(ChannelBuffer.class);
        DecodingState state = DecodingState.EQUIPMENT_IDENTIFIER;

        // Create alarm register with 3 alarms: replace battery and 2 mbus alarms
        final byte[] alarmRegister = new byte[] { 0x00, 0x18, 0x00, 0x02 };

        byte[] bytes = setupDsmr4Buffer(buffer, IDENTIFIER, alarmRegister);

        // CALL

        Object pushNotificationObject = decoder.decode(ctx, channel, buffer, state);

        // VERIFY

        assertThat(pushNotificationObject instanceof DlmsPushNotification).isTrue();
        DlmsPushNotification dlmsPushNotification = (DlmsPushNotification) pushNotificationObject;
        assertThat(dlmsPushNotification.getEquipmentIdentifier()).isEqualTo(IDENTIFIER);
        assertThat(dlmsPushNotification.getTriggerType()).isEqualTo(PUSH_ALARM_TRIGGER);
        assertThat(dlmsPushNotification.toByteArray()).isEqualTo(bytes);

        Set<AlarmTypeDto> alarms = dlmsPushNotification.getAlarms();
        assertThat(alarms.size()).isEqualTo(3);
        assertThat(alarms.contains(REPLACE_BATTERY)).isTrue();
        assertThat(alarms.contains(COMMUNICATION_ERROR_M_BUS_CHANNEL_4)).isTrue();
        assertThat(alarms.contains(FRAUD_ATTEMPT_M_BUS_CHANNEL_1)).isTrue();

        verifyDsmr4BufferCalls(buffer, alarmRegister);
    }

    private byte[] setupDsmr4Buffer(ChannelBuffer buffer, String identifier, byte[] data) {

        final byte[] bytes = new byte[EQUIPMENT_IDENTIFIER_LENGTH + COMMA_LENGTH + data.length] ;

        System.arraycopy(identifier.getBytes(), 0, bytes, 0, EQUIPMENT_IDENTIFIER_LENGTH);
        System.arraycopy(new byte[] { COMMA }, 0, bytes, EQUIPMENT_IDENTIFIER_LENGTH, COMMA_LENGTH);
        System.arraycopy(data, 0, bytes, EQUIPMENT_IDENTIFIER_LENGTH + COMMA_LENGTH, data.length);

        doAnswer(invocation-> {
            byte[] outputValue = (byte[])invocation.getArguments()[0];
            System.arraycopy(bytes, 0, outputValue, 0, EQUIPMENT_IDENTIFIER_LENGTH + 1);
            return null;
        }).when(buffer).readBytes(any(byte[].class), eq(0), eq(EQUIPMENT_IDENTIFIER_LENGTH + 1));

        doAnswer(invocation-> {
            byte[] outputValue = (byte[])invocation.getArguments()[0];
            System.arraycopy(bytes, EQUIPMENT_IDENTIFIER_LENGTH + 1, outputValue, 0, data.length);
            return null;
        }).when(buffer).readBytes(any(byte[].class), eq(0), eq(data.length));

        when(buffer.writerIndex()).thenReturn(bytes.length);
        when(buffer.readerIndex()).thenReturn(EQUIPMENT_IDENTIFIER_LENGTH + 1);

        return bytes;
    }

    private void verifyDsmr4BufferCalls(ChannelBuffer buffer, byte[] data) {
        verify(buffer, times(1)).readBytes(any(byte[].class), eq(0), eq(EQUIPMENT_IDENTIFIER_LENGTH + 1));
        verify(buffer, times(1)).readBytes(any(byte[].class), eq(0), eq(data.length));
        verify(buffer, times(1)).writerIndex();
        verify(buffer, times(1)).readerIndex();
    }

    @Test
    public void decodeSmr5AlarmsWithLogicalNames() throws UnknownDecodingStateException,
            UnrecognizedMessageDataException {

        // Test the 4 possible logical names (2 different trigger types)
        decodeSmr5AlarmsWithLogicalName(SCHEDULER_OBISCODE_BYTES, PUSH_SCHEDULER_TRIGGER);
        decodeSmr5AlarmsWithLogicalName(SCHEDULER_SETUP_OBISCODE_BYTES, PUSH_SCHEDULER_TRIGGER);
        decodeSmr5AlarmsWithLogicalName(SMR5_EXTERNAL_TRIGGER_OBISCODE_BYTES, PUSH_SMS_TRIGGER);

        // Any other logical name should result in an empty trigger type
        decodeSmr5AlarmsWithLogicalName(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 }, "");
    }

    @Test
    public void decodeSmr5AlarmsWithDateTime() throws UnknownDecodingStateException,
            UnrecognizedMessageDataException {

        decodeSmr5AlarmsWithLogicalName(SCHEDULER_OBISCODE_BYTES, PUSH_SCHEDULER_TRIGGER, true);
    }

    @Test
    public void decodeSmr5AlarmsWithAlarmRegister() throws UnrecognizedMessageDataException {

        // SETUP

        decoder = new DlmsPushNotificationDecoder();

        ChannelBuffer buffer = mock(ChannelBuffer.class);
        DecodingState state = DecodingState.EQUIPMENT_IDENTIFIER;

        // Create alarm register with 4 alarms: replace battery, 2 mbus alarms and phase outage detected (new in SMR5.1)
        final byte[] alarmRegister = new byte[] { 0x10, 0x18, 0x00, 0x02 };

        setupSmr5BufferWithAlarm(buffer, IDENTIFIER, ALARM_OBISCODE_BYTES, alarmRegister);

        // CALL

        Object pushNotificationObject = decoder.decode(ctx, channel, buffer, state);

        // VERIFY

        assertThat(pushNotificationObject instanceof DlmsPushNotification).isTrue();
        DlmsPushNotification dlmsPushNotification = (DlmsPushNotification) pushNotificationObject;
        assertThat(dlmsPushNotification.getEquipmentIdentifier()).isEqualTo(IDENTIFIER);
        assertThat(dlmsPushNotification.getTriggerType()).isEqualTo(PUSH_ALARM_TRIGGER);

        Set<AlarmTypeDto> alarms = dlmsPushNotification.getAlarms();
        assertThat(alarms.size()).isEqualTo(4);
        assertThat(alarms.contains(REPLACE_BATTERY)).isTrue();
        assertThat(alarms.contains(COMMUNICATION_ERROR_M_BUS_CHANNEL_4)).isTrue();
        assertThat(alarms.contains(FRAUD_ATTEMPT_M_BUS_CHANNEL_1)).isTrue();
        assertThat(alarms.contains(PHASE_OUTAGE_DETECTED_L1)).isTrue();

        verifySmr5BufferCallsWithAlarm(buffer, alarmRegister);

        // Verify the addressing bytes, the 0x0F for data-notification and the invoke-id bytes were skipped
        verify(buffer, times(1)).skipBytes(SMR5_NUMBER_OF_BYTES_FOR_ADDRESSING + 1 + SMR5_NUMBER_OF_BYTES_FOR_INVOKE_ID);
    }

    private void decodeSmr5AlarmsWithLogicalName(byte[] logicalName, String expecterTriggerType) throws UnknownDecodingStateException,
            UnrecognizedMessageDataException {
        decodeSmr5AlarmsWithLogicalName(logicalName, expecterTriggerType, false);
    }

    private void decodeSmr5AlarmsWithLogicalName(byte[] logicalName, String expecterTriggerType,
            boolean withDateTime) throws UnrecognizedMessageDataException {

        // SETUP

        decoder = new DlmsPushNotificationDecoder();

        ChannelBuffer buffer = mock(ChannelBuffer.class);
        DecodingState state = DecodingState.EQUIPMENT_IDENTIFIER;

        setupSmr5Buffer(buffer, IDENTIFIER, logicalName, withDateTime);

        // CALL

        Object pushNotificationObject = decoder.decode(ctx, channel, buffer, state);

        // VERIFY

        assertThat(pushNotificationObject instanceof DlmsPushNotification).isTrue();
        DlmsPushNotification dlmsPushNotification = (DlmsPushNotification) pushNotificationObject;
        assertThat(dlmsPushNotification.getEquipmentIdentifier()).isEqualTo(IDENTIFIER);
        assertThat(dlmsPushNotification.getTriggerType()).isEqualTo(expecterTriggerType);
        assertThat(dlmsPushNotification.getAlarms().isEmpty()).isTrue();

        verifySmr5BufferCalls(buffer);

        // Verify the addressing bytes, the 0x0F for data-notification and the invoke-id bytes were skipped
        verify(buffer, times(1)).skipBytes(SMR5_NUMBER_OF_BYTES_FOR_ADDRESSING + 1 + SMR5_NUMBER_OF_BYTES_FOR_INVOKE_ID);

        if (withDateTime) {
            // Verify the date-time bytes were skipped
            verify(buffer, times(1)).skipBytes(SMR5_NUMBER_OF_BYTES_FOR_DATETIME);
        }
    }

    private void setupSmr5Buffer(ChannelBuffer buffer, String identifier, byte[] logicalName) {
        setupSmr5Buffer(buffer, identifier, logicalName, false);
    }

    private void setupSmr5Buffer(ChannelBuffer buffer, String identifier, byte[] logicalName, boolean withDateTime) {
        /**
         * Alarm example for SMR5, without alarm register:
         *  0F                                                       // Data-notification
         *  00 00 00 01                                              // Long-invoke-id-and-priority (can be ignored)
         *  00                                                       // Date-time (length 0)
         *  02 02                                                    // Structure (0x02), 2 elements
         *    09 11                                                  // Octetstring (0x09), length 11
         *      45 58 58 58 58 31 32 33 34 35 36 37 38 39 30 31 32   // Equipment id EXXXX123456789012
         *    09 06                                                  // Octetstring (0x09), length 6
         *      00 00 19 09 00 FF                                    // Logical name: Push setup schedule
         **/

        int dateTimeLength = 0;
        if (withDateTime) {
            dateTimeLength = SMR5_NUMBER_OF_BYTES_FOR_DATETIME;
        }

        when(buffer.getByte(8)).thenReturn((byte)0x0F);        // Data-notification

        when(buffer.readByte())
                .thenReturn((byte)dateTimeLength)              // Date time length
                .thenReturn((byte)STRUCTURE)                   // Data type structure
                .thenReturn((byte)0x02)                        // Length 2
                .thenReturn((byte)OCTET_STRING)                // Data type octet string
                .thenReturn((byte)EQUIPMENT_IDENTIFIER_LENGTH) // Length
                .thenReturn((byte)OCTET_STRING)                // Data type octet string
                .thenReturn((byte)LOGICAL_NAME_LENGTH);        // Length

        doAnswer(invocation-> {
            byte[] outputValue = (byte[])invocation.getArguments()[0];
            System.arraycopy(identifier.getBytes(), 0, outputValue, 0, EQUIPMENT_IDENTIFIER_LENGTH);
            return null;
        }).when(buffer).readBytes(any(byte[].class), eq(0), eq(EQUIPMENT_IDENTIFIER_LENGTH));

        doAnswer(invocation-> {
            byte[] outputValue = (byte[])invocation.getArguments()[0];
            System.arraycopy(logicalName, 0, outputValue, 0, LOGICAL_NAME_LENGTH);
            return null;
        }).when(buffer).readBytes(any(byte[].class), eq(0), eq(LOGICAL_NAME_LENGTH));
    }

    private void verifySmr5BufferCalls(ChannelBuffer buffer) {
        verify(buffer, times(1)).getByte(8);
        verify(buffer, times(7)).readByte();
        verify(buffer, times(1)).readBytes(any(byte[].class), eq(0), eq(EQUIPMENT_IDENTIFIER_LENGTH ));
        verify(buffer, times(1)).readBytes(any(byte[].class), eq(0), eq(LOGICAL_NAME_LENGTH));
    }

    private void setupSmr5BufferWithAlarm(ChannelBuffer buffer, String identifier, byte[] logicalName,
            byte[] alarmRegister) {
        /**
         * Alarm example for SMR5, with alarm register:
         *  0F                                                       // Data-notification
         *  00 00 00 01                                              // Long-invoke-id-and-priority (can be ignored)
         *  00                                                       // Date-time (length 0)
         *  02 03                                                    // Structure (0x02), 3 elements
         *    09 11                                                  // Octetstring (0x09), length 11
         *      45 58 58 58 58 31 32 33 34 35 36 37 38 39 30 31 32   // Equipment id EXXXX123456789012
         *    09 06                                                  // Octetstring (0x09), length 6
         *      00 01 19 09 00 FF                                    // Logical name: Push setup alarms
         *    06                                                     // Double long unsigned (0x06)
         *      00 00 00 02                                          // Alarm register, with Replace battery set
         **/

        when(buffer.getByte(8)).thenReturn((byte)0x0F);        // Data-notification

        when(buffer.readByte())
                .thenReturn((byte)0x00)                        // Date time, length 0
                .thenReturn((byte)STRUCTURE)                   // Data type structure
                .thenReturn((byte)0x03)                        // Length 3
                .thenReturn((byte)OCTET_STRING)                // Data type octet string
                .thenReturn((byte)EQUIPMENT_IDENTIFIER_LENGTH) // Length
                .thenReturn((byte)OCTET_STRING)                // Data type octet string
                .thenReturn((byte)LOGICAL_NAME_LENGTH)         // Length
                .thenReturn((byte)DOUBLE_LONG_UNSIGNED);       // Data type double long unsigned

        doAnswer(invocation-> {
            byte[] outputValue = (byte[])invocation.getArguments()[0];
            System.arraycopy(identifier.getBytes(), 0, outputValue, 0, EQUIPMENT_IDENTIFIER_LENGTH);
            return null;
        }).when(buffer).readBytes(any(byte[].class), eq(0), eq(EQUIPMENT_IDENTIFIER_LENGTH));

        doAnswer(invocation-> {
            byte[] outputValue = (byte[])invocation.getArguments()[0];
            System.arraycopy(logicalName, 0, outputValue, 0, LOGICAL_NAME_LENGTH);
            return null;
        }).when(buffer).readBytes(any(byte[].class), eq(0), eq(LOGICAL_NAME_LENGTH));

        doAnswer(invocation-> {
            byte[] outputValue = (byte[])invocation.getArguments()[0];
            System.arraycopy(alarmRegister, 0, outputValue, 0, alarmRegister.length);
            return null;
        }).when(buffer).readBytes(any(byte[].class), eq(0), eq(alarmRegister.length));
    }

    private void verifySmr5BufferCallsWithAlarm(ChannelBuffer buffer, byte[] alarmRegister) {
        verify(buffer, times(1)).getByte(8);
        verify(buffer, times(8)).readByte();
        verify(buffer, times(1)).readBytes(any(byte[].class), eq(0), eq(EQUIPMENT_IDENTIFIER_LENGTH ));
        verify(buffer, times(1)).readBytes(any(byte[].class), eq(0), eq(LOGICAL_NAME_LENGTH));
        verify(buffer, times(1)).readBytes(any(byte[].class), eq(0), eq(alarmRegister.length));
    }
}
