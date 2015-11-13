/**
 * Copyright 2015 Smart Society Services B.V.
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

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
    public int compareTo(final SeasonProfile o) {
        return o.seasonProfileName.compareTo(this.seasonProfileName);
    }
}
