package com.alliander.osgp.adapter.protocol.oslp.infra.networking;


public class OslpCallbackHandler {

    private final OslpResponseHandler responseHandler;

    public OslpCallbackHandler(final OslpResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    protected OslpResponseHandler getDeviceResponseHandler() {
        return this.responseHandler;
    }
}
