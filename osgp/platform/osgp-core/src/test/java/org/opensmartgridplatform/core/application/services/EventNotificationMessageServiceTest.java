/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.core.application.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.opensmartgridplatform.core.domain.model.domain.DomainRequestService;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderTimestampService;

public class EventNotificationMessageServiceTest {

    @Mock
    private CorrelationIdProviderTimestampService correlationIdProviderTimestampService;

    @Mock
    private String netmanagementOrganisation;

    @Mock
    private DomainRequestService domainRequestService;

    @Mock
    private EventNotificationHelperService eventNotificationHelperService;

    @InjectMocks
    private EventNotificationMessageService eventNotificationMessageService;

    @Test
    void handleEventTest() {
        //EventNotificationDto eventNotificationDto = new EventNotificationDto();
        //this.eventNotificationMessageService.handleEvent(final String deviceIdentification, final EventNotificationDto event)
    }
}
