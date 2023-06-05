// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

public class OslpCallbackHandler {

  private final OslpResponseHandler responseHandler;

  public OslpCallbackHandler(final OslpResponseHandler responseHandler) {
    this.responseHandler = responseHandler;
  }

  protected OslpResponseHandler getDeviceResponseHandler() {
    return this.responseHandler;
  }
}
