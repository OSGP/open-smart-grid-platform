// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.microgrids.valueobjects;

import java.io.Serializable;

public class ReportIdentifier implements Serializable {

  private static final long serialVersionUID = -3960060997429091933L;

  private final String id;

  public ReportIdentifier(final String id) {
    this.id = id;
  }

  public String getId() {
    return this.id;
  }
}
