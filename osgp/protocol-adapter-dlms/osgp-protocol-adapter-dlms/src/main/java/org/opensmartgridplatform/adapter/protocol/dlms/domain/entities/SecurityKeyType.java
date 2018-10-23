/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.entities;

public enum SecurityKeyType {
    /**
     * DLMS master key (Key Encryption Key)
     */
    E_METER_MASTER,
    /**
     * DLMS authentication key
     */
    E_METER_AUTHENTICATION,
    /**
     * DLMS global unicast encryption key
     */
    E_METER_ENCRYPTION,
    /**
     * M-Bus Default key
     */
    G_METER_MASTER,
    /**
     * M-Bus User key
     */
    G_METER_ENCRYPTION,
    /**
     * Password (e.g. used as DLMS Low Level Security secret)
     */
    PASSWORD
}
