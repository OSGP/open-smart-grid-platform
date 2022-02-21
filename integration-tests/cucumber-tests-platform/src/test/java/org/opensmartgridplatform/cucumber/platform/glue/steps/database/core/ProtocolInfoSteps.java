package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo.Builder;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ProtocolInfoSteps {

  @Autowired private ProtocolInfoRepository protocolInfoRepository;

  @Given("^a protocol$")
  public void aProtocol(final Map<String, String> settings) {
    this.protocolInfoRepository.save(this.getProtocolFromMap(settings));
  }

  @Then("^I validate that there is only one protocol record$")
  public void validateProtocol(final Map<String, String> settings) {
    final ProtocolInfo expected = this.getProtocolFromMap(settings);
    final ProtocolInfo actual =
        this.protocolInfoRepository.findByProtocolAndProtocolVersion(
            getProtocol(settings), getProtocolVersion(settings));
    assertThat(expected.getProtocol()).isEqualTo(actual.getProtocol());
    assertThat(expected.getProtocolVersion()).isEqualTo(actual.getProtocolVersion());
    assertThat(expected.getProtocolVariant()).isEqualTo(actual.getProtocolVariant());
  }

  private ProtocolInfo getProtocolFromMap(final Map<String, String> settings) {
    final Builder builder = new ProtocolInfo.Builder();
    builder.withProtocol(getProtocol(settings));
    builder.withProtocolVersion(getProtocolVersion(settings));
    builder.withProtocolVariant(getProtocolVariant(settings));
    return builder.build();
  }

  private static String getProtocol(final Map<String, String> settings) {
    return getString(settings, PlatformKeys.KEY_PROTOCOL, PlatformDefaults.DEFAULT_PROTOCOL);
  }

  private static String getProtocolVersion(final Map<String, String> settings) {
    return getString(
        settings, PlatformKeys.KEY_PROTOCOL_VERSION, PlatformDefaults.DEFAULT_PROTOCOL_VERSION);
  }

  private static String getProtocolVariant(final Map<String, String> settings) {
    return getString(
        settings, PlatformKeys.KEY_PROTOCOL_VARIANT, PlatformDefaults.DEFAULT_PROTOCOL_VARIANT);
  }
}
