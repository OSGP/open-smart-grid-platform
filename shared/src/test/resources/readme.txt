first external preparation....

GENERATE AES SECRET
- openssl enc -aes-128-cbc -k eduard -P -md sha1|grep key=|cut -d"=" -f2 > secrethex
- xxd -p -r secrethex secret.aes

select keys from security_key, then for each line:

  DECRYPT EXISTING KEYS

  - xxd -p -r <hex> <bin>
  - openssl rsautl -decrypt -in <bin> -inkey devicekey_priv.der -out <decrypted> -keyform DER -raw

  ENCRYPT KEYS AGAIN
  - openssl enc -e -aes-128-cbc -in <plain> -out <encrypted> -iv 000102030405060708090a0b0c0d0e0f -K <secret bits in hex>
  - xxd -p <encrypted>

  store new keys

