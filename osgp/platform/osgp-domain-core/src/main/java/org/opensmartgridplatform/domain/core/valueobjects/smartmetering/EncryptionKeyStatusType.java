//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public enum EncryptionKeyStatusType {
  NO_ENCRYPTION_KEY,
  ENCRYPTION_KEY_SET,
  ENCRYPTION_KEY_TRANSFERRED,
  ENCRYPTION_KEY_SET_AND_TRANSFERRED,
  ENCRYPTION_KEY_IN_USE;
}
