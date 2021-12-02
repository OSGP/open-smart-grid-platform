COPY(
SELECT security_key FROM security_key INNER JOIN dlms_device ON security_key.dlms_device_id = dlms_device.id
WHERE device_identification = 'DEVICE_IDENTIFICATION' AND valid_to is null AND security_key_type like '%_AUTH%') 
  TO '/tmp/keys/hexauth'  ;

COPY(
SELECT security_key FROM security_key INNER JOIN dlms_device ON security_key.dlms_device_id = dlms_device.id
WHERE device_identification = 'DEVICE_IDENTIFICATION' AND valid_to is null AND security_key_type like '%_ENCR%') 
  TO '/tmp/keys/hexenc'  ;

COPY(
SELECT security_key FROM security_key INNER JOIN dlms_device ON security_key.dlms_device_id = dlms_device.id
WHERE device_identification = 'DEVICE_IDENTIFICATION' AND valid_to is null AND security_key_type like '%_MAST%') 
  TO '/tmp/keys/hexmaster'  ;
