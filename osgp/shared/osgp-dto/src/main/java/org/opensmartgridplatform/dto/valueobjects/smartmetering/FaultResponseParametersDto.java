// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FaultResponseParametersDto implements Serializable {

  private static final long serialVersionUID = 3530067384508337669L;

  private final List<FaultResponseParameterDto> parameterList;

  public FaultResponseParametersDto(final List<FaultResponseParameterDto> parameterList) {
    this.parameterList = new ArrayList<>(parameterList);
  }

  @Override
  public String toString() {
    return "FaultResponseParametersDto[" + this.parameterList + "]";
  }

  public List<FaultResponseParameterDto> getParameterList() {
    return new ArrayList<>(this.parameterList);
  }
}
