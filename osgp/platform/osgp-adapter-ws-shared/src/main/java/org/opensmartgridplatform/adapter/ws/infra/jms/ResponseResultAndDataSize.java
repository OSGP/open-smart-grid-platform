// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
