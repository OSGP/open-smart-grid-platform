/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.rest.client;

public class DlmsAttributeValuesClientException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = -6599182946786998965L;

  public DlmsAttributeValuesClientException(final String message) {
    super(message);
  }

  public DlmsAttributeValuesClientException(final String message, final Throwable t) {
    super(message, t);
  }
}
