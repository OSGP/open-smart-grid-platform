/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.repositories.EventRepository;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

public class TransactionalEventServiceTest {

  private final Date now = DateTime.now().toDate();

  @InjectMocks private TransactionalEventService transactionalEventService;

  @Mock private EventRepository eventRepository;

  @BeforeEach
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void serviceReturnsOneEvent() {
    final Slice<Event> mockSlice = this.mockSliceOfEvents(1);
    final PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.ASC, "id");
    Mockito.when(this.eventRepository.findByDateTimeBefore(this.now, pageRequest))
        .thenReturn(mockSlice);

    final List<Event> events = this.transactionalEventService.getEventsBeforeDate(this.now, 10);
    assertThat(events.size()).isEqualTo(1);
  }

  @Test
  public void serviceReturnsTenEvents() {
    final Slice<Event> mockSlice = this.mockSliceOfEvents(10);
    final PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.ASC, "id");
    Mockito.when(this.eventRepository.findByDateTimeBefore(this.now, pageRequest))
        .thenReturn(mockSlice);

    final List<Event> events = this.transactionalEventService.getEventsBeforeDate(this.now, 10);
    assertThat(events.size()).isEqualTo(10);
  }

  @Test
  public void serviceDeletesEvents() {
    final List<Event> events = this.mockSliceOfEvents(10).getContent();

    try {
      this.transactionalEventService.deleteEvents(events);
    } catch (final Exception e) {
      fail("Unexpected exception! " + e.getMessage());
    }
  }

  private Slice<Event> mockSliceOfEvents(final int numberOfEvents) {
    final Date oneMonthAgo = DateTime.now().minusMonths(1).toDate();

    final List<Event> events = new ArrayList<>();
    for (int i = 0; i < numberOfEvents; i++) {
      final Event event =
          new Event("test", oneMonthAgo, EventType.DIAG_EVENTS_GENERAL, "description", 1);
      events.add(event);
    }

    return new SliceImpl<>(events);
  }
}
