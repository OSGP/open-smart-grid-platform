/**
 * Copyright 2012-2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.support.ws;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

public class ContentLengthHeaderRemoveInterceptor implements HttpRequestInterceptor {

  @Override
  public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
    // Remove Content Length Header from request
    request.removeHeaders(HTTP.CONTENT_LEN);
  }
}
