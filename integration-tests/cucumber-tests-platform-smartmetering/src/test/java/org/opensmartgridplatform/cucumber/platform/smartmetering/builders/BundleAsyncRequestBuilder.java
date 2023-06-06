// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.builders;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleAsyncRequest;

public class BundleAsyncRequestBuilder extends AbstractAsyncRequestBuilder<BundleAsyncRequest> {

  public BundleAsyncRequestBuilder() {
    super(BundleAsyncRequest.class);
  }

  @Override
  public BundleAsyncRequest build() {
    final BundleAsyncRequest result = new BundleAsyncRequest();
    result.setDeviceIdentification(this.deviceIdentification);
    result.setCorrelationUid(this.correlationUid);
    return result;
  }
}
