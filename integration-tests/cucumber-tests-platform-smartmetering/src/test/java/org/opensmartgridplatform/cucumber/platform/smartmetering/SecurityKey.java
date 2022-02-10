/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering;

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
      "6c06ab7308b5a028b601a8ff4466acd2ffb1bde9a9d19498a00ce03362ae52c0"
          + "d0c41a8c21a4a0004c84aaefd82421d32be9c339aaafb335011ee82ef4837383"
          + "6de48824a743817e42a25b3ac53c873ca41c4821e0401e0ceab28919398d458c"
          + "966fbfddf2116764082a73fb3409863d4f867c8999f285234f873e6b163e14e5"
          + "61857d73c4319c1ee6e813389dfce178c9834b9e77845a57fe40f6098dd657f4"
          + "6d711440fc7704710144e83f0d73bda8ae3ef2319b88a1ff964cb198c5f65eaa"
          + "777e793bddb2fb6719221da7500e580c9ab743cb4050fc54cae826ad32fd7f15"
          + "b9fa9ee7fc63d966cce7e7a1fe2456fe370dfca0daf18fb38eacc1938c475090"),
  SECURITY_KEY_2(
      "a7a9a90e9f071d90df02486bf3bd2c5c9f25cf63a05ef97d65a6250fbcbed9c1",
      "38fae8596aef42153d07b4921f31df4c12ee13f6dfc3fca40de7be2990bcab2b"
          + "cf54c6e36d2750bac9537bf559b7ac77f5e0ca8786d6af8809d7d38273585fca"
          + "f3e932b0de32dcfacfb485fd9a0e15532c3b61be9d9230d667b129919b42cef3"
          + "84d17e27dceaf861aad260efafc70daa6ec251fd31e1f593fd838ea34b20fc9a"
          + "a2afa2120eb7db74b70b83b648373947a1d809044ed934b22186bb573ff12905"
          + "752194129dec346095de9dc362313b4b19ae9449576aac86a750b383cbb50ff0"
          + "2d8ac9f59a91dc5808afeefdcc30a5fd13069a8ed1c9de17afa6ae31439dc103"
          + "75b72d795ff5f554ecb78473ad906de85197f762e93382d6e443c1c87fc25470"),
  SECURITY_KEY_3(
      "a69dc3f14398f90fcf08b01c0b3f1cf2c99d228887e56e3f291b47e8ef86f43a",
      "4eb0e52ab21b96cd075a2bb3a0a75f25ba8ecd305a08a408d49d49e5042f949b"
          + "4a4c843b769107023df7146dc4885ba75f0c5f39dcd09d13bb12870d28e87347"
          + "26f3a251141726eba902cfdebe3c3bf873723eeafcf769d8e4eb4f88793cc7ad"
          + "5083040c3a8af21e5fa40d1a2c9f48b2b3771f91ef154feb74dca45374d08b00"
          + "cd1571c700b2a83e1f6d5503934ebcda42c25606171b65066b68ec13a40d367f"
          + "06662088d656e6345667a5a77c2deac8f6be6a842ad6fbd23a5904619b0216a2"
          + "6affff7932cf5331f34332a1672b393d822c1fcc4ce03bae293602820a570645"
          + "6354b34da90052e3704ac4b7680ec0ad2b1c1e0959fb8ab9e5a7964f865f08a5"),
  SECURITY_KEY_4(
      "072393e2b530abc080791c8cd75aa9400503196d64076af74d5c482ad6886143",
      "5a399c485144c1465866c946c53a234bc9d98f96318017f0dc1f21854a586630"
          + "6580df6cb5a25707170ec62fee182bba05f941e36dca884492da85f0087a0fe6"
          + "89fc89f731cc7138b09edc31ce553ed21c7422bf88adf8cb8b5b58704efd655d"
          + "14d09e28c5b05dcb8b18731813be80e66824f4b1cec12903115e4b1e3f92e87a"
          + "94e366299b0089403cc5fd8c85701e38213ea109a7ab77f23d6d0fba56f44124"
          + "583d4856c6999e066691d22804949c722feebe93174060d1f4db9a3c057996ad"
          + "ba3dbd480994474fc06f1ba817fe712eae581517758f941052f0e96b5429e9aa"
          + "de0e038e3b1eabac2b30ac523baac874f3c6eb91eb6ff6475cd38d87d330b25b"),
  SECURITY_KEY_5(
      "7a121f8c11885817505ac8c4ecb64c9105cd48afc36110ebf9792c73bb3e5e1d",
      "28f57d0b46031e3bb4d1aec572a1483ff5921618e1413084f8ec3ddcbc1c26b7"
          + "ddc44d2ca8330670a8b117170a6716c2c157aaa6e7a446b20486e6ead9030762"
          + "46d994b847d6f6a8b26e09e332fef9e5a1ebc7013c7d352aef900329af1644ed"
          + "e426e0ea5ae71dfaf74ea53769387a25d70b8cbd3b0c94c358e4d1278d62dcf0"
          + "2d91228ae6a046a00555e83eb82de859612ab3efe12ca065de20ec7be6fecff9"
          + "e2409398ae721558c8f28602cd92562d5e0efae65c7c4e2667701d967abdce60"
          + "5fd170bde9331db94fd43732c37ebb633160ca6a84e1aa32d4a18efdd1b0b429"
          + "03f7bab0edc7e3b58cabfdb9a25cdcc328541081ed984da3aac176147c4de139"),
  SECURITY_KEY_6(
      "c50697dc11408e369310c4d4066d44de25ecf09bd6488b28dca8ecc61dec2bb3",
      "366614bcfa0ff0d0db6e55f0276a7e42ab1e414f1b55f93d9497d3176e1d6b77"
          + "772fea08b9f2e6e69de2b5a717e6fd9ace9a71bd4b2405408a8b9f9716d9bed5"
          + "ddbbb14654617a2c3be1f0eeb5988e4a02c887e26078e664a6a7fad982d7c25b"
          + "029f925363d715c7ddbf446b1ce1ffd26b5a6b8cd58c2b7a2f70c708abb42f19"
          + "4b797f118a054ee8ebad7db0f03273f9ab25d1e8d75bf9c4817f1156b9e72a6b"
          + "11f1766476cabd6c8df6b5b570efdeae1213ca8712a766ee8f77c987f8595323"
          + "776953722180c2bd7053ae66e7d0d10a3806b2235666566e2fd72bc1732f2fd4"
          + "bcbaff462099b013a814081f73714792c39b206a9d05de51b7273731ec372911"),

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
