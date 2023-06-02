//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories;

import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class FirmwareFileCachingRepository extends ByteArrayCachingRepository {

  public FirmwareFileCachingRepository() {
    // Public constructor
  }

  protected FirmwareFileCachingRepository(final Map<String, byte[]> cache) {
    // Protected constructor for testing
    super(cache);
  }
}
