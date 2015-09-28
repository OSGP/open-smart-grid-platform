package com.alliander.osgp.adapter.ws.smartmetering.domain.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

/**
 * An entity class which contains the information of a the synchronized time.
 */
@Entity
public class SynchronizeTimeReads extends AbstractEntity {

	private static final long serialVersionUID = -136966569210717654L;

    @Column
    private String deviceIdentification;

    @Column
    private String correlationUid;
    
    @Column
    private Date dateTime;
    
    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

	public String getCorrelationUid() {
		return correlationUid;
	}

	public void setCorrelationUid(String correlationUid) {
		this.correlationUid = correlationUid;
	}
	
}
