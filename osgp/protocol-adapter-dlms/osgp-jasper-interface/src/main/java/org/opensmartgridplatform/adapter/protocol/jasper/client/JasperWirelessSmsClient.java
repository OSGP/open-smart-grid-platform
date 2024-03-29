// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.client;

import org.opensmartgridplatform.adapter.protocol.jasper.response.SendSMSResponse;
import org.opensmartgridplatform.jasper.exceptions.OsgpJasperException;

public interface JasperWirelessSmsClient {

  public SendSMSResponse sendWakeUpSMS(final String iccId) throws OsgpJasperException;
}
