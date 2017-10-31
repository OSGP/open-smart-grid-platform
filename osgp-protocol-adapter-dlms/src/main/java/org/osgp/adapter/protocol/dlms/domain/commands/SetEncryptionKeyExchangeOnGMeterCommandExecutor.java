/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.util.encoders.Hex;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SecurityUtils;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.interfaceclass.method.MBusClientMethod;
import org.osgp.adapter.protocol.dlms.application.models.ProtocolMeterInfo;
import org.osgp.adapter.protocol.dlms.application.services.DomainHelperService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GMeterInfoDto;
import com.alliander.osgp.shared.exceptionhandling.EncrypterException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.security.EncryptionService;

@Component()
public class SetEncryptionKeyExchangeOnGMeterCommandExecutor
        extends AbstractCommandExecutor<ProtocolMeterInfo, MethodResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetEncryptionKeyExchangeOnGMeterCommandExecutor.class);

    private static final int CLASS_ID = 72;
    private static final ObisCode OBIS_CODE_INTERVAL_MBUS_1 = new ObisCode("0.1.24.1.0.255");
    private static final ObisCode OBIS_CODE_INTERVAL_MBUS_2 = new ObisCode("0.2.24.1.0.255");
    private static final ObisCode OBIS_CODE_INTERVAL_MBUS_3 = new ObisCode("0.3.24.1.0.255");
    private static final ObisCode OBIS_CODE_INTERVAL_MBUS_4 = new ObisCode("0.4.24.1.0.255");

    private static final Map<Integer, ObisCode> OBIS_HASHMAP = new HashMap<>();
    static {
        OBIS_HASHMAP.put(1, OBIS_CODE_INTERVAL_MBUS_1);
        OBIS_HASHMAP.put(2, OBIS_CODE_INTERVAL_MBUS_2);
        OBIS_HASHMAP.put(3, OBIS_CODE_INTERVAL_MBUS_3);
        OBIS_HASHMAP.put(4, OBIS_CODE_INTERVAL_MBUS_4);
    }

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private DomainHelperService domainHelperService;

    public SetEncryptionKeyExchangeOnGMeterCommandExecutor() {
        super(GMeterInfoDto.class);
    }

    @Override
    public ProtocolMeterInfo fromBundleRequestInput(final ActionRequestDto bundleInput)
            throws ProtocolAdapterException {

        this.checkActionRequestType(bundleInput);
        final GMeterInfoDto gMeterInfoDto = (GMeterInfoDto) bundleInput;

        try {
            final DlmsDevice gMeterDevice = this.domainHelperService
                    .findDlmsDevice(gMeterInfoDto.getDeviceIdentification());

            return new ProtocolMeterInfo(gMeterInfoDto.getChannel(), gMeterInfoDto.getDeviceIdentification(),
                    gMeterDevice.getValidSecurityKey(SecurityKeyType.G_METER_ENCRYPTION).getKey(),
                    gMeterDevice.getValidSecurityKey(SecurityKeyType.G_METER_MASTER).getKey());

        } catch (final FunctionalException e) {
            LOGGER.error("Error looking up G-Meter " + gMeterInfoDto.getDeviceIdentification(), e);
            throw new ProtocolAdapterException("Error looking up G-Meter " + gMeterInfoDto.getDeviceIdentification(),
                    e);
        }
    }

    @Override
    public ActionResponseDto asBundleResponse(final MethodResultCode executionResult) throws ProtocolAdapterException {

        this.checkMethodResultCode(executionResult);

        return new ActionResponseDto("Setting encryption key exchange on Gas meter was successful");
    }

    @Override
    public MethodResultCode execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final ProtocolMeterInfo protocolMeterInfo) throws ProtocolAdapterException, FunctionalException {
        try {
            LOGGER.debug("SetEncryptionKeyExchangeOnGMeterCommandExecutor.execute called");

            // Decrypt the cipher text using the private key.
            final byte[] decryptedEncryptionKey = this.encryptionService
                    .decrypt(Hex.decode(protocolMeterInfo.getEncryptionKey()));
            final byte[] decryptedMasterKey = this.encryptionService
                    .decrypt(Hex.decode(protocolMeterInfo.getMasterKey()));

            final ObisCode obisCode = OBIS_HASHMAP.get(protocolMeterInfo.getChannel());

            final MethodParameter methodTransferKey = this.getTransferKeyToMBusMethodParameter(obisCode,
                    decryptedMasterKey, decryptedEncryptionKey);

            conn.getDlmsMessageListener()
                    .setDescription("SetEncryptionKeyExchangeOnGMeter for channel " + protocolMeterInfo.getChannel()
                            + ", call method: " + JdlmsObjectToStringUtil.describeMethod(methodTransferKey));

            MethodResult methodResultCode = conn.getConnection().action(methodTransferKey);
            this.checkMethodResultCode(methodResultCode, "getTransferKeyToMBusMethodParameter");
            LOGGER.info("Success!: Finished calling getTransferKeyToMBusMethodParameter class_id {} obis_code {}",
                    CLASS_ID, obisCode);

            final MethodParameter methodSetEncryptionKey = this.getSetEncryptionKeyMethodParameter(obisCode,
                    decryptedEncryptionKey);
            methodResultCode = conn.getConnection().action(methodSetEncryptionKey);
            this.checkMethodResultCode(methodResultCode, "getSetEncryptionKeyMethodParameter");
            LOGGER.info("Success!: Finished calling setEncryptionKey class_id {} obis_code {}", CLASS_ID, obisCode);

            return MethodResultCode.SUCCESS;
        } catch (final IOException e) {
            LOGGER.error("Unexpected exception while connecting with device", e);
            throw new ConnectionException(e);
        } catch (final EncrypterException e) {
            LOGGER.error("Unexpected exception during decryption of security keys", e);
            throw new ProtocolAdapterException(
                    "Unexpected exception during decryption of security keys, reason = " + e.getMessage());
        }
    }

    private void checkMethodResultCode(final MethodResult methodResultCode, final String methodParameterName)
            throws ProtocolAdapterException {
        if (methodResultCode == null || !MethodResultCode.SUCCESS.equals(methodResultCode.getResultCode())) {
            throw new ProtocolAdapterException(
                    "Error while executing " + methodParameterName + ". Reason = " + methodResultCode.getResultCode());
        }
    }

    private MethodParameter getTransferKeyToMBusMethodParameter(final ObisCode obisCode, final byte[] defaultMBusKey,
            final byte[] encryptionKey) throws ProtocolAdapterException {
        byte[] encryptedEncryptionkey;
        try {
            encryptedEncryptionkey = SecurityUtils.cipherWithAes128(defaultMBusKey, encryptionKey);
        } catch (final GeneralSecurityException e) {
            LOGGER.error("Unexpected exception during getTransferKeyToMBusMethodParameter", e);
            throw new ProtocolAdapterException(e.getMessage());
        }

        final DataObject methodParameter = DataObject.newOctetStringData(encryptedEncryptionkey);
        return new MethodParameter(MBusClientMethod.TRANSFER_KEY, obisCode, methodParameter);
    }

    private MethodParameter getSetEncryptionKeyMethodParameter(final ObisCode obisCode, final byte[] encryptionKey)
            throws IOException {
        final DataObject methodParameter = DataObject.newOctetStringData(encryptionKey);
        return new MethodParameter(MBusClientMethod.SET_ENCRYPTION_KEY, obisCode, methodParameter);
    }

}
