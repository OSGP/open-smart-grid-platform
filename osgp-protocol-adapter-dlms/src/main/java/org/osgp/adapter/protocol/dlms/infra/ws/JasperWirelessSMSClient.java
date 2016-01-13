package org.osgp.adapter.protocol.dlms.infra.ws;

import com.jasperwireless.api.ws.service.sms.SendSMSResponse;

public interface JasperWirelessSMSClient {

    public SendSMSResponse sendWakeUpSMS(final String iccid);
}
