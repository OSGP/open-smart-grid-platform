/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database.adapterprotocoldlms;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsSecurityKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;

@Component
public class DlmsDeviceSteps {

    private static final SecurityKeyType E_METER_SECURITY_KEYTYPES[] = new SecurityKeyType[]
            {SecurityKeyType.E_METER_ENCRYPTION, SecurityKeyType.E_METER_MASTER, SecurityKeyType.E_METER_AUTHENTICATION};

    private static final String E_METER_SECURITY_KEYS[] = new String[] {
        "bc082efed278e1bbebddc0431877d4fa2df7728229f3e03c57b2549142b40d047b35011dbf9f77ad91db5fe6f19a7b9c",
        "bc082efed278e1bbebddc0431877d4fa16374b00e96dd102beab666dcb72efbd1f0b868412497f6d3d0c62caa4700585",
        "bc082efed278e1bbebddc0431877d4fae80fa4e72925b6ad0bc67c84b8721598eda8458bcc1b2827fe6e5e7918ce22fd"
    };

    private static final SecurityKeyType G_METER_SECURITY_KEYTYPES[] = new SecurityKeyType[]
            {SecurityKeyType.G_METER_ENCRYPTION, SecurityKeyType.G_METER_MASTER};

    private static final String G_METER_SECURITY_KEYS[] = new String[] {
        "bc082efed278e1bbebddc0431877d4fa2dad5528387ae4ba11f98995baaca9b371ac6590f06d40e142f789f64dbb4537",
        "bc082efed278e1bbebddc0431877d4fa16374b00e96dd102beab666dcb72efbd1f0b868412497f6d3d0c62caa4700585"
    };

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private DlmsSecurityKeyRepository securityKeyRepository;

    public void insertDlmsDevice(final Map<String, String> settings) {
        final DlmsDevice dlmsDevice = this.insertCommonDlms(settings);

        // Now create the DLMS device in the DLMS database
        if (Defaults.SMART_METER_G.equals(getString(settings, Keys.KEY_DEVICE_TYPE, Defaults.DLMS_DEFAULT_DEVICE_TYPE))) {
            this.insertDlmsGasMeter(dlmsDevice, settings);
        } else {
            this.insertDlmsEMeter(dlmsDevice, settings);
        }
    }

    private DlmsDevice insertCommonDlms(final Map<String, String> settings) {
        final String deviceIdentification = getString(settings, Keys.KEY_DEVICE_IDENTIFICATION,
                Defaults.DEFAULT_DEVICE_IDENTIFICATION);
        final DlmsDevice dlmsDevice = new DlmsDevice(deviceIdentification);
        dlmsDevice.setInDebugMode(getBoolean(settings, Keys.KEY_INDEBUGMODE, Defaults.DEFAULT_INDEBUGMODE));
        dlmsDevice.setCommunicationMethod(
                getString(settings, Keys.KEY_COMM_METHOD, Defaults.DLMS_DEFAULT_COMMUNICATION_METHOD));
        dlmsDevice.setIpAddressIsStatic(
                getBoolean(settings, Keys.KEY_IP_ADDR_IS_STATIC, Defaults.DLMS_DEFAULT_IP_ADDRESS_IS_STATIC));

        return dlmsDevice;
    }


    private void insertDlmsEMeter(final DlmsDevice dlmsDevice, final Map<String, String> settings) {
        dlmsDevice.setPort(Defaults.DLMS_DEFAULT_PORT);
        dlmsDevice.setLogicalId(Defaults.DLMS_DEFAULT_LOGICAL_ID);
        dlmsDevice.setHls5Active(Defaults.DLMS_DEFAULT_HSL5_ACTIVE);

        this.dlmsDeviceRepository.save(dlmsDevice);
        this.insertDlmsSecurityKeys(dlmsDevice, E_METER_SECURITY_KEYTYPES, E_METER_SECURITY_KEYS);
    }

    private void insertDlmsGasMeter(final DlmsDevice dlmsDevice, final Map<String, String> settings) {
        dlmsDevice.setHls5Active(false);

        this.dlmsDeviceRepository.save(dlmsDevice);
        this.insertDlmsSecurityKeys(dlmsDevice, G_METER_SECURITY_KEYTYPES, G_METER_SECURITY_KEYS);
    }


    private void insertDlmsSecurityKeys(final DlmsDevice dlmsDevice,
            final SecurityKeyType securityKeyTypes[], final String keys[]) {
        final Date validFrom = new DateTime(2016,1,1,1,1,0).toDate();
        final List<SecurityKey> securityKeys = new ArrayList<>();
        for (int i = 0; i < securityKeyTypes.length; i++) {
            securityKeys.add(new SecurityKey(dlmsDevice, securityKeyTypes[i], keys[i], validFrom, null));
        }

        for (final SecurityKey seckey : securityKeys) {
            this.securityKeyRepository.save(seckey);
        }
    }

    public DlmsDevice findDlmsDevice(final String deviceIdentification) {
        return this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
    }
}

