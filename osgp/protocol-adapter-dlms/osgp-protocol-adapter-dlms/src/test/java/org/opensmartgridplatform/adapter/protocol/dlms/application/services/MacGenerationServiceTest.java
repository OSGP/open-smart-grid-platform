package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.imagedata.FirmwareImageData;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.imagedata.FirmwareImageDataTest;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

@ExtendWith(MockitoExtension.class)
public class MacGenerationServiceTest {

  @InjectMocks MacGenerationService macGenerationService;
  @Mock SecretManagementService secretManagementService;

  // FIRMWARE UPDATE AUTHENTICATION KEY
  final byte[] authenticationKey = Hex.decode("F9AA9442108723357221D7AFCCD41BD1");
  final String expectedIv = "e91e40050010500300400011";
  final String expectedMac = "b4375a6b43de6d2421628bba7d6ee0e6";

  private FirmwareImageData firmwareImageData;
  private final Long identificationNumber = Long.decode("0x40050010");
  private final String deviceIdentification = "G0035161000054016";

  @BeforeEach
  public void init() throws IOException {
    final String filename = "integra-v00400011-snffffffff-newmods.bin";
    final InputStream resourceAsStream =
        FirmwareImageDataTest.class.getClassLoader().getResourceAsStream(filename);

    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int nRead;
    final byte[] data = new byte[1024];
    while ((nRead = resourceAsStream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }

    buffer.flush();
    final byte[] byteArray = buffer.toByteArray();

    this.firmwareImageData = new FirmwareImageData(byteArray);
    this.firmwareImageData.addIdentificationNumber(this.identificationNumber);
  }

  @Test
  void calculateMac() throws IOException, ProtocolAdapterException {
    // GIVEN
    when(this.secretManagementService.getKey(
            any(String.class), eq(SecurityKeyType.G_METER_FIRMWARE_UPDATE_AUTHENTICATION)))
        .thenReturn(this.authenticationKey);
    // WHEN
    final byte[] calculatedMac =
        this.macGenerationService.calculateMac(this.deviceIdentification, this.firmwareImageData);
    // THEN
    //    assertThat(this.toHex(calculatedMac)).isEqualTo(this.expectedMac);
    assertThat(Hex.toHexString(calculatedMac)).isEqualTo(this.expectedMac);
  }

  @Test
  public void testIV() throws IOException {
    // WHEN
    final byte[] iv = this.macGenerationService.createIV(this.firmwareImageData);
    // THEN
    //    assertThat(this.toHex(iv)).isEqualTo(this.expectedIv);
    assertThat(Hex.toHexString(iv)).isEqualTo(this.expectedIv);
  }
}
