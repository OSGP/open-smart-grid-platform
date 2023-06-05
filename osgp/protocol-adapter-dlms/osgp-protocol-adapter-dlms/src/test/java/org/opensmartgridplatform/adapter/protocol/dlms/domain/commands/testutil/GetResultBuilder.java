// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil;

import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;

/** Creates new instances, for testing purposes only!. */
public class GetResultBuilder {
  static int counter = 0;

  public GetResult build() {
    counter += 1;
    return new GetResultImpl(DataObject.newInteger32Data(100 + counter));
  }
}
