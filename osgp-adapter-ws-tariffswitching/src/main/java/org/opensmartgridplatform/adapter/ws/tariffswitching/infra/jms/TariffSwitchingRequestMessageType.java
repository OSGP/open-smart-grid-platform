/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.tariffswitching.infra.jms;

/**
 * Enumeration of tariff switching request message types
 */
public enum TariffSwitchingRequestMessageType {
    SET_TARIFF, GET_TARIFF_STATUS, SET_TARIFF_SCHEDULE, RESUME_SCHEDULE
}
