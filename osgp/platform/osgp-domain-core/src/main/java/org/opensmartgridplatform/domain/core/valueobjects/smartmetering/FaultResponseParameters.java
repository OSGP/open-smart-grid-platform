/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
