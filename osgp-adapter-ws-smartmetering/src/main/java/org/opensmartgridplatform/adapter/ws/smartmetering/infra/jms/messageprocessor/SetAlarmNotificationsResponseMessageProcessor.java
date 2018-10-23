/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.messageprocessor;

import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

/**
 * Class for processing smart metering set alarm notifications response messages
 */
@Component
public class SetAlarmNotificationsResponseMessageProcessor extends DomainResponseMessageProcessor {

    protected SetAlarmNotificationsResponseMessageProcessor() {
        super(MessageType.SET_ALARM_NOTIFICATIONS);
    }
}
