/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.filters;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

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
