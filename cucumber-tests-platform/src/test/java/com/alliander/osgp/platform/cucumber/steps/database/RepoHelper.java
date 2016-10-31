package com.alliander.osgp.platform.cucumber.steps.database;

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

import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;

@Component
public class RepoHelper {

    private static final String DLMS_DEFAULT_COMMUNICATION_METHOD = "GPRS";
    private static final Boolean DLMS_DEFAULT_IP_ADDRESS_IS_STATIC = true;
    private static final long DLMS_DEFAULT_PORT = 4059L;
    private static final Boolean DLMS_DEFAULT_HSL5_ACTIVE = true;
    private static final String DLMS_DEFAULT_DEVICE_TYPE = "SMART_METER_E";

    private static final String DLMS_GAS_DEVICE = "SMART_METER_G";

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
        // "a0630ea30008565b17cbbc93f0a87b16d334184f4f3a75658f19f89720c1f8b1" deze stond op tst2
    };

//  private static final String DEFAULT_ALIAS = "Test";
//  private static final String DEFAULT_CONTAINER_CITY = "Esloo";
//  private static final String DEFAULT_CONTAINER_POSTAL_CODE = "6171 AE";
//  private static final String DEFAULT_CONTAINER_STREET = "Mauritsweg";
//  private static final String DEFAULT_CONTAINER_NUMBER = "109";
//  private static final String DEFAULT_CONTAINER_MUNICIPALITY = "Stein";
//  private static final Float DEFAULT_GPSLATITUDE = 5.0F;
//  private static final Float DEFAULT_GPSLONGITUDE = 5.0F;
//  private static final Boolean DEFAULT_ACTIVE = true;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private DlmsSecurityKeyRepository securityKeyRepository;

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    public void insertDlmsDevice(final Map<String, String> settings) {
        final DlmsDevice dlmsDevice = this.insertCommonDlms(settings);

        // Now create the DLMS device in the DLMS database
        if (DLMS_GAS_DEVICE.equals(getString(settings, Keys.KEY_DEVICE_TYPE, DLMS_DEFAULT_DEVICE_TYPE))) {
            this.insertDlmsGasMeter(dlmsDevice, settings);
        } else {
            this.insertDlmsEMeter(dlmsDevice, settings);
        }
    }

    private DlmsDevice insertCommonDlms(final Map<String, String> settings) {
        final String deviceIdentification = getString(settings, Keys.KEY_DEVICE_IDENTIFICATION,
                DeviceSteps.DEFAULT_DEVICE_IDENTIFICATION);
        final DlmsDevice dlmsDevice = new DlmsDevice(deviceIdentification);
        dlmsDevice.setInDebugMode(getBoolean(settings, Keys.KEY_INDEBUGMODE, Defaults.DEFAULT_INDEBUGMODE));
        dlmsDevice.setCommunicationMethod(
                getString(settings, Keys.KEY_COMM_METHOD, DLMS_DEFAULT_COMMUNICATION_METHOD));
        dlmsDevice.setIpAddressIsStatic(
                getBoolean(settings, Keys.KEY_IP_ADDR_IS_STATIC, DLMS_DEFAULT_IP_ADDRESS_IS_STATIC));

        return dlmsDevice;
    }


    private void insertDlmsEMeter(final DlmsDevice dlmsDevice, final Map<String, String> settings) {
        dlmsDevice.setPort(DLMS_DEFAULT_PORT);
        dlmsDevice.setHls5Active(DLMS_DEFAULT_HSL5_ACTIVE);

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
