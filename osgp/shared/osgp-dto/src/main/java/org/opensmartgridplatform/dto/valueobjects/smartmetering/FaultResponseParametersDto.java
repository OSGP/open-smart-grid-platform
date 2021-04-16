/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
