//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.infra.jms;

public class EndpointClassAndMethod {
  private final String className;
  private final String methodName;

  public EndpointClassAndMethod(final String className, final String methodName) {
    this.className = className;
    this.methodName = methodName;
  }

  public String getClassName() {
    return this.className;
  }

  public String getMethodName() {
    return this.methodName;
  }
}
