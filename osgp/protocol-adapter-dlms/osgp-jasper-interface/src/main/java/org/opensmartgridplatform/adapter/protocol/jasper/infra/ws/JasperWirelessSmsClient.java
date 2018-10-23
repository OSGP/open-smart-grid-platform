/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.infra.ws;

import java.util.List;

import org.apache.ws.security.WSConstants;
import org.opensmartgridplatform.adapter.protocol.jasper.config.JasperWirelessAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.soap.security.wss4j.Wss4jSecurityInterceptor;

import com.jasperwireless.api.ws.service.GetSMSDetailsRequest;
import com.jasperwireless.api.ws.service.GetSMSDetailsResponse;
import com.jasperwireless.api.ws.service.ObjectFactory;
import com.jasperwireless.api.ws.service.SendSMSRequest;
import com.jasperwireless.api.ws.service.SendSMSResponse;

public class JasperWirelessSmsClient {

    private static final String WAKEUPSMS_TYPE = "wakeupsms";

    private static final ObjectFactory WS_CLIENT_FACTORY = new ObjectFactory();

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private JasperWirelessAccess jasperWirelessAccess;

    public SendSMSResponse sendWakeUpSMS(final String iccid) {

        final SendSMSRequest sendSMSRequest = WS_CLIENT_FACTORY.createSendSMSRequest();
        sendSMSRequest.setLicenseKey(this.jasperWirelessAccess.getLicenseKey());
        sendSMSRequest.setMessageId(this.correlationIdProviderService.getCorrelationId(WAKEUPSMS_TYPE, iccid));
        sendSMSRequest.setMessageText("");
        sendSMSRequest.setMessageTextEncoding("");
        sendSMSRequest.setSentToIccid(iccid);
        sendSMSRequest.setVersion(this.jasperWirelessAccess.getApiVersion());

        for (final ClientInterceptor interceptor : this.webServiceTemplate.getInterceptors()) {
            if (interceptor instanceof Wss4jSecurityInterceptor) {
                setUsernameToken((Wss4jSecurityInterceptor) interceptor, this.jasperWirelessAccess.getUsername(),
                        this.jasperWirelessAccess.getPassword());
            }
        }

        // override default uri
        this.webServiceTemplate.setDefaultUri(this.jasperWirelessAccess.getUri());

        return (SendSMSResponse) this.webServiceTemplate.marshalSendAndReceive(sendSMSRequest, new SoapActionCallback(
                "http://api.jasperwireless.com/ws/service/sms/SendSMS"));
    }

    public GetSMSDetailsResponse getSMSDetails(final Long smsMessageId, final String iccid) {

        final GetSMSDetailsRequest getSMSDetailsRequest = WS_CLIENT_FACTORY.createGetSMSDetailsRequest();
        getSMSDetailsRequest.setLicenseKey(this.jasperWirelessAccess.getLicenseKey());
        getSMSDetailsRequest.setMessageId(this.correlationIdProviderService.getCorrelationId(WAKEUPSMS_TYPE, iccid));

        final GetSMSDetailsRequest.SmsMsgIds smsMsgIds = new GetSMSDetailsRequest.SmsMsgIds();
        final List<Long> smsMsgId = smsMsgIds.getSmsMsgId();
        smsMsgId.add(smsMessageId);
        getSMSDetailsRequest.setSmsMsgIds(smsMsgIds);
        getSMSDetailsRequest.setMessageTextEncoding("");
        getSMSDetailsRequest.setVersion(this.jasperWirelessAccess.getApiVersion());

        for (final ClientInterceptor interceptor : this.webServiceTemplate.getInterceptors()) {
            if (interceptor instanceof Wss4jSecurityInterceptor) {
                setUsernameToken((Wss4jSecurityInterceptor) interceptor, this.jasperWirelessAccess.getUsername(),
                        this.jasperWirelessAccess.getPassword());
            }
        }

        // override default uri
        this.webServiceTemplate.setDefaultUri(this.jasperWirelessAccess.getUri());

        return (GetSMSDetailsResponse) this.webServiceTemplate.marshalSendAndReceive(getSMSDetailsRequest,
                new SoapActionCallback("http://api.jasperwireless.com/ws/service/sms/GetSMSDetails"));
    }

    private static void setUsernameToken(final Wss4jSecurityInterceptor interceptor, final String user,
            final String pass) {
        interceptor.setSecurementUsername(user);
        interceptor.setSecurementPassword(pass);
        interceptor.setSecurementPasswordType(WSConstants.PW_TEXT);
    }
}
