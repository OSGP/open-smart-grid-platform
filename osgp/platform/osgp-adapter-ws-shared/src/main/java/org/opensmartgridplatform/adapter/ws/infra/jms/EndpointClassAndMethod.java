/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
