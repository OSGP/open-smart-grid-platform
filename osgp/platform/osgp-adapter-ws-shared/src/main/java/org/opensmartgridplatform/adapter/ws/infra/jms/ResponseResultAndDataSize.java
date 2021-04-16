/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.infra.jms;

public class ResponseResultAndDataSize {
  private final String responseResult;
  private final int responseDataSize;

  public ResponseResultAndDataSize(final String responseResult, final int responseDataSize) {
    this.responseResult = responseResult;
    this.responseDataSize = responseDataSize;
  }

  public String getResult() {
    return this.responseResult;
  }

  public int getDataSize() {
    return this.responseDataSize;
  }
}
