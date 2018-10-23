/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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
