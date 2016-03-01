package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

public class Certification implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -4199748600003850659L;

    private String certificateUrl;

    private String certificateDomain;

    public Certification(final String certificateUrl, final String certificateDomain) {
        this.certificateUrl = certificateUrl;
        this.certificateDomain = certificateDomain;
    }

    public String getCertificateUrl() {
        return this.certificateUrl;
    }

    public String getCertificateDomain() {
        return this.certificateDomain;
    }

}
