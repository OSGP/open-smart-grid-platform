package org.osgp.adapter.protocol.dlms.infra.ws;

import org.osgp.adapter.protocol.dlms.application.config.JwccWSConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.security.wss4j.Wss4jSecurityInterceptor;

import com.jasperwireless.api.ws.service.sms.ObjectFactory;
import com.jasperwireless.api.ws.service.sms.SendSMSRequest;
import com.jasperwireless.api.ws.service.sms.SendSMSResponse;

@Service
public class JasperWirelessSMSClientImpl implements JasperWirelessSMSClient {

    private static final ObjectFactory WS_CLIENT_FACTORY = new ObjectFactory();

    @Autowired
    JwccWSConfig jwccWSConfig;

    @Autowired
    WebServiceTemplate webServiceTemplate;

    @Override
    public SendSMSResponse sendWakeUpSMS(final String iccid) {

        final SendSMSRequest sendSMSRequest = WS_CLIENT_FACTORY.createSendSMSRequest();
        sendSMSRequest.setLicenseKey(this.jwccWSConfig.getLicenseKey());
        sendSMSRequest.setMessageId("");
        sendSMSRequest.setMessageText("");
        sendSMSRequest.setMessageTextEncoding("");
        sendSMSRequest.setSentToIccid(iccid);
        sendSMSRequest.setVersion(this.jwccWSConfig.getApi_version());

        for (final ClientInterceptor interceptor : this.webServiceTemplate.getInterceptors()) {
            if (interceptor instanceof Wss4jSecurityInterceptor) {
                setUsernameToken((Wss4jSecurityInterceptor) interceptor, this.jwccWSConfig.getUsername(),
                        this.jwccWSConfig.getPassword());
            }
        }

        // override default uri
        this.webServiceTemplate.setDefaultUri(this.jwccWSConfig.getSms_uri());

        return (SendSMSResponse) this.webServiceTemplate.marshalSendAndReceive(sendSMSRequest);

    }

    private static void setUsernameToken(final Wss4jSecurityInterceptor interceptor, final String user,
            final String pass) {
        interceptor.setSecurementUsername(user);
        interceptor.setSecurementPassword(pass);
    }
}
