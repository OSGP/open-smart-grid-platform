/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.endpoints;

import org.apache.tomcat.util.buf.HexUtils;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretTypes;
import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecrets;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;
import org.opensmartgridplatform.secretmanagement.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmanagement.application.exception.TechnicalServiceFaultException;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.EncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.EncryptionDelegate;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.EncryptionProviderType;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.Secret;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SoapEndpointDataTypeConverter {

    private static final String KEY_REFERENCE = "1"; //only one key in use
    private EncryptionDelegate encryptionDelegate;

    public SoapEndpointDataTypeConverter(
            @Qualifier("DefaultEncryptionDelegate") final EncryptionDelegate defaultEncryptionDelegate) {
        this.encryptionDelegate = defaultEncryptionDelegate;
    }

    public List<SecretType> convertToSecretTypes(SecretTypes soapSecretTypes) {

        if (soapSecretTypes == null) {
            throw new TechnicalServiceFaultException("Missing input: secret types");
        }

        List<org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType> soapSecretTypeList =
                soapSecretTypes.getSecretType();
        List<SecretType> secretTypeList = new ArrayList<>();

        for (org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType soapSecretType :
                soapSecretTypeList) {
            SecretType secretType = convertToSecretType(soapSecretType);
            secretTypeList.add(secretType);
        }

        return secretTypeList;
    }

    public List<TypedSecret> convertToTypedSecrets(TypedSecrets soapTypedSecrets) {

        if (soapTypedSecrets == null) {
            throw new TechnicalServiceFaultException("Missing input: typed secrets");
        }

        List<org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret> soapTypedSecretsList
                = soapTypedSecrets.getTypedSecret();
        List<TypedSecret> typedSecretList = new ArrayList<>();

        for (org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret soapTypedSecret :
                soapTypedSecretsList) {
            typedSecretList.add(decryptAndConvertSoapTypedSecret(soapTypedSecret));
        }

        return typedSecretList;
    }

    public TypedSecrets convertToSoapTypedSecrets(List<TypedSecret> typedSecrets) {
        TypedSecrets soapTypedSecrets = new TypedSecrets();

        List<org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret> soapTypedSecretList =
                soapTypedSecrets.getTypedSecret();

        for (TypedSecret typedSecret : typedSecrets) {
            org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret soapTypedSecret =
                    encryptAndConvertSoapTypedSecret(
                    typedSecret);
            soapTypedSecretList.add(soapTypedSecret);
        }

        return soapTypedSecrets;
    }

    private SecretType convertToSecretType(
            org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType soapSecretType) {
        return SecretType.valueOf(soapSecretType.value());
    }

    private org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType convertToSoapSecretType(
            SecretType secretType) {
        return org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.SecretType.fromValue(
                secretType.name());
    }

    private org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret encryptAndConvertSoapTypedSecret(
            TypedSecret typedSecret) {
        org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret soapTypedSecret =
                new org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret();

        String encodedSecret = typedSecret.getSecret();
        byte[] rawSecret = HexUtils.fromHexString(encodedSecret);
        Secret secret = new Secret(rawSecret);
        EncryptedSecret encryptedSecret = encryptionDelegate.encrypt(EncryptionProviderType.RSA, secret, KEY_REFERENCE);
        soapTypedSecret.setSecret(HexUtils.toHexString(encryptedSecret.getSecret()));

        SecretType secretType = typedSecret.getSecretType();
        soapTypedSecret.setType(convertToSoapSecretType(secretType));

        return soapTypedSecret;
    }

    private TypedSecret decryptAndConvertSoapTypedSecret(
            org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TypedSecret soapTypedSecret) {
        TypedSecret typedSecret = new TypedSecret();

        byte[] rawEncryptedSecret = HexUtils.fromHexString(soapTypedSecret.getSecret());
        EncryptedSecret encryptedSecret = new EncryptedSecret(EncryptionProviderType.RSA, rawEncryptedSecret);
        Secret decryptedSecret = encryptionDelegate.decrypt(encryptedSecret, KEY_REFERENCE);

        typedSecret.setSecret(HexUtils.toHexString(decryptedSecret.getSecret()));
        typedSecret.setSecretType(convertToSecretType(soapTypedSecret.getType()));

        return typedSecret;
    }

}
