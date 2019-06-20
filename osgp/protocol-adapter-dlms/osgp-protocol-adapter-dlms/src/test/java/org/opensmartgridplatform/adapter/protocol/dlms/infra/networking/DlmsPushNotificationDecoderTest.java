package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_4;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_1;
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
    private static final int COMMA_LENGTH = 1;

    private static final String PUSH_SCHEDULER_TRIGGER = "Push scheduler";
    private static final String PUSH_CSD_TRIGGER = "Push csd wakeup";
    private static final String PUSH_SMS_TRIGGER = "Push sms wakeup";
    private static final String PUSH_ALARM_TRIGGER = "Push alarm monitor";

    private static final byte[] SCHEDULER_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x0F, 0x00, 0x04, (byte) 0xFF };
    private static final byte[] CSD_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x02, 0x02, 0x00, (byte) 0xFF };
    private static final byte[] SMS_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x02, 0x03, 0x00, (byte) 0xFF };

    private static final String IDENTIFIER = "EXXXX123456789012";
    private static final byte COMMA = 0x2C;

    private DlmsPushNotificationDecoder decoder;

    @Mock
    ChannelHandlerContext ctx;

    @Mock
    Channel channel;

    @Test
    public void decodeDsmr4AlarmsWithLogicalNames() throws UnknownDecodingStateException,
            UnrecognizedMessageDataException {

        // Test the 3 possible logical names
        decodeDsmr4AlarmsWithLogicalName(SCHEDULER_OBISCODE_BYTES, PUSH_SCHEDULER_TRIGGER);
        decodeDsmr4AlarmsWithLogicalName(CSD_OBISCODE_BYTES, PUSH_CSD_TRIGGER);
        decodeDsmr4AlarmsWithLogicalName(SMS_OBISCODE_BYTES, PUSH_SMS_TRIGGER);

        // Any other logical name should result in an empty trigger type
        decodeDsmr4AlarmsWithLogicalName(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 }, "");
    }

    private void decodeDsmr4AlarmsWithLogicalName(byte[] logicalName, String expecterTriggerType) throws UnknownDecodingStateException,
            UnrecognizedMessageDataException {

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
    }

    @Test
    public void decodeDsmr4AlarmsWithAlarmRegister() throws UnknownDecodingStateException,
            UnrecognizedMessageDataException {

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
}
