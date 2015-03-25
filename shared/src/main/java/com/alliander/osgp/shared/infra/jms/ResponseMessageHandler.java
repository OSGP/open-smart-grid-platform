package com.alliander.osgp.shared.infra.jms;

public interface ResponseMessageHandler {

    void handleResponse(ResponseMessage responseMessage);
}
