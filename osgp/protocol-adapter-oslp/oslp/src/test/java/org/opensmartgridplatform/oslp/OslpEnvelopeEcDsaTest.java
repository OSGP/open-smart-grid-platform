// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.oslp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.oslp.Oslp.Message;
import org.opensmartgridplatform.oslp.Oslp.RegisterDeviceResponse;
import org.opensmartgridplatform.shared.security.CertificateHelper;

/** Unittests for OSLP envelope with ECDSA security. */
public class OslpEnvelopeEcDsaTest {

  // Private DER SIM key, base64 encoded using
  // http://www.motobit.com/util/base64-decoder-encoder.asp
  private static final String PRIVATE_KEY_BASE_64 =
      "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQg8ydsIOMoTlBPn6rJezELYFLUUuQe"
          + "3GvrhI3TDJj1yNyhRANCAAQ0UmJgxWImQ5wgepQ65nlsK0lvYb/GW6nx4ngLgncDZmWH3Pck8eC1"
          + "xsKg1goWpvl7P1um4cIjKyBwfqf8FxZa";

  // Public DER SIM key, base64 encoded using
  // http://www.motobit.com/util/base64-decoder-encoder.asp
  private static final String PUBLIC_KEY_BASE_64 =
      "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAENFJiYMViJkOcIHqUOuZ5bCtJb2G/xlup8eJ4C4J3"
          + "A2Zlh9z3JPHgtcbCoNYKFqb5ez9bpuHCIysgcH6n/BcWWg==";

  // Public DER TEST key, base64 encoded using
  // http://www.motobit.com/util/base64-decoder-encoder.asp
  private static final String DEVIATING_PUBLIC_KEY_BASE_64 =
      "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEnIZyewkhRF8YsYG7ec02t9NEzuMmwPQaCnzpkexE"
          + "o2fp1t1PbVA64+zMEOUtyft79ooWaWvdWsuTHU752bqLTA==";

  private static final String KEY_TYPE = "EC";
  private static final String SIGNATURE = "SHA256withECDSA";
  private static final String PROVIDER = "SunEC";

  private static final DateTimeFormatter FORMAT = DateTimeFormat.forPattern("yyyyMMddHHmmss");

  /**
   * Valid must pass when decryption succeeds using correct keys
   *
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @throws NoSuchProviderException
   */
  @Test
  public void buildOslpMessageSuccess()
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
          NoSuchProviderException, Exception {
    final OslpEnvelope request = this.buildMessage();

    // Validate security key is set in request
    final byte[] securityKey = request.getSecurityKey();
    assertThat(securityKey.length).isEqualTo(OslpEnvelope.SECURITY_KEY_LENGTH);
    assertThat(ArrayUtils.isEmpty(securityKey)).isFalse();

    // Verify the message using public certificate
    final OslpEnvelope response =
        new OslpEnvelope.Builder()
            .withSignature(SIGNATURE)
            .withProvider(this.provider())
            .withSecurityKey(request.getSecurityKey())
            .withDeviceId(request.getDeviceId())
            .withSequenceNumber(request.getSequenceNumber())
            .withPayloadMessage(request.getPayloadMessage())
            .build();

    assertThat(
            response.validate(
                CertificateHelper.createPublicKeyFromBase64(
                    PUBLIC_KEY_BASE_64, KEY_TYPE, this.provider())))
        .isTrue();
  }

  /**
   * Valid must fail when decryption fails using incorrect keys
   *
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @throws NoSuchProviderException
   */
  @Test
  public void buildOslpMessageDecryptFailure()
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
          NoSuchProviderException, Exception {
    final OslpEnvelope request = this.buildMessage();

    // Verify the message using wrong public certificate
    final OslpEnvelope response =
        new OslpEnvelope.Builder()
            .withSignature(SIGNATURE)
            .withProvider(this.provider())
            .withDeviceId(request.getDeviceId())
            .withSequenceNumber(request.getSequenceNumber())
            .withSecurityKey(request.getSecurityKey())
            .withDeviceId(request.getDeviceId())
            .withPayloadMessage(request.getPayloadMessage())
            .build();

    assertThat(
            response.validate(
                CertificateHelper.createPublicKeyFromBase64(
                    DEVIATING_PUBLIC_KEY_BASE_64, KEY_TYPE, this.provider())))
        .isFalse();
  }

  /**
   * Valid must fail when message is changed and hash does not match
   *
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @throws NoSuchProviderException
   */
  @Test
  public void buildOslpMessageSignatureFailure()
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
          NoSuchProviderException {
    final OslpEnvelope request = this.buildMessage();

    final byte[] fakeDeviceId = new byte[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9};

    // Validate security key is set in request
    final byte[] securityKey = request.getSecurityKey();
    assertThat(securityKey.length).isEqualTo(OslpEnvelope.SECURITY_KEY_LENGTH);
    assertThat(ArrayUtils.isEmpty(securityKey)).isFalse();

    // Verify the message using public certificate
    final OslpEnvelope response =
        new OslpEnvelope.Builder()
            .withSignature(SIGNATURE)
            .withProvider(this.provider())
            .withSecurityKey(request.getSecurityKey())
            .withDeviceId(fakeDeviceId)
            .withSequenceNumber(request.getSequenceNumber())
            .withPayloadMessage(request.getPayloadMessage())
            .build();

    assertThat(
            response.validate(
                CertificateHelper.createPublicKeyFromBase64(
                    PUBLIC_KEY_BASE_64, KEY_TYPE, this.provider())))
        .isFalse();
  }

  /**
   * Valid must fail when message when signature length is corrupted
   *
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @throws NoSuchProviderException
   */
  @Test
  public void buildOslpMessageSignatureCorrupt()
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
          NoSuchProviderException {
    final OslpEnvelope request = this.buildMessage();

    // Validate security key is set in request
    final byte[] securityKey = request.getSecurityKey();
    assertThat(securityKey.length).isEqualTo(OslpEnvelope.SECURITY_KEY_LENGTH);
    assertThat(ArrayUtils.isEmpty(securityKey)).isFalse();

    // Verify the message using public certificate
    final OslpEnvelope response =
        new OslpEnvelope.Builder()
            .withSignature(SIGNATURE)
            .withProvider(this.provider())
            .withSecurityKey(request.getSecurityKey())
            .withDeviceId(request.getDeviceId())
            .withSequenceNumber(request.getSequenceNumber())
            .withPayloadMessage(request.getPayloadMessage())
            .build();

    // Corrupt the length of the ASN.1 DSA signature
    final byte[] corruptedSecurityKey = response.getSecurityKey();
    corruptedSecurityKey[1] = (byte) 129;
    response.setSecurityKey(corruptedSecurityKey);

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              response.validate(
                  CertificateHelper.createPublicKeyFromBase64(
                      PUBLIC_KEY_BASE_64, KEY_TYPE, this.provider()));
            });
  }

  /**
   * Valid must fail when decryption fails using incorrect keys
   *
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @throws NoSuchProviderException
   */
  @Test()
  public void buildOslpMessageIncorrectSignature()
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
          NoSuchProviderException {
    final byte[] deviceId = new byte[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
    final byte[] sequenceNumber = new byte[] {0, 1};

    final Message message = this.buildRegisterResponse();

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              new OslpEnvelope.Builder()
                  .withSignature("Incorrect")
                  .withProvider(this.provider())
                  .withPrimaryKey(
                      CertificateHelper.createPrivateKeyFromBase64(
                          PRIVATE_KEY_BASE_64, KEY_TYPE, this.provider()))
                  .withDeviceId(deviceId)
                  .withSequenceNumber(sequenceNumber)
                  .withPayloadMessage(message)
                  .build();
            });
  }

  /**
   * Valid must fail when decryption fails using incorrect keys
   *
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @throws NoSuchProviderException
   */
  @Test()
  public void buildOslpMessageIncorrectProvider()
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
          NoSuchProviderException {
    final byte[] deviceId = new byte[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
    final byte[] sequenceNumber = new byte[] {0, 1};

    final Message message = this.buildRegisterResponse();

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              new OslpEnvelope.Builder()
                  .withSignature(SIGNATURE)
                  .withProvider("Incorrect")
                  .withPrimaryKey(
                      CertificateHelper.createPrivateKeyFromBase64(
                          PRIVATE_KEY_BASE_64, KEY_TYPE, this.provider()))
                  .withDeviceId(deviceId)
                  .withSequenceNumber(sequenceNumber)
                  .withPayloadMessage(message)
                  .build();
            });
  }

  private OslpEnvelope buildMessage()
      throws NoSuchAlgorithmException, InvalidKeySpecException, IOException,
          NoSuchProviderException {
    final byte[] deviceId = new byte[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
    final byte[] sequenceNumber = new byte[] {0, 1};

    return new OslpEnvelope.Builder()
        .withSignature(SIGNATURE)
        .withProvider(this.provider())
        .withPrimaryKey(
            CertificateHelper.createPrivateKeyFromBase64(
                PRIVATE_KEY_BASE_64, KEY_TYPE, this.provider()))
        .withDeviceId(deviceId)
        .withSequenceNumber(sequenceNumber)
        .withPayloadMessage(this.buildRegisterResponse())
        .build();
  }

  private Message buildRegisterResponse() {
    // Both random numbers should be between 0 and 65535 (16 bit value).
    final int randomDevice = 53568;
    final int randomPlatform = 17643;

    return Message.newBuilder()
        .setRegisterDeviceResponse(
            RegisterDeviceResponse.newBuilder()
                .setStatus(Oslp.Status.OK)
                .setCurrentTime(Instant.now().toString(FORMAT))
                .setRandomDevice(randomDevice)
                .setRandomPlatform(randomPlatform))
        .build();
  }

  private String provider() {
    return PROVIDER;
  }
}
