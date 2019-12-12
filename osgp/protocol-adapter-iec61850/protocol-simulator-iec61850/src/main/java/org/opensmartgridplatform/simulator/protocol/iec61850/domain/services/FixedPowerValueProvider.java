package org.opensmartgridplatform.simulator.protocol.iec61850.domain.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "rtu.transformer.simulation.enabled", matchIfMissing = true, havingValue = "false")
public class FixedPowerValueProvider implements ValueProvider<Float> {

    @Value("${rtu.transformer.default.power:100}")
    private float power;

    @Override
    public Float getValue() {
        return this.power;
    }
}
