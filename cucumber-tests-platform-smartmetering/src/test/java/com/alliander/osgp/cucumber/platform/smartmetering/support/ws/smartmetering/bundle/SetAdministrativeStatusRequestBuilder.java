package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetAdministrativeStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SetAdministrativeStatusRequestBuilder {

    private static final AdministrativeStatusType DEFAULT_STATUS_TYPE = AdministrativeStatusType.ON;

    private AdministrativeStatusType statusType;

    public SetAdministrativeStatusRequestBuilder withDefaults() {
        this.statusType = DEFAULT_STATUS_TYPE;
        return this;
    }

    public SetAdministrativeStatusRequestBuilder fromParameterMap(final Map<String, String> parameters) {
        this.statusType = this.getAdministrativeStatusType(parameters);
        return this;
    }

    public SetAdministrativeStatusRequest build() {
        final SetAdministrativeStatusRequest request = new SetAdministrativeStatusRequest();
        request.setAdministrativeStatusType(this.statusType);
        return request;
    }

    private AdministrativeStatusType getAdministrativeStatusType(final Map<String, String> parameters) {
        if (parameters.containsKey(PlatformSmartmeteringKeys.ADMINISTRATIVE_STATUS_TYPE)) {
            return AdministrativeStatusType
                    .fromValue(parameters.get(PlatformSmartmeteringKeys.ADMINISTRATIVE_STATUS_TYPE));
        }
        return DEFAULT_STATUS_TYPE;
    }
}
