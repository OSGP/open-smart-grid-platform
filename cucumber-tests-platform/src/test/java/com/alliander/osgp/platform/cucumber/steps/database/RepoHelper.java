package com.alliander.osgp.platform.cucumber.steps.database;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getDate;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getFloat;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getLong;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getShort;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.ProtocolInfoRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;

@Component
public class RepoHelper {

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
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    @Autowired
    private OrganisationRepository organizationRepository;

    @Autowired
    private ProtocolInfoRepository protocolInfoRepository;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private DlmsSecurityKeyRepository securityKeyRepository;

    public void insertDevice(final Map<String, String> settings) {
        // Set the required stuff
        final String deviceIdentification = settings.get(Keys.KEY_DEVICE_IDENTIFICATION);
        final Device device = new Device(deviceIdentification);

        this.updateDevice(device, settings);
    }

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

    @Transactional(transactionManager="txMgrCore")
    public void insertSmartMeter(final Map<String, String> inputSettings) {
        final SmartMeter smartMeter = new SmartMeter(
                getString(inputSettings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION),
                getString(inputSettings, Keys.KEY_ALIAS, Defaults.DEFAULT_ALIAS),
                getString(inputSettings, Keys.KEY_CITY, Defaults.DEFAULT_CONTAINER_CITY),
                getString(inputSettings, Keys.KEY_POSTCODE, Defaults.DEFAULT_CONTAINER_POSTALCODE),
                getString(inputSettings, Keys.KEY_STREET, Defaults.DEFAULT_CONTAINER_STREET),
                getString(inputSettings, Keys.KEY_NUMBER, Defaults.DEFAULT_CONTAINER_NUMBER),
                getString(inputSettings, Keys.KEY_MUNICIPALITY, Defaults.DEFAULT_CONTAINER_MUNICIPALITY),
                getFloat(inputSettings, Keys.KEY_LATITUDE, Defaults.DEFAULT_LATITUDE),
                getFloat(inputSettings, Keys.KEY_LONGITUDE, Defaults.DEFAULT_LONGITUDE)
                );

        final Protocol protocol = ProtocolHelper.getProtocol(Protocol.ProtocolType.DSMR);
        Map<String, String> settings = this.putSetting(inputSettings, Keys.KEY_PROTOCOL, protocol.getProtocol());
        settings = this.putSetting(settings, Keys.KEY_PROTOCOL_VERSION, protocol.getVersion());

        if (settings.containsKey(Keys.KEY_GATEWAY_DEVICE_ID)) {
            smartMeter.setChannel(getShort(settings, Keys.KEY_CHANNEL, Defaults.DEFAULT_CHANNEL));
            final Device smartEMeter = this.deviceRepository.findByDeviceIdentification(settings.get(Keys.KEY_GATEWAY_DEVICE_ID));
            smartMeter.updateGatewayDevice(smartEMeter);
        }

        this.smartMeterRepository.save(smartMeter);

        final Device device = this.deviceRepository.findByDeviceIdentification(getString(inputSettings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        this.updateDevice(device, settings);
    }

    private void updateDevice(final Device device, final Map<String, String> settings) {

        // Now set the optional stuff
        device.setActivated(getBoolean(settings, Keys.KEY_IS_ACTIVATED, Defaults.DEFAULT_IS_ACTIVATED));
        device.setTechnicalInstallationDate(getDate(settings, Keys.KEY_TECH_INSTALL_DATE).toDate());

        final DeviceModel deviceModel = this.deviceModelRepository
                .findByModelCode(getString(settings, Keys.KEY_DEVICE_MODEL, Defaults.DEFAULT_DEVICE_MODEL_MODEL_CODE));
        device.setDeviceModel(deviceModel);

        device.updateProtocol(this.protocolInfoRepository.findByProtocolAndProtocolVersion(
                getString(settings, Keys.KEY_PROTOCOL, Defaults.DEFAULT_PROTOCOL),
                getString(settings, Keys.KEY_PROTOCOL_VERSION, Defaults.DEFAULT_PROTOCOL_VERSION)));

        device.updateRegistrationData(InetAddress.getLoopbackAddress(),
                getString(settings, Keys.KEY_DEVICE_TYPE, Defaults.DEFAULT_DEVICE_TYPE));

        device.setVersion(getLong(settings, Keys.KEY_VERSION));
        device.setActive(getBoolean(settings, Keys.KEY_ACTIVE, Defaults.DEFAULT_ACTIVE));
        device.addOrganisation(getString(settings, Keys.KEY_ORGANIZATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        device.updateMetaData(getString(settings, Keys.KEY_ALIAS, Defaults.DEFAULT_ALIAS),
                getString(settings, Keys.KEY_CITY, Defaults.DEFAULT_CONTAINER_CITY),
                getString(settings, Keys.KEY_POSTCODE, Defaults.DEFAULT_CONTAINER_POSTALCODE),
                getString(settings, Keys.KEY_STREET, Defaults.DEFAULT_CONTAINER_STREET),
                getString(settings, Keys.KEY_NUMBER, Defaults.DEFAULT_CONTAINER_NUMBER),
                getString(settings, Keys.KEY_MUNICIPALITY, Defaults.DEFAULT_CONTAINER_MUNICIPALITY),
                getFloat(settings, Keys.KEY_LATITUDE, Defaults.DEFAULT_LATITUDE),
                getFloat(settings, Keys.KEY_LONGITUDE, Defaults.DEFAULT_LONGITUDE));

        final Device device2 = this.deviceRepository.save(device);

        final Organisation organization = this.organizationRepository.findByOrganisationIdentification(
                getString(settings, Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        final DeviceFunctionGroup functionGroup = getEnum(settings, Keys.KEY_DEVICE_FUNCTION_GRP, DeviceFunctionGroup.class,
                DeviceFunctionGroup.OWNER);

        final DeviceAuthorization authorization = device.addAuthorization(organization, functionGroup);
        final Device savedDevice = this.deviceRepository.save(device2);
        this.deviceAuthorizationRepository.save(authorization);

        ScenarioContext.Current().put(Keys.KEY_DEVICE_IDENTIFICATION, savedDevice.getDeviceIdentification());
    }

    public DlmsDevice findDlmsDevice(final String deviceIdentification) {
        return this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
    }


    private Map<String, String> putSetting(final Map<String, String> settings, final String key, final String value) {
        final Map<String, String> result = new HashMap<String, String>();
        result.putAll(settings);
        result.put(key, value);
        return result;
    }


}
