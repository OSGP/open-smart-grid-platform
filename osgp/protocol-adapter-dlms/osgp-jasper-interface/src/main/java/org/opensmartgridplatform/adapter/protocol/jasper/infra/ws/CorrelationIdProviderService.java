// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.infra.ws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CorrelationIdProviderService {

  private static final String SEPARATOR = "|||";

  public String getCorrelationId(final String type, final String iccid) {

    return type
        + SEPARATOR
        + iccid
        + SEPARATOR
        + this.getCurrentDateString()
        + SEPARATOR
        + UUID.randomUUID();
  }

  private String getCurrentDateString() {
    final Date now = new Date();
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmmssSSS");
    return sdf.format(now);
  }
}
