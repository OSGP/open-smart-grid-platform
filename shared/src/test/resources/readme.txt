first external preparation....

GENERATE AES SECRET
- openssl enc -aes-128-cbc -k eduard -P -md sha1|grep key=|cut -d"=" -f2|xxd -p -r > <aes128cbckey>

select keys from security_key, then for each line:

  DECRYPT EXISTING KEYS

  - xxd -p -r <hex> <bin>
  - openssl rsautl -decrypt -in <bin> -inkey devicekey_priv.der -out <decrypted> -keyform DER -raw

  ENCRYPT KEYS AGAIN
  - openssl enc -e -aes-128-cbc -in <plain> -out <encrypted> -iv 000102030405060708090a0b0c0d0e0f -K <secret bits in hex>
  - xxd -p <encrypted>

  store new keys

psql -d osgp_adapter_protocol_dlms -U osp_admin -c \
 "select * from security_key where valid_to is null and char_length(security_key) > 15" | while read line
do

  echo -c $line|cut -d"|" -f9|sed -e 's/^[[:space:]]*//'|sed -e 's/[[:space:]]*$//' > hexkey
  xxd -p -r hexkey binkey
  openssl rsautl -decrypt -in binkey -out deckey -inkey devicekey_priv.der -keyform DER -raw

  openssl enc -e -aes-128-cbc -in deckey -out enckey -iv 000102030405060708090a0b0c0d0e0f -K ˋcat secrethexˋ
  xxd -p enckey newhexkey

  id=ˋecho -c $line|cut -d"|" -f1|sed -e 's/^[[:space:]]*//'|sed -e 's/[[:space:]]*$//ˋ
  type=ˋecho -c $line|cut -d"|" -f6|sed -e 's/^[[:space:]]*//'|sed -e 's/[[:space:]]*$//ˋ
  dlmsid=ˋecho -c $line|cut -d"|" -f5|sed -e 's/^[[:space:]]*//'|sed -e 's/[[:space:]]*$//ˋ
  version=ˋecho -c $line|cut -d"|" -f4|sed -e 's/^[[:space:]]*//'|sed -e 's/[[:space:]]*$//ˋ
  version=ˋexpr $version + 1ˋ
  newhexkey=ˋcat newhexkeyˋ

# creation_time | modification_time | version | dlms_device_id | security_key_type | valid_from | valid_to | security_key

  psql -d osgp_adapter_protocol_dlms -U osp_admin -c \
    "update security_key set valid_to=current_timestamp() where id=$id"

  psql -d osgp_adapter_protocol_dlms -U osp_admin -c \
    "insert into security_key (creation_time, modification_time, version, dlms_device_id, security_key_type, valid_from, security_key)\
     values (current_timestamp(),current_timestamp(),$version,$dlmsid,'$type',current_timestamp(),'$newhexkey')"
    
done
