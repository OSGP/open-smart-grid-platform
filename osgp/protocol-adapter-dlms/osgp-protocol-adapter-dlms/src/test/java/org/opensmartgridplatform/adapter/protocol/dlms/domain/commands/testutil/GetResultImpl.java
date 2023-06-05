// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
