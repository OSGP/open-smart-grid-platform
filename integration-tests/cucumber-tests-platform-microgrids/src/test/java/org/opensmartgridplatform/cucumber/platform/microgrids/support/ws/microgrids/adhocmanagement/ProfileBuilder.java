/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.microgrids.support.ws.microgrids.adhocmanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.Profile;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.ProfileEntry;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;

public class ProfileBuilder {

  private List<Profile> profiles = new ArrayList<>();

  private Integer id;
  private String node;
  private List<ProfileEntry> profileEntries;

  public ProfileBuilder() {}

  public ProfileBuilder withId(final Integer id) {
    this.id = id;
    return this;
  }

  public ProfileBuilder withNode(final String node) {
    this.node = node;
    return this;
  }

  public ProfileBuilder withProfileEntries(final List<ProfileEntry> profileEntries) {
    this.profileEntries = profileEntries;
    return this;
  }

  public Profile build() {
    final Profile profile = new Profile();
    profile.setId(this.id);
    profile.setNode(this.node);
    if (this.profileEntries != null) {
      profile.getProfileEntry().addAll(this.profileEntries);
    }
    return profile;
  }

  public List<Profile> buildList() {
    return this.profiles;
  }

  public ProfileBuilder withSettings(final Map<String, String> settings, final int systemIndex) {
    if (!SettingsHelper.hasKey(settings, PlatformKeys.KEY_NUMBER_OF_PROFILES, systemIndex)) {
      throw new AssertionError(
          "The Step DataTable must contain the number of profiles for key \""
              + SettingsHelper.makeKey(PlatformKeys.KEY_NUMBER_OF_PROFILES, systemIndex)
              + "\" when creating a set data request.");
    }
    final int numberOfProfiles =
        SettingsHelper.getIntegerValue(settings, PlatformKeys.KEY_NUMBER_OF_PROFILES, systemIndex);
    for (int i = 1; i <= numberOfProfiles; i++) {
      this.profiles.add(this.withSettings(settings, systemIndex, i).build());
    }

    return this;
  }

  private ProfileBuilder withSettings(
      final Map<String, String> settings, final int systemIndex, final int index) {
    final int[] indexes = {systemIndex, index};
    this.withId(SettingsHelper.getIntegerValue(settings, PlatformKeys.KEY_PROFILE_ID, indexes));
    this.withNode(SettingsHelper.getStringValue(settings, PlatformKeys.KEY_PROFILE_NODE, indexes));
    this.withProfileEntries(
        new ProfileEntryBuilder().withSettings(settings, systemIndex, index).buildList());

    return this;
  }
}
