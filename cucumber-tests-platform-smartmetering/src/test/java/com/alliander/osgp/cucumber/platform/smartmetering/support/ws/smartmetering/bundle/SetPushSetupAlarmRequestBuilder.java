package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import java.math.BigInteger;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetPushSetupAlarmRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SetPushSetupAlarmRequestBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetPushSetupAlarmRequestBuilder.class);

    private static final String DEFAULT_HOST = "localhost";
    private static final BigInteger DEFAULT_PORT = new BigInteger("9598");

    private String host;
    private BigInteger port;

    public SetPushSetupAlarmRequestBuilder withDefaults() {
        this.host = DEFAULT_HOST;
        this.port = DEFAULT_PORT;
        return this;
    }

    public SetPushSetupAlarmRequestBuilder fromParameterMap(final Map<String, String> parameters) {
        this.host = this.getHost(parameters);
        this.port = this.getPort(parameters);
        return this;
    }

    public SetPushSetupAlarmRequest build() {
        final SetPushSetupAlarmRequest request = new SetPushSetupAlarmRequest();
        final PushSetupAlarm pushSetupAlarm = new PushSetupAlarm();
        pushSetupAlarm.setHost(this.host);
        pushSetupAlarm.setPort(this.port);
        request.setPushSetupAlarm(pushSetupAlarm);
        return request;
    }

    private String getHost(final Map<String, String> parameters) {
        if (parameters.containsKey(PlatformSmartmeteringKeys.HOSTNAME)) {
            return parameters.get(PlatformSmartmeteringKeys.HOSTNAME);
        }
        LOGGER.debug("Key for host not found, using default value.");
        return DEFAULT_HOST;
    }

    private BigInteger getPort(final Map<String, String> parameters) {
        if (parameters.containsKey(PlatformSmartmeteringKeys.PORT)) {
            return new BigInteger(parameters.get(PlatformSmartmeteringKeys.PORT));
        }
        LOGGER.debug("Key for port not found, using default value.");
        return DEFAULT_PORT;
    }
}
