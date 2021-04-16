/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
