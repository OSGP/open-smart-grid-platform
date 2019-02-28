package org.opensmartgridplatform.simulator.protocol.iec60870.server;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.TypeId;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class Iec60870ASduHandler {

    private final Logger logger;

    @Autowired
    protected Iec60870ASduHandlerMap iec60870ASduHandlerMap;

    private TypeId typeId;

    public Iec60870ASduHandler(final Logger Logger, final TypeId typeId) {
        this.logger = Logger;
        this.typeId = typeId;
    }

    public abstract void accept(Connection t, ASdu u) throws IOException;

    public TypeId getTypeId() {
        return this.typeId;
    }

    @PostConstruct
    protected void register() {
        this.logger.info("Registering ASdu Handler {}", this.getClass().getSimpleName());
        this.iec60870ASduHandlerMap.registerHandler(this.typeId, this);
    }
}
