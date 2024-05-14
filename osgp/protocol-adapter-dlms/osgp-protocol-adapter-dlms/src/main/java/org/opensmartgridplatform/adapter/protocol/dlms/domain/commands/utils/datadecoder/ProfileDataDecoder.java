// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.datadecoder;

import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "profileDataDecoder")
public class ProfileDataDecoder {

  @Autowired private DlmsHelper dlmsHelper;

  public String decodeAttributeValue(
      final CosemObject objectFromProfile,
      final Attribute attributeFromProfile,
      final DataObject attributeData) {

    return null;
  }
}
