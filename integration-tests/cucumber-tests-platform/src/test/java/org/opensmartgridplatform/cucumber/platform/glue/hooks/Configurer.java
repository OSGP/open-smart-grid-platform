//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.glue.hooks;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DefaultDataTableCellTransformer;
import io.cucumber.java.DefaultDataTableEntryTransformer;
import io.cucumber.java.DefaultParameterTransformer;
import java.lang.reflect.Type;

public class Configurer {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @DefaultParameterTransformer
  @DefaultDataTableEntryTransformer
  @DefaultDataTableCellTransformer
  public Object defaultTransformer(final Object fromValue, final Type toValueType) {
    final JavaType javaType = this.objectMapper.constructType(toValueType);
    return this.objectMapper.convertValue(fromValue, javaType);
  }
}
