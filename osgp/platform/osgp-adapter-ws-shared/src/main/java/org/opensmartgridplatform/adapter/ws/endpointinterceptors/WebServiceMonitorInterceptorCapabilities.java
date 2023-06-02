//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.endpointinterceptors;

public class WebServiceMonitorInterceptorCapabilities {

  boolean soapMessageLoggingEnabled;
  boolean soapMessagePrintingEnabled;

  public WebServiceMonitorInterceptorCapabilities(
      final boolean soapMessageLoggingEnabled, final boolean soapMessagePrintingEnabled) {
    this.soapMessageLoggingEnabled = soapMessageLoggingEnabled;
    this.soapMessagePrintingEnabled = soapMessagePrintingEnabled;
  }

  public boolean isSoapMessageLoggingEnabled() {
    return this.soapMessageLoggingEnabled;
  }

  public boolean isSoapMessagePrintingEnabled() {
    return this.soapMessagePrintingEnabled;
  }

  @Override
  public String toString() {
    return "WebServiceMonitorInterceptorCapabilities [soapMessageLoggingEnabled="
        + this.soapMessageLoggingEnabled
        + ", soapMessagePrintingEnabled="
        + this.soapMessagePrintingEnabled
        + "]";
  }
}
