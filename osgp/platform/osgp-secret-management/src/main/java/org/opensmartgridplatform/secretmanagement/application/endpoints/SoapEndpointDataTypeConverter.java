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
import org.opensmartgridplatform.ws.schema.core.secret.management.SecretTypes;
import org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecrets;
import org.springframework.stereotype.Component;

@Component
public class SoapEndpointDataTypeConverter {
    public List<SecretType> convertToSecretTypes(final SecretTypes soapSecretTypes) {
        final List<org.opensmartgridplatform.ws.schema.core.secret.management.SecretType> soapSecretTypeList =
                soapSecretTypes.getSecretType();
        return soapSecretTypeList.stream().map(this::convertToSecretType).collect(Collectors.toList());
    }

    public List<TypedSecret> convertToTypedSecrets(final TypedSecrets soapTypedSecrets) throws OsgpException {
        if (soapTypedSecrets == null) {
            throw new TechnicalException("Missing input: typed secrets");
        }
        final List<org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecret> soapTypedSecretsList =
                soapTypedSecrets
                .getTypedSecret();
        return soapTypedSecretsList.stream().map(this::decryptAndConvertSoapTypedSecret).collect(Collectors.toList());
    }

    public TypedSecrets convertToSoapTypedSecrets(final List<TypedSecret> typedSecrets) {
        final TypedSecrets soapTypedSecrets = new TypedSecrets();
        final List<org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecret> soapTypedSecretList =
                soapTypedSecrets
                .getTypedSecret();
        for (final TypedSecret typedSecret : typedSecrets) {
            final org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecret soapTypedSecret = this
                    .encryptAndConvertSoapTypedSecret(typedSecret);
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

        final byte[] rsaSecret = typedSecret.getSecret();
        soapTypedSecret.setSecret(HexUtils.toHexString(rsaSecret));

        final SecretType secretType = typedSecret.getSecretType();
        soapTypedSecret.setType(this.convertToSoapSecretType(secretType));

        return soapTypedSecret;
    }

    public TypedSecret decryptAndConvertSoapTypedSecret(
            final org.opensmartgridplatform.ws.schema.core.secret.management.TypedSecret soapTypedSecret) {
        final byte[] rsaEncryptedSecret = HexUtils.fromHexString(soapTypedSecret.getSecret());
        return new TypedSecret(rsaEncryptedSecret, this.convertToSecretType(soapTypedSecret.getType()));
    }

}
