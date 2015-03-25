package com.alliander.osgp.shared.infra.jms;

public interface ResponseMessageSender {

    void send(ResponseMessage responseMessage);
}
