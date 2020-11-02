/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.opensmartgridplatform.adapter.domain.core.application.config.PersistenceDomainCoreConfig;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.repositories.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service uses {@link PersistenceDomainCoreConfig} application
 * configuration class.
 */
@Service
@Transactional(transactionManager = "transactionManager")
public class TransactionalEventService {

    private static final int PAGE_SIZE = 500;

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalEventService.class);

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getEventsBeforeDate(final Date date, final int pageSize) {
        final PageRequest pageRequest = PageRequest.of(0, pageSize, Sort.Direction.ASC, "id");
        final Slice<Event> slice = this.eventRepository.findByDateTimeBefore(date, pageRequest);
        final List<Event> events = slice.getContent();
        LOGGER.info("Found {} events with date time before {}.", events.size(), date);

        return events;
    }

    public void deleteEvents(final List<Event> events) {
        final int size = events.size();
        final List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());

        final int listSize = ids.size();

        for (int pageNumber = 0; pageNumber < (listSize + PAGE_SIZE - 1) / PAGE_SIZE; pageNumber++) {
            final int fromIndex = pageNumber * PAGE_SIZE;
            final int toIndex = Math.min(listSize, pageNumber * PAGE_SIZE + PAGE_SIZE);

            final List<Long> sublistToDelete = ids.subList(fromIndex, toIndex);
            this.eventRepository.deleteBatchById(sublistToDelete);
        }

        LOGGER.info("{} events deleted.", size);
    }

}
