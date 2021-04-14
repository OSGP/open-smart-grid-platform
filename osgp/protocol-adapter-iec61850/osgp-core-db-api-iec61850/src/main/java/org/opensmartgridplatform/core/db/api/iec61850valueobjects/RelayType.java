/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.db.api.iec61850valueobjects;

import org.opensmartgridplatform.dto.valueobjects.DomainTypeDto;

public enum RelayType {
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
