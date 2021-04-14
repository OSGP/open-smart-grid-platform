/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
