/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsLogItemRequestMessage;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsLogItemRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.requests.to.core.OsgpRequestMessageSender;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

@ExtendWith(MockitoExtension.class)
class PushedMessageProcessorTest {

  private static final String IDENTIFIER = "EXXXX123456789012";
  private static final String CORRELATION_UID = "CORRELATION_UID";
  private static final String IP_ADDRESS = "127.0.0.1";

  private static final String PUSH_CSD_TRIGGER = "Push csd wakeup";
  private static final String PUSH_SMS_TRIGGER = "Push sms wakeup";
  private static final String PUSH_ALARM_TRIGGER = "Push alarm monitor";

  @Mock private OsgpRequestMessageSender osgpRequestMessageSender;
  @Mock private DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender;
  @InjectMocks private PushedMessageProcessor processor;

  @Test
  void testProcessNoAction() throws IOException {
    final byte[] bytes = AlarmGeneratorUtil.smr5Alarm(IDENTIFIER, 1, Collections.emptyList());
    final DlmsPushNotification message =
        this.newDlmsPushNotification(bytes, IDENTIFIER, PUSH_CSD_TRIGGER, Collections.emptySet());

    this.processor.process(message, IDENTIFIER, CORRELATION_UID, IP_ADDRESS);

    verifyNoInteractions(this.osgpRequestMessageSender);
    verifyNoInteractions(this.dlmsLogItemRequestMessageSender);
  }

  @Test
  void testProcessAlarm() throws IOException {
    final byte[] bytes = AlarmGeneratorUtil.smr5Alarm(IDENTIFIER, 1, Collections.emptyList());
    final DlmsPushNotification message =
        this.newDlmsPushNotification(bytes, IDENTIFIER, PUSH_ALARM_TRIGGER, Collections.emptySet());

    this.processor.process(message, IDENTIFIER, CORRELATION_UID, IP_ADDRESS);

    verify(this.osgpRequestMessageSender)
        .send(
            any(RequestMessage.class),
            eq(MessageType.PUSH_NOTIFICATION_ALARM.name()),
            any(MessageMetadata.class));
    verify(this.dlmsLogItemRequestMessageSender).send(any(DlmsLogItemRequestMessage.class));
  }

  @Test
  void testProcessSms() throws IOException {
    final byte[] bytes = AlarmGeneratorUtil.smr5Alarm(IDENTIFIER, 1, Collections.emptyList());
    final DlmsPushNotification message =
        this.newDlmsPushNotification(bytes, IDENTIFIER, PUSH_SMS_TRIGGER, Collections.emptySet());

    this.processor.process(message, IDENTIFIER, CORRELATION_UID, IP_ADDRESS);

    verify(this.osgpRequestMessageSender)
        .send(any(RequestMessage.class), eq(MessageType.PUSH_NOTIFICATION_SMS.name()), isNull());
    verify(this.dlmsLogItemRequestMessageSender).send(any(DlmsLogItemRequestMessage.class));
  }

  private DlmsPushNotification newDlmsPushNotification(
      final byte[] bytes,
      final String equipmentIdentifier,
      final String triggerType,
      final Set<AlarmTypeDto> alarms) {
    return new DlmsPushNotification(bytes, equipmentIdentifier, triggerType, alarms);
  }
}
