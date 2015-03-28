package com.alliander.osgp.platform.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.alliander.osgp.platform.application.mapping.OslpMapper;
import com.alliander.osgp.shared.security.CertificateHelper;

public class OslpDeviceServiceBuilder {

    private static final String CIPHER = "RSA/ECB/PKCS1Padding";

    private static final int CLIENT_PORT = 12121;

    private static final String DIGEST = "SHA-512";

    private static final String KEY_TYPE = "RSA";

    private static final int LOCAL_CLIENT_PORT = 12123;

    private static final String PRIVATE_KEY_BASE_64 = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMZLix1u8mSOk8LrMW7wZHgskw0J"
            + "VY/EECl5BaxZKtJxIwB3W/9zaceYMgbBSE+7RcjAheRGncmF3DvuD0j40937PyV3OE48YsfPVecP"
            + "Xa+NIfVpYWwGa2QEYYvjBZ5FjD6zeubCe80fKhB2bXLM1SDXiuvoemSt161rC4m8hUvXAgMBAAEC"
            + "gYEAvOZ6QC/Q+bpZSPaEwQqAq3rLG0ApIivEub1wih7njFH65hbOrStlOZ7jCUxXdp0QfY3p/uzG"
            + "o5PBmdXO+dUQ/lcpZSJzvmlf1gfEZAL7088pe9fyvwLRuiCOzw6b+j5AoQLfXrFv3fDZlWf1z82q"
            + "Dc8cGNspYrvCSnjSRG4izQECQQD51biejn12Qan57c7nbF++xgaRmBQcURDFhFltb7vGajoYwSvp"
            + "9w42pTNL29yAPlFqx9X+FsLCu58g4TJUlDRtAkEAyzA7ET8Az3PqXcL6VwykZQE37HVnLZwb7d4u"
            + "y7+TIqXCuGDkHAjQ4bsHrGzheJI8fgqyOmvGxMY3P658aCyu0wJABJQPExDHadBgPg1GmmUZCBT2"
            + "79oanD48EXKQdPn0NfmiYOvBU0NMxmGWpBA+ZTc/JLbOzB48qXbovqCB3JzurQJBAJ/iufgeLZMQ"
            + "0ZEqRjeNeScJyGnHEIOxXcDVntkxTKRs70aK57Svsz6NH8KsgtePqw47eHfEK0rX9s2jjb2ju4UC"
            + "QQDUw3UoM9nLSmPBqPhWpNiTThOISZNmTMXEmEvb0D3A0Tpmbu6ciTr1sJRUFQi4WRTaxaqM3sdj"
            + "cPjXvilnNkk6";

    private OslpDeviceService oslpDeviceService;
	
	public OslpDeviceServiceBuilder(OslpDeviceService oslpDeviceService) {
		this.oslpDeviceService = oslpDeviceService;
	}
	
	public OslpDeviceServiceBuilder withChannelHandler(OslpChannelHandlerClient oslpChannelHandler) {
		this.oslpDeviceService.setOslpChannelHandler(oslpChannelHandler);
		return this;
	}
	
	public OslpDeviceServiceBuilder withMapper(OslpMapper mapper) {
		this.oslpDeviceService.setMapper(mapper);
		return this;
	}
	
	public OslpDeviceService build() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		this.oslpDeviceService.setCipher(CIPHER);
		this.oslpDeviceService.setDigest(DIGEST);
		this.oslpDeviceService.setOslpPortClient(CLIENT_PORT);
		this.oslpDeviceService.setOslpPortClientLocal(LOCAL_CLIENT_PORT);
		this.oslpDeviceService.setPrivateKey(CertificateHelper.createPrivateKeyFromBase64(PRIVATE_KEY_BASE_64, KEY_TYPE));

		return this.oslpDeviceService;
	}
}
