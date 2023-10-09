// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import java.util.List;
import lombok.Getter;

@Getter
public class TypeBasedValue {
  private List<String> types;
  private String value;
}
