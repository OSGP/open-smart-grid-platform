/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

/** Enumeration indicating the domain of the platform. */
public enum PlatformDomain {
  /** Domain containing all common functionality, like installation and management. */
  COMMON,
  /** Domain containing all public lighting functionality, like setlight and light schedules. */
  PUBLIC_LIGHTING,
  /** Domain containing all tariff switching functionality, like tariff schedules. */
  TARIFF_SWITCHING,
  MICROGRIDS
}
