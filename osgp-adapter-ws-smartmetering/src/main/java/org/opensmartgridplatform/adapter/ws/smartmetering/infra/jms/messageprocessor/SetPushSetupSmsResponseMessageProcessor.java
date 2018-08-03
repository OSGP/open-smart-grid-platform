/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.messageprocessor;

import org.springframework.stereotype.Component;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;

/**
 * Class for processing smart metering set push setup sms response messages
 */
@Component
public class SetPushSetupSmsResponseMessageProcessor extends DomainResponseMessageProcessor {

    protected SetPushSetupSmsResponseMessageProcessor() {
        super(DeviceFunction.SET_PUSH_SETUP_SMS);
    }
}
