// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.oslp;

import com.google.protobuf.ByteString;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.opensmartgridplatform.oslp.Oslp.Message;

/** Utility methods to ease usage of OSLP. */
public final class OslpUtils {

  /** List of signature types which do not allow trailing data and need to be truncated. */
  private static final String[] TRUNCATE_SIGNATURES = {
    "NONEwithECDSA", "SHA1withECDSA", "SHA256withECDSA", "SHA384withECDSA", "SHA512withECDSA"
  };

  private OslpUtils() {
    // Empty constructor for static helper class.
  }

  /**
   * Converts an {@link Integer} to a {@link ByteString}.
   *
   * @param i the {@link Integer} to convert.
   * @return the {@link ByteString}.
   * @throws IllegalArgumentException thrown when i is null.
   */
  public static ByteString integerToByteString(final Integer i) {
    if (i == null) {
      throw new IllegalArgumentException("Null cannot be converted to ByteString.");
    }

    return ByteString.copyFrom(new byte[] {i.byteValue()});
  }

  /**
   * Convert byteString to Integer
   *
   * @param b bytestring input
   * @return converted integer
   */
  public static Integer byteStringToInteger(final ByteString b) {
    if (b == null) {
      throw new IllegalArgumentException("Null cannot be converted to Integer.");
    }

    if (b.isEmpty()) {
      return null;
    }

    return (int) b.byteAt(0);
  }

  /**
   * Combine all bytes which need to be signed from the OSLP envelope.
   *
   * @return array of bytes which can be signed
   */
  public static byte[] createSignBytes(final OslpEnvelope envelope) {
    byte[] message = ArrayUtils.addAll(envelope.getSequenceNumber(), envelope.getDeviceId());
    message = ArrayUtils.addAll(message, envelope.getLengthIndicator());
    message = ArrayUtils.addAll(message, envelope.getPayloadMessage().toByteArray());

    return message;
  }

  /**
   * Create a signature of specified message.
   *
   * @param message message bytes to sign
   * @param privateKey private key to use for signing
   * @param signature signature algorithm to use
   * @param provider provider which supplies the signature algorithm
   * @return signature
   * @throws GeneralSecurityException when configuration is incorrect.
   */
  public static byte[] createSignature(
      final byte[] message,
      final PrivateKey privateKey,
      final String signature,
      final String provider)
      throws GeneralSecurityException {

    final Signature signatureBuilder = Signature.getInstance(signature, provider);
    signatureBuilder.initSign(privateKey, new SecureRandom());
    signatureBuilder.update(message);
    return signatureBuilder.sign();
  }

  /**
   * Validate the signature against the message.
   *
   * @param message message to validate
   * @param securityKey signature to validate
   * @param publicKey public key to use for decryption of signature
   * @param signature signature algorithm to use
   * @param provider provider which supplies algorithm
   * @return true when signature is correct, false when it's not
   * @throws GeneralSecurityException when configuration is incorrect.
   */
  public static boolean validateSignature(
      final byte[] message,
      final byte[] securityKey,
      final PublicKey publicKey,
      final String signature,
      final String provider)
      throws GeneralSecurityException {

    // Using ECDSA as signature
    final Signature signatureBuilder = Signature.getInstance(signature, provider);
    signatureBuilder.initVerify(publicKey);
    signatureBuilder.update(message);

    int signatureLength = securityKey.length;

    if (ArrayUtils.contains(TRUNCATE_SIGNATURES, signature)) {
      // Fix for https://bugs.openjdk.java.net/browse/JDK-8161571
      // Read 2nd byte as length indicator for the actual signature bytes,
      // include 2 bytes for 1st 2 bytes
      // Ensure the byte (which is signed) is converted correctly to a
      // positive int
      signatureLength = securityKey[1] + 2 & 0xFF;
      if (signatureLength > securityKey.length) {
        throw new GeneralSecurityException(
            "Size indicator in ASN.1 DSA signature to large [" + signatureLength + "]");
      }
    }
    // Truncate the string to actual ASN.1 DSA length, removing padding
    final byte[] truncated = Arrays.copyOf(securityKey, signatureLength);
    return signatureBuilder.verify(truncated);
  }

  public static boolean isOslpResponse(final OslpEnvelope envelope) {

    final Message message = envelope.getPayloadMessage();

    final boolean[] hasResponse = {
      message.hasRegisterDeviceResponse(),
      message.hasConfirmRegisterDeviceResponse(),
      message.hasStartSelfTestResponse(),
      message.hasStopSelfTestResponse(),
      message.hasUpdateFirmwareResponse(),
      message.hasSetLightResponse(),
      message.hasSetEventNotificationsResponse(),
      message.hasEventNotificationResponse(),
      message.hasSetScheduleResponse(),
      message.hasGetFirmwareVersionResponse(),
      message.hasGetStatusResponse(),
      message.hasResumeScheduleResponse(),
      message.hasSetRebootResponse(),
      message.hasSetTransitionResponse(),
      message.hasSetConfigurationResponse(),
      message.hasGetConfigurationResponse(),
      message.hasSwitchConfigurationResponse(),
      message.hasGetActualPowerUsageResponse(),
      message.hasGetPowerUsageHistoryResponse(),
      message.hasSwitchFirmwareResponse(),
      message.hasUpdateDeviceSslCertificationResponse(),
      message.hasSetDeviceVerificationKeyResponse()
    };

    return BooleanUtils.or(hasResponse);
  }
}
