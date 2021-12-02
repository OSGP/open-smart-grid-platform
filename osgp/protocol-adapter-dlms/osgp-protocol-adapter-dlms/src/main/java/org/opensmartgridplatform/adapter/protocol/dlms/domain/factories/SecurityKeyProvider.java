/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

/**
 * Functional interface used in connectors to obtain keys.
 *
 * @see SecureDlmsConnector
 */
@FunctionalInterface
public interface SecurityKeyProvider {
  Map<SecurityKeyType, byte[]> getKeys(
      final MessageMetadata messageMetadata,
      String deviceIdentification,
      List<SecurityKeyType> keyTypes);
}
