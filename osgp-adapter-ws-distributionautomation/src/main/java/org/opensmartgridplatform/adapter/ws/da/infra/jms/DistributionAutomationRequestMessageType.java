/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.da.infra.jms;

/**
 * Enumeration of da request message types
 */
public enum DistributionAutomationRequestMessageType {
    GET_POWER_QUALITY_VALUES,
    GET_POWER_QUALITY_VALUES_PERIODIC,
    GET_DEVICE_MODEL,
    GET_HEALTH_STATUS
}
