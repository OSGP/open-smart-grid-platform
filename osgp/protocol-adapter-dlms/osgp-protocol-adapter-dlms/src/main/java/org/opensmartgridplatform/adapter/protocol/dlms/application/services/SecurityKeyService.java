/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public interface SecurityKeyService {
    byte[] reEncryptKey(final byte[] externallyEncryptedKey, final SecurityKeyType keyType) throws FunctionalException;
    byte[] decryptKey(final byte[] encryptedKey, final SecurityKeyType keyType) throws ProtocolAdapterException;
    byte[] encryptKey(final byte[] plainKey, final SecurityKeyType keyType) throws ProtocolAdapterException;

    byte[] getDlmsMasterKey(final String deviceIdentification);
    byte[] getDlmsAuthenticationKey(final String deviceIdentification);
    byte[] getDlmsGlobalUnicastEncryptionKey(final String deviceIdentification);

    //G_MASTER key
    byte[] getMbusDefaultKey(final String mbusDeviceIdentification);

    //G_METER_ENCRYPTION_KEY, currently not used
    byte[] getMbusUserKey(final String mbusDeviceIdentification);

    //PPP_PASSWORD
    byte[] getDlmsPassword(final String deviceIdentification);

    //this method should store a new key (only one 'new' key may exist) that has validFrom set to null (state=NEW)
    DlmsDevice storeNewKey(final DlmsDevice device, final byte[] encryptedKey, final SecurityKeyType keyType);

    //this method should change the validFrom of the 'new' key so that it becomes valid
    DlmsDevice validateNewKey(final DlmsDevice device, final SecurityKeyType keyType) throws ProtocolAdapterException;

    byte[] generateKey();
    byte[] generateAndEncryptKey();
    byte[] encryptMbusUserKey(final byte[] mbusDefaultKey, final byte[] mbusUserKey) throws ProtocolAdapterException;
}
