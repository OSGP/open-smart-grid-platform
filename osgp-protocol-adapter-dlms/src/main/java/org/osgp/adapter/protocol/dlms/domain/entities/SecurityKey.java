package org.osgp.adapter.protocol.dlms.domain.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

@Entity
public class SecurityKey extends AbstractEntity {

    private static final long serialVersionUID = 2664922854864532720L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dlms_device_id")
    private DlmsDevice dlmsDevice;

    @Enumerated(EnumType.STRING)
    private SecurityKeyType securityKeyType;

    @Column(nullable = false)
    private Date validFrom;

    @Column(nullable = true)
    private Date validTo;

    @Column
    private String securityKey;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final SecurityKey key = (SecurityKey) o;

        return Objects.equals(this.getDlmsDevice(), key.getDlmsDevice())
                && Objects.equals(this.getSecurityKeyType(), key.getSecurityKeyType())
                && Objects.equals(this.getValidFrom(), key.getValidFrom());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDlmsDevice(), this.getSecurityKeyType(), this.getValidFrom());
    }

    public DlmsDevice getDlmsDevice() {
        return this.dlmsDevice;
    }

    public void setDlmsDevice(final DlmsDevice dlmsDevice) {
        this.dlmsDevice = dlmsDevice;
        if (!dlmsDevice.getSecurityKeys().contains(this)) {
            dlmsDevice.getSecurityKeys().add(this);
        }
    }

    public SecurityKeyType getSecurityKeyType() {
        return this.securityKeyType;
    }

    public void setSecurityKeyType(final SecurityKeyType securityKeyType) {
        this.securityKeyType = securityKeyType;
    }

    public Date getValidFrom() {
        return this.validFrom;
    }

    public void setValidFrom(final Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return this.validTo;
    }

    public void setValidTo(final Date validTo) {
        this.validTo = validTo;
    }

    public void setSecurityKey(final String key) {
        this.securityKey = key;
    }

    public String getSecurityKey() {
        return this.securityKey;
    }
}
