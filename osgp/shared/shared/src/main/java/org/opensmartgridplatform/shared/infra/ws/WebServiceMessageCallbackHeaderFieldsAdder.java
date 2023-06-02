//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.infra.ws;

import java.io.IOException;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

public class WebServiceMessageCallbackHeaderFieldsAdder implements WebServiceMessageCallback {

  private static final String NAMESPACE = "http://www.opensmartgridplatform.org/schemas/common";

  private final Map<String, String> keyValues;

  public WebServiceMessageCallbackHeaderFieldsAdder(final Map<String, String> keyValues) {
    this.keyValues = keyValues;
  }

  @Override
  public void doWithMessage(final WebServiceMessage message)
      throws IOException, TransformerException {

    final SoapMessage soapMessage = (SoapMessage) message;
    final SoapHeader header = soapMessage.getSoapHeader();

    this.keyValues
        .entrySet()
        .forEach(
            k -> {
              final SoapHeaderElement headerElement =
                  header.addHeaderElement(new QName(NAMESPACE, k.getKey()));
              headerElement.setText(k.getValue());
            });
  }
}
