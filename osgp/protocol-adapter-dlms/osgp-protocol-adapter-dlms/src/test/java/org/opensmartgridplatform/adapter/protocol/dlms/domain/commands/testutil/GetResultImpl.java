/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;

/** Basic GetResult implementation, for testing purposes only! */
public class GetResultImpl implements GetResult {
  private final DataObject resultData;
  private final AccessResultCode accessResultCode;

  public GetResultImpl(final DataObject resultData) {
    this.resultData = resultData;
    this.accessResultCode = AccessResultCode.SUCCESS;
  }

  public GetResultImpl(final DataObject resultData, final AccessResultCode accessResultCode) {
    this.resultData = resultData;
    this.accessResultCode = accessResultCode;
  }

  @Override
  public DataObject getResultData() {
    return this.resultData;
  }

  @Override
  public AccessResultCode getResultCode() {
    return this.accessResultCode;
  }

  @Override
  public boolean requestSuccessful() {
    throw new UnsupportedOperationException("Not implemented");
  }
}
