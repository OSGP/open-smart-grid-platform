/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class ActionResponse implements Serializable {

  private static final long serialVersionUID = -7017185186062612901L;

  private final OsgpResultType result;
  private final String exception;
  private final String resultString;

  protected ActionResponse() {
    this(null, null, null);
  }

  public ActionResponse(
      final OsgpResultType result, final String exception, final String resultString) {
    this.result = result;
    this.exception = exception;
    this.resultString = resultString;
  }

  public OsgpResultType getResult() {
    return this.result;
  }

  public String getException() {
    return this.exception;
  }

  public boolean hasException() {
    return this.exception != null;
  }

  public String getResultString() {
    return this.resultString;
  }
}
