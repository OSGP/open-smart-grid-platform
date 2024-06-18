// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.config;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static javax.crypto.Cipher.DECRYPT_MODE;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

@SuppressWarnings({"java:S5998", "java:S6035", "java:S5855"})
public class KeystoreBuilder {

  private static final Pattern CERT_PATTERN =
      Pattern.compile(
          "-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+"
              + // Header
              "([a-z0-9+/=\\r\\n]+)"
              + // Base64 text
              "-+END\\s+.*CERTIFICATE[^-]*-+", // Footer
          CASE_INSENSITIVE);

  private static final Pattern KEY_PATTERN =
      Pattern.compile(
          "-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+"
              + // Header
              "([a-z0-9+/=\\r\\n]+)"
              + // Base64 text
              "-+END\\s+.*PRIVATE\\s+KEY[^-]*-+", // Footer
          CASE_INSENSITIVE);

  private KeystoreBuilder() {
    // Prevent instantiation
  }

  public static SSLSocketFactory createSslSocketFactory(
      final KeyStore keyStore, final KeyStore trustStore, final String protocol) {
    try {
      final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
      trustManagerFactory.init(trustStore);

      final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("PKIX");
      keyManagerFactory.init(keyStore, "".toCharArray());

      final SSLContext sslContext = SSLContext.getInstance(protocol);
      sslContext.init(
          keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

      return sslContext.getSocketFactory();
    } catch (final UnrecoverableKeyException e) {
      throw new IllegalArgumentException(
          "Redis SSLSocketFactory configuration unsuccessful, keystore UnrecoverableKeyException problem.",
          e);
    } catch (final GeneralSecurityException e) {
      throw new IllegalArgumentException(
          "Redis SSLSocketFactory configuration unsuccessful, keystore GeneralSecurityException problem.",
          e);
    }
  }

  public static KeyStore loadKeyStore(final File certificateChainFile, final File privateKeyFile)
      throws GeneralSecurityException, IOException {
    return loadKeyStore(certificateChainFile, privateKeyFile, Optional.empty());
  }

  public static KeyStore loadTrustStore(final File certificateChainFile)
      throws IOException, GeneralSecurityException {
    final KeyStore keyStore = KeyStore.getInstance("JKS");
    keyStore.load(null, null);

    final List<X509Certificate> certificateChain = readCertificateChain(certificateChainFile);
    for (final X509Certificate certificate : certificateChain) {
      final X500Principal principal = certificate.getSubjectX500Principal();
      keyStore.setCertificateEntry(principal.getName("RFC2253"), certificate);
    }
    return keyStore;
  }

  private static KeyStore loadKeyStore(
      final File certificateChainFile,
      final File privateKeyFile,
      final Optional<String> keyPassword)
      throws IOException, GeneralSecurityException {
    Security.addProvider(new BouncyCastleProvider());
    final PKCS8EncodedKeySpec encodedKeySpec = readPrivateKey(privateKeyFile, keyPassword);

    final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    final PrivateKey key = keyFactory.generatePrivate(encodedKeySpec);

    final List<X509Certificate> certificateChain = readCertificateChain(certificateChainFile);
    if (certificateChain.isEmpty()) {
      throw new CertificateException(
          "Certificate file does not contain any certificates: " + certificateChainFile);
    }

    final KeyStore keyStore = KeyStore.getInstance("JKS");
    keyStore.load(null, null);
    keyStore.setKeyEntry(
        "key",
        key,
        keyPassword.orElse("").toCharArray(),
        certificateChain.toArray(Certificate[]::new));
    return keyStore;
  }

  private static List<X509Certificate> readCertificateChain(final File certificateChainFile)
      throws IOException, GeneralSecurityException {
    final String contents = readFile(certificateChainFile);

    final Matcher matcher = CERT_PATTERN.matcher(contents);
    final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    final List<X509Certificate> certificates = new ArrayList<>();

    int start = 0;
    while (matcher.find(start)) {
      final byte[] buffer = base64Decode(matcher.group(1));
      certificates.add(
          (X509Certificate)
              certificateFactory.generateCertificate(new ByteArrayInputStream(buffer)));
      start = matcher.end();
    }

    return certificates;
  }

  private static PKCS8EncodedKeySpec readPrivateKey(
      final File keyFile, final Optional<String> keyPassword)
      throws IOException, GeneralSecurityException {
    final String content = readFile(keyFile);

    final Matcher matcher = KEY_PATTERN.matcher(content);
    if (!matcher.find()) {
      throw new KeyStoreException("found no private key: " + keyFile);
    }
    final byte[] encodedKey = base64Decode(matcher.group(1));

    if (keyPassword.isEmpty()) {
      return new PKCS8EncodedKeySpec(encodedKey);
    }

    final EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(encodedKey);
    final SecretKeyFactory keyFactory =
        SecretKeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName());
    final SecretKey secretKey =
        keyFactory.generateSecret(new PBEKeySpec(keyPassword.get().toCharArray()));

    final Cipher cipher = Cipher.getInstance(encryptedPrivateKeyInfo.getAlgName());
    cipher.init(DECRYPT_MODE, secretKey, encryptedPrivateKeyInfo.getAlgParameters());

    return encryptedPrivateKeyInfo.getKeySpec(cipher);
  }

  private static byte[] base64Decode(final String base64) {
    return Base64.getMimeDecoder().decode(base64.getBytes(US_ASCII));
  }

  private static String readFile(final File file) throws IOException {
    try (final Reader reader = new InputStreamReader(new FileInputStream(file), US_ASCII)) {
      final StringBuilder stringBuilder = new StringBuilder();

      final CharBuffer buffer = CharBuffer.allocate(2048);
      while (reader.read(buffer) != -1) {
        buffer.flip();
        stringBuilder.append(buffer);
        buffer.clear();
      }
      return stringBuilder.toString();
    }
  }
}
