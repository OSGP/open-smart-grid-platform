// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.security;

/**
 * Specifies which encryption/decryption provider is to be used. HSM, Thales Hardware Security
 * Module. JRE, Sun Java Runtime Environment's crypto implementation.
 */
public enum EncryptionProviderType {
  HSM, // Thales HSM
  JRE, // Sun crypto
}
