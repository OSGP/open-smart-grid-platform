package org.osgp.adapter.protocol.dlms.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;

import com.alliander.osgp.dto.valueobjects.smartmetering.SmartMeteringDevice;

public class DeviceConverter extends BidirectionalConverter<SmartMeteringDevice, DlmsDevice> {

    @Override
    public DlmsDevice convertTo(final SmartMeteringDevice source, final Type<DlmsDevice> destinationType) {
        final DlmsDevice dlmsDevice = new DlmsDevice();
        dlmsDevice.setDeviceIdentification(source.getDeviceIdentification());
        dlmsDevice.setCommunicationMethod(source.getCommunicationMethod());
        dlmsDevice.setCommunicationProvider(source.getCommunicationProvider());
        dlmsDevice.setICCId(source.getICCId());
        dlmsDevice.setHLS3Active(source.isHLS3Active());
        dlmsDevice.setHLS4Active(source.isHLS4Active());
        dlmsDevice.setHLS5Active(source.isHLS5Active());

        dlmsDevice.setMasterKey(source.getMasterKey());
        dlmsDevice.setAuthenticationKey(source.getAuthenticationKey());
        dlmsDevice.setGlobalEncryptionUnicastKey(source.getGlobalEncryptionUnicastKey());

        if (source.getMasterKey() != null) {
            final SecurityKey masterKey = new SecurityKey();
            masterKey.setSecurityKeyType(SecurityKeyType.E_METER_MASTER);
            masterKey.setSecurityKey(source.getMasterKey());
            masterKey.setValidFrom(source.getDeliveryDate());
            dlmsDevice.addSecurityKey(masterKey);
        }

        if (source.getAuthenticationKey() != null) {
            final SecurityKey authenticationKey = new SecurityKey();
            authenticationKey.setSecurityKeyType(SecurityKeyType.E_METER_AUTHENTICATION);
            authenticationKey.setSecurityKey(source.getAuthenticationKey());
            authenticationKey.setValidFrom(source.getDeliveryDate());
            dlmsDevice.addSecurityKey(authenticationKey);
        }

        if (source.getGlobalEncryptionUnicastKey() != null) {
            final SecurityKey encryptionKey = new SecurityKey();
            encryptionKey.setSecurityKeyType(SecurityKeyType.E_METER_ENCRYPTION);
            encryptionKey.setSecurityKey(source.getGlobalEncryptionUnicastKey());
            encryptionKey.setValidFrom(source.getDeliveryDate());
            dlmsDevice.addSecurityKey(encryptionKey);
        }

        return dlmsDevice;
    }

    @Override
    public SmartMeteringDevice convertFrom(final DlmsDevice source, final Type<SmartMeteringDevice> destinationType) {
        throw new UnsupportedOperationException("convertFrom of class DeviceConverter is not implemented.");
    }

}
