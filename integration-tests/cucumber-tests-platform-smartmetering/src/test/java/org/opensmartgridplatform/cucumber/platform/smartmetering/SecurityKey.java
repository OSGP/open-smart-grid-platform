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
      "9eab9df8169a9c22d694067435b584d573b1a57d62d491b58fd9058e99486166"
          + "6831fb9f5ddbf5aba9ef169256cffc8e540c34b3f92246d062889eca13639fe3"
          + "17e92beec86b48b14d5ef4b74682497eed7d8ea3ae6ea3dfa1877045653cb989"
          + "146f826b2d97a3294a2aa22f804b1f389d0684482dde33e6cdfc51700156e3be"
          + "94fc8d5b3a1302b3f3992564982e7cd7885c26fa96eeb7cab5a13d6d7fd341f6"
          + "65d61581dd71f652dc278823216ab75b5a430edc826021c4a2dc9de95fbdfb0e"
          + "79421e2662743650690bc6b69b0b91035e96cb6396626aa1c252cddf87046dc5"
          + "3b9da0c8d74b517c2845b2e8eaaf72e97d41df1c4ce232e7bb082c82154e9ae5"),
  SECURITY_KEY_E(
      "867424ac75b6d53c89276d304608321f0a1f6e401f453f84adf3477c7ee1623c",
      "4e6fb5bd62d7a21f87438c04f518939cce7cfe8259ff40d9e3ff4a3a8c3befda"
          + "d191eb066c8332d6d3066a2ed866774616c2b893da4543998eb57fcf35323cd2"
          + "b41960e857c1a99f5cb59405081712ab23da97353014f500046756eab2620d13"
          + "a269b83cbefbdfb5e275862b34dd407fd745a1bca18f1b66cb114641212579c6"
          + "da03e86be2973f8dd6988b15bb6e9ef0f5637827829fc2241891c050a95ef5fc"
          + "787f740a40aa2d528c69f99c76ad380bba3725929fcbe11ab72cf61e342ab95f"
          + "c3b883372c110830f28144894aa2919a590822b1e594b807e86f49093982b871"
          + "c658db0b6c08a90bae55c731efb3d40f245d8c0ad1478b55fa68cced3c1386a7"),
  SECURITY_KEY_M(
      "55dc88791e6c8f6aff4c8be7714fb8d2ae3d02693ec474593acd3523ee032638",
      "6fa7f5f19812391b2803a142f17c67aa0e3fc23b537ae6f9cd34a850d4fd5f4d"
          + "60a3b2bdd6f8cb356e00e6c4e104fb5ea521eeabd8cb69d8f7a5cbe2b20e010c"
          + "089ee346aaa13c9abdc5e0c9ba0fcafff53d2dcd3c1b7a8ee3c3f76e0d00fcd0"
          + "43940586f055c5e19a0fa7eeff6a7894e128029eaf11c1734565f3f5b614bfab"
          + "9ea5ce24bf34d2e59878dc2401bd175333315ce197d4243dced9c4e28a23bc91"
          + "dca432985debe81cf5912df7e99b28f596f335e80678d7b5d1edc93be8bf22d7"
          + "7b2e172ccd7c6907454a983999840bf540343d281e8f9871386f005fe40065fc"
          + "be218bdc605be4e759cb1b8d5760eab7b8ceb95cfae2224c15045834962f9b6b"),
  MBUS_USER_KEY(
      "17ec0e5f6a3314df6239cf9f1b902cbfc9f39e82c57a40ffd8a3e552cc720c92",
      "a0be6ca007ccdd440cf3e87a885bfda73db8ec1ce5483cc874adeaba7910a1b9"
          + "a6455398d36bc2fca9026e9f949e555d6cc590002301dbbc97cf2ed7b3d4bd9d"
          + "8e14c63a813f814114fa2c24bc57db6808b303de34f6ec29873ac6885f6606a7"
          + "1e7c585ddee0b01ab84a6cc504e7c3bc3533df880a2696cc2531863b74e1bd05"
          + "bcca22966d8abd02b6379e9c61e01e09ed0a3e55af52fa9dc5fc64fc1f71e0b6"
          + "b72439a1f326aac5e581b56dc2952c3f8f19389dca200246aa9cf169922c55a5"
          + "f1b07784f7f9da9e6949f5508dbb72c4f8ee0935eb4fa51ebaf39c4cee57d837"
          + "0e37ce43c62df834cfeed0ed33029fc12472d051e93cd630fe16e876a5001b42"),

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
