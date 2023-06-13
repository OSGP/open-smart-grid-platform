// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.endpointinterceptors;

import java.util.Iterator;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;

public class SoapHeaderEndpointInterceptorHelper {

  private SoapHeaderEndpointInterceptorHelper() {}

  public static String getHeaderValue(final SoapHeader soapHeader, final String valueName) {
    String value = "";
    final Iterator<SoapHeaderElement> iterator = soapHeader.examineAllHeaderElements();

    while (iterator.hasNext()) {
      final SoapHeaderElement element = iterator.next();

      if (element.getName().getLocalPart().equals(valueName)) {
        value = element.getText();
        break;
      }
    }

    return value;
  }
}
