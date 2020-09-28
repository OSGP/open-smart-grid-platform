/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.endpoints;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.tomcat.util.buf.HexUtils;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;
import org.opensmartgridplatform.secretmanagement.application.domain.TypedSecret;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.EncryptionDelegate;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.opensmartgridplatform.ws.schema.core.secret.management.SecretTypes;
import org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecrets;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SoapEndpointDataTypeConverter {

    private static final String KEY_REFERENCE = "1"; //only one key in use
    private final EncryptionDelegate encryptionDelegate;

    public SoapEndpointDataTypeConverter(
            @Qualifier("DefaultEncryptionDelegate") final EncryptionDelegate defaultEncryptionDelegate) {
        this.encryptionDelegate = defaultEncryptionDelegate;
    }

    public List<SecretType> convertToSecretTypes(final SecretTypes soapSecretTypes) {

        final List<org.opensmartgridplatform.ws.schema.core.secret.management.SecretType> soapSecretTypeList = soapSecretTypes.getSecretType();

        return soapSecretTypeList.stream().map(soapSecretType -> this.convertToSecretType(soapSecretType)).collect(
                Collectors.toList());
    }

    public List<TypedSecret> convertToTypedSecrets(final TypedSecrets soapTypedSecrets) throws OsgpException {

        if (soapTypedSecrets == null) {
            throw new TechnicalException("Missing input: typed secrets");
        }

        final List<org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecret> soapTypedSecretsList =
                soapTypedSecrets.getTypedSecret();

        return soapTypedSecretsList.stream().map(
                soapTypedSecret -> this.decryptAndConvertSoapTypedSecret(soapTypedSecret)).collect(Collectors.toList());
    }

    public TypedSecrets convertToSoapTypedSecrets(final List<TypedSecret> typedSecrets) {
        final TypedSecrets soapTypedSecrets = new TypedSecrets();

        final List<org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecret> soapTypedSecretList =
                soapTypedSecrets.getTypedSecret();

        for (final TypedSecret typedSecret : typedSecrets) {
            final org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecret soapTypedSecret = this.encryptAndConvertSoapTypedSecret(
                    typedSecret);
            soapTypedSecretList.add(soapTypedSecret);
        }

        return soapTypedSecrets;
    }

    public SecretType convertToSecretType(
            final org.opensmartgridplatform.ws.schema.core.secret.management.SecretType soapSecretType) {
        return SecretType.valueOf(soapSecretType.value());
    }

    private org.opensmartgridplatform.ws.schema.core.secret.management.SecretType convertToSoapSecretType(
            final SecretType secretType) {
        return org.opensmartgridplatform.ws.schema.core.secret.management.SecretType.fromValue(secretType.name());
    }

    private org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecret encryptAndConvertSoapTypedSecret(
            final TypedSecret typedSecret) {
        final org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecret soapTypedSecret =
                new org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecret();

        final String encodedSecret = typedSecret.getSecret();
        final byte[] rawSecret = HexUtils.fromHexString(encodedSecret);
        final EncryptedSecret encryptedSecret = this.encryptionDelegate.encrypt(EncryptionProviderType.RSA, rawSecret, KEY_REFERENCE);
        soapTypedSecret.setSecret(HexUtils.toHexString(encryptedSecret.getSecret()));

        final SecretType secretType = typedSecret.getSecretType();
        soapTypedSecret.setType(this.convertToSoapSecretType(secretType));

        return soapTypedSecret;
    }

    public TypedSecret decryptAndConvertSoapTypedSecret(
            final org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecret soapTypedSecret) {
        final TypedSecret typedSecret = new TypedSecret();

        final byte[] rawEncryptedSecret = HexUtils.fromHexString(soapTypedSecret.getSecret());
        final EncryptedSecret encryptedSecret = new EncryptedSecret(EncryptionProviderType.RSA, rawEncryptedSecret);
        final byte[] decryptedSecret = this.encryptionDelegate.decrypt(encryptedSecret, KEY_REFERENCE);

        typedSecret.setSecret(HexUtils.toHexString(decryptedSecret));
        typedSecret.setSecretType(this.convertToSecretType(soapTypedSecret.getType()));

        return typedSecret;
    }

}
