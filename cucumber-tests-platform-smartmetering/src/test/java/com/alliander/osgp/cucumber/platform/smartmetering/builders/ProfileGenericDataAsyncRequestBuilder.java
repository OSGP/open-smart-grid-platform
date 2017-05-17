/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.builders;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataAsyncRequest;

public class ProfileGenericDataAsyncRequestBuilder extends AbstractAsyncRequestBuilder<ProfileGenericDataAsyncRequest> {

    public ProfileGenericDataAsyncRequestBuilder() {
        super(ProfileGenericDataAsyncRequest.class);
    }

    @Override
    public ProfileGenericDataAsyncRequest build() {
        final ProfileGenericDataAsyncRequest result = new ProfileGenericDataAsyncRequest();
        result.setDeviceIdentification(this.deviceIdentification);
        result.setCorrelationUid(this.correlationUid);
        return result;
    }

}
