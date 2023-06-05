// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories;

import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class FirmwareImageIdentifierCachingRepository extends ByteArrayCachingRepository {

  public FirmwareImageIdentifierCachingRepository() {
    // Public constructor
  }

  protected FirmwareImageIdentifierCachingRepository(final Map<String, byte[]> cache) {
    // Protected constructor for testing
    super(cache);
  }
}
