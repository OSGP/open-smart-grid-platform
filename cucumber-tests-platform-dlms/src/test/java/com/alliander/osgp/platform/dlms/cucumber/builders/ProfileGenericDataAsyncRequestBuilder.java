package com.alliander.osgp.platform.dlms.cucumber.builders;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataAsyncRequest;

public class ProfileGenericDataAsyncRequestBuilder extends AbstractAsyncRequestBuilder {

    public ProfileGenericDataAsyncRequestBuilder() {
        super(ProfileGenericDataAsyncRequest.class);
    }

    @Override
    public ProfileGenericDataAsyncRequest build() {
        ProfileGenericDataAsyncRequest result = new ProfileGenericDataAsyncRequest();
        result.setDeviceIdentification(this.deviceIdentification);
        result.setCorrelationUid(this.correlationUid);
        return result;
    }

}
