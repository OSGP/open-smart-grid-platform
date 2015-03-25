package com.alliander.osgp.adapter.protocol.oslp.infra.networking;

import com.alliander.osgp.oslp.OslpEnvelope;

public interface OslpResponseHandler {

    void handleResponse(OslpEnvelope oslpResponse);

    void handleException(Throwable t);

}
