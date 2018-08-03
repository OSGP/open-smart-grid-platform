/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getLong;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getShort;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.Device;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class DeviceFactory {

    private DeviceFactory() {
        // Private constructor for utility class.
    }

    public static Device fromParameterMap(final Map<String, String> requestParameters) {

        final Map<String, String> settings = SettingsHelper.addDefault(requestParameters,
                PlatformKeys.KEY_DEVICE_DELIVERY_DATE, PlatformDefaults.DLMS_DEFAULT_DEVICE_DELIVERY_DATE);

        final Device device = new Device();
        device.setDeviceIdentification(getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        device.setDeviceType(getString(settings, PlatformKeys.KEY_DEVICE_TYPE, PlatformDefaults.DEFAULT_DEVICE_TYPE));

        device.setCommunicationMethod(settings.get(PlatformKeys.KEY_DEVICE_COMMUNICATIONMETHOD));
        device.setCommunicationProvider(settings.get(PlatformKeys.KEY_DEVICE_COMMUNICATIONPROVIDER));
        device.setICCId(settings.get(PlatformKeys.KEY_DEVICE_ICCID));

        device.setDSMRVersion(settings.get(PlatformKeys.KEY_DEVICE_DSMRVERSION));
        device.setSupplier(settings.get(PlatformKeys.KEY_DEVICE_SUPPLIER));

        device.setHLS3Active(getBoolean(settings, PlatformKeys.KEY_DEVICE_HLS3ACTIVE, PlatformDefaults.DLMS_DEFAULT_HSL3_ACTIVE));
        device.setHLS4Active(getBoolean(settings, PlatformKeys.KEY_DEVICE_HLS4ACTIVE, PlatformDefaults.DLMS_DEFAULT_HSL4_ACTIVE));
        device.setHLS5Active(getBoolean(settings, PlatformKeys.KEY_DEVICE_HLS5ACTIVE, PlatformDefaults.DLMS_DEFAULT_HSL5_ACTIVE));

        device.setMasterKey(RequestFactoryHelper.hexDecodeDeviceKey(settings.get(PlatformKeys.KEY_DEVICE_MASTERKEY),
                PlatformKeys.KEY_DEVICE_MASTERKEY));
        device.setAuthenticationKey(RequestFactoryHelper.hexDecodeDeviceKey(
                settings.get(PlatformKeys.KEY_DEVICE_AUTHENTICATIONKEY), PlatformKeys.KEY_DEVICE_AUTHENTICATIONKEY));
        device.setGlobalEncryptionUnicastKey(RequestFactoryHelper.hexDecodeDeviceKey(
                settings.get(PlatformKeys.KEY_DEVICE_ENCRYPTIONKEY), PlatformKeys.KEY_DEVICE_ENCRYPTIONKEY));

        device.setDeliveryDate(
                SettingsHelper.getXmlGregorianCalendarValue(settings, PlatformKeys.KEY_DEVICE_DELIVERY_DATE));

        device.setMbusIdentificationNumber(
                getLong(settings, PlatformSmartmeteringKeys.MBUS_IDENTIFICATION_NUMBER, null));
        device.setMbusManufacturerIdentification(
                getString(settings, PlatformSmartmeteringKeys.MBUS_MANUFACTURER_IDENTIFICATION));
        device.setMbusVersion(getShort(settings, PlatformSmartmeteringKeys.MBUS_VERSION, null));
        device.setMbusDeviceTypeIdentification(
                getShort(settings, PlatformSmartmeteringKeys.MBUS_DEVICE_TYPE_IDENTIFICATION, null));
        device.setMbusDefaultKey(RequestFactoryHelper.hexDecodeDeviceKey(
                settings.get(PlatformSmartmeteringKeys.MBUS_DEFAULT_KEY), PlatformSmartmeteringKeys.MBUS_DEFAULT_KEY));

        return device;
    }
}
