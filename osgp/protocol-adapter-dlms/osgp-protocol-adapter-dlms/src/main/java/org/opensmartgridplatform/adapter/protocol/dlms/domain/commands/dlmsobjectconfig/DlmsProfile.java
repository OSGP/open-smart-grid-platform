package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import java.util.List;

public class DlmsProfile extends DlmsObject {

    private final List<DlmsCaptureObject> captureObjects;
    private final ProfileCaptureTime captureTime;

    public DlmsProfile(final DlmsObjectType type, final String obisCode, final List<DlmsCaptureObject> captureObjects,
            final ProfileCaptureTime captureTime) {
        super(type, 7, obisCode);
        this.captureObjects = captureObjects;
        this.captureTime = captureTime;
    }

    public List<DlmsCaptureObject> getCaptureObjects() {
        return this.captureObjects;
    }

    public ProfileCaptureTime getCaptureTime() {
        return this.captureTime;
    }
}
