// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.services.objectconfig;

import lombok.Builder;
import lombok.Getter;
import org.opensmartgridplatform.dlms.objectconfig.AccessType;
import org.opensmartgridplatform.dlms.objectconfig.DlmsDataType;
import org.opensmartgridplatform.dlms.objectconfig.ValueBasedOnModel;
import org.opensmartgridplatform.dlms.objectconfig.ValueType;

@Getter
@Builder
public class AttributeValidatorDTO {
  private int id;
  private String description;
  private String note;
  private DlmsDataType datatype;
  private ValueType valuetype;
  private String value;
  private ValueBasedOnModel valuebasedonmodel;
  private AccessType access;
}
