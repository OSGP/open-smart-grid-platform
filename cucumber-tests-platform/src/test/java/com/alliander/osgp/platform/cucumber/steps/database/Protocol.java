package com.alliander.osgp.platform.cucumber.steps.database;


public class Protocol {

    private final ProtocolType type;
    private final String protocol;
    private final String version;

    public Protocol(final ProtocolType type, final String protocol, final String version) {
        super();
        this.type = type;
        this.protocol = protocol;
        this.version = version;
    }

    public ProtocolType getType() {
        return this.type;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public String getVersion() {
        return this.version;
    }

    public enum ProtocolType {
        OSLP,
        DSMR,
        DLMS;
    }

}
