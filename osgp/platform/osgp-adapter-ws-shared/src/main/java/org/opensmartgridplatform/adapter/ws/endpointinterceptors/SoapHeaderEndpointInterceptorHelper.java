/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
