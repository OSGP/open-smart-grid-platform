/**
 * Copyright 2017 Smart Society Services B.V.
 */
package com.alliander.osgp.shared.infra.ws;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

public class MessagePriorityWebServiceMessageCallback implements WebServiceMessageCallback {

    private static final String NAMESPACE = "http://www.alliander.com/schemas/osp/common";

    private final int messagePriority;

    public MessagePriorityWebServiceMessageCallback(final int messagePriority) {
        this.messagePriority = messagePriority;
    }

    @Override
    public void doWithMessage(final WebServiceMessage message) throws IOException, TransformerException {

        final SoapMessage soapMessage = (SoapMessage) message;
        final SoapHeader header = soapMessage.getSoapHeader();
        final SoapHeaderElement messagePriorityElement = header
                .addHeaderElement(new QName(NAMESPACE, "MessagePriority"));
        messagePriorityElement.setText(String.valueOf(this.messagePriority));
    }
}