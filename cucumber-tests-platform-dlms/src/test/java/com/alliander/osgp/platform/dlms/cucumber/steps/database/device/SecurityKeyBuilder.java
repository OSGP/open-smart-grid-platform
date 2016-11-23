package com.alliander.osgp.platform.dlms.cucumber.steps.database.device;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsSecurityKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.dlms.cucumber.steps.Defaults;
import com.alliander.osgp.platform.dlms.cucumber.steps.Keys;

public class SecurityKeyBuilder implements Builder<SecurityKey> {

    private SecurityKeyType securityKeyType = null;
    private Date validFrom = Defaults.DEFAULT_VALID_FROM;
    private Date validTo = Defaults.DEFAULT_VALID_TO;
    private Long version = Defaults.DEFAULT_VERSION;
    private String key = Defaults.DEFAULT_SECURITY_KEY_A;

    private DlmsDevice dlmsDevice;

    public SecurityKeyBuilder setSecurityKeyType(final SecurityKeyType securityKeyType) {
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

    public SecurityKeyBuilder setVersion(final Long version) {
        this.version = version;
        return this;
    }

    public SecurityKeyBuilder setKey(final String key) {
        this.key = key;
        return this;
    }

    public SecurityKeyBuilder setDlmsDevice(final DlmsDevice dlmsDevice) {
        this.dlmsDevice = dlmsDevice;
        return this;
    }

    public SecurityKeyBuilder buildSecurityKey(final Map<String, String> inputSettings) {
        if (inputSettings.containsKey(Keys.KEY_VERSION)) {
            this.setVersion(Long.parseLong(inputSettings.get(Keys.KEY_VERSION)));
        }
        if (inputSettings.containsKey(Keys.KEY_VALID_FROM)) {
            try {
                this.setValidFrom(format.parse(inputSettings.get(Keys.KEY_VALID_FROM)));
            } catch (final ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (inputSettings.containsKey(Keys.KEY_VALID_TO)) {
            try {
                this.setValidTo(format.parse(inputSettings.get(Keys.KEY_VALID_TO)));
            } catch (final ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (inputSettings.containsKey(Keys.KEY_SECURITY_KEY_A)) {
            this.setKey(inputSettings.get(Keys.KEY_SECURITY_KEY_A));
        }

        if (inputSettings.containsKey(Keys.KEY_SECURITY_KEY_M)) {
            this.setKey(inputSettings.get(Keys.KEY_SECURITY_KEY_M));
        }

        if (inputSettings.containsKey(Keys.KEY_SECURITY_KEY_E)) {
            this.setKey(inputSettings.get(Keys.KEY_SECURITY_KEY_E));
        }

        return this;
    }

    @Override
    public SecurityKey build() {
        final SecurityKey securityKey = new SecurityKey(this.dlmsDevice, this.securityKeyType, this.key, this.validFrom,
                this.validTo);

        securityKey.setVersion(this.version);
        securityKey.setValidFrom(validFrom);
        securityKey.setValidTo(validTo);
        return securityKey;
    }
}
