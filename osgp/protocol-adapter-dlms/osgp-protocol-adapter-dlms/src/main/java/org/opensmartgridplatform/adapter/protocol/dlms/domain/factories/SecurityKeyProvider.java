// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
