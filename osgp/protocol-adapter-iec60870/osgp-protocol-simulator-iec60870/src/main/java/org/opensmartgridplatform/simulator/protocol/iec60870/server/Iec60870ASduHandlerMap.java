package org.opensmartgridplatform.simulator.protocol.iec60870.server;

import java.util.HashMap;
import java.util.Map;

import org.openmuc.j60870.TypeId;
import org.springframework.stereotype.Component;

@Component
public class Iec60870ASduHandlerMap {

    private Map<TypeId, Iec60870ASduHandler> handlers = new HashMap<>();

    public Iec60870ASduHandler getHandler(final TypeId typeId) {
        return this.handlers.get(typeId);
    }

    public void registerHandler(final TypeId typeId, final Iec60870ASduHandler handler) {
        this.handlers.put(typeId, handler);
    }

}
