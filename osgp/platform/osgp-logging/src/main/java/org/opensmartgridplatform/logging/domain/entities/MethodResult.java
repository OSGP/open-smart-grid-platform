//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
