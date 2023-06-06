// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;

public class MeasurementIdentifierDto implements Serializable {

  private static final long serialVersionUID = 5587798706867134143L;

  private int id;
  private String node;

  public MeasurementIdentifierDto(final int id, final String node) {
    this.id = id;
    this.node = node;
  }

  public int getId() {
    return this.id;
  }

  public String getNode() {
    return this.node;
  }
}
