/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public enum AttributeAccessModeType {
  NO_ACCESS,
  READ_ONLY,
  WRITE_ONLY,
  READ_AND_WRITE,
  AUTHENTICATED_READ_ONLY,
  AUTHENTICATED_WRITE_ONLY,
  AUTHENTICATED_READ_AND_WRITE;
}
