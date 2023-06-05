// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.da.iec61850;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogicalNodeDto implements Serializable {
  private static final long serialVersionUID = -5205998771499617879L;

  private final String name;
  private final List<DataSampleDto> data;

  public LogicalNodeDto(final String name, final List<DataSampleDto> data) {
    this.name = name;
    this.data = data;
  }

  public String getName() {
    return this.name;
  }

  public List<DataSampleDto> getData() {
    return Collections.unmodifiableList(this.data != null ? this.data : new ArrayList<>());
  }
}
