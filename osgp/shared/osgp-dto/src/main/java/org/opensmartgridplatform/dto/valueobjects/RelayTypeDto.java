// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

public enum RelayTypeDto {
  LIGHT {
    @Override
    public DomainTypeDto domainType() {
      return DomainTypeDto.PUBLIC_LIGHTING;
    }
  },
  TARIFF {
    @Override
    public DomainTypeDto domainType() {
      return DomainTypeDto.TARIFF_SWITCHING;
    }
  },
  TARIFF_REVERSED {
    @Override
    public DomainTypeDto domainType() {
      return DomainTypeDto.TARIFF_SWITCHING;
    }
  };

  public abstract DomainTypeDto domainType();
}
