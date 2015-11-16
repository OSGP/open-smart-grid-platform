/**
 * Copyright 2015 Smart Society Services B.V.
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;

public class SeasonProfile implements Comparable<SeasonProfile>, Serializable {

    private static final long serialVersionUID = -9110599718005128216L;

    private String seasonProfileName;

    private Date seasonStart;

    private WeekProfile weekProfile;

    public SeasonProfile(final String seasonProfileName, final Date seasonStart, final WeekProfile weekProfile) {
        super();
        this.seasonProfileName = seasonProfileName;
        this.seasonStart = seasonStart;
        this.weekProfile = weekProfile;
    }

    public String getSeasonProfileName() {
        return this.seasonProfileName;
    }

    public Date getSeasonStart() {
        return this.seasonStart;
    }

    public WeekProfile getWeekProfile() {
        return this.weekProfile;
    }

    @Override
    public String toString() {
        return "SeasonProfile [seasonProfileName=" + this.seasonProfileName + ", seasonStart=" + this.seasonStart
                + ", weekProfile=" + this.weekProfile + "]";
    }

    @Override
    public int compareTo(final SeasonProfile other) {
        return other.seasonProfileName.compareTo(this.seasonProfileName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.seasonProfileName == null) ? 0 : this.seasonProfileName.hashCode());
        result = prime * result + ((this.seasonStart == null) ? 0 : this.seasonStart.hashCode());
        result = prime * result + ((this.weekProfile == null) ? 0 : this.weekProfile.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final SeasonProfile other = (SeasonProfile) obj;
        if (this.seasonProfileName == null) {
            if (other.seasonProfileName != null) {
                return false;
            }
        } else if (!this.seasonProfileName.equals(other.seasonProfileName)) {
            return false;
        }
        if (this.seasonStart == null) {
            if (other.seasonStart != null) {
                return false;
            }
        } else if (!this.seasonStart.equals(other.seasonStart)) {
            return false;
        }
        if (this.weekProfile == null) {
            if (other.weekProfile != null) {
                return false;
            }
        } else if (!this.weekProfile.equals(other.weekProfile)) {
            return false;
        }
        return true;
    }
}
