/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.logging.domain.entities;

public class MethodResult {
  private final String applicationName;
  private final String className;
  private final String methodName;
  private final String responseResult;
  private final int responseDataSize;

  public MethodResult(
      final String applicationName,
      final String className,
      final String methodName,
      final String responseResult,
      final int responseDataSize) {
    this.applicationName = applicationName;
    this.className = className;
    this.methodName = methodName;
    this.responseResult = responseResult;
    this.responseDataSize = responseDataSize;
  }

  public String getApplicationName() {
    return this.applicationName;
  }

  public String getClassName() {
    return this.className;
  }

  public String getMethodName() {
    return this.methodName;
  }

  public String getResponseResult() {
    return this.responseResult;
  }

  public int getResponseDataSize() {
    return this.responseDataSize;
  }
}
