/**
 * Copyright 2021 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;

/**
 * Functional interface used in connectors to obtain keys.
 * @see Hls5Connector
 */
public interface SecurityKeyProvider {
    byte[] getKey(String deviceIdentification, SecurityKeyType type);
}
