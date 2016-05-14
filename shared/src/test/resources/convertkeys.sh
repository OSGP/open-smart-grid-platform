#!/bin/sh
#

[ -f secret.aes ] || { echo "geneate (AND KEEP IT SAFE!) or provide file secret.aes (see readme.txt how to generate)!!"; exit 1; }
[ -f devicekey_priv.der ] || { echo "You need the original rsa private key: devicekey_priv.der!!"; exit 1; }

echo "output and errors go to converion.log!!!"

exec > conversion.log
exec 2>&1

xxd -p secret.aes secrethex

[ $? -eq 0 ] || { echo "hex conversion aes key failed"; exit 1; }

echo searching keys to convert

psql -d osgp_adapter_protocol_dlms -U osp_admin -t -c \
 "select * from security_key where valid_to is null and char_length(security_key) > 15 order by dlms_device_id" | while read line
do

  [ -z "$line" ] && { echo "invalid line, end of input?"; continue; }

# id | creation_time | modification_time | version | dlms_device_id | security_key_type | valid_from | valid_to | security_key

  id=$(echo -n $line|cut -d"|" -f1|sed -e 's/^[[:space:]]*//'|sed -e 's/[[:space:]]*$//')
  keytype=$(echo -n $line|cut -d"|" -f6|sed -e 's/^[[:space:]]*//'|sed -e 's/[[:space:]]*$//')
  dlmsid=$(echo -n $line|cut -d"|" -f5|sed -e 's/^[[:space:]]*//'|sed -e 's/[[:space:]]*$//')
  version=$(echo -n $line|cut -d"|" -f4|sed -e 's/^[[:space:]]*//'|sed -e 's/[[:space:]]*$//')
  version=$(expr $version + 1)

  echo -n "id $id, device $dlmsid, type $keytype: decrypt.."

  echo -n $line|cut -d"|" -f9|sed -e 's/^[[:space:]]*//'|sed -e 's/[[:space:]]*$//' > hexkey
  xxd -p -r hexkey binkey
  openssl rsautl -decrypt -in binkey -out deckey -inkey devicekey_priv.der -keyform DER -raw

  [ $? -eq 0 ] || { echo "decrypting rsa key failed (already converted to aes?)"; continue; }

  echo -n "encrypt.."

  openssl enc -e -aes-128-cbc -in deckey -out enckey -iv 000102030405060708090a0b0c0d0e0f -K $(cat secrethex)

  [ $? -eq 0 ] || { echo "aes encryption of key failed"; continue; }

  xxd -p enckey newhexkey
  newhexkey=$(cat newhexkey|tr -d \\n)

  echo -n "invalidate.."

  psql -d osgp_adapter_protocol_dlms -U osp_admin -c \
    "update security_key set valid_to=current_timestamp where id=$id"

  [ $? -eq 0 ] || { echo "invalidating current key failed"; continue; }

  echo -n "insert new key.."

  psql -d osgp_adapter_protocol_dlms -U osp_admin -c \
    "insert into security_key (creation_time, modification_time, version, dlms_device_id, security_key_type, valid_from, security_key)\
     values (current_timestamp,current_timestamp,$version,$dlmsid,'$keytype',current_timestamp,'$newhexkey')"

  [ $? -eq 0 ] || { echo "inserting new key failed, rollback"; psql -d osgp_adapter_protocol_dlms -U osp_admin -c "update security_key set valid_to=null where id=$id"; continue; }

  echo "converted, next row"
    
done
