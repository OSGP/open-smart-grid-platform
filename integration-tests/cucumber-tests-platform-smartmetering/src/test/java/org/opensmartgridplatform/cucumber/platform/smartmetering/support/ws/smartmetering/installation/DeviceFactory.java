// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getShort;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.Device;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.SecurityKey;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class DeviceFactory {

  private DeviceFactory() {
    // Private constructor for utility class.
  }

  public static Device fromParameterMap(final Map<String, String> requestParameters) {

    Map<String, String> settings =
        SettingsHelper.addDefault(
            requestParameters,
            PlatformKeys.KEY_DEVICE_DELIVERY_DATE,
            PlatformDefaults.DLMS_DEFAULT_DEVICE_DELIVERY_DATE);
    settings =
        SettingsHelper.addDefault(
            settings,
            PlatformKeys.KEY_DEVICE_PROTOCOL_NAME,
            PlatformDefaults.DEFAULT_DEVICE_PROTOCOL_NAME);

    final Device device = new Device();
    device.setDeviceIdentification(
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    device.setDeviceType(
        getString(settings, PlatformKeys.KEY_DEVICE_TYPE, PlatformDefaults.DEFAULT_DEVICE_TYPE));

    device.setCommunicationMethod(settings.get(PlatformKeys.KEY_DEVICE_COMMUNICATIONMETHOD));
    device.setCommunicationProvider(settings.get(PlatformKeys.KEY_DEVICE_COMMUNICATIONPROVIDER));
    device.setIccId(settings.get(PlatformKeys.KEY_DEVICE_ICCID));

    device.setProtocolName(settings.get(PlatformKeys.KEY_DEVICE_PROTOCOL_NAME));
    device.setProtocolVersion(settings.get(PlatformKeys.KEY_DEVICE_PROTOCOL_VERSION));
    device.setProtocolVariant(settings.get(PlatformKeys.KEY_DEVICE_PROTOCOL_VARIANT));
    device.setSupplier(settings.get(PlatformKeys.KEY_DEVICE_SUPPLIER));
    device.setTimezone(settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_TIMEZONE));

    device.setHls3Active(
        getBoolean(
            settings,
            PlatformKeys.KEY_DEVICE_HLS3ACTIVE,
            PlatformDefaults.DLMS_DEFAULT_HSL3_ACTIVE));
    device.setHls4Active(
        getBoolean(
            settings,
            PlatformKeys.KEY_DEVICE_HLS4ACTIVE,
            PlatformDefaults.DLMS_DEFAULT_HSL4_ACTIVE));
    device.setHls5Active(
        getBoolean(
            settings,
            PlatformKeys.KEY_DEVICE_HLS5ACTIVE,
            PlatformDefaults.DLMS_DEFAULT_HSL5_ACTIVE));

    device.setMasterKey(
        RequestFactoryHelper.hexDecodeDeviceKey(
            getHexDecodeDeviceKey(settings, PlatformKeys.KEY_DEVICE_MASTERKEY),
            PlatformKeys.KEY_DEVICE_MASTERKEY));
    device.setAuthenticationKey(
        RequestFactoryHelper.hexDecodeDeviceKey(
            getHexDecodeDeviceKey(settings, PlatformKeys.KEY_DEVICE_AUTHENTICATIONKEY),
            PlatformKeys.KEY_DEVICE_AUTHENTICATIONKEY));
    device.setGlobalEncryptionUnicastKey(
        RequestFactoryHelper.hexDecodeDeviceKey(
            getHexDecodeDeviceKey(settings, PlatformKeys.KEY_DEVICE_ENCRYPTIONKEY),
            PlatformKeys.KEY_DEVICE_ENCRYPTIONKEY));

    device.setDeliveryDate(
        SettingsHelper.getXmlGregorianCalendarValue(
            settings, PlatformKeys.KEY_DEVICE_DELIVERY_DATE));

    device.setMbusIdentificationNumber(
        getString(settings, PlatformSmartmeteringKeys.MBUS_IDENTIFICATION_NUMBER));
    device.setMbusManufacturerIdentification(
        getString(settings, PlatformSmartmeteringKeys.MBUS_MANUFACTURER_IDENTIFICATION));
    device.setMbusVersion(getShort(settings, PlatformSmartmeteringKeys.MBUS_VERSION, null));
    device.setMbusDeviceTypeIdentification(
        getShort(settings, PlatformSmartmeteringKeys.MBUS_DEVICE_TYPE_IDENTIFICATION, null));
    device.setMbusDefaultKey(
        RequestFactoryHelper.hexDecodeDeviceKey(
            getHexDecodeDeviceKey(settings, PlatformSmartmeteringKeys.MBUS_DEFAULT_KEY),
            PlatformSmartmeteringKeys.MBUS_DEFAULT_KEY));
    device.setMbusUserKey(
        RequestFactoryHelper.hexDecodeDeviceKey(
            getHexDecodeDeviceKey(settings, PlatformSmartmeteringKeys.MBUS_USER_KEY),
            PlatformSmartmeteringKeys.MBUS_USER_KEY));
    device.setMbusFirmwareUpdateAuthenticationKey(
        RequestFactoryHelper.hexDecodeDeviceKey(
            getHexDecodeDeviceKey(
                settings, PlatformSmartmeteringKeys.MBUS_FIRMWARE_UPDATE_AUTHENTICATION_KEY),
            PlatformSmartmeteringKeys.MBUS_FIRMWARE_UPDATE_AUTHENTICATION_KEY));
    device.setMbusP0Key(
        RequestFactoryHelper.hexDecodeDeviceKey(
            getHexDecodeDeviceKey(settings, PlatformSmartmeteringKeys.MBUS_P0_KEY),
            PlatformSmartmeteringKeys.MBUS_P0_KEY));

    return device;
  }

  private static String getHexDecodeDeviceKey(
      final Map<String, String> settings, final String platformKey) {
    final String inputKeyName = settings.get(platformKey);
    String hexDecodeDeviceKey = null;
    if (inputKeyName != null) {
      final SecurityKey securityKey = SecurityKey.valueOf(inputKeyName);
      hexDecodeDeviceKey = securityKey.getSoapRequestKey();
    }
    return hexDecodeDeviceKey;
  }
}
