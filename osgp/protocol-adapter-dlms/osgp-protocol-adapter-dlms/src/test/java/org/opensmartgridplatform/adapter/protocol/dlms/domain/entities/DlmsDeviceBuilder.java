package org.opensmartgridplatform.adapter.protocol.dlms.domain.entities;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DefaultValue.notSet;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DefaultValue.setTo;

/** Creates new instances, for testing purposes only. */
public class DlmsDeviceBuilder {
    private DefaultValue<Boolean> lls1Active = notSet();
    private DefaultValue<Boolean> hls3Active = notSet();
    private DefaultValue<Boolean> hls4Active = notSet();
    private DefaultValue<Boolean> hls5Active = notSet();

    public DlmsDevice build() {
        final DlmsDevice device = new DlmsDevice();
        device.setLls1Active(this.lls1Active.orElse(false));
        device.setHls3Active(this.hls3Active.orElse(false));
        device.setHls4Active(this.hls4Active.orElse(false));
        device.setHls5Active(this.hls5Active.orElse(false));
        return device;
    }

    public DlmsDeviceBuilder withLls1Active(final boolean lls1Active) {
        this.lls1Active = setTo(lls1Active);
        return this;
    }

    public DlmsDeviceBuilder withHls3Active(final boolean hls3Active) {
        this.hls3Active = setTo(hls3Active);
        return this;
    }

    public DlmsDeviceBuilder withHls4Active(final boolean hls4Active) {
        this.hls4Active = setTo(hls4Active);
        return this;
    }

    public DlmsDeviceBuilder withHls5Active(final boolean hls5Active) {
        this.hls5Active = setTo(hls5Active);
        return this;
    }
}