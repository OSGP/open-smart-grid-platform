/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.interfaceclass.method.MBusClientMethod;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecurityKeyService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GMeterInfoDto;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component()
public class SetEncryptionKeyExchangeOnGMeterCommandExecutor
        extends AbstractCommandExecutor<GMeterInfoDto, MethodResultCode> {

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
    @Qualifier("secretManagementService")
    private SecurityKeyService securityKeyService;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    public SetEncryptionKeyExchangeOnGMeterCommandExecutor() {
        super(GMeterInfoDto.class);
    }

    @Override
    public ActionResponseDto asBundleResponse(final MethodResultCode executionResult) throws ProtocolAdapterException {
        this.checkMethodResultCode(executionResult);
        return new ActionResponseDto("M-Bus User key exchange on Gas meter was successful");
    }

    @Override
    public MethodResultCode execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final GMeterInfoDto gMeterInfo) throws ProtocolAdapterException {
        try {
            LOGGER.debug("SetEncryptionKeyExchangeOnGMeterCommandExecutor.execute called");

            final String mbusDeviceIdentification = gMeterInfo.getDeviceIdentification();
            final int channel = gMeterInfo.getChannel();
            DlmsDevice mbusDevice = this.dlmsDeviceRepository.findByDeviceIdentification(mbusDeviceIdentification);
            if (mbusDevice == null) {
                throw new ProtocolAdapterException("Unknown M-Bus device: " + mbusDeviceIdentification);
            }

            final byte[] mbusUserKey = this.securityKeyService.generateKey();
            final byte[] mbusDefaultKey = this.securityKeyService
                    .getMbusDefaultKey(gMeterInfo.getDeviceIdentification());

            final SecurityKeyType mbusUserKeyType = SecurityKeyType.G_METER_ENCRYPTION;
            final byte[] encryptedUserKey = this.securityKeyService.encryptKey(mbusUserKey, mbusUserKeyType);
            mbusDevice = this.securityKeyService.storeNewKey(mbusDevice, encryptedUserKey, mbusUserKeyType);

            final ObisCode obisCode = OBIS_HASHMAP.get(channel);

            final MethodParameter methodTransferKey = this
                    .getTransferKeyToMBusMethodParameter(obisCode, mbusDefaultKey, mbusUserKey);

            conn.getDlmsMessageListener().setDescription("SetEncryptionKeyExchangeOnGMeter for channel " + channel
                    + ", call M-Bus Setup transfer_key method: " + JdlmsObjectToStringUtil
                    .describeMethod(methodTransferKey));

            MethodResult methodResultCode = conn.getConnection().action(methodTransferKey);
            this.checkMethodResultCode(methodResultCode, "M-Bus Setup transfer_key");
            LOGGER.info("Successfully invoked M-Bus Setup transfer_key method: class_id {} obis_code {}", CLASS_ID,
                    obisCode);

            conn.getDlmsMessageListener().setDescription(
                    "SetEncryptionKeyExchangeOnGMeter for channel " + gMeterInfo.getChannel()
                            + ", call M-Bus Setup set_encryption_key method: " + JdlmsObjectToStringUtil
                            .describeMethod(methodTransferKey));

            final MethodParameter methodSetEncryptionKey = this
                    .getSetEncryptionKeyMethodParameter(obisCode, mbusUserKey);
            methodResultCode = conn.getConnection().action(methodSetEncryptionKey);
            this.checkMethodResultCode(methodResultCode, "M-Bus Setup set_encryption_key");
            LOGGER.info("Successfully invoked M-Bus Setup set_encryption_key method: class_id {} obis_code {}",
                    CLASS_ID, obisCode);

            this.securityKeyService.validateNewKey(mbusDevice, mbusUserKeyType);

            return MethodResultCode.SUCCESS;
        } catch (final IOException e) {
            throw new ConnectionException(e);
        } catch (final EncrypterException e) {
            throw new ProtocolAdapterException(
                    "Unexpected exception during decryption of security keys, reason = " + e.getMessage(), e);
        }
    }

    private void checkMethodResultCode(final MethodResult methodResultCode, final String methodParameterName)
            throws ProtocolAdapterException {
        if (methodResultCode == null || !MethodResultCode.SUCCESS.equals(methodResultCode.getResultCode())) {
            String message = "Error while executing " + methodParameterName + ".";
            if (methodResultCode != null) {
                message += " Reason = " + methodResultCode.getResultCode();
            }
            throw new ProtocolAdapterException(message);
        }
    }

    private MethodParameter getTransferKeyToMBusMethodParameter(final ObisCode obisCode, final byte[] mbusDefaultKey,
            final byte[] mbusUserKey) throws ProtocolAdapterException {

        final byte[] encryptedUserKey = this.securityKeyService.encryptMbusUserKey(mbusDefaultKey, mbusUserKey);
        final DataObject methodParameter = DataObject.newOctetStringData(encryptedUserKey);
        return new MethodParameter(MBusClientMethod.TRANSFER_KEY, obisCode, methodParameter);
    }

    private MethodParameter getSetEncryptionKeyMethodParameter(final ObisCode obisCode, final byte[] encryptionKey) {
        final DataObject methodParameter = DataObject.newOctetStringData(encryptionKey);
        return new MethodParameter(MBusClientMethod.SET_ENCRYPTION_KEY, obisCode, methodParameter);
    }

}
