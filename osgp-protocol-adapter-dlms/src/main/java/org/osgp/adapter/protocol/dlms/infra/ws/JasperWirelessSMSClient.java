package org.osgp.adapter.protocol.dlms.infra.ws;

import com.jasperwireless.api.ws.service.sms.GetSMSDetailsResponse;
import com.jasperwireless.api.ws.service.sms.SendSMSResponse;

public interface JasperWirelessSMSClient {

    public SendSMSResponse sendWakeUpSMS(final String iccid);

    public GetSMSDetailsResponse getSMSDetails(final String smsMessageId, final String iccid);
}
