// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObisCode;

public class CosemObisCodeConverter extends CustomConverter<CosemObisCode, byte[]> {
  @Override
  public byte[] convert(
      final CosemObisCode arg0, final Type<? extends byte[]> arg1, final MappingContext context) {
    return arg0.toByteArray();
  }
}
