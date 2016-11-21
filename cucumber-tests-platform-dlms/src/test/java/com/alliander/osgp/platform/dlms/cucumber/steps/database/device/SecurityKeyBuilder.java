package com.alliander.osgp.platform.dlms.cucumber.steps.database.device;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsSecurityKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.dlms.cucumber.steps.Defaults;
import com.alliander.osgp.platform.dlms.cucumber.steps.Keys;

public class SecurityKeyBuilder {

    @Autowired
    private DlmsSecurityKeyRepository securityKeyRepository;

    private String dlmsDeviceId = Defaults.DEFAULT_DLMS_DEVICE_ID;
    private String securityKeyType = null;
    private Date validFrom = Defaults.DEFAULT_VALID_FROM;
    private Date validTo = Defaults.DEFAULT_VALID_TO;
    private String securityKey = null;

    public SecurityKeyBuilder setDlmsDeviceId(final String dlmsDeviceId) {
        this.dlmsDeviceId = dlmsDeviceId;
        return this;
    }

    public SecurityKeyBuilder setSecurityKeyType(final String securityKeyType) {
        this.securityKeyType = securityKeyType;
        return this;
    }

    public SecurityKeyBuilder setValidFrom(final Date validFrom) {
        this.validFrom = validFrom;
        return this;
    }

    public SecurityKeyBuilder setValidTo(final Date validTo) {
        this.validTo = validTo;
        return this;
    }

    public SecurityKeyBuilder setSecurityKey(final String securityKey) {
        this.securityKey = securityKey;
        return this;
    }

    public SecurityKey buildSecurityKey(final Map<String, String> inputSettings,
            final SecurityKeyType securityKeyTypes[], final String securityKeys[]) {
        final SecurityKey securityKey = new SecurityKey();
        final DlmsDevice dlmsDevice = new DlmsDevice();
        final DateFormat format = new SimpleDateFormat("MM dd yyyy", Locale.getDefault());
        if (inputSettings.containsKey(Keys.KEY_DLMS_DEVICE_ID)) {
            dlmsDevice.setDeviceIdentification((inputSettings.get(Keys.KEY_DLMS_DEVICE_ID)));
        }
        if (inputSettings.containsKey(Keys.KEY_VERSION)) {
            securityKey.setVersion(Long.parseLong(inputSettings.get(Keys.KEY_VERSION)));
        }
        if (inputSettings.containsKey(Keys.KEY_VALID_FROM)) {
            try {
                securityKey.setValidFrom(format.parse(inputSettings.get(Keys.KEY_VALID_FROM)));
            } catch (final ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (inputSettings.containsKey(Keys.KEY_VALID_TO)) {
            try {
                securityKey.setValidTo(format.parse(inputSettings.get(Keys.KEY_VALID_TO)));
            } catch (final ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        final List<SecurityKey> keys = new ArrayList<>();
        for (int i = 0; i < securityKeyTypes.length; i++) {
            keys.add(new SecurityKey(dlmsDevice, securityKeyTypes[i], securityKeys[i], this.validFrom, null));
        }

        for (final SecurityKey seckey : keys) {
            this.securityKeyRepository.save(seckey);
        }

        // this.securityKeyRepository.save(securityKey);
        return securityKey;
    }
}
