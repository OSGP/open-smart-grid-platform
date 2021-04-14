/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ManagementMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventLogCategory;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FindEventsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FindEventsRequestDataList;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FindEventsRequestList;

public class FindEventsRequestMessageDataContainerMappingTest {

  private final ManagementMapper managementMapper = new ManagementMapper();

  // Test if mapping with a null List succeeds
  @Test
  public void testWithNullList() {
    // build test data
    final FindEventsRequestDataList container = new FindEventsRequestDataList(null);
    // actual mapping
    final FindEventsRequestList containerDto =
        this.managementMapper.map(container, FindEventsRequestList.class);
    // test mapping
    assertThat(containerDto).isNotNull();
    assertThat(containerDto.getFindEventsQueryList()).isNull();
  }

  // Test if mapping with an empty List succeeds
  @Test
  public void testWithEmptyList() {
    // build test data
    final FindEventsRequestDataList container =
        new FindEventsRequestDataList(new ArrayList<FindEventsRequestData>());
    // actual mapping
    final FindEventsRequestList containerDto =
        this.managementMapper.map(container, FindEventsRequestList.class);
    // test mapping
    assertThat(containerDto).isNotNull();
    assertThat(containerDto.getFindEventsQueryList()).isNotNull();
    assertThat(containerDto.getFindEventsQueryList()).isEmpty();
  }

  // Test if mapping with a non-empty List succeeds
  @Test
  public void testWithNonEmptyList() {
    // build test data
    final FindEventsRequestData findEventsQuery =
        new FindEventsRequestData(
            EventLogCategory.STANDARD_EVENT_LOG, new DateTime(), new DateTime());
    final ArrayList<FindEventsRequestData> findEventsQueryList = new ArrayList<>();
    findEventsQueryList.add(findEventsQuery);
    final FindEventsRequestDataList container = new FindEventsRequestDataList(findEventsQueryList);
    // actual mapping
    final FindEventsRequestList containerDto =
        this.managementMapper.map(container, FindEventsRequestList.class);
    // test mapping
    assertThat(containerDto).isNotNull();
    assertThat(containerDto.getFindEventsQueryList()).isNotNull();
    assertThat(containerDto.getFindEventsQueryList().get(0).getEventLogCategory().name())
        .isEqualTo(container.getFindEventsQueryList().get(0).getEventLogCategory().name());
    assertThat(containerDto.getFindEventsQueryList().get(0).getFrom())
        .isEqualTo(container.getFindEventsQueryList().get(0).getFrom());
    assertThat(containerDto.getFindEventsQueryList().get(0).getUntil())
        .isEqualTo(container.getFindEventsQueryList().get(0).getUntil());
  }
}
