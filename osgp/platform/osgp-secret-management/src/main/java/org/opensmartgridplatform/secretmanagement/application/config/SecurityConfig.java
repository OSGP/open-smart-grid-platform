/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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

  @Value("${encryption.rsa.public.key.protocol.adapter.resource:#{null}}")
  private Resource rsaPublicKeyProtocolAdapterResource;

  @Value("${encryption.rsa.private.key.secret.management.resource:#{null}}")
  private Resource rsaPrivateKeySecretManagementResource;

  @Value("${jre.encryption.key.resource}")
  private Resource jreEncryptionKeyResource;

  @Value("${hsm.keystore.resource:#{null}}")
  private Optional<Resource> hsmKeystoreResource;

  @Value("${encryption.provider.type}")
  private String encryptionProviderTypeName;

  @Bean("DefaultEncryptionDelegate")
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

  @Bean(name = "encrypterWithProtocolAdapterPublicKey")
  public RsaEncrypter encrypterWithProtocolAdapterPublicKey() {
    try {
      final File publicKeyProtocolAdapterFile = this.rsaPublicKeyProtocolAdapterResource.getFile();
      final RsaEncrypter rsaEncrypter = new RsaEncrypter();
      rsaEncrypter.setPublicKeyStore(publicKeyProtocolAdapterFile);
      return rsaEncrypter;
    } catch (final IOException e) {
      throw new IllegalStateException(
          "Could not initialize encrypterWithProtocolAdapterPublicKey", e);
    }
  }

  @Bean(name = "decrypterWithSecretManagementPrivateKey")
  public RsaEncrypter decrypterWithSecretManagementPrivateKey() {
    try {
      final File privateKeySecretManagementFile =
          this.rsaPrivateKeySecretManagementResource.getFile();
      final RsaEncrypter rsaEncrypter = new RsaEncrypter();
      rsaEncrypter.setPrivateKeyStore(privateKeySecretManagementFile);
      return rsaEncrypter;
    } catch (final IOException e) {
      throw new IllegalStateException(
          "Could not initialize decrypterWithSecretManagementPrivateKey", e);
    }
  }

  @Bean
  public EncryptionProviderType getEncryptionProviderType() {
    return EncryptionProviderType.valueOf(this.encryptionProviderTypeName);
  }
}
