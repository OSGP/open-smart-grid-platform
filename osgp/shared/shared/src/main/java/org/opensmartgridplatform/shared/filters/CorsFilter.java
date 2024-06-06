// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/** Filter which allows all cross-origin requests (for testing purposes. */
@Provider
public class CorsFilter implements ContainerResponseFilter {

  private static final String ACCESS_CONTROL_ALLOW_HEADER = "Access-Control-Allow-Origin";

  @Override
  public void filter(
      final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
      throws IOException {
    responseContext.getHeaders().putSingle(ACCESS_CONTROL_ALLOW_HEADER, "*");
  }
}
