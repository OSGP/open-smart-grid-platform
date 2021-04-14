/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.microgrids.support.ws.microgrids.adhocmanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.XMLGregorianCalendar;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.ProfileEntry;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;

public class ProfileEntryBuilder {

  private List<ProfileEntry> profileEntries = new ArrayList<>();

  private Integer id;
  private XMLGregorianCalendar time;
  private double value;

  public ProfileEntryBuilder() {}

  public ProfileEntryBuilder withId(final Integer id) {
    this.id = id;
    return this;
  }

  public ProfileEntryBuilder withTime(final XMLGregorianCalendar time) {
    this.time = time;
    return this;
  }

  public ProfileEntryBuilder withValue(final double value) {
    this.value = value;
    return this;
  }

  public ProfileEntry build() {
    final ProfileEntry profileEntry = new ProfileEntry();
    profileEntry.setId(this.id);
    profileEntry.setTime(this.time);
    profileEntry.setValue(this.value);
    return profileEntry;
  }

  public List<ProfileEntry> buildList() {
    return this.profileEntries;
  }

  public ProfileEntryBuilder withSettings(
      final Map<String, String> settings, final int systemIndex, final int profileIndex) {
    final int[] indexes = {systemIndex, profileIndex};
    if (!SettingsHelper.hasKey(settings, PlatformKeys.KEY_NUMBER_OF_PROFILE_ENTRIES, indexes)) {
      throw new AssertionError(
          "The Step DataTable must contain the number of profile entries for key \""
              + SettingsHelper.makeKey(PlatformKeys.KEY_NUMBER_OF_PROFILE_ENTRIES, indexes)
              + "\" when creating a set data request.");
    }
    final int numberOfProfileEntries =
        SettingsHelper.getIntegerValue(
            settings, PlatformKeys.KEY_NUMBER_OF_PROFILE_ENTRIES, indexes);
    for (int i = 1; i <= numberOfProfileEntries; i++) {
      this.profileEntries.add(this.withSettings(settings, systemIndex, profileIndex, i).build());
    }

    return this;
  }

  private ProfileEntryBuilder withSettings(
      final Map<String, String> settings,
      final int systemIndex,
      final int profileIndex,
      final int index) {
    final int[] indexes = {systemIndex, profileIndex, index};
    this.withId(
        SettingsHelper.getIntegerValue(settings, PlatformKeys.KEY_PROFILE_ENTRY_ID, indexes));
    this.withTime(
        SettingsHelper.getXmlGregorianCalendarValue(
            settings, PlatformKeys.KEY_PROFILE_ENTRY_TIME, indexes));
    this.withValue(
        SettingsHelper.getDoubleValue(settings, PlatformKeys.KEY_PROFILE_ENTRY_VALUE, indexes));

    return this;
  }
}
