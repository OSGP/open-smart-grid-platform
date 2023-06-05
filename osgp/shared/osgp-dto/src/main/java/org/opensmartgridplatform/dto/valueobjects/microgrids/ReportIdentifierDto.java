// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;

public class ReportIdentifierDto implements Serializable {

  private static final long serialVersionUID = -3960060997429091932L;

  private final String id;

  public ReportIdentifierDto(final String id) {
    this.id = id;
  }

  public String getId() {
    return this.id;
  }
}
