// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.Address;

@ExtendWith(MockitoExtension.class)
public class AddressConversionTest {

  private static final String CITY = "City";
  private static final String MUNICIPALITY = "Municipality";
  private static final int NUMBER = 42;
  private static final String NUMBER_ADDITION = "ADD";
  private static final String POSTAL_CODE = "1234AB";
  private static final String STREET = "Street";

  @InjectMocks private DeviceManagementMapper mapper;

  @Mock private SsldRepository ssldRepository;

  private Address createAddress() {
    return new Address(CITY, POSTAL_CODE, STREET, NUMBER, NUMBER_ADDITION, MUNICIPALITY);
  }

  private org.opensmartgridplatform.adapter.ws.schema.core.common.Address createWsAddress() {
    final org.opensmartgridplatform.adapter.ws.schema.core.common.Address address =
        new org.opensmartgridplatform.adapter.ws.schema.core.common.Address();
    address.setCity(CITY);
    address.setMunicipality(MUNICIPALITY);
    address.setNumber(NUMBER);
    address.setNumberAddition(NUMBER_ADDITION);
    address.setPostalCode(POSTAL_CODE);
    address.setStreet(STREET);
    return address;
  }

  @BeforeEach
  public void setUp() throws Exception {
    this.mapper.initialize();
  }

  @Test
  public void shouldConvertAddressToContainer() {
    // Arrange
    final Address source = this.createAddress();
    final org.opensmartgridplatform.adapter.ws.schema.core.common.Address expected =
        this.createWsAddress();

    // Act
    final org.opensmartgridplatform.adapter.ws.schema.core.common.Address actual =
        this.mapper.map(
            source, org.opensmartgridplatform.adapter.ws.schema.core.common.Address.class);

    // Assert
    assertThat(actual).isEqualToComparingFieldByField(expected);
  }

  @Test
  public void shouldConvertWsAddressToAddress() {
    // Arrange
    final org.opensmartgridplatform.adapter.ws.schema.core.common.Address source =
        this.createWsAddress();
    final Address expected = this.createAddress();

    // Act
    final Address actual = this.mapper.map(source, Address.class);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void shouldMapNullValue() {
    // Arrange
    final org.opensmartgridplatform.adapter.ws.schema.core.common.Address source = null;
    final Address expected = null;

    // Act
    final Address actual = this.mapper.map(source, Address.class);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }
}
