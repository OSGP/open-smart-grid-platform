/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support.ws.microgrids.adhocmanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.Profile;
import com.alliander.osgp.platform.cucumber.steps.Keys;

public class ProfileBuilder {

    protected List<Profile> profile = new ArrayList<>();
    private Integer id;
    private String node;

    public ProfileBuilder() {
    }

    public ProfileBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public ProfileBuilder withNode(String node) {
        this.node = node;
        return this;
    }
    
    public Profile build() {
        final Profile profile = new Profile();
        profile.setId(this.id);
        profile.setNode(this.node);
        return profile;
    }

    public List<Profile> buildList() {
        return this.profile;
    }

    public ProfileBuilder withSettings(final Map<String, String> settings) {
        for (int i = 1; i <= this.count(settings, Keys.KEY_PROFILE_ID); i++) {
            this.profile.add(this.withSettings(settings, i).build());
        }

        return this;
    }

    private ProfileBuilder withSettings(final Map<String, String> settings, final int index) {
        if (this.hasKey(settings, Keys.KEY_PROFILE_ID, index)) {
            this.withId(Integer.parseInt(getStringValue(settings, Keys.KEY_PROFILE_ID, index)));
        } 
        if (this.hasKey(settings, Keys.KEY_PROFILE_NODE, index)) {
            this.withNode(getStringValue(settings, Keys.KEY_PROFILE_NODE, index));
        } 
        return this;
    }

    private int count(final Map<String, String> settings, final String keyPrefix) {
        for (int i = 10; i > 0; i--) {
            if (this.hasKey(settings, keyPrefix, i)) {
                return i;
            }
        }
        return 0;
    }

    private boolean hasKey(final Map<String, String> settings, final String keyPrefix, final int index) {
        return settings.containsKey(makeKey(keyPrefix, index));
    }

    private String makeKey(final String keyPrefix, int index) {
        return keyPrefix + "_" + index;
    }

    private String getStringValue(final Map<String, String> settings, final String keyPrefix, final int index) {
        String key = makeKey(keyPrefix, index);
        return settings.get(key);
    }
}
