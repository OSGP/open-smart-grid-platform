/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.infra.ws;

import java.util.List;

import org.apache.ws.security.WSConstants;
import org.opensmartgridplatform.adapter.protocol.jasper.config.JasperWirelessAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;

import com.jasperwireless.api.ws.service.GetSMSDetailsRequest;
import com.jasperwireless.api.ws.service.GetSMSDetailsResponse;
import com.jasperwireless.api.ws.service.ObjectFactory;
import com.jasperwireless.api.ws.service.SendSMSRequest;
import com.jasperwireless.api.ws.service.SendSMSResponse;

public class JasperWirelessSmsClient {

    private static final String WAKEUPSMS_TYPE = "wakeupsms";

    private static final ObjectFactory WS_CLIENT_FACTORY = new ObjectFactory();
    private static final String SERVICE_GET_SMSDETAILS = "http://api.jasperwireless.com/ws/service/sms/GetSMSDetails";
    private static final String SERVICE_SMS_SEND_SMS = "http://api.jasperwireless.com/ws/service/sms/SendSMS";

    @Autowired
    private WebServiceTemplate jasperWebServiceTemplate;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private JasperWirelessAccess jasperWirelessAccess;

    public SendSMSResponse sendWakeUpSMS(final String iccId) {

        final SendSMSRequest sendSMSRequest = WS_CLIENT_FACTORY.createSendSMSRequest();
        sendSMSRequest.setLicenseKey(this.jasperWirelessAccess.getLicenseKey());
        sendSMSRequest.setMessageId(this.correlationIdProviderService.getCorrelationId(WAKEUPSMS_TYPE, iccId));
        sendSMSRequest.setMessageText("");
        sendSMSRequest.setMessageTextEncoding("");
        sendSMSRequest.setSentToIccid(iccId);
        sendSMSRequest.setVersion(this.jasperWirelessAccess.getApiVersion());

        this.setInterceptorUsernameTokens();

        // override default uri
        this.jasperWebServiceTemplate.setDefaultUri(this.jasperWirelessAccess.getUri());

        return (SendSMSResponse) this.jasperWebServiceTemplate.marshalSendAndReceive(sendSMSRequest,
                new SoapActionCallback(SERVICE_SMS_SEND_SMS));
    }

    private void setInterceptorUsernameTokens() {

        for (final ClientInterceptor interceptor : this.jasperWebServiceTemplate.getInterceptors()) {
            if (interceptor instanceof Wss4jSecurityInterceptor) {
                this.setUsernameToken((Wss4jSecurityInterceptor) interceptor, this.jasperWirelessAccess.getUsername(),
                        this.jasperWirelessAccess.getPassword());
            }
        }
    }

    GetSMSDetailsResponse getSMSDetails(final Long smsMessageId, final String iccId) {

        final GetSMSDetailsRequest getSMSDetailsRequest = WS_CLIENT_FACTORY.createGetSMSDetailsRequest();
        getSMSDetailsRequest.setLicenseKey(this.jasperWirelessAccess.getLicenseKey());
        getSMSDetailsRequest.setMessageId(this.correlationIdProviderService.getCorrelationId(WAKEUPSMS_TYPE, iccId));

        final GetSMSDetailsRequest.SmsMsgIds smsMsgIds = new GetSMSDetailsRequest.SmsMsgIds();
        final List<Long> smsMsgId = smsMsgIds.getSmsMsgId();
        smsMsgId.add(smsMessageId);
        getSMSDetailsRequest.setSmsMsgIds(smsMsgIds);
        getSMSDetailsRequest.setMessageTextEncoding("");
        getSMSDetailsRequest.setVersion(this.jasperWirelessAccess.getApiVersion());

        this.setInterceptorUsernameTokens();

        // override default uri
        this.jasperWebServiceTemplate.setDefaultUri(this.jasperWirelessAccess.getUri());

        return (GetSMSDetailsResponse) this.jasperWebServiceTemplate.marshalSendAndReceive(getSMSDetailsRequest,
                new SoapActionCallback(SERVICE_GET_SMSDETAILS));
    }

    private void setUsernameToken(final Wss4jSecurityInterceptor interceptor, final String user, final String pass) {
        interceptor.setSecurementUsername(user);
        interceptor.setSecurementPassword(pass);
        interceptor.setSecurementPasswordType(WSConstants.PW_TEXT);
    }
}
