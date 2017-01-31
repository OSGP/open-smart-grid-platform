/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.entities;

public enum SecurityKeyType {
    E_METER_MASTER,
    G_METER_MASTER,
    E_METER_AUTHENTICATION,
    E_METER_ENCRYPTION,
    G_METER_ENCRYPTION,
    M_BUS_USER,
    PASSWORD
}
