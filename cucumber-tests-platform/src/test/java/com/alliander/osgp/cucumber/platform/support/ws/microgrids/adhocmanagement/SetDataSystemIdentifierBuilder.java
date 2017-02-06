/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.support.ws.microgrids.adhocmanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.Profile;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetDataSystemIdentifier;
import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetPoint;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;

public class SetDataSystemIdentifierBuilder {

    private List<SetDataSystemIdentifier> systems = new ArrayList<>();

    private Integer id;
    private String type;
    private List<SetPoint> setPoints;
    private List<Profile> profiles;

    public SetDataSystemIdentifierBuilder() {
    }

    public SetDataSystemIdentifierBuilder withId(final Integer id) {
        this.id = id;
        return this;
    }

    public SetDataSystemIdentifierBuilder withType(final String type) {
        this.type = type;
        return this;
    }

    public SetDataSystemIdentifierBuilder withSetPoints(final List<SetPoint> setPoints) {
        this.setPoints = setPoints;
        return this;
    }

    public SetDataSystemIdentifierBuilder withProfiles(final List<Profile> profiles) {
        this.profiles = profiles;
        return this;
    }

    public SetDataSystemIdentifier build() {
        final SetDataSystemIdentifier system = new SetDataSystemIdentifier();
        system.setId(this.id);
        system.setType(this.type);
        if (this.setPoints != null) {
            system.getSetPoint().addAll(this.setPoints);
        }
        if (this.profiles != null) {
            system.getProfile().addAll(this.profiles);
        }
        return system;
    }

    public List<SetDataSystemIdentifier> buildList() {
        return this.systems;
    }

    public SetDataSystemIdentifierBuilder withSettings(final Map<String, String> settings) {
        if (!settings.containsKey(Keys.KEY_NUMBER_OF_SYSTEMS)) {
            throw new AssertionError(
                    "The Step DataTable must contain the number of systems for key \""
                            + Keys.KEY_NUMBER_OF_SYSTEMS + "\" when creating a set data request.");
        }
        final int numberOfSystems = Integer.parseInt(settings.get(Keys.KEY_NUMBER_OF_SYSTEMS));
        for (int i = 1; i <= numberOfSystems; i++) {
            this.systems.add(this.withSettings(settings, i).build());
        }

        return this;
    }

    private SetDataSystemIdentifierBuilder withSettings(final Map<String, String> settings, final int index) {
        this.withId(SettingsHelper.getIntegerValue(settings, Keys.KEY_SYSTEM_ID, index));
        this.withType(SettingsHelper.getStringValue(settings, Keys.KEY_SYSTEM_TYPE, index));
        this.withSetPoints(new SetPointBuilder().withSettings(settings, index).buildList());
        this.withProfiles(new ProfileBuilder().withSettings(settings, index).buildList());

        return this;
    }
}
