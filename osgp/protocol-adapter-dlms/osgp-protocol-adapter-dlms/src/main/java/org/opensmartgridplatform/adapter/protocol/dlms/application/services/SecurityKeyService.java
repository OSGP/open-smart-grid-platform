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
    byte[] getMbusDefaultKey(final String mbusDeviceIdentification);
    byte[] getMbusUserKey(final String mbusDeviceIdentification);
    byte[] getDlmsPassword(final String deviceIdentification);
    DlmsDevice storeNewKey(final DlmsDevice device, final byte[] encryptedKey, final SecurityKeyType keyType);
    DlmsDevice validateNewKey(final DlmsDevice device, final SecurityKeyType keyType) throws ProtocolAdapterException;
    byte[] generateKey();
    byte[] generateAndEncryptKey();
    byte[] encryptMbusUserKey(final byte[] mbusDefaultKey, final byte[] mbusUserKey) throws ProtocolAdapterException;
}
