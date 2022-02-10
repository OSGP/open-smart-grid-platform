/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering;

/*
 * The SOAP-request keys are RSA encrypted using 'RSA/ECB/OAEPPadding' transformation
 */
public enum SecurityKey {
  SECURITY_KEY_A(
      "c19fe80a22a0f6c5cdaad0826c4d204f23694ded08d811b66e9b845d9f2157d2",
      "5eef89cb53cb3319049403d26d73ff640f7c48512d9d4da2b4d54a75a0276402"
          + "db1ea7c1dc2907ff2ca1ddb8b72fe08cc9b07632c688596b2608462016110897"
          + "9b4ac3e39dcd3667a543bb2a6cf56a56ddddece6913e6244d5d5d539eb17ba1c"
          + "7c214a8a5f3ce74bb63075928cd28f443532ec1c662f6c90d18fa9f1e34ca0d5"
          + "cd0e7224a9b422229d434a0f469ff137c37ef8fbf67001b4cd484a59daac38a0"
          + "73cbacfd9f34b43dfccfd0e194b5fd5fafd7e6d3ed2806efc2fab6d21272ec8b"
          + "88573043d11ea270a3de7fd9175db344862c974c9528ac5b94d0c109665fb80f"
          + "e94eaa4a942a07110be991a51028aedf6cc2c5668267d8737b075c013a55a4c9"),
  SECURITY_KEY_E(
      "867424ac75b6d53c89276d304608321f0a1f6e401f453f84adf3477c7ee1623c",
      "4b8d8a0beac5078a4d3d39d9628ec5f94781cf153f739c07d1ba64ae581fd81c"
          + "cba91aae24188e12e55c6afbe97cd5fc9473488fafddc603fd59a4658885bd61"
          + "47756e42e5259e9bb7ef4b8f5212e92793d47cce79ec9b5f3be0015ff1e372d2"
          + "f641346a50cacaa85e51de62403b2d2ae73298d05bf65440cfeb5d162f4f5076"
          + "8b2c0af8a5611d1379cfdb62b00314eaf8616dee6454974c60d14ef61e050fe6"
          + "1fa8117f7bdc49f2876aca75c4ea1f253656a981bf9d86370ad5841766234207"
          + "c204634656f349ed7156f6e3685e415389d6f9352c388439c7e4c6c8491a896d"
          + "c008e121ae3bd7b8bc21a44174f31463e7f4433f5bbbad7c7a5cb0c90634f08c"),
  SECURITY_KEY_M(
      "55dc88791e6c8f6aff4c8be7714fb8d2ae3d02693ec474593acd3523ee032638",
      "95f993610f3a5956062052815a7dcb95c4165b361b9cb633a7aa16bd4c402235"
          + "4859ac0668a37476508ebe912d2e55a819c2cd233d6c239b272a86b1fc8416d6"
          + "1c15ccb3a5b195f75eb3f6b7a34c2e6780d6d2835a30d6ef23df1228bacf04bc"
          + "e3ce2da041f8d64afd9eeb21903e5032465cb518a8c1dd91dc51b2163d380ae2"
          + "2b13943bfa5898e06741355516dd433dae3fa9fe8edaacb3b9459ac68f650a0d"
          + "d7a4eab9317f6e9f9b99dcee8f0b3fdd81751e6eb9a143656f40aa15bd32feca"
          + "fd2b76ebc1cba421cc205941552a00a4e645d2370a4995052935d55af515f739"
          + "4bcddf173ad2082ad206fcb894ddbd3a7abe8196f6d0ad78535bd0bb510c4828"),
  MBUS_USER_KEY(
      "17ec0e5f6a3314df6239cf9f1b902cbfc9f39e82c57a40ffd8a3e552cc720c92",
      "005528c5f47bd5abeae2eff7bcb36cde0910b97fbc2e88ee0afd80b6629bd760"
          + "0777755e734334bb33772fce892df58ba4bc29a31f836a468bb441ffb6fefe93"
          + "846976710ca4cd500bc41a603d3a206db206f83e32b1129d22511911ba35e357"
          + "6ca5d56e08de3d50e402c96e550af07b75f8a7362bf6398e8fd23f597fe843ee"
          + "1f091f87db6c50754183df729b86afe2f060332e01451ecfad5b508fbc674a29"
          + "fde420eab3220069fcecee3ac0c704ed3bc2f8ec0aad25844bf62548cf1afa79"
          + "16b93d841d37ad26373d776e4fef04adc592cb55a51891c536dd3a089fbf0507"
          + "98b9468db5f66c6a1c057796a9ba34cbba6d8dec86b3b8d4ad437429cbdd7abe"),
  SECURITY_KEY_G_ENCRYPTION(
      "867424ac75b6d53c89276d304608321f0a1f6e401f453f84adf3477c7ee1623c", null),
  SECURITY_KEY_G_MASTER("867424ac75b6d53c89276d304608321f0a1f6e401f453f84adf3477c7ee1623c", null),

  PASSWORD("e7233dec0dfbf031960e21c149c3293e", ""),

  SECURITY_KEY_1(
      "6748a4b0c2e15074f16ca54c0ce26bdec3d47ff8cc1280d2d3a98c3c02953088",
      "36f60f8d9f6477b63a8d00bf20593df3f966079128e60a096fca163f8ea1e411"
          + "914347f3fb42d01dca032a2ac815a5cae413a70a99d0f69c86f86cdb8c140669"
          + "8998b52806f2a1e949fdddc6a749c16f2de4d6a86ae35ffeb94c5c57b4bb3641"
          + "e806ebfe6ed7c595fe8c99619a3a3c1eb6cb7ce8d3af64f8274c0472469cdbb7"
          + "263fca7de4f6eac4dd1e30d70a43e61daf518b752658ab348043e1f71f962ce4"
          + "6cebfe23c3219838d16bda1e5c337e7a1ba256318c3c35d2e7435dde1091c3c5"
          + "bb54ef2c505696028f44e1cf0df0a4fe1683a836f7d2d173b73cb59b86ac107d"
          + "03a3b4ca9407e6d12eb6cbd9af11412a6d6e63b09f42bfda08df6bb6425cba8b"),
  SECURITY_KEY_2(
      "a7a9a90e9f071d90df02486bf3bd2c5c9f25cf63a05ef97d65a6250fbcbed9c1",
      "02835b236bfe5fe65b2340c35d80c1bf15ecda58bccbc780ff614ce4e3c72014"
          + "32968c6a45662296b368337fbe71c129720829753f2a52482e3e792c441b3260"
          + "2e96ed2a869c3ca4531dd726eaf2d16d44eb1618447c7babe6f70f525773756f"
          + "76656b2bb790f58d347ae30483229309cdcf88e65d092940a3b6d50425774212"
          + "e9d0e4fc221300a62a3e91597debf93fdf7819c5f19492d74f87ae0286b5f955"
          + "6b6c7b2d464b7462ae9d9a6cb57f0c9fc6a8e9a1d77f6ba293b8410350a455c6"
          + "d553e6c99f79eca124b20acd88e167596161a9092f44ecfc3b42dba154f9e7b1"
          + "ede9a36da4869c45daa8d7ce3b9bc499f3e959d4d0f837eeeab7fe92b5492c0a"),
  SECURITY_KEY_3(
      "a69dc3f14398f90fcf08b01c0b3f1cf2c99d228887e56e3f291b47e8ef86f43a",
      "4924196175a983c496a1b7e327e7925a60aaf29036941ba2115da58b9b7aa972"
          + "ca4ec6cae58f73e63e6d0b48ed29ac15b2d5b48b773194c8a8d23042b78412c0"
          + "c23fb8bf753ebc2033cf614beaab493ab2873e3597b5faaaab343b7225c06f39"
          + "50754f4f29fd3680cd9697074dedb33c8e11861c5a38166a5abc7e9a6906c041"
          + "4af8d579ffcf94d754a439e3edf8c129f682038e7b4671c5837cc6839c3baa65"
          + "0aad7e6d779bd5b565ec3fa80196fe1b7842ea28a90babbfe7e6bb7b643160f5"
          + "8bb86527ee95adc154c95b2cd07b956da59891bea4c35305fc1e6f535297afc2"
          + "5b2f495a76d3d4333ddf4de5766e14a3a7924fb699bcc7a8ee25068690fb5403"),
  SECURITY_KEY_4(
      "072393e2b530abc080791c8cd75aa9400503196d64076af74d5c482ad6886143",
      "4e5d93666aee922e0e09985383f74f5dfeae149a9e3a7b5430967b61d5e4e74a"
          + "d83127728bf61cf1ab09083bc14f21a3c67176323b95666614f6cadb69606b5d"
          + "2a950a892d65576d69e25b5a6de92f9b66be32d1f0f43beddd957d534ed2f615"
          + "078017affbd4e60d3154ef2d6e13388a6d6892146766d7a024f3eda61a4ad6ce"
          + "36629367c24dbf495dd84ab44d87e8b2b4f5b903f0b600abfef32f3775e6f6fc"
          + "b007b309fe8eae5b56df9a682feccb015c7e1018b83fc2b3374b3a6b72909642"
          + "136e41edc62846fa8560a8ac2723cd20e0e07898911b91958131d15f593d5f55"
          + "4742304eb987256d2f3084cd9b7fc031f656ce37a47efc473832c10fad7a32c4"),
  SECURITY_KEY_5(
      "7a121f8c11885817505ac8c4ecb64c9105cd48afc36110ebf9792c73bb3e5e1d",
      "96b89a4c1d91fbc7eb9ab2dc924da3651f175cd6520ad8459a1a9975df4fe2a9"
          + "0a62543a11e1bc97677523879e39b5ebfac0c3a272c0763fd6da62fc9175162e"
          + "79d480653bd60fb660bfe3383625f55f4bc31d5329267d4cba15c760065f14d5"
          + "3c925250fb903185e44961847b03aa50eb7fe982b00ff16c677a03f4e2f67fa7"
          + "8f0dfe4cbe224a797e6d5dfcf0429e09a6b8303459c341a159a2d8714e8c7d3e"
          + "bfe42f8895f4a4197c80b6a0e2a10c3102ea5bf8567c2952cf051e187bea0516"
          + "3479a9cfb3dcd37d017b06ffa16aa241ffc3d765602f81aa557ed81d8e44e565"
          + "733cb41f3cc95b2444c236304f78142043d503ef9957e4af4f94857a238debd5"),
  SECURITY_KEY_6(
      "c50697dc11408e369310c4d4066d44de25ecf09bd6488b28dca8ecc61dec2bb3",
      "250288f64f75a6bc63dceb02aee1595040036fdeb5d1a944fed74afaf429e6f7"
          + "7cdc7c8eb55b0fbedb5e3565afd931049375966be3245151c3cbfbba9395f7e5"
          + "780992c93fdeff4df664dfb6d9db30e317d235459001113e136b2496431b64e6"
          + "f895aa7fc7ce2da0ba404e04f4193f92bf27efc11fe2bb073de78689b3db05e1"
          + "cbaae1ec418091d194c6a281cc3a3bbd336d6ce31b43c21cd60f614143e877b4"
          + "4143dde90a91a419b66b2e8f17055259dddb0a89cdb6cbf35089e4a1e2e7eef4"
          + "1fefc9990031379099affde00a12f178d86df8d7e84cf5361950465f08299107"
          + "55e34321312bcd5dbdeb459e970072fb14f2f8b3144166dfd5c7712fd32d3fdd"),

  INCORRECT_SECURITY_KEY_1(
      "34567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef12", "def0123456789abc"),
  INCORRECT_SECURITY_KEY_2(
      "1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef", "abc0123456789def"),
  INCORRECT_SECURITY_KEY_3(
      "567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234", "abcdef0123456789"),

  EMPTY_SECURITY_KEY("", "");

  private String databaseKey;
  private String soapRequestKey;

  SecurityKey(final String databaseKey, final String soapRequestKey) {
    this.databaseKey = databaseKey;
    this.soapRequestKey = soapRequestKey;
  }

  public String getDatabaseKey() {
    return this.databaseKey;
  }

  public String getSoapRequestKey() {
    return this.soapRequestKey;
  }
}
