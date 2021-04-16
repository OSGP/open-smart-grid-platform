/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.security;

import static org.apache.commons.codec.binary.Hex.decodeHex;
import static org.apache.commons.codec.binary.Hex.encodeHexString;

import org.apache.commons.codec.DecoderException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

/**
 * Command-line tool for encrypting keys, using {@link EncryptionService}. Parameters: <path to
 * secret key> <string to encrypt>
 */
public class EncryptionTool {
  private final EncryptionService encryptionService;

  public EncryptionTool(final String secretKeyPath) throws FunctionalException {
    this.encryptionService = new EncryptionService().withSecretKeyAt(secretKeyPath);
  }

  @SuppressWarnings("squid:S106") // It's ok to write to System.out for a command-line tool.
  public static void main(final String... args) throws Exception {
    if (args.length < 2) {
      System.out.println(
          "Usage: java "
              + EncryptionTool.class.getCanonicalName()
              + " <path to secret key>"
              + " <strings to encrypt>");
      return;
    }
    final EncryptionTool encryptionTool = new EncryptionTool(args[0]);
    System.out.println("===== Results: =====");
    for (int i = 1; i < args.length; i++) {
      System.out.printf("source: %s encrypted: %s%n", args[i], encryptionTool.encrypt(args[i]));
    }
  }

  private String encrypt(final String s) throws FunctionalException, DecoderException {
    return encodeHexString(this.encryptionService.encrypt(decodeHex(s.toCharArray())));
  }
}
