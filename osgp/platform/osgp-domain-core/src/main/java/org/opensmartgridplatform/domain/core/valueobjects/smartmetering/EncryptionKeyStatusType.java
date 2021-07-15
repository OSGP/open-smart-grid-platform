/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public enum EncryptionKeyStatusType {
  NO_ENCRYPTION_KEY,
  ENCRYPTION_KEY_SET,
  ENCRYPTION_KEY_TRANSFERRED,
  ENCRYPTION_KEY_SET_AND_TRANSFERRED,
  ENCRYPTION_KEY_IN_USE;
}
