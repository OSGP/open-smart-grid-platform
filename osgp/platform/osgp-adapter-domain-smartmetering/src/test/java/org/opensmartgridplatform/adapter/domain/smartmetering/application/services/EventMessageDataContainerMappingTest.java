//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ManagementMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventMessagesResponse;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventMessageDataResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventTypeDto;

public class EventMessageDataContainerMappingTest {

  private final ManagementMapper managementMapper = new ManagementMapper();

  // Test if mapping with a null List succeeds
  @Test
  public void testWithNullList() {
    // build test data
    final EventMessageDataResponseDto containerDto = new EventMessageDataResponseDto(null);
    // actual mapping
    final EventMessagesResponse container =
        this.managementMapper.map(containerDto, EventMessagesResponse.class);
    // test mapping
    assertThat(container).isNotNull();
    assertThat(container.getEvents()).isNull();
  }

  // Test if mapping with an empty list succeeds
  @Test
  public void testWithEmptyList() {
    // build test data
    final EventMessageDataResponseDto containerDto =
        new EventMessageDataResponseDto(new ArrayList<EventDto>());
    // actual mapping
    final EventMessagesResponse container =
        this.managementMapper.map(containerDto, EventMessagesResponse.class);
    // test mapping
    assertThat(container).isNotNull();
    assertThat(container.getEvents()).isNotNull();
    assertThat(container.getEvents()).isEmpty();
  }

  // Test if mapping with a filled List succeeds
  @Test
  public void testWithFilledList() {
    // build test data
    final EventDto event =
        new EventDto(new DateTime(), new Integer(1), new Integer(2), "STANDARD_EVENT_LOG");
    event.setEventTypeDto(EventTypeDto.POWER_FAILURE);

    final ArrayList<EventDto> events = new ArrayList<>();
    events.add(event);
    final EventMessageDataResponseDto containerDto = new EventMessageDataResponseDto(events);
    // actual mapping
    final EventMessagesResponse container =
        this.managementMapper.map(containerDto, EventMessagesResponse.class);
    // test mapping
    assertThat(container).isNotNull();
    assertThat(container.getEvents()).isNotNull();
    assertThat(container.getEvents().get(0).getTimestamp())
        .isEqualTo(containerDto.getEvents().get(0).getTimestamp());
    assertThat(container.getEvents().get(0).getEventCode())
        .isEqualTo(containerDto.getEvents().get(0).getEventCode());
    assertThat(container.getEvents().get(0).getEventType().name())
        .isEqualTo(containerDto.getEvents().get(0).getEventTypeDto().name());
    assertThat(container.getEvents().get(0).getEventCounter())
        .isEqualTo(containerDto.getEvents().get(0).getEventCounter());
  }
}
