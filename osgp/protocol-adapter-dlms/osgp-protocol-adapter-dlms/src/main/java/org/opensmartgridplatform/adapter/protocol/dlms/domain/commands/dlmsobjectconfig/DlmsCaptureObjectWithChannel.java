package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

public class DlmsCaptureObjectWithChannel extends DlmsCaptureObject {
    private final int channel;

    public DlmsCaptureObjectWithChannel(final DlmsObject object, final int channel, final int attributeId) {
        super(object, attributeId);
        this.channel = channel;
    }

    public DlmsCaptureObjectWithChannel(final DlmsObject object, final int channel) {
        super(object, object.getDefaultAttributeId());
        this.channel = channel;
    }

    public int getChannel() {
        return this.channel;
    }
}
