package org.opensmartgridplatform.secretmgmt.serviceclient;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class SoapConnector extends WebServiceGatewaySupport {

    public Object callWebService(String url, Object request){
        return getWebServiceTemplate().marshalSendAndReceive(url, request);
    }
}
