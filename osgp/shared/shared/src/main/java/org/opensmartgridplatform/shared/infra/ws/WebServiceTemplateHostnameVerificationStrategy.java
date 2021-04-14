/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.ws;

public enum WebServiceTemplateHostnameVerificationStrategy {

  /**
   * No verification of hostnames. Turns hostname verification off. This implementation is a no-op,
   * and never throws an SSLException.
   */
  ALLOW_ALL_HOSTNAMES,

  /**
   * The HostnameVerifier that works the same way as Curl and Firefox. The hostname must match
   * either the first CN, or any of the subject-alts. A wildcard can occur in the CN, and in any of
   * the subject-alts.
   */
  BROWSER_COMPATIBLE_HOSTNAMES
}
