/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.security;

/**
 * Specifies which encryption/decryption provider is to be used. HSM, Thales Hardware Security
 * Module. JRE, Sun Java Runtime Environment's crypto implementation.
 */
public enum EncryptionProviderType {
  HSM, // Thales HSM
  JRE, // Sun crypto
}
