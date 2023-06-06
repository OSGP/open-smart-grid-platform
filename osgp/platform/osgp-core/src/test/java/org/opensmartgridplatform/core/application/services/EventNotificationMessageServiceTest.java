// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.core.domain.model.domain.DomainRequestService;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationDto;
import org.opensmartgridplatform.dto.valueobjects.EventTypeDto;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderTimestampService;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

@ExtendWith(MockitoExtension.class)
public class EventNotificationMessageServiceTest {

  @Mock private CorrelationIdProviderTimestampService correlationIdProviderTimestampService;

  @Mock private DomainRequestService domainRequestService;

  @Mock private EventNotificationHelperService eventNotificationHelperService;

  @InjectMocks private EventNotificationMessageService eventNotificationMessageService;

  @Test
  void sendsLightSensorReportsLightEventToDomainTest() throws UnknownEntityException {
    final String deviceUid = "testUid";
    final String deviceIdentification = "testIdentification";
    final DateTime dateTime = DateTime.now();
    final EventTypeDto eventTypeDto = EventTypeDto.LIGHT_SENSOR_REPORTS_LIGHT;
    final String description = "Sensor reports light";
    final Integer index = 0;
    final EventNotificationDto eventNotificationDto =
        new EventNotificationDto(deviceUid, dateTime, eventTypeDto, description, index);

    this.eventNotificationMessageService.handleEvent(deviceIdentification, eventNotificationDto);

    final ArgumentMatcher<RequestMessage> matchesEventType =
        (final RequestMessage message) ->
            ((Event) message.getRequest()).getEventType() == EventType.LIGHT_SENSOR_REPORTS_LIGHT;

    verify(this.domainRequestService)
        .send(argThat(matchesEventType), eq(MessageType.EVENT_NOTIFICATION.name()), any());
  }
}
