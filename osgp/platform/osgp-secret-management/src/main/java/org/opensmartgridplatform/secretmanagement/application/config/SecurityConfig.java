//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.secretmanagement.application.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.security.DefaultEncryptionDelegate;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.opensmartgridplatform.shared.security.RsaEncrypter;
import org.opensmartgridplatform.shared.security.providers.EncryptionProvider;
import org.opensmartgridplatform.shared.security.providers.HsmEncryptionProvider;
import org.opensmartgridplatform.shared.security.providers.JreEncryptionProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class SecurityConfig {

  @Value("${encryption.rsa.public.key.secret.management.client}")
  private Resource rsaPublicKeySecretManagementClient;

  @Value("${encryption.rsa.private.key.secret.management}")
  private Resource rsaPrivateKeySecretManagement;

  @Value("${jre.encryption.key.resource}")
  private Resource jreEncryptionKeyResource;

  @Value("${hsm.keystore.resource:#{null}}")
  private Optional<Resource> hsmKeystoreResource;

  @Value("${encryption.provider.type}")
  private String encryptionProviderTypeName;

  @Bean("DefaultEncryptionDelegateForKeyStorage")
  public DefaultEncryptionDelegate getEncryptionDelegate() {
    return new DefaultEncryptionDelegate(this.getDefaultEncryptionProviders());
  }

  private List<EncryptionProvider> getDefaultEncryptionProviders() {
    final List<EncryptionProvider> encryptionProviderList = new ArrayList<>();

    try {
      final JreEncryptionProvider jreEncryptionProvider =
          new JreEncryptionProvider(this.jreEncryptionKeyResource.getFile());
      encryptionProviderList.add(jreEncryptionProvider);

      if (this.hsmKeystoreResource.isPresent()) {
        final HsmEncryptionProvider hsmEncryptionProvider =
            new HsmEncryptionProvider(this.hsmKeystoreResource.get().getFile());
        encryptionProviderList.add(hsmEncryptionProvider);
      }

      return encryptionProviderList;
    } catch (final IOException | EncrypterException e) {
      throw new IllegalStateException("Error creating default encryption providers", e);
    }
  }

  // RsaEncrypter for encrypting secrets to be sent to the client of Secret Management.
  @Bean(name = "encrypterForSecretManagementClient")
  public RsaEncrypter encrypterForSecretManagementClient() {
    try {
      final File publicKeySecretManagementFile = this.rsaPublicKeySecretManagementClient.getFile();
      final RsaEncrypter rsaEncrypter = new RsaEncrypter();
      rsaEncrypter.setPublicKeyStore(publicKeySecretManagementFile);
      return rsaEncrypter;
    } catch (final IOException e) {
      throw new IllegalStateException("Could not initialize encrypterForSecretManagementClient", e);
    }
  }

  // RsaEncrypter for decrypting secrets received by Secret Management.
  @Bean(name = "decrypterForSecretManagement")
  public RsaEncrypter decrypterForSecretManagement() {
    try {
      final File privateKeySecretManagementFile = this.rsaPrivateKeySecretManagement.getFile();
      final RsaEncrypter rsaEncrypter = new RsaEncrypter();
      rsaEncrypter.setPrivateKeyStore(privateKeySecretManagementFile);
      return rsaEncrypter;
    } catch (final IOException e) {
      throw new IllegalStateException("Could not initialize decrypterForSecretManagement", e);
    }
  }

  @Bean
  public EncryptionProviderType getEncryptionProviderType() {
    return EncryptionProviderType.valueOf(this.encryptionProviderTypeName);
  }
}
