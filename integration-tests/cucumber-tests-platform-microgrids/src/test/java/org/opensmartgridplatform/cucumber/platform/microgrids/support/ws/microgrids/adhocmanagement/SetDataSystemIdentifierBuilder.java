// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.microgrids.support.ws.microgrids.adhocmanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.Profile;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataSystemIdentifier;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetPoint;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;

public class SetDataSystemIdentifierBuilder {

  private List<SetDataSystemIdentifier> systems = new ArrayList<>();

  private Integer id;
  private String type;
  private List<SetPoint> setPoints;
  private List<Profile> profiles;

  public SetDataSystemIdentifierBuilder() {}

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
    if (!settings.containsKey(PlatformKeys.KEY_NUMBER_OF_SYSTEMS)) {
      throw new AssertionError(
          "The Step DataTable must contain the number of systems for key \""
              + PlatformKeys.KEY_NUMBER_OF_SYSTEMS
              + "\" when creating a set data request.");
    }
    final int numberOfSystems = Integer.parseInt(settings.get(PlatformKeys.KEY_NUMBER_OF_SYSTEMS));
    for (int i = 1; i <= numberOfSystems; i++) {
      this.systems.add(this.withSettings(settings, i).build());
    }

    return this;
  }

  private SetDataSystemIdentifierBuilder withSettings(
      final Map<String, String> settings, final int index) {
    this.withId(SettingsHelper.getIntegerValue(settings, PlatformKeys.KEY_SYSTEM_ID, index));
    this.withType(SettingsHelper.getStringValue(settings, PlatformKeys.KEY_SYSTEM_TYPE, index));
    this.withSetPoints(new SetPointBuilder().withSettings(settings, index).buildList());
    this.withProfiles(new ProfileBuilder().withSettings(settings, index).buildList());

    return this;
  }
}
