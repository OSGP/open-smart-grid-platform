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
        this.insertCommonDlms(settings);

        // Now create the DLMS device in the DLMS database
        if (DLMS_GAS_DEVICE.equals(getString(settings, Keys.KEY_DEVICE_TYPE, DLMS_DEFAULT_DEVICE_TYPE))) {
            this.insertDlmsGasMeter(settings);
        } else {
            this.insertDlmsEMeter(settings);
        }
    }

    private void insertCommonDlms(final Map<String, String> settings) {
        final String deviceIdentification = getString(settings, Keys.KEY_DEVICE_IDENTIFICATION,
                DeviceSteps.DEFAULT_DEVICE_IDENTIFICATION);
        final DlmsDevice dlmsDevice = new DlmsDevice(deviceIdentification);
        dlmsDevice.setInDebugMode(getBoolean(settings, Keys.KEY_INDEBUGMODE, Defaults.DEFAULT_INDEBUGMODE));
    }


    private void insertDlmsEMeter(final Map<String, String> settings) {
        final String deviceIdentification = getString(settings, Keys.KEY_DEVICE_IDENTIFICATION,
                DeviceSteps.DEFAULT_DEVICE_IDENTIFICATION);
        final DlmsDevice dlmsDevice = new DlmsDevice(deviceIdentification);
        dlmsDevice.setCommunicationMethod(
                getString(settings, Keys.KEY_COMM_METHOD, DLMS_DEFAULT_COMMUNICATION_METHOD));
        dlmsDevice.setIpAddressIsStatic(
                getBoolean(settings, Keys.KEY_IP_ADDR_IS_STATIC, DLMS_DEFAULT_IP_ADDRESS_IS_STATIC));
        dlmsDevice.setPort(DLMS_DEFAULT_PORT);
        dlmsDevice.setHls5Active(DLMS_DEFAULT_HSL5_ACTIVE);
        this.dlmsDeviceRepository.save(dlmsDevice);
        this.insertDlmsSecurityKeys(dlmsDevice);
    }

    private void insertDlmsGasMeter(final Map<String, String> settings) {
        final String deviceIdentification = getString(settings, Keys.KEY_DEVICE_IDENTIFICATION,
                DeviceSteps.DEFAULT_DEVICE_IDENTIFICATION);
        final DlmsDevice dlmsDevice = new DlmsDevice(deviceIdentification);
        dlmsDevice.setCommunicationMethod(
                getString(settings, Keys.KEY_COMM_METHOD, DLMS_DEFAULT_COMMUNICATION_METHOD));
        dlmsDevice.setIpAddressIsStatic(
                getBoolean(settings, Keys.KEY_IP_ADDR_IS_STATIC, DLMS_DEFAULT_IP_ADDRESS_IS_STATIC));
        dlmsDevice.setHls5Active(false);

        this.dlmsDeviceRepository.save(dlmsDevice);
        this.insertDlmsSecurityKeys(dlmsDevice);
    }


    private void insertDlmsSecurityKeys(final DlmsDevice dlmsDevice) {
        final Date validFrom = new DateTime(2016,1,1,1,1,0).toDate();
        final List<SecurityKey> securityKeys = new ArrayList<>();
        securityKeys.add(new SecurityKey(dlmsDevice, SecurityKeyType.E_METER_ENCRYPTION,
                "bc082efed278e1bbebddc0431877d4fa2df7728229f3e03c57b2549142b40d047b35011dbf9f77ad91db5fe6f19a7b9c", validFrom, null));
        securityKeys.add(new SecurityKey(dlmsDevice, SecurityKeyType.E_METER_AUTHENTICATION,
                "bc082efed278e1bbebddc0431877d4fae80fa4e72925b6ad0bc67c84b8721598eda8458bcc1b2827fe6e5e7918ce22fd", validFrom, null));
        securityKeys.add(new SecurityKey(dlmsDevice, SecurityKeyType.E_METER_MASTER,
                "bc082efed278e1bbebddc0431877d4fa16374b00e96dd102beab666dcb72efbd1f0b868412497f6d3d0c62caa4700585", validFrom, null));

        for (final SecurityKey seckey : securityKeys) {
            this.securityKeyRepository.save(seckey);
        }
    }


    public DlmsDevice findDlmsDevice(final String deviceIdentification) {
        return this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
    }
}

/*
"id";"creation_time";"modification_time";"device_identification";"version";"icc_id";"communication_provider";"communication_method";"hls3active";"hls4active";"hls5active";"challenge_length";"with_list_supported";"selective_access_supported";"ip_address_is_static";"port";"client_id";"logical_id";"in_debug_mode"
64;"2016-10-10 07:56:59.862";"2016-10-10 07:56:59.862";"TESTG102400000001";0;"iccid";"KPN";"GPRS";f;f;f;;f;f;f;;;;f

*/