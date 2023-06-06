// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

public enum RelayType {
  LIGHT {
    @Override
    public DomainType domainType() {
      return DomainType.PUBLIC_LIGHTING;
    }
  },
  TARIFF {
    @Override
    public DomainType domainType() {
      return DomainType.TARIFF_SWITCHING;
    }
  },
  TARIFF_REVERSED {
    @Override
    public DomainType domainType() {
      return DomainType.TARIFF_SWITCHING;
    }
  };

  public abstract DomainType domainType();
}
