//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.core.builders;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_CONTAINER_CITY;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_CONTAINER_MUNICIPALITY;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_CONTAINER_NUMBER;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_CONTAINER_NUMBER_ADDITION;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_CONTAINER_POSTALCODE;
import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_CONTAINER_STREET;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_CONTAINER_CITY;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_CONTAINER_MUNICIPALITY;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_CONTAINER_NUMBER;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_CONTAINER_NUMBER_ADDITION;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_CONTAINER_POSTALCODE;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_CONTAINER_STREET;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.core.common.Address;

public class AddressBuilder implements CucumberBuilder<Address> {

  private String city = DEFAULT_CONTAINER_CITY;
  private String municipality = DEFAULT_CONTAINER_MUNICIPALITY;
  private Integer number = DEFAULT_CONTAINER_NUMBER;
  private String numberAddition = DEFAULT_CONTAINER_NUMBER_ADDITION;
  private String postalCode = DEFAULT_CONTAINER_POSTALCODE;
  private String street = DEFAULT_CONTAINER_STREET;

  @Override
  public Address build() {
    final Address address = new Address();
    address.setCity(this.city);
    address.setMunicipality(this.municipality);
    address.setNumber(this.number);
    address.setNumberAddition(this.numberAddition);
    address.setPostalCode(this.postalCode);
    address.setStreet(this.street);
    return address;
  }

  @Override
  public CucumberBuilder<Address> withSettings(final Map<String, String> settings) {
    this.updateCity(settings);
    this.updateMunicipality(settings);
    this.updateNumber(settings);
    this.updateNumberAddition(settings);
    this.updatePostalCode(settings);
    this.updateStreet(settings);
    return this;
  }

  private void updateCity(final Map<String, String> settings) {
    if (settings.containsKey(KEY_CONTAINER_CITY)) {
      this.city = getString(settings, KEY_CONTAINER_CITY);
    }
  }

  private void updateMunicipality(final Map<String, String> settings) {
    if (settings.containsKey(KEY_CONTAINER_MUNICIPALITY)) {
      this.municipality = getString(settings, KEY_CONTAINER_MUNICIPALITY);
    }
  }

  private void updateNumber(final Map<String, String> settings) {
    if (settings.containsKey(KEY_CONTAINER_NUMBER)) {
      this.number = getInteger(settings, KEY_CONTAINER_NUMBER);
    }
  }

  private void updateNumberAddition(final Map<String, String> settings) {
    if (settings.containsKey(KEY_CONTAINER_NUMBER_ADDITION)) {
      this.numberAddition = getString(settings, KEY_CONTAINER_NUMBER_ADDITION);
    }
  }

  private void updatePostalCode(final Map<String, String> settings) {
    if (settings.containsKey(KEY_CONTAINER_POSTALCODE)) {
      this.postalCode = getString(settings, KEY_CONTAINER_POSTALCODE);
    }
  }

  private void updateStreet(final Map<String, String> settings) {
    if (settings.containsKey(KEY_CONTAINER_STREET)) {
      this.street = getString(settings, KEY_CONTAINER_STREET);
    }
  }
}
