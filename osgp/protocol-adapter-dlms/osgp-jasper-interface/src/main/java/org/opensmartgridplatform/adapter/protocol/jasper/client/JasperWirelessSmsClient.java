package org.opensmartgridplatform.adapter.protocol.jasper.client;

import org.opensmartgridplatform.adapter.protocol.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.adapter.protocol.jasper.response.SendSMSResponse;

public interface JasperWirelessSmsClient {

  public SendSMSResponse sendWakeUpSMS(final String iccId) throws OsgpJasperException;
}
