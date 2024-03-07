// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/** Jax-RS filter which disables caching of responses. */
@Provider
public class NoCacheResponseFilter implements ContainerResponseFilter {

  /** Apply no chaching headers to response message. */
  @Override
  public void filter(
      final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
      throws IOException {
    // Disable caching
    final CacheControl cc = new CacheControl();
    cc.setNoCache(true);
    cc.setNoStore(true);
    cc.setMustRevalidate(true);
    cc.setSMaxAge(0);

    // Set cache control header
    responseContext.getHeaders().putSingle(HttpHeaders.CACHE_CONTROL, cc.toString());
  }
}
