/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.IdentificationNumber;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.ManufacturerId;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

@Slf4j
public class SetEncryptionKeyExchangeOnGMeterMacGeneration {

  private static final int IV_LENGTH = 12;
  private static final int HEADER_LENGTH = 35;
  private static final int ADDRESS_LENGTH = 8;
  private static final int SECURITY_LENGTH = 16;

  public byte[] calculateMac(
      final DlmsDevice device,
      final int keyId,
      final int keySize,
      final Integer kcc,
      final byte[] newKey,
      final byte[] masterKey)
      throws ProtocolAdapterException {

    final byte[] iv = this.createIV(device, kcc);

    log.debug("Calculated IV: {}", Hex.toHexString(iv));

    final byte[] keyData = new byte[newKey.length + 2];
    keyData[0] = (byte) keyId;
    keyData[1] = (byte) keySize;
    System.arraycopy(newKey, 0, keyData, 2, newKey.length);

    log.debug("Key data: {}", Hex.toHexString(keyData));

    final byte[] encryptedKeyData = this.encryptKey(masterKey, keyData);

    log.debug("Encrypted key data: {}", Hex.toHexString(encryptedKeyData));

    final CipherParameters cipherParameters = new KeyParameter(masterKey);
    final ParametersWithIV parameterWithIV = new ParametersWithIV(cipherParameters, iv);

    final int macSizeBits = 64;
    final GMac mac = new GMac(new GCMBlockCipher(new AESEngine()), macSizeBits);

    mac.init(parameterWithIV);

    mac.update(encryptedKeyData, 0, encryptedKeyData.length);
    final byte[] generatedMac = new byte[mac.getMacSize()];
    mac.doFinal(generatedMac, 0);

    if (generatedMac.length != 8) {
      throw new ProtocolAdapterException(
          String.format(
              "Unable to generate correct MAC: length of generated MAC (%d) is not 8",
              generatedMac.length));
    }
    return generatedMac;
  }

  public byte[] createIV(final DlmsDevice device, final Integer kcc)
      throws ProtocolAdapterException {
    final byte mBusVersion = 6;
    final byte medium = 3;
    //    this.logIV(firmwareFile, ivLength);
    return ByteBuffer.allocate(IV_LENGTH)
        .put(Arrays.reverse(this.getMbusIdentificationNnumber(device)))
        .put(this.getMbusManufacturerId(device))
        .put(mBusVersion)
        .put(medium)
        .put(this.getKCC(kcc))
        .array();
  }

  private byte[] getKCC(final Integer kcc) throws ProtocolAdapterException {
    if (kcc != null) {
      final byte[] byteArrayWithKcc = BigInteger.valueOf(kcc).toByteArray();
      return this.addPadding(byteArrayWithKcc, 4);
    } else {
      // If kcc is null, then use number of seconds since 2000
      final LocalDateTime january2000 = LocalDateTime.of(2000, 1, 1, 0, 0);
      final long numberOfSeconds = ChronoUnit.SECONDS.between(january2000, LocalDateTime.now());
      return BigInteger.valueOf(numberOfSeconds).toByteArray();
    }
  }

  private byte[] getMbusManufacturerId(final DlmsDevice device) {
    final String mbusManufacturerIdentification = device.getMbusManufacturerIdentification();
    final ManufacturerId manufacturerId =
        ManufacturerId.fromIdentification(mbusManufacturerIdentification);
    return BigInteger.valueOf(manufacturerId.getId()).toByteArray();
  }

  private byte[] getMbusIdentificationNnumber(final DlmsDevice device) {
    final String mbusIdentificationNumber = device.getMbusIdentificationNumber();
    final IdentificationNumber identificationNumber =
        IdentificationNumber.fromTextualRepresentation(mbusIdentificationNumber);
    return BigInteger.valueOf(
            identificationNumber.getIdentificationNumberInBcdRepresentationAsLong())
        .toByteArray();
  }

  private byte[] addPadding(final byte[] input, final int size) throws ProtocolAdapterException {
    if (input.length > size) {
      throw new ProtocolAdapterException(
          String.format(
              "Input for padding should not be larger (%d) than size (%d)", input.length, size));
    }

    final byte[] output = new byte[size];
    System.arraycopy(input, 0, output, size - input.length, input.length);

    return output;
  }

  private byte[] encryptKey(final byte[] masterKey, final byte[] dataToEncrypt)
      throws ProtocolAdapterException {
    try {
      final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      final SecretKeySpec key = new SecretKeySpec(masterKey, "AES");
      cipher.init(Cipher.ENCRYPT_MODE, key);
      return cipher.doFinal(dataToEncrypt);
    } catch (final Exception e) {
      throw new ProtocolAdapterException("Encryption failed", e);
    }
  }
}
