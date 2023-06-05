// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FaultResponseParameters implements Serializable {

  private static final long serialVersionUID = 8795642120627073707L;

  private final List<FaultResponseParameter> parameterList;

  public FaultResponseParameters(final List<FaultResponseParameter> parameterList) {
    this.parameterList = new ArrayList<>(parameterList);
  }

  @Override
  public String toString() {
    return "FaultResponseParameters[" + this.parameterList + "]";
  }

  public List<FaultResponseParameter> getParameterList() {
    return new ArrayList<>(this.parameterList);
  }
}
