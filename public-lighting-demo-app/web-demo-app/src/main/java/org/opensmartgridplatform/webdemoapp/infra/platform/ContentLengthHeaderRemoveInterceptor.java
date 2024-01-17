// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdemoapp.infra.platform;

import java.io.IOException;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;

public class ContentLengthHeaderRemoveInterceptor implements HttpRequestInterceptor {

  @Override
  public void process(
      final HttpRequest httpRequest,
      final EntityDetails entityDetails,
      final HttpContext httpContext)
      throws HttpException, IOException {
    httpRequest.removeHeaders(HttpHeaders.CONTENT_LENGTH);
  }
}
