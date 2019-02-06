/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging;

import javax.jms.ObjectMessage;

import org.opensmartgridplatform.shared.exceptionhandling.ProtocolAdapterException;

@FunctionalInterface
public interface Iec60870MessageProcessor {

    void processMessage(ObjectMessage message) throws ProtocolAdapterException;

}
